package com.chuangrui.deadNumber

import java.text.SimpleDateFormat
import java.util
import java.util.{Random, UUID}

import com.chuangrui.utils.{DeadNumberUtils, HbaseDeadNumberUtils, HbaseUtils, JSONUtils}
import org.apache.commons.lang.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object SaveDeadNumberHbase {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SaveDeadNumberHbase")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val hdfs = "hdfs://cr"

    val deadMap = DeadNumberUtils.getDeadCode();
    System.out.println("deadMap deadMap deadMap:"+deadMap.size());
    val dMap = sc.broadcast(deadMap)


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

      val msgRDD = sc.textFile(path).repartition(partion)
      val tupleRDD:RDD[(String,(String,Integer))] = msgRDD.map(line=> {
        val json = JSONUtils.getJson(line)
        val deliverResultReal = json.getString("deliverResultReal")

        val mobile = json.getString("mobile")
        val ctime:String = json.getString("ctime")
        val deadMap = dMap.value
        val sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var real:String = ""
        real = deliverResultReal
        if(deadMap.get(real) != null) {
          (mobile,(ctime,0))
        } else if("DELIVRD".equals(real)){
          (mobile,(ctime,1))
        }else {
          null
        }
      })
      val filterRDD:RDD[(String,(String,Integer))] = tupleRDD.filter(tuple => {
        if(tuple != null) {
          true
        } else {
          false
        }
      })

      val lineRDD = filterRDD.groupByKey().map(tuple =>{
        val iterator = tuple._2
        var status = 1
        var ctime = "2000-01-01 00:00:00"
        for( l <-iterator) {
          if(l._1.compareTo(ctime)>0) {
            status = l._2
            ctime = l._1
          }
        }
        val r = new Random()
        val index = r.nextInt(partion)+1
        (index,tuple._1+"@"+ctime+"@"+status)

      }).groupByKey().map(tuple=>{

        var index:Int = 0
        var list:util.ArrayList[String] = new util.ArrayList()
        for( l <-tuple._2) {
          list.add(l)
          index = index+1
          if(index % 10000 == 0) {
            HbaseDeadNumberUtils.saveOrUpdateBatchDeadNumberHbase(list)
            list.clear();
          }
        }
        if(list.size()>0) {
          HbaseDeadNumberUtils.saveOrUpdateBatchDeadNumberHbase(list)
          list.clear();
        }
        tuple._1
      })
//      lineRDD.saveAsTextFile(hdfs+"/data/testDead")

      //originRDD 存放的是 手机号@原始级别@最大级别@通道id#通道id#通道id...#通道id@公司名称#公司名称...#公司名称

      System.out.println(lineRDD.count());
      time=time+24*3600*1000l

    }

  }

  def deadNumber(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()
    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 10000 == 0) {
        HbaseDeadNumberUtils.saveOrUpdateBatchDeadNumberHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {
      HbaseDeadNumberUtils.saveOrUpdateBatchDeadNumberHbase(list)
      list.clear();
    }
    iter
  }


}
