package com.chuangrui.channelblack

import java.text.SimpleDateFormat
import java.util

import com.chuangrui.utils.{HbaseUtils, JSONUtils}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object GetChannelBlackOnline {

  def main(args: Array[String]): Unit = {

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("GetChannelBlackOnline")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)


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

    //读取发送短信数据，处理通道黑名单
    val time = args.apply(0).toString
    val timePath = time.replace("-","/")
    val fileName = "msg-"+time+".txt.gz"
    val path = hdfs+"/data/message/msg/"+timePath+"/"+fileName
    System.out.println("path path path path:"+path)
    val msgRDD = sc.textFile(path)
    val tupleRDD = msgRDD.map(line=> {
      val json = JSONUtils.getJson(line)
      val deliverResult = json.getString("deliverResult")
      val mobile = json.getString("mobile")
      val channelId = json.getString("channelId")
      val channelCompanyMap = ccMap.value
      val map = blackMap.value
      if(map.get(deliverResult) != null) {
        val level = map.get(deliverResult)
        var companyName = channelCompanyMap.get(channelId)
        if(StringUtils.isEmpty(companyName)) {
          companyName = " "
        }
        (mobile,level+"@"+companyName)
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
      var companyNames = ""
      for( l <-iterator) {
        companyNames = companyNames + l.toString.split("@").apply(1).toString+"#"
        if(Integer.parseInt( l.toString.split("@").apply(0))>level) {
          level = Integer.parseInt( l.toString.split("@").apply(0))
        }
      }

      //手机号+初始级别+计算公司之后的级别+通道id
      tuple._1+"@"+level+"@"+companyNames
    })

    def funct(iter: Iterator[String]) :  Iterator[String] = {
      var index:Int = 0
      var list:util.ArrayList[String] = new util.ArrayList()
      while (iter.hasNext) {
        val e = iter.next;
        list.add(e)
        index = index+1
        if(index % 10000 == 0) {

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
    val finalRDD = lineRDD.mapPartitions(funct)
    System.out.println(finalRDD.count())

  }


}
