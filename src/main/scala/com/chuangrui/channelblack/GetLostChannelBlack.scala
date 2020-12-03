package com.chuangrui.channelblack

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.chuangrui.utils.{JSONUtils, MessageDataHandle}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object GetLostChannelBlack {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GetLostChannelBlack")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    //黑名单级别数据
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

    //通道所属公司数据
    val channelCompanyRDD = sc.textFile("hdfs://node1:9000/data/message/channel.txt")
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
    val path = "hdfs://node1:9000/data/message/msg/"+timePath+"/"+fileName
    System.out.println("path path path path:"+path)
    val msgRDD = sc.textFile(path)
    val tupleRDD = msgRDD.map(line=> {
      val json = JSONUtils.getJson(line)
      val deliverResult = json.getString("deliverResult")
      val mobile = json.getString("mobile")
      val channelLable = json.getString("channelId")
      val map = blackMap.value
      if(map.get(deliverResult) != null) {
        val level = map.get(deliverResult)
        (mobile,level+"@"+channelLable)
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
      var channelId = ""
      for( l <-iterator) {
        channelId = channelId + l.toString.split("@").apply(1).toString+"#"
        if(Integer.parseInt( l.toString.split("@").apply(0))>level) {
          level = Integer.parseInt( l.toString.split("@").apply(0))
        }
      }
      //手机号+初始级别+计算公司之后的级别+通道id
      tuple._1+"@"+level+"@"+level+"@"+channelId
    })

    //originRDD 存放的是 手机号@原始级别@最大级别@通道id#通道id#通道id...#通道id@公司名称#公司名称...#公司名称
    val originRDD = lineRDD.map(line =>{
      var returnLine:String = line
      var companyName:String = ""
      val channelIds = line.split("@").apply(3).split("#")
      val channelMap = ccMap.value
      for(cId <- channelIds) {
        val name  = channelMap.get(cId)
        //有的通道没有所属公司
        if(name != null) {
          if(!companyName.contains(name)) {
            companyName =companyName + name+"#"
          }
        }
      }

      (returnLine,companyName)

    }).filter(tuple=>{
      if(StringUtils.isEmpty(tuple._2)) {
        false
      } else {
        true
      }
    }).map(tuple =>{
      tuple._1+"@"+tuple._2
    })


    originRDD.saveAsTextFile("hdfs://node1:9000/data/channel-black/"+timePath)

    //第一次跑需要将下面的代码注释掉，开放上面originRDD.saveAsTextFile("hdfs://node1:9000/data/channel-black/"+timePath)的代码
    //第二次跑，需要注释掉originRDD.saveAsTextFile("hdfs://node1:9000/data/channel-black/"+timePath)的代码，放开下面的代码

//    val format = new SimpleDateFormat("yyyy-MM-dd")
//    val date:util.Date = format.parse(time)
//    val yestoday = date.getTime-24*3600*1000l;
//    val yestodayStr = format.format(yestoday)
//    val yestodayPath = yestodayStr.replace("-","/")
//    val historyRDD = sc.textFile("hdfs://node1:9000/data/channel-black/"+yestodayPath+"/")
//
//    val currentTupleRDD = historyRDD.union(originRDD).map(line =>{
//      val mobile = line.split("@").apply(0)
//      (mobile,line)
//    })
//
//    val resultRDD = currentTupleRDD.groupByKey().map(tuple =>{
//      val iterator = tuple._2.iterator
//      var maxOriginLevel:Integer = 0
//      var maxLevel:Integer = 0
//      var companyNames:String = ""
//      var channelIds:String = ""
//      var line = "            "
//      for( l <-iterator) {
//          line = line+l.toString+"               "
//         val array = l.toString.split("@")
//         if(Integer.parseInt(array.apply(1)) > maxOriginLevel) {
//           maxOriginLevel = Integer.parseInt(array.apply(1))
//         }
//        //channelIds
//        val cIds = array.apply(3).toString.split("#")
//        for(c <- cIds) {
//          if(!channelIds.contains(c)) {
//            channelIds = channelIds+c+"#"
//          }
//        }
//        //公司名称
//        val companys = array.apply(4).toString.split("#")
//        for(company <- companys) {
//          if(!companyNames.contains(company)) {
//            companyNames = companyNames + company+"#"
//          }
//        }
//      }
//      maxLevel = maxOriginLevel+ companyNames.split("#").length-1
//      //手机号+初始级别+计算公司之后的级别+通道id
//      tuple._1+"@"+maxOriginLevel+"@"+maxLevel+"@"+channelIds+"@"+companyNames+ line
//
//    })



//    resultRDD.saveAsTextFile("hdfs://node1:9000/data/channel-black/"+timePath)


  }


}
