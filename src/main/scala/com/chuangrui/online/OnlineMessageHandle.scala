package com.chuangrui.online

import java.text.SimpleDateFormat
import java.util
import java.util.Random

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, HbaseUtils, JSONUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object OnlineMessageHandle {


//  var wordArray:Broadcast[Array[String]] =null
//  var model:Broadcast[NaiveBayesModel] = null
//  var sign:Broadcast[util.HashMap[Integer,String]] = null

//  var replyArray:Broadcast[Array[String]] = null
//  var replyModel:Broadcast[NaiveBayesModel] = null
//  var zcList:Broadcast[util.List[String]] = null
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("OnlineMessageHandle")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //短信模型加载
    val msgMaxTime = ConnectionUtils.selectMaxTime()
    val sameModel = NaiveBayesModel.load(sc, hdfs+"/model/"+msgMaxTime.replace("-","/")+"/")
    val array:Array[String] = ConnectionUtils.getWords()
    val signMap:util.HashMap[Integer,String] =ConnectionUtils.getSign2();
    System.out.println("array array array:"+array.length)
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)

    //回复模型加载
    val replyMaxTime = ConnectionUtils.selectReplyMaxTime()
    val replySortModel = NaiveBayesModel.load(sc, hdfs+"/replyModel/"+replyMaxTime.replace("-","/")+"/")
    val zangci:util.List[String] = ConnectionUtils.getZangCi()
    val replySortArray:Array[String] = ConnectionUtils.getReplyWords()
    System.out.println("array array array:"+replySortArray.length)
    val replyArray = sc.broadcast(replySortArray)
    val replyModel = sc.broadcast(replySortModel)
    val zcList = sc.broadcast(zangci)

    //黑名单级别数据
    val blackRDD = sc.textFile(hdfs+"/data/message/blacklevel.txt")
    val rowArray = blackRDD.map(line =>{
      val ss = line.split("\\,")
      Row(ss(0),ss(1))
    }).collect()
    var map:util.HashMap[String,Integer] = new util.HashMap[String,Integer]()
    for(row <- rowArray) {
      map.put(row(0).toString ,Integer.parseInt(row(1).toString))
    }
    System.out.println("map map map:"+map.size());
    val blackMap = sc.broadcast(map)

    //通道所属公司数据
    val channelCompanyRDD = sc.textFile(hdfs+"/data/message/channel.txt")
    val channelRowArray = channelCompanyRDD.map(line =>{
      val json = JSONUtils.getJson(line)
      Row(json.getString("id"),json.getString("companyName"))
    }).collect()
    var channelMap:util.HashMap[String,String] = new util.HashMap[String,String]()
    for(row <- channelRowArray) {
      channelMap.put(row(0).toString ,row(1).toString)
    }
    System.out.println("channelMap channelMap channelMap:"+channelMap.size());
    val ccMap = sc.broadcast(channelMap)

    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime
    while (startTime <= endTime) {
      //短信内容
        val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val jsonRDD:RDD[JSONObject] = inputRDD.map(line =>{
          try {
            var json:JSONObject = JSONUtils.getJson(line)
            val content = json.getString("content")
            if (!StringUtils.isEmpty(content) ) {
              val word_array = wordArray.value
              val msgModel = model.value
              val signMap = sign.value
              val wordList = PaoDingCut.cutString(json.getString("sign")+content)
              var crrentArray: Array[Double] = new Array[Double](word_array.length)
              for (i <- 0 to crrentArray.length - 1) {
                crrentArray(i) = 0
              }
              for (i <- 0 to wordList.size() - 1) {
                for (j <- 0 to word_array.length - 1) {
                  if (word_array(j).equals(wordList.get(i))) {
                    crrentArray(j) = crrentArray(j) + 1
                  }
                }
              }
              val result = msgModel.predict(Vectors.dense(crrentArray))
              val msgSort = signMap.get(result.toInt)
              json.put("msgSort",msgSort)
              json
            } else {
              null
            }
          } catch {
            case ex => {
              null
            }
          }
        }).filter(json =>{
          if(json !=null) {
            true
          } else {
            false
          }
        })

        jsonRDD.persist(StorageLevel.MEMORY_ONLY)
        //处理黑名单
        handleBlack(jsonRDD,sc,sf,startTime,partition,ccMap,blackMap)
        //保存短信
//        handleMsg(jsonRDD,sc,sf,startTime,partition)
        //保存频次
        handleFrequency(jsonRDD,sc,sf,startTime,partition)
        //客户评分
        handleCustomer(jsonRDD,sc,sf,startTime,partition)
//        回复评分
//        if(sf.format(startTime).compareTo("2018-08-11") > 0) {
//          handleReply(jsonRDD,sc,sf,startTime,partition,replyArray,replyModel,zcList)
//        }
      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }


  def handleMsg(inputRDD:RDD[JSONObject],sc:SparkContext,sf:SimpleDateFormat,startTime:Long,partition:Int) :Unit = {
    val newRDD = inputRDD.map(json =>{
        val mobile = json.getString("mobile")
        val ctime = json.getString("ctime")
        val yM = ctime.substring(0,7)
        (mobile+"@"+yM,json)
    }).groupByKey().map(tuple =>{
      val mobile = tuple._1;
      val jsonArray = new JSONArray()
      for(json <- tuple._2.iterator) {
        jsonArray.add(json)
      }
      val r = new Random()
      val index = r.nextInt(partition)+1
      (index,mobile+"@"+jsonArray.toJSONString)
    }).groupByKey()

    newRDD.map(tuple=>{

      var index:Int = 0
      var list:util.ArrayList[String] = new util.ArrayList()
      for( l <-tuple._2) {
        list.add(l)
        index = index+1
        if(index % 5000 == 0) {
          HbaseUtils.saveOrUpdateBatchMsgHbase(list)
          list.clear();
        }
      }
      if(list.size()>0) {
        HbaseUtils.saveOrUpdateBatchMsgHbase(list)
        list.clear();
      }
      tuple._1
    }).count()

  }

  def msgFunc(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()

    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 5000 == 0) {

        HbaseUtils.saveOrUpdateBatchMsgHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      HbaseUtils.saveOrUpdateBatchMsgHbase(list )
      list.clear();
    }
    iter
  }

  def handleBlack(inputRDD:RDD[JSONObject],sc:SparkContext,sf:SimpleDateFormat,startTime:Long,partition:Int,ccMap:Broadcast[util.HashMap[String,String]],blackMap:Broadcast[util.HashMap[String,Integer]]):Unit = {
    val tupleRDD = inputRDD.map(json=> {
      val deliverResult = json.getString("deliverResult")
      val mobile = json.getString("mobile")
      val channelId = json.getString("channelId")
      val map = blackMap.value
      val channelMap = ccMap.value
      if(map.get(deliverResult) != null && !StringUtils.isEmpty(channelMap.get(channelId))) {
        val level = map.get(deliverResult)
        (mobile,level+"@"+channelMap.get(channelId))
      } else {
        (mobile,null)
      }
    })
    val filterRDD = tupleRDD.filter(tuple => {
      if(tuple._2 != null) {
        true
      } else {
        false
      }
    })

    val lineRDD = filterRDD.groupByKey().map(tuple =>{
      val iterator = tuple._2.iterator
      var level = 0
      var companyName = ""
      for( l <-iterator) {
        if(!companyName.contains(l.toString.split("@").apply(1))) {
          companyName = companyName + l.toString.split("@").apply(1)+","
        }

        if(Integer.parseInt( l.toString.split("@").apply(0))>level) {
          level = Integer.parseInt( l.toString.split("@").apply(0))
        }
      }
      if(!StringUtils.isEmpty(companyName)) {
        companyName = companyName.substring(0,companyName.length-1)
      }
      val r = new Random()
      val index = r.nextInt(partition)+1
      //手机号+初始级别+计算公司之后的级别+通道id
      (index,tuple._1+"@"+level+"@"+companyName)
    }).groupByKey().map(tuple=>{

      var index:Int = 0
      var list:util.ArrayList[String] = new util.ArrayList()
      for( l <-tuple._2) {
        list.add(l)
        index = index+1
        if(index % 10000 == 0) {
          HbaseUtils.saveOrUpdateBatchChannelBlackHbase(list)
          list.clear();
        }
      }
      if(list.size()>0) {
        HbaseUtils.saveOrUpdateBatchChannelBlackHbase(list)
        list.clear();
      }
      tuple._1
    })

    //originRDD 存放的是 手机号@原始级别@最大级别@通道id#通道id#通道id...#通道id@公司名称#公司名称...#公司名称

//    val originRDD = lineRDD.mapPartitions(funcBlack)
    System.out.println(lineRDD.count());
  }

  def funcBlack(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()
    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 5000 == 0) {
        HbaseUtils.saveOrUpdateBatchChannelBlackHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {
      HbaseUtils.saveOrUpdateBatchChannelBlackHbase(list )
      list.clear();
    }
    iter
  }

  def handleCustomer(inputRDD:RDD[JSONObject],sc:SparkContext,sf:SimpleDateFormat,startTime:Long,partition:Int):Unit = {
    val tupleRDD = inputRDD.map(json =>{
        val msgSort = json.getString("msgSort")
        val mobile = json.getString("mobile")
        val customerId = json.getString("customerId")
        val jsonObject = JSONUtils.getJSONObject(msgSort,customerId)
        (mobile ,jsonObject.toJSONString)
    })
    val resultRDD = tupleRDD.groupByKey().map(tuple =>{
      val mobile = tuple._1
      val iterator = tuple._2.iterator
      var finalJson:JSONObject = new JSONObject()
      for(s <- iterator) {
        val currentJSON:JSONObject = JSONUtils.getJson(s)
        for(key <-JSONUtils.getJsonKeySet(s)) {
          val keyIds:String = finalJson.getString(key)
          if(StringUtils.isEmpty(keyIds)) {
            finalJson.put(key,currentJSON.getString(key))
          } else {
            if(!keyIds.contains(currentJSON.getString(key))) {
              finalJson.put(key,finalJson.getString(key)+","+currentJSON.getString(key))
            }
          }
        }
      }
      val r = new Random()
      val index = r.nextInt(partition)+1
      (index,mobile+"@"+finalJson.toJSONString)
    }).groupByKey().map(tuple=>{

      var index:Int = 0
      var list:util.ArrayList[String] = new util.ArrayList()
      for( l <-tuple._2) {
        list.add(l)
        index = index+1
        if(index % 5000 == 0) {
          HbaseUtils.saveOrUpdateBatchCustomerSocreHbase(list)
          list.clear();
        }
      }
      if(list.size()>0) {
        HbaseUtils.saveOrUpdateBatchCustomerSocreHbase(list)
        list.clear();
      }
      tuple._1
    })
//    val finallRDD = resultRDD.mapPartitions(  customerFunc  )
    System.out.println(resultRDD.count());
  }

  def customerFunc(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()
    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 5000 == 0) {

        HbaseUtils.saveOrUpdateBatchCustomerSocreHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      HbaseUtils.saveOrUpdateBatchCustomerSocreHbase(list )
      list.clear();
    }
    iter
  }

  def handleFrequency(inputRDD:RDD[JSONObject],sc:SparkContext,sf:SimpleDateFormat,startTime:Long,partition:Int) :Unit = {
    inputRDD.map(json =>{

        val mobile = json.getString("mobile")
        val ctime = json.getString("ctime")
        val msgSort =json.getString("msgSort")
        val day = ctime.substring(0,10)
        (mobile+"@"+msgSort+"@"+day,1)

    }).groupByKey().map(tuple =>{
      val mobileSortDay = tuple._1.split("@");
      var num = 0
      for(t <- tuple._2.iterator) {
        num = num +t
      }
      val json = new JSONObject()
      json.put(mobileSortDay.apply(2),num)
      val r = new Random()
      val index = r.nextInt(partition)+1
      (index,mobileSortDay.apply(0)+"@"+mobileSortDay.apply(1)+"@"+json.toJSONString)
    }).groupByKey().map(tuple=>{

      var index:Int = 0
      var list:util.ArrayList[String] = new util.ArrayList()
      for( l <-tuple._2) {
        list.add(l)
        index = index+1
        if(index % 5000 == 0) {
          HbaseUtils.saveOrUpdateBatchFrequencyHbase(list)
          list.clear();
        }
      }
      if(list.size()>0) {
        HbaseUtils.saveOrUpdateBatchFrequencyHbase(list)
        list.clear();
      }
      tuple._1
    }).count()

  }

  def frequencyFunc(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()

    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 5000 == 0) {

        HbaseUtils.saveOrUpdateBatchFrequencyHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      HbaseUtils.saveOrUpdateBatchFrequencyHbase(list )
      list.clear();
    }
    iter
  }


  def handleReply(inputRDD:RDD[JSONObject],sc:SparkContext,sf:SimpleDateFormat,startTime:Long,partition:Int,
   replyArray:Broadcast[Array[String]],replyModel:Broadcast[NaiveBayesModel],zcList:Broadcast[util.List[String]]
  ) :Unit ={
    //拿最近7天的回复信息
    val hdfs = "hdfs://cr"
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
          jsonObj.getString("content"),
          jsonObj.getString("channelId"),
          jsonObj.getString("smUuid"),
          jsonObj.getString("deliverTime")
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

    var smUuidReplyMap:util.HashMap[String,String] = new util.HashMap[String,String]()
    var replyMap:util.HashMap[String,String] = new util.HashMap[String,String]()
    if(replyRow != null && replyRow.length >0) {
      for(row <- replyRow) {
        if(row(4) != null && !StringUtils.isEmpty(row(4).toString) ) {
          smUuidReplyMap.put(row(1).toString+row(4).toString ,row(2).toString)
        } else {

          replyMap.put(row(0).toString+row(1).toString+row(3).toString ,row(2).toString+"@"+row(5).toString)
        }
      }
    }


    val replyBroad =  sc.broadcast(replyMap)
    val smUuidReplyBroad = sc.broadcast(smUuidReplyMap)

    val resultRDD:RDD[JSONObject]   = inputRDD.map(jsonObj => {

      //发送成功 并且通道id大于0
      if (jsonObj != null && !StringUtils.isEmpty(jsonObj.getString("channelId")) && Integer.parseInt(jsonObj.getString("channelId"))>0 && (jsonObj.getString("deliverResultReal") == null || "DELIVRD".equals(jsonObj.getString("deliverResultReal")) )) {
        try {
          //回复，但是smuuid为空
          val replymap = replyBroad.value
          //回复，但是smuuid有值
          val smuuidreplymap = smUuidReplyBroad.value

          //通过手机号+smUuid找到回复
          val smUuidKey = jsonObj.getString("mobile")+jsonObj.getString("smUuid")
          if(!StringUtils.isEmpty(smuuidreplymap.get(smUuidKey))) {
            jsonObj.put("replyContent",smuuidreplymap.get(smUuidKey))
            jsonObj
          } else {
            //smuuid找不到，再尝试客户id+手机号+通道id找。
            val key = jsonObj.getInteger("customerId")+jsonObj.getString("mobile")+jsonObj.getString("channelId")
            if( StringUtils.isEmpty(replymap.get(key))) {
              null
            } else {
              val replyContent = replymap.get(key).split("@").apply(0)
              val replyDeliverTime = replymap.get(key).split("@").apply(1)
              val msgDeleverTime = jsonObj.getString("deliverTime")
              val tempSf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
              val replyTime:Long = tempSf.parse(replyDeliverTime).getTime
              val msgTime:Long = tempSf.parse(msgDeleverTime).getTime
              if(replyTime-msgTime<5*3600*1000l || msgTime-replyTime < 5*3600*1000l ) {
                jsonObj.put("replyContent",replyContent)
                jsonObj
              } else {
                null
              }
            }
          }
        } catch {
          case ex => {
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
    resultRDD.persist(StorageLevel.MEMORY_ONLY)
    resultRDD.saveAsTextFile("hdfs://cr/data/message/msg-reply/"+sf.format(startTime)+"/")

    val strRDD =resultRDD.map(json =>{

      val msgSort= json.getString("msgSort")
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

        val mobile = json.getString("mobile")

        //          (mobile,content+"@"+msgSort+"@"+replyContent+"@"+rResult)
        (mobile+"@"+msgSort,rResult )

    }).groupByKey().map(tuple =>{
      val mobileSort = tuple._1
      val iterator = tuple._2.iterator
      var minScore:Double = -10000
      for(s <- iterator) {
        val currentScore:Double = s
        if(currentScore >= minScore) {
          minScore = currentScore

        }
        //str = str+"***"+s
      }
      mobileSort+"@"+minScore
    }).map(line =>{
      val ss = line.split("@")
      val json = new JSONObject()
      json.put(ss.apply(1),ss.apply(2))

      ss.apply(0)+"@"+json.toJSONString
    })

    strRDD.mapPartitions(replyFunc).count()
  }

  def replyFunc(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()
    while (iter.hasNext) {
      val e = iter.next;

      list.add(e)
      index = index+1
      if(index % 5000 == 0) {

        HbaseUtils.saveOrUpdateBatchReplySocreHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      HbaseUtils.saveOrUpdateBatchReplySocreHbase(list )
      list.clear();
    }
    iter
  }

}
