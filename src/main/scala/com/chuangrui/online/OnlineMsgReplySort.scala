package com.chuangrui.online

import java.util
import java.util.Date

import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, Constant, HbaseUtils}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

object OnlineMsgReplySort {

  Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

  def main(args: Array[String]): Unit = {

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("OnlineMsgReplySort")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc,Seconds(5))

    conf.set("","")

    val timeStr = ConnectionUtils.selectMaxTime()
    val replyTimeStr = ConnectionUtils.selectReplyMaxTime();
    System.out.println(timeStr+"  "+replyTimeStr)
    val replyModel = NaiveBayesModel.load(sc, hdfs+"/replyModel/"+replyTimeStr.replace("-","/")+"/")
    val replyWordArray:Array[String] = ConnectionUtils.getReplyWords()
    val zangci:util.List[String] = ConnectionUtils.getZangCi()
    val zcList = sc.broadcast(zangci)

    val sameModel = NaiveBayesModel.load(sc, hdfs+"/model/"+timeStr.replace("-","/")+"/")
    val array:Array[String] = ConnectionUtils.getWords()
    val signMap:util.HashMap[Integer,String] =ConnectionUtils.getSign2();
    System.out.println("array array array:"+array.length)
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val rmodel = sc.broadcast(replyModel)
    val rwArray = sc.broadcast(replyWordArray)
    val sign = sc.broadcast(signMap)

    val topicsSet="dd".split(",").toSet
    val kafkaParams= mutable.HashMap[String,String]()
    //必须添加以下参数，否则会报错
    kafkaParams.put("bootstrap.servers" ,"210.5.152.207:9092")
    kafkaParams.put("group.id", "group2")
    kafkaParams.put("auto.offset.reset", "latest")
    kafkaParams.put("enable.auto.commit", "true")
    kafkaParams.put("auto.commit.interval.ms", "1000")
    kafkaParams.put("session.timeout.ms", "30000")
    kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams.put("value.deserializer" , "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaParams.put("zookeeper.connect","node4:2181,node5:2181,node6:2181")
    kafkaParams.put("zookeeper.connection.timeout.ms","100000")
    kafkaParams.put("metadata.broker.list", "node4:9092,node5:9092,node6:9092") ;


    val messages=KafkaUtils.createDirectStream [String,String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String,String](topicsSet,kafkaParams)
    )


    val lines = messages.map(_.value)

    lines.foreachRDD(rdd => {

      val date = new Date()
      rdd.persist(StorageLevel.MEMORY_ONLY)
      val handleRdd = rdd.filter(line=>{
        val ss = line.split("@@@")
        if(Constant.MSG_TYPE.equals(ss.apply(2))) {
          true
        } else {
          false
        }
      }).map(line =>{

        try {
          val ss =line.split("@@@")
          val uuid = ss.apply(0)
          val msg = ss.apply(1)

          val word_array = wordArray.value
          val msgModel = model.value
          val wordList = PaoDingCut.cutString(msg)
          val signMap = sign.value
          var crrentArray:Array[Double] = new Array[Double](word_array.length)
          for(i<-0 to crrentArray.length-1) {
            crrentArray(i) =0
          }
          for(i <- 0 to wordList.size()-1) {
            for(j<-0 to word_array.length-1) {
              if(word_array(j).equals( wordList.get(i))) {
                crrentArray(j) = crrentArray(j)+1
              }
            }
          }
          val result = msgModel.predict(Vectors.dense(crrentArray))
          val msgSort = signMap.get(result.toInt)
//           (uuid,(msgSort,msg))
//          ConnectionUtils.saveMessageSortResult(msg,uuid,msgSort)
          uuid+"@@@"+msgSort+"@@@"+msg
        } catch {
          case ex => {
            println("line line line line line:" + line)
            null
          }
        }

      }).mapPartitions(msgFunc)

      System.out.println("msg count:"+handleRdd.count())

      //处理回复
      val replyRdd = rdd.filter(line=>{
        val ss = line.split("@@@")
        if(Constant.REPLY_TYPE.equals(ss.apply(2))) {
          true
        } else {
          false
        }
      }).map(line =>{

        val ss =line.split("@@@")
        val uuid = ss.apply(0)
        val reply = ss.apply(1)

        val word_array = rwArray.value
        val replyModel = rmodel.value
        val wordList = PaoDingCut.cutString(reply)
        val zc = zcList.value
        for(i<- 0 to zc.size()-1) {
          if(line.toLowerCase().contains(zc.get(i))) {
            wordList.add(zc.get(i))
          }
        }
        var crrentArray:Array[Double] = new Array[Double](word_array.length)
        for(i<-0 to crrentArray.length-1) {
          crrentArray(i) =0
        }
        for(i <- 0 to wordList.size()-1) {
          for(j<-0 to word_array.length-1) {
            if(word_array(j).equals( wordList.get(i))) {
              crrentArray(j) = crrentArray(j)+1
            }
          }
        }
        val result = replyModel.predict(Vectors.dense(crrentArray))

        ConnectionUtils.saveReplySortResult(reply,uuid,result.toString)
        line
      })

      System.out.println("reply count:"+replyRdd.count())
    })

    ssc.start()
    ssc.awaitTermination()

  }


  def msgFunc(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()

    while (iter.hasNext) {
      val e = iter.next;
      if(e != null) {
        list.add(e)
      }
//      list.add(e)
      index = index+1
      if(index % 500 == 0) {

        ConnectionUtils.saveMessageSortResultBatch(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      ConnectionUtils.saveMessageSortResultBatch(list)
      list.clear();
    }
    iter
  }
}
