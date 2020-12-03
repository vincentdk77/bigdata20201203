package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.JSONUtils
import com.chuangrui.version_3.utils.ConnectionSignMgrUtils
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object SecondSplitMessage {


//  var wordArray:Broadcast[Array[String]] =null
//  var model:Broadcast[NaiveBayesModel] = null
//  var sign:Broadcast[util.HashMap[Integer,String]] = null

//  var replyArray:Broadcast[Array[String]] = null
//  var replyModel:Broadcast[NaiveBayesModel] = null
//  var zcList:Broadcast[util.List[String]] = null
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("SecondSplitMessage").set("Spark.storage.memoryFraction", "0.6")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val yanzhengmaId = ConnectionSignMgrUtils.getSignIdByCode("yanzhengma")
    val yanzhengmaSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(2,yanzhengmaId);
    val yzmMap = sc.broadcast(yanzhengmaSignMap)

  val tongzhiId = ConnectionSignMgrUtils.getSignIdByCode("tongzhi")
  val tongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(2,tongzhiId);
  val tzMap = sc.broadcast(tongzhiSignMap)

  val yingxiaoId = ConnectionSignMgrUtils.getSignIdByCode("yingxiao")
  val yingxiaoSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(2,yingxiaoId);
  val yxMap = sc.broadcast(yingxiaoSignMap)


  //二级验证码加载  yanzhengma
  val secondYzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("yanzhengma",2)
  val secondYzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/second/yanzhengma/"+secondYzmMaxTime.replace("-","/")+"/")
  val secondYzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("yanzhengma",2)
  System.out.println("secondYzmArray secondYzmArray secondYzmArray:"+secondYzmWordArray.length)
  val secondYzmArray = sc.broadcast(secondYzmWordArray)
  val secondYzmModel = sc.broadcast(secondYzmMsgModel)

  //二级通知加载  tongzhi
  val secondTzMaxTime = ConnectionSignMgrUtils.selectMaxTime("tongzhi",2)
  val secondTzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/second/tongzhi/"+secondTzMaxTime.replace("-","/")+"/")
  val secondTzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("tongzhi",2)
  System.out.println("secondTzWordArray secondTzWordArray secondTzWordArray:"+secondTzWordArray.length)
  val secondTzArray = sc.broadcast(secondTzWordArray)
  val secondTzModel = sc.broadcast(secondTzMsgModel)

  //二级营销加载  yingxiao
  val secondYxMaxTime = ConnectionSignMgrUtils.selectMaxTime("yingxiao",2)
  val secondYxMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/second/yingxiao/"+secondYxMaxTime.replace("-","/")+"/")
  val secondYxWordArray:Array[String] = ConnectionSignMgrUtils.getWords("yingxiao",2)
  System.out.println("secondYxWordArray secondYxWordArray secondYxWordArray:"+secondYxWordArray.length)
  val secondYxArray = sc.broadcast(secondYxWordArray)
  val secondYxModel = sc.broadcast(secondYxMsgModel)

    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt

  val executeCode = args.apply(3)
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime

    while (startTime <= endTime) {
      //验证码
      if(executeCode.charAt(1) == '1') {
        val inputRDD = sc.textFile(hdfs + "/data/level/first/yanzhengma/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition)
        val yzmRDD: RDD[(String, String)] = inputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = secondYzmArray.value
          val msgModel = secondYzmModel.value
          val signMap = yzmMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          (msgSort, json.toJSONString)
        })
        yzmRDD.persist(StorageLevel.MEMORY_ONLY)
        import scala.collection.JavaConversions._
        for (key <- yanzhengmaSignMap.keySet()) {
          val signCode = yanzhengmaSignMap.get(key)
          yzmRDD.filter(tuple => {
            if (tuple._1.equals(signCode)) {
              true
            } else {
              false
            }
          }).map(tuple => {
            val msg = tuple._2
            msg
          }).coalesce(partition).saveAsTextFile("hdfs://cr/data/level/second/" + signCode + "/" + sf.format(startTime).replace("-", "/") + "/", classOf[GzipCodec])
        }
        yzmRDD.unpersist()
      }



      //通知
      if(executeCode.charAt(2) == '1') {
        val tzinputRDD = sc.textFile(hdfs + "/data/level/first/tongzhi/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition)
        val tzRDD: RDD[(String, String)] = tzinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = secondTzArray.value
          val msgModel = secondTzModel.value
          val signMap = tzMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          (msgSort, json.toJSONString)
        })
        tzRDD.persist(StorageLevel.MEMORY_ONLY)
        import scala.collection.JavaConversions._
        for (key <- tongzhiSignMap.keySet()) {

          val signCode = tongzhiSignMap.get(key)
          tzRDD.filter(tuple => {
            if (tuple._1.equals(signCode)) {
              true
            } else {
              false
            }
          }).map(tuple => {
            val msg = tuple._2
            msg
          }).coalesce(partition).saveAsTextFile("hdfs://cr/data/level/second/" + signCode + "/" + sf.format(startTime).replace("-", "/") + "/", classOf[GzipCodec])
        }
        tzRDD.unpersist()
      }

      //营销
      if(executeCode.charAt(0) == '1') {
        val yxinputRDD = sc.textFile(hdfs + "/data/level/first/yingxiao/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition)
        val yxRDD: RDD[(String, String)] = yxinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = secondYxArray.value
          val msgModel = secondYxModel.value
          val signMap = yxMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          (msgSort, json.toJSONString)
        })
        yxRDD.persist(StorageLevel.MEMORY_AND_DISK)
        import scala.collection.JavaConversions._
        for (key <- yingxiaoSignMap.keySet()) {
          val signCode = yingxiaoSignMap.get(key)
          yxRDD.filter(tuple => {
            if (tuple._1.equals(signCode)) {
              true
            } else {
              false
            }
          }).map(tuple => {
            val msg = tuple._2
            msg
          }).coalesce(partition).saveAsTextFile("hdfs://cr/data/level/second/" + signCode + "/" + sf.format(startTime).replace("-", "/") + "/", classOf[GzipCodec])
        }
        yxRDD.unpersist()
      }

      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }





}
