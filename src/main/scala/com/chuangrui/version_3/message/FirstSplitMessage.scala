package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util
import java.util.Random

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.chuangrui.node.GetMysqlByNode1
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, HbaseUtils, JSONUtils}
import com.chuangrui.version_3.utils.ConnectionSignMgrUtils
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object FirstSplitMessage {


//  var wordArray:Broadcast[Array[String]] =null
//  var model:Broadcast[NaiveBayesModel] = null
//  var sign:Broadcast[util.HashMap[Integer,String]] = null

//  var replyArray:Broadcast[Array[String]] = null
//  var replyModel:Broadcast[NaiveBayesModel] = null
//  var zcList:Broadcast[util.List[String]] = null
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("FirstSplitMessage")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //短信模型加载
    val msgMaxTime = ConnectionSignMgrUtils.selectMaxTime(null,1)
    val sameModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/first/"+msgMaxTime.replace("-","/")+"/")
    val array:Array[String] = ConnectionSignMgrUtils.getWords(null,1)
    val signMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(1,0);
    System.out.println("array array array:"+array.length)
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)

  //非法短信加载
  val feifaRDD = sc.textFile(hdfs+"/data/dataset/message/feifa/feifa.txt")
  val feifaArray:Array[String] =feifaRDD.collect()
  val feifaMap:util.HashMap[String,Integer] = new util.HashMap[String,Integer]()
  for(s<- feifaArray) {
      feifaMap.put(s,1)
  }
  val ffMap = sc.broadcast(feifaMap)

    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val executeCode = args.apply(3)

    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime

    while (startTime <= endTime) {
      //短信内容
        val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val jsonRDD:RDD[(String,String)] = inputRDD.map(line =>{
          try {
            var json:JSONObject = JSONUtils.getJson(line)
            val content = json.getString("content")
            var signCode = json.getString("sign")
            val feifaMap = ffMap.value
            if (!StringUtils.isEmpty(content) && feifaMap.get(signCode+content) == null) {
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
//              json.put("content",GetMysqlByNode1.replace(content))
              (msgSort,json.toJSONString)
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
      jsonRDD.persist(StorageLevel.MEMORY_AND_DISK)
      import scala.collection.JavaConversions._



      if(executeCode.charAt(0) == '1') {
        jsonRDD.filter(tuple => {
          if(tuple._1.equals("yanzhengma")) {
            true
          } else {
            false
          }
        }).map(tuple =>{
          val msg = tuple._2
          msg
        }).coalesce(200).saveAsTextFile("hdfs://cr/data/level/first/yanzhengma/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
      }
      if(executeCode.charAt(1) == '1') {
        jsonRDD.filter(tuple => {
          if(tuple._1.equals("tongzhi")) {
            true
          } else {
            false
          }
        }).map(tuple =>{
          val msg = tuple._2
          msg
        }).coalesce(200).saveAsTextFile("hdfs://cr/data/level/first/tongzhi/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
      }
      if(executeCode.charAt(2) == '1') {
        jsonRDD.filter(tuple => {
          if(tuple._1.equals("yingxiao")) {
            true
          } else {
            false
          }
        }).map(tuple =>{
          val msg = tuple._2
          msg
        }).coalesce(200).saveAsTextFile("hdfs://cr/data/level/first/yingxiao/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
      }
//      for (key <- signMap.keySet()) {
//        val signCode = signMap.get(key)
//        jsonRDD.filter(tuple => {
//          if(tuple._1.equals(signCode)) {
//            true
//          } else {
//            false
//          }
//        }).map(tuple =>{
//           val msg = tuple._2
//            msg
//        }).coalesce(200).saveAsTextFile("hdfs://cr/data/level/first/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
//      }


      jsonRDD.unpersist()
      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }

}
