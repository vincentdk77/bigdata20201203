package com.chuangrui.reply

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, JSONUtils}
import org.apache.commons.lang.StringUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}


object FindReplyMessageBySmUuidToMysql2 {
  def main(args: Array[String]): Unit = {

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("FindReplyMessageBySmUuidToMysql2" +
      "")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)


    //回复模型加载
    val replyMaxTime = ConnectionUtils.selectReplyMaxTime()
    val replySortModel = NaiveBayesModel.load(sc, hdfs+"/replyModel/"+replyMaxTime.replace("-","/")+"/")
    val zangci:util.List[String] = ConnectionUtils.getZangCi()
    val replySortArray:Array[String] = ConnectionUtils.getReplyWords()
    System.out.println("array array array:"+replySortArray.length)
    val replyArray = sc.broadcast(replySortArray)
    val replyModel = sc.broadcast(replySortModel)
    val zcList = sc.broadcast(zangci)



    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime
    while (startTime <= endTime) {

      //拿最近3天的回复信息
      var replyRDD =  sc.textFile(hdfs+"/data/message/reply/"+sf.format(startTime).replace("-","/")+"/*.txt")
      for(i <- (1 until 3)){
        replyRDD =  replyRDD.union(sc.textFile(hdfs+"/data/message/reply/"+sf.format(startTime+i*24*3600*1000l).replace("-","/")+"/reply-"+sf.format(startTime+i*24*3600*1000l)+".txt"))
      }

      val replyRowRDD = replyRDD.map(line =>{
        val jsonObj:JSONObject = JSONUtils.getJson(line)
        if (jsonObj.getString("mobile") != null && jsonObj.getString("customerId") != null && jsonObj.getString("channelId") != null) {
          Row(
            jsonObj.getInteger("customerId").toString,
            jsonObj.getString("mobile"),
            jsonObj.getString("channelId"),
            jsonObj.getString("smUuid"),
            jsonObj.toJSONString
          )
        } else {
          println("line:" + line)
          null
        }
      }).filter(row => {
        if(row == null ) {
          false
        } else {
          true
        }
      })

      val replyRow:Array[Row] =  replyRowRDD.collect()

      var smUuidReplyMap:util.HashMap[String,JSONObject] = new util.HashMap[String,JSONObject]()
      var replyMap:util.HashMap[String,JSONObject] = new util.HashMap[String,JSONObject]()
      if(replyRow != null && replyRow.length >0) {
        for(row <- replyRow) {

          if(row(3) != null && !StringUtils.isEmpty(row(3).toString) ) {
            smUuidReplyMap.put(row(1).toString+row(3).toString ,JSONUtils.getJson(row(4).toString)  )
          } else {
            replyMap.put(row(0).toString+row(1).toString+row(2).toString ,JSONUtils.getJson(row(4).toString) )
          }
        }
      }


      val replyBroad =  sc.broadcast(replyMap)
      val smUuidReplyBroad = sc.broadcast(smUuidReplyMap)

      //短信内容
      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)

