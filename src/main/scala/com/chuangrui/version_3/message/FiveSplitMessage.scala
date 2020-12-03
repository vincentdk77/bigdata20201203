package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.JSONUtils
import com.chuangrui.version_3.utils.ConnectionSignMgrUtils
import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object FiveSplitMessage {


//  var wordArray:Broadcast[Array[String]] =null
//  var model:Broadcast[NaiveBayesModel] = null
//  var sign:Broadcast[util.HashMap[Integer,String]] = null

//  var replyArray:Broadcast[Array[String]] = null
//  var replyModel:Broadcast[NaiveBayesModel] = null
//  var zcList:Broadcast[util.List[String]] = null
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("FiveSplitMessage")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val pingangedaitongzhiId = ConnectionSignMgrUtils.getSignIdByCode("pingangedaitongzhi")
    val pingangedaitongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(5,pingangedaitongzhiId);
    val pagdMap = sc.broadcast(pingangedaitongzhiSignMap)

  val wangdaitongzhiId = ConnectionSignMgrUtils.getSignIdByCode("wangdaitongzhi")
  val wangdaitongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(5,wangdaitongzhiId);
  val wdtzMap = sc.broadcast(wangdaitongzhiSignMap)

  val yinhangdaikuantongzhiId = ConnectionSignMgrUtils.getSignIdByCode("yinhangdaikuantongzhi")
  val yinhangdaikuantongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(5,yinhangdaikuantongzhiId);
  val yhdkMap = sc.broadcast(yinhangdaikuantongzhiSignMap)

  //五级平安个贷通知加载  		pingangedaitongzhi
  val fivePagdtzMaxTime = ConnectionSignMgrUtils.selectMaxTime("pingangedaitongzhi",5)
  val fivePagdtzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/five/pingangedaitongzhi/"+fivePagdtzMaxTime.replace("-","/")+"/")
  val fivePagdtzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("pingangedaitongzhi",5)
  System.out.println("fivePagdtzWordArray fivePagdtzWordArray fivePagdtzWordArray:"+fivePagdtzWordArray.length)
  val fivePagdtzArray = sc.broadcast(fivePagdtzWordArray)
  val fivePagdtzModel = sc.broadcast(fivePagdtzMsgModel)

  //五级网贷通知加载  		wangdaitongzhi
  val fiveWdtzMaxTime = ConnectionSignMgrUtils.selectMaxTime("wangdaitongzhi",5)
  val fiveWdtzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/five/wangdaitongzhi/"+fiveWdtzMaxTime.replace("-","/")+"/")
  val fiveWdtzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("wangdaitongzhi",5)
  System.out.println("fiveWdtzWordArray fiveWdtzWordArray fiveWdtzWordArray:"+fiveWdtzWordArray.length)
  val fiveWdtzArray = sc.broadcast(fiveWdtzWordArray)
  val fiveWdtzModel = sc.broadcast(fiveWdtzMsgModel)

  //五级银行贷款通知加载  		yinhangdaikuantongzhi
  val fiveYhdktzMaxTime = ConnectionSignMgrUtils.selectMaxTime("yinhangdaikuantongzhi",5)
  val fiveYhdktzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/five/yinhangdaikuantongzhi/"+fiveYhdktzMaxTime.replace("-","/")+"/")
  val fiveYhdktzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("yinhangdaikuantongzhi",5)
  System.out.println("fiveYhdktzWordArray fiveYhdktzWordArray fiveYhdktzWordArray:"+fiveYhdktzWordArray.length)
  val fiveYhdktzArray = sc.broadcast(fiveYhdktzWordArray)
  val fiveYhdktzModel = sc.broadcast(fiveYhdktzMsgModel)


  val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime

    while (startTime <= endTime) {
      //平安个贷通知
        val pagdtzinputRDD = sc.textFile(hdfs+"/data/level/four/pingangedaitongzhi/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val pagdRDD:RDD[(String,String)] = pagdtzinputRDD.map(line =>{
          var json:JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = fivePagdtzArray.value
          val msgModel = fivePagdtzModel.value
          val signMap = pagdMap.value
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
          (msgSort,json.toJSONString)
        })
      pagdRDD.persist(StorageLevel.MEMORY_ONLY)
      import scala.collection.JavaConversions._
      for (key <- pingangedaitongzhiSignMap.keySet()) {
        val signCode = pingangedaitongzhiSignMap.get(key)
        pagdRDD.filter(tuple => {
          if(tuple._1.equals(signCode)) {
            true
          } else {
            false
          }
        }).map(tuple =>{
           val msg = tuple._2
            msg
        }).coalesce(20).saveAsTextFile("hdfs://cr/data/level/five/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
      }



      //网贷通知
      val wdtzinputRDD = sc.textFile(hdfs+"/data/level/four/wangdaitongzhi/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
      val wdtzRDD:RDD[(String,String)] = wdtzinputRDD.map(line =>{
        var json:JSONObject = JSONUtils.getJson(line)
        val content = json.getString("content")
        val word_array = fiveWdtzArray.value
        val msgModel = fiveWdtzModel.value
        val signMap = wdtzMap.value
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
        (msgSort,json.toJSONString)
      })
      wdtzRDD.persist(StorageLevel.MEMORY_ONLY)
      import scala.collection.JavaConversions._
      for (key <- wangdaitongzhiSignMap.keySet()) {
        val signCode = wangdaitongzhiSignMap.get(key)
        wdtzRDD.filter(tuple => {
          if(tuple._1.equals(signCode)) {
            true
          } else {
            false
          }
        }).map(tuple =>{
          val msg = tuple._2
          msg
        }).coalesce(20).saveAsTextFile("hdfs://cr/data/level/five/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
      }

      //银行贷款通知
      val yhdktzinputRDD = sc.textFile(hdfs+"/data/level/four/yinhangdaikuantongzhi/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
      val yhdkRDD:RDD[(String,String)] = yhdktzinputRDD.map(line =>{
        var json:JSONObject = JSONUtils.getJson(line)
        val content = json.getString("content")
        val word_array = fiveYhdktzArray.value
        val msgModel = fiveYhdktzModel.value
        val signMap = yhdkMap.value
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
        (msgSort,json.toJSONString)
      })
      yhdkRDD.persist(StorageLevel.MEMORY_ONLY)
      import scala.collection.JavaConversions._
      for (key <- yinhangdaikuantongzhiSignMap.keySet()) {
        val signCode = yinhangdaikuantongzhiSignMap.get(key)
        yhdkRDD.filter(tuple => {
          if(tuple._1.equals(signCode)) {
            true
          } else {
            false
          }
        }).map(tuple =>{
          val msg = tuple._2
          msg
        }).coalesce(20).saveAsTextFile("hdfs://cr/data/level/five/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
      }

      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }





}
