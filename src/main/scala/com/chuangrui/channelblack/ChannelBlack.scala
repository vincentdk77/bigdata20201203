package com.chuangrui.channelblack

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.chuangrui.utils.{JSONUtils, MessageDataHandle}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object ChannelBlack {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ChannelBlack")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val blackRDD = sc.textFile("hdfs://node1:9000/data/message/blacklevel.txt")
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

   val date = new Date();
    val format = new SimpleDateFormat("yyyy-MM-dd")
    val timePath = format.format(date).replace("-","/")
    val fileName = "msg-"+format.format(date)+".zip"
    val path = "hdfs://node1:9000/data/message/"+timePath+"/"+fileName
    System.out.println("path path path path:"+path)
    val msgRDD = sc.textFile(path)
    val tupleRDD = msgRDD.map(line=> {
      val json = JSONUtils.getJson(line)
      val deliverResult = json.getString("deliverResult")
      val mobile = json.getString("mobile")
      val map = blackMap.value
      if(map.get(deliverResult) != null) {
        val level = map.get(deliverResult)
        (mobile,level)
      } else {
        (mobile,0)
      }
    })
    val filterRDD = tupleRDD.filter(tuple => {
     if(Integer.parseInt(tuple._2.toString) > 0) {
       true
     } else {
       false
     }
    })

    val lineRDD = filterRDD.groupByKey().map(tuple =>{
      val iterator = tuple._2.iterator
      var level = 0
      for( l <-iterator) {
        if(Integer.parseInt(l.toString)>level) {
          level = Integer.parseInt(l.toString)
        }
      }
      tuple._1+","+level
    })

    lineRDD.saveAsTextFile("hdfs://node1:9000/data/channel-black/")


  }


}