      val resultRDD:RDD[JSONObject]   = inputRDD.map(line => {
        val jsonObj:JSONObject = JSONUtils.getJson(line)
        //发送成功 并且通道id大于0
        if (jsonObj != null && !StringUtils.isEmpty(jsonObj.getString("channelId")) && Integer.parseInt(jsonObj.getString("channelId"))>0 && (jsonObj.getString("deliverResultReal") == null || "DELIVRD".equals(jsonObj.getString("deliverResultReal")) )) {
          try {
            //回复，但是smuuid为空
            val replymap = replyBroad.value
            //回复，但是smuuid有值
            val smuuidreplymap = smUuidReplyBroad.value

            //通过手机号+smUuid找到回复
            val smUuidKey = jsonObj.getString("mobile")+jsonObj.getString("smUuid")
            if(smuuidreplymap.get(smUuidKey) != null) {
              val replyJson = smuuidreplymap.get(smUuidKey)
              jsonObj.put("replyId",replyJson.getString("id"));
              jsonObj.put("replyCustomerId",replyJson.getString("customerId"));
              jsonObj.put("replyContent",replyJson.getString("content"));
              jsonObj.put("replyExtNo",replyJson.getString("extNo"));
              jsonObj.put("replyDeliverTime",replyJson.getString("deliverTime"));
              jsonObj.put("replyChannelId",replyJson.getString("channelId"));
              jsonObj.put("replyCtime",replyJson.getString("ctime"));
              jsonObj.put("replyLtime",replyJson.getString("ltime"));
              jsonObj
            } else {
              //smuuid找不到，再尝试客户id+手机号+通道id找。
              val key = jsonObj.getInteger("customerId")+jsonObj.getString("mobile")+jsonObj.getString("channelId")
              if( replymap.get(key) == null) {
                null
              } else {
                val replyJson = replymap.get(key)
                val replyDeliverTime = replyJson.getString("deliverTime")
                val msgDeleverTime = jsonObj.getString("deliverTime")
                val tempSf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val replyTime:Long = tempSf.parse(replyDeliverTime).getTime
                val msgTime:Long = tempSf.parse(msgDeleverTime).getTime
                if(replyTime-msgTime<=2*24*3600*1000l || msgTime-replyTime <= 2*24*3600*1000l ) {
                  jsonObj.put("replyId",replyJson.getString("id"));
                  jsonObj.put("replyCustomerId",replyJson.getString("customerId"));
                  jsonObj.put("replyContent",replyJson.getString("content"));
                  jsonObj.put("replyExtNo",replyJson.getString("extNo"));
                  jsonObj.put("replyDeliverTime",replyJson.getString("deliverTime"));
                  jsonObj.put("replyChannelId",replyJson.getString("channelId"));
                  jsonObj.put("replyCtime",replyJson.getString("ctime"));
                  jsonObj.put("replyLtime",replyJson.getString("ltime"));
                  jsonObj
                } else {
                  null
                }
              }
            }
          } catch {
            case ex => {
              println("line:" + ex)
              null
            }
          }
        } else {
          null
        }
      }).filter(row => {
        if(row != null ) {
          true
        } else {
          false
        }
      })


      val strRDD =resultRDD.map(json =>{

          //预测回复类型
          val replyContent = json.getString("replyContent")
          var rResult:Double = 0
          if(replyContent.toLowerCase.equals("t") || replyContent.toLowerCase.equals("td") || replyContent.toLowerCase.equals("丅")) {
            rResult = -1
          } else {
            val rWordList = PaoDingCut.cutString(replyContent)
            val rModel = replyModel.value
            val zc = zcList.value
            val rArray = replyArray.value

            for(i<- 0 to zc.size()-1) {
              if(replyContent.toLowerCase().contains(zc.get(i))) {
                rWordList.add(zc.get(i))
              }
            }
            var rCrrentArray:Array[Double] = new Array[Double](rArray.length)
            for(i<-0 to rCrrentArray.length-1) {
              rCrrentArray(i) =0
            }
            for(i <- 0 to rWordList.size()-1) {
              for(j<-0 to rArray.length-1) {
                if(rArray(j).equals( rWordList.get(i))) {
                  rCrrentArray(j) = rCrrentArray(j)+1
                }
              }
            }
             rResult = rModel.predict(Vectors.dense(rCrrentArray))
          }


          json.put("replyLevel",rResult)
          json.toJSONString
      })
//      strRDD.persist(StorageLevel.MEMORY_ONLY)
      strRDD.coalesce(10).saveAsTextFile(hdfs+"/data/testreply/"+sf.format(startTime).replace("-","/")+"/")
//      strRDD.mapPartitions(msgFunc).count()
//      strRDD.coalesce(10).saveAsTextFile(hdfs+"/data/testreply/"+sf.format(startTime).replace("-","/")+"/")
      System.out.println("time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }

  def msgFunc(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()
    while (iter.hasNext) {
      val e = iter.next;

      list.add(e)
      index = index+1
      if(index % 10 == 0) {

        ConnectionUtils.saveMsgReply(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      ConnectionUtils.saveMsgReply(list )
      list.clear();
    }
    iter
  }

}
