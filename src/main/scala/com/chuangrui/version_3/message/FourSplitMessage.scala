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


object FourSplitMessage {


//  var wordArray:Broadcast[Array[String]] =null
//  var model:Broadcast[NaiveBayesModel] = null
//  var sign:Broadcast[util.HashMap[Integer,String]] = null

//  var replyArray:Broadcast[Array[String]] = null
//  var replyModel:Broadcast[NaiveBayesModel] = null
//  var zcList:Broadcast[util.List[String]] = null
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("FourSplitMessage")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val daikuanId = ConnectionSignMgrUtils.getSignIdByCode("daikuan")
    val daikuanSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(4,daikuanId);
    val dkMap = sc.broadcast(daikuanSignMap)

  val daikuantongzhiId = ConnectionSignMgrUtils.getSignIdByCode("daikuantongzhi")
  val daikuantongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(4,daikuantongzhiId);
  val dktzMap = sc.broadcast(daikuantongzhiSignMap)

  val daikuanyanzhengmaId = ConnectionSignMgrUtils.getSignIdByCode("daikuanyanzhengma")
  val daikuanyanzhengmaSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(4,daikuanyanzhengmaId);
  val dkyzmMap = sc.broadcast(daikuanyanzhengmaSignMap)

  val maichangId = ConnectionSignMgrUtils.getSignIdByCode("maichang")
  val maichangSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(4,maichangId);
  val mcMap = sc.broadcast(maichangSignMap)

  //四级贷款加载  		daikuan
  val fourDkMaxTime = ConnectionSignMgrUtils.selectMaxTime("daikuan",4)
  val fourDkMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/daikuan/"+fourDkMaxTime.replace("-","/")+"/")
  val fourDkWordArray:Array[String] = ConnectionSignMgrUtils.getWords("daikuan",4)
  System.out.println("fourDkWordArray fourDkWordArray fourDkWordArray:"+fourDkWordArray.length)
  val fourDkArray = sc.broadcast(fourDkWordArray)
  val fourDkModel = sc.broadcast(fourDkMsgModel)

  //四级贷款通知加载  		daikuantongzhi
  val fourDktzMaxTime = ConnectionSignMgrUtils.selectMaxTime("daikuantongzhi",4)
  val fourDktzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/daikuantongzhi/"+fourDktzMaxTime.replace("-","/")+"/")
  val fourDktzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("daikuantongzhi",4)
  System.out.println("fourDktzWordArray fourDktzWordArray fourDktzWordArray:"+fourDktzWordArray.length)
  val fourDktzArray = sc.broadcast(fourDktzWordArray)
  val fourDktzModel = sc.broadcast(fourDktzMsgModel)

  //四级贷款验证码加载  		daikuanyanzhengma
  val fourDkyzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("daikuanyanzhengma",4)
  val fourDkyzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/daikuanyanzhengma/"+fourDkyzmMaxTime.replace("-","/")+"/")
  val fourDkyzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("daikuanyanzhengma",4)
  System.out.println("fourDkyzmWordArray fourDkyzmWordArray fourDkyzmWordArray:"+fourDkyzmWordArray.length)
  val fourDkyzmArray = sc.broadcast(fourDkyzmWordArray)
  val fourDkyzmModel = sc.broadcast(fourDkyzmMsgModel)

  //四级卖场加载  		maichang
  val fourMcMaxTime = ConnectionSignMgrUtils.selectMaxTime("maichang",4)
  val fourMcMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/maichang/"+fourMcMaxTime.replace("-","/")+"/")
  val fourMcWordArray:Array[String] = ConnectionSignMgrUtils.getWords("maichang",4)
  System.out.println("fourMcWordArray fourMcWordArray fourMcWordArray:"+fourMcWordArray.length)
  val fourMcArray = sc.broadcast(fourMcWordArray)
  val fourMcModel = sc.broadcast(fourMcMsgModel)


  val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
  val executeCode = args.apply(3)
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime

    while (startTime <= endTime) {
      //贷款
      if(executeCode.charAt(0) == '1') {
        val dkinputRDD = sc.textFile(hdfs+"/data/level/third/daikuan/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val dkRDD:RDD[(String,String)] = dkinputRDD.map(line =>{
          var json:JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = fourDkArray.value
          val msgModel = fourDkModel.value
          val signMap = dkMap.value
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
        dkRDD.persist(StorageLevel.MEMORY_ONLY)
        import scala.collection.JavaConversions._
        for (key <- daikuanSignMap.keySet()) {
          val signCode = daikuanSignMap.get(key)
          dkRDD.filter(tuple => {
            if(tuple._1.equals(signCode)) {
              true
            } else {
              false
            }
          }).map(tuple =>{
            val msg = tuple._2
            msg
          }).coalesce(20).saveAsTextFile("hdfs://cr/data/level/four/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
        }

        //卖场
        val mcinputRDD = sc.textFile(hdfs+"/data/level/third/maichang/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val mcRDD:RDD[(String,String)] = mcinputRDD.map(line =>{
          var json:JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = fourMcArray.value
          val msgModel = fourMcModel.value
          val signMap = mcMap.value
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
        mcRDD.persist(StorageLevel.MEMORY_ONLY)
        import scala.collection.JavaConversions._
        for (key <- maichangSignMap.keySet()) {
          val signCode = maichangSignMap.get(key)
          mcRDD.filter(tuple => {
            if(tuple._1.equals(signCode)) {
              true
            } else {
              false
            }
          }).map(tuple =>{
            val msg = tuple._2
            msg
          }).coalesce(20).saveAsTextFile("hdfs://cr/data/level/four/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
        }
      }




      //贷款通知
      if(executeCode.charAt(1) == '1') {
        val dktzinputRDD = sc.textFile(hdfs+"/data/level/third/daikuantongzhi/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val dktzRDD:RDD[(String,String)] = dktzinputRDD.map(line =>{
          var json:JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = fourDktzArray.value
          val msgModel = fourDktzModel.value
          val signMap = dktzMap.value
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
        dktzRDD.persist(StorageLevel.MEMORY_ONLY)
        import scala.collection.JavaConversions._
        for (key <- daikuantongzhiSignMap.keySet()) {
          val signCode = daikuantongzhiSignMap.get(key)
          dktzRDD.filter(tuple => {
            if(tuple._1.equals(signCode)) {
              true
            } else {
              false
            }
          }).map(tuple =>{
            val msg = tuple._2
            msg
          }).coalesce(20).saveAsTextFile("hdfs://cr/data/level/four/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
        }
      }


      //贷款验证码
      if(executeCode.charAt(2) == '1') {
        val dkyzminputRDD = sc.textFile(hdfs+"/data/level/third/daikuanyanzhengma/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val dkyzmRDD:RDD[(String,String)] = dkyzminputRDD.map(line =>{
          var json:JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val word_array = fourDkyzmArray.value
          val msgModel = fourDkyzmModel.value
          val signMap = dkyzmMap.value
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
        dkyzmRDD.persist(StorageLevel.MEMORY_ONLY)
        import scala.collection.JavaConversions._
        for (key <- daikuanyanzhengmaSignMap.keySet()) {
          val signCode = daikuanyanzhengmaSignMap.get(key)
          dkyzmRDD.filter(tuple => {
            if(tuple._1.equals(signCode)) {
              true
            } else {
              false
            }
          }).map(tuple =>{
            val msg = tuple._2
            msg
          }).coalesce(20).saveAsTextFile("hdfs://cr/data/level/four/"+signCode+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])
        }
      }




      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }





}
