package com.chuangrui.channelblack

import java.text.SimpleDateFormat
import java.util

import com.chuangrui.utils.{ConnectionUtils, HbaseUtils, JSONUtils}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

object SaveChannelBlackToMysql {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SaveChannelBlackToMysql")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val hdfs = "hdfs://cr"

    //黑名单级别数据
    val blackRDD = sc.textFile(hdfs+"/data/message/blacklevel2.txt")
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
    val channelCompanyRDD = sc.textFile(hdfs+"/data/message/channel2.txt")
    val channelRowArray = channelCompanyRDD.map(line =>{
      val ss = line.split(",")
      Row(ss.apply(0),ss.apply(1))
    }).collect()
    var channelMap:util.HashMap[String,String] = new util.HashMap[String,String]()
    for(row <- channelRowArray) {
      channelMap.put(row(0).toString ,row(1).toString)
    }
    System.out.println("channelMap channelMap channelMap:"+channelMap.size());
    val ccMap = sc.broadcast(channelMap)


    //读取发送短信数据，处理通道黑名单
    val startStr = args.apply(0).toString
    val endStr = args.apply(1).toString
    val partion = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var time = sf.parse(startStr).getTime
    val endTime = sf.parse(endStr).getTime
    while(time<=endTime) {
    System.out.println("time time time:"+sf.format(time));
      val timePath = sf.format(time).replace("-","/")
      //val fileName = "msg-"+time+".txt.gz"
      val path = hdfs+"/data/message/msg/"+timePath+"/*.gz"
      System.out.println("path path path path:"+path)
      val msgRDD = sc.textFile(path).repartition(partion)
      val tupleRDD = msgRDD.map(line=> {
        val json = JSONUtils.getJson(line)
        val deliverResult = json.getString("deliverResultReal")
        val mobile = json.getString("mobile")
        val channelId = json.getString("channelId")
        val ctime = json.getString("ctime")
        val map = blackMap.value
        val channelMap = ccMap.value
        if(map.get(deliverResult) != null && !StringUtils.isEmpty(channelMap.get(channelId))) {
          val level = map.get(deliverResult)
          (mobile,level+"@"+channelMap.get(channelId)+"@"+ctime)
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
        var ctime =  ""
        for( l <-iterator) {
          if(!companyName.contains(l.toString.split("@").apply(1))) {
            companyName = companyName + l.toString.split("@").apply(1)+","
          }

          if(Integer.parseInt( l.toString.split("@").apply(0))>level) {
            level = Integer.parseInt( l.toString.split("@").apply(0))
          }
          ctime = l.toString.split("@").apply(2)

        }
        if(!StringUtils.isEmpty(companyName)) {
          companyName = companyName.substring(0,companyName.length-1)
        }
        //手机号+初始级别+计算公司之后的级别+通道id
        tuple._1+"@"+level+"@"+companyName+"@"+ctime
      })
      lineRDD.persist(StorageLevel.MEMORY_ONLY)
      lineRDD.count()
      //originRDD 存放的是 手机号@原始级别@最大级别@通道id#通道id#通道id...#通道id@公司名称#公司名称...#公司名称
      val originRDD = lineRDD.coalesce(1).mapPartitions(funcBlack)
      System.out.println(originRDD.count());
      time=time+24*3600*1000l

    }

  }

  def funcBlack(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()
    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 2000 == 0) {
        ConnectionUtils.saveChannelBlack(list)
        list.clear();
      }
    }
    if(list.size()>0) {
      ConnectionUtils.saveChannelBlack(list )
      list.clear();
    }

    iter
  }


}
