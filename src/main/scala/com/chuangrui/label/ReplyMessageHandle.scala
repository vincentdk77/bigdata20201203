package com.chuangrui.label

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.utils.{JSONUtils, MessageDataHandle}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.storage.StorageLevel

object ReplyMessageHandle {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ReplyMessageHandle")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //回复内容
    val replyRDD =  sc.textFile("hdfs://node1:9000/data/message/reply-2019-04-06.txt")
    replyRDD.take(30).foreach(println)
    val replyRowRDD = replyRDD.map(line =>{
      val jsonObj:JSONObject = JSONUtils.getJson(line)
      if (jsonObj != null) {
        try {
          val timeDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          val time:Timestamp = if (null == jsonObj.getString("ctime")) null else new Timestamp(timeDF.parse(jsonObj.getString("ctime")).getTime )
          val row = Row(
            jsonObj.getInteger("customerId"),
            jsonObj.getString("mobile"),
            jsonObj.getString("replyContent"),
            time,
            jsonObj.getString("channelId")
          )
          row
        } catch {
          case ex => {
            println("line:" + ex)
            val row = null
            row
          }
        }
      } else {
        val row = null
        row
      }
    })
    val replyRow =  replyRowRDD.collect()
    var map:util.HashMap[String,Row] = new util.HashMap[String,Row]()

    var i=0
    for(row <- replyRow) {
      i = i+1
      if(i<100) {
        System.out.println(row(0).toString+row(1).toString+row(4).toString);
      }
       map.put(row(0).toString+row(1).toString+row(4).toString ,row)
    }
    System.out.println("map map map:"+map.size());

    val replyMap =  sc.broadcast(map)

    //短信内容
    val inputRDD = sc.textFile("hdfs://node1:9000/data/message/2019-04-06.txt")

    val rowRDD  = inputRDD.map(line => {
      val jsonObj:JSONObject = JSONUtils.getJson(line)
      if (jsonObj != null) {
        try {
          val timeDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
          val time:Timestamp = if (null == jsonObj.getString("ctime")) null else new Timestamp( java.lang.Long.parseLong( jsonObj.getString("ctime")))
          val row = Row(
            jsonObj.getInteger("customerId"),
            jsonObj.getString("mobile"),
            jsonObj.getString("content"),
            time,
            jsonObj.getString("channelId")
          )
          row
        } catch {
          case ex => {
            println("line:" + ex)
            val row = null
            row
          }
        }
      } else {
        val row = null
        row
      }
    }).filter(row => {
      if(row != null && row(2) != null) {
        true
      } else {
        false
      }
    })

    rowRDD.persist(StorageLevel.MEMORY_ONLY)
    val num = rowRDD.count()
    System.out.println("num num num num:"+num)
    rowRDD.take(30).foreach(row =>{
      System.out.println(row(0).toString+row(1).toString+row(4).toString)
    })

    val msgReplyRDD = rowRDD.map(row =>{
      val map = replyMap.value
      System.out.println(row(0).toString+row(1).toString+row(4).toString+" result:"+map.get(row(0).toString+row(1).toString+row(4).toString))
      val replyRow = map.get(row(0).toString+row(1).toString+row(4).toString)
      if(replyRow != null && replyRow(2) != null) {
        Row(row(0),row(1),row(2),row(3),row(4),replyRow(2))
      } else {
        Row(row(0),row(1),row(2),row(3),row(4),null)
      }
    }).filter(row => {
      if(row(5) == null) {
        false
      } else {
        true
      }
    })

    msgReplyRDD.persist(StorageLevel.MEMORY_ONLY)

   // msgReplyRDD.take(30).foreach(println)

    msgReplyRDD.saveAsTextFile("hdfs://node1:9000/data/result/")
//    rowRDD.persist(StorageLevel.MEMORY_ONLY_SER)
//    val df = sqlContext.createDataFrame(rowRDD, MessageSchema.row)
//    df.registerTempTable("message")
//    val distinctDF  = sqlContext.sql("select distinct * from message")
//    distinctDF.map()


  }
}
