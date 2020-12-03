package com.chuangrui.reply


import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.utils.{JSONUtils, MessageDataHandle}
import org.apache.commons.lang.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Row, SQLContext}


object FindReplyMessage {
  def main(args: Array[String]): Unit = {

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("FindReplyMessage")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime
    while (startTime <= endTime) {

      //拿最近7天的回复信息
      var map:util.HashMap[String,String] = new util.HashMap[String,String]()
      var replyRDD =  sc.textFile(hdfs+"/data/message/reply/"+sf.format(startTime).replace("-","/")+"/*.txt")

      for(i <- (1 until 3)){
        replyRDD =  replyRDD.union(sc.textFile(hdfs+"/data/message/reply/"+sf.format(startTime+i*24*3600*1000l).replace("-","/")+"/reply-"+sf.format(startTime+i*24*3600*1000l)+".txt"))
      }

      val replyRowRDD = replyRDD.map(line =>{
        val jsonObj:JSONObject = JSONUtils.getJson(line)

        if (jsonObj.getString("mobile") != null) {
          Row(
            jsonObj.getInteger("customerId").toString,
            jsonObj.getString("mobile"),
            jsonObj.getString("content"),
            jsonObj.getString("channelId")
          )
        } else {
          println("line:" + line)
          null
        }
      }).filter(row => {
        if(row == null) {
          false
        } else {
          true
        }
      })

      val replyRow:Array[Row] =  replyRowRDD.collect()

      for(row <- replyRow) {
        map.put(row(0).toString+row(1).toString+row(3).toString ,row(2).toString)
      }

      val replyMap =  sc.broadcast(map)

      //短信内容
      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)

      val resultRDD  = inputRDD.map(line => {
        val jsonObj:JSONObject = JSONUtils.getJson(line)
        if (jsonObj != null && (jsonObj.getString("deliverResult") == null || "DELIVRD".equals(jsonObj.getString("deliverResult")) )) {
          try {
            val map = replyMap.value
            val key = jsonObj.getInteger("customerId")+jsonObj.getString("mobile")+jsonObj.getString("channelId")
            if( StringUtils.isEmpty(map.get(key))) {
              null
            } else {
              jsonObj.put("replyContent",map.get(key))
              jsonObj.toJSONString
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

      resultRDD.coalesce(1).saveAsTextFile(hdfs+"/data/message/msg-reply/"+sf.format(startTime).replace("-","/")+"/")
      startTime = startTime+24*3600*1000l;

    }
  }

}
