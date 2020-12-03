package com.chuangrui.test

import java.text.SimpleDateFormat

import com.chuangrui.utils.{JSONUtils, MessageDataHandle}
import org.apache.commons.lang.StringUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

object TestMobile {
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("TestMobile")
    conf.set("spark.default.parallelism","40")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val startTimeStr = args.apply(0)
    val endTimeStr = args.apply(1)
    val partition = args.apply(2).toInt
    //key@value#key@value
   // val params = args.apply(3).toString
    //val kv = params.split("#")


    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(startTimeStr).getTime
    val endTime = sf.parse(endTimeStr).getTime
    while(startTime <= endTime) {
      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
//      inputRDD.persist(StorageLevel.MEMORY_AND_DISK)
//      val originCount  = inputRDD.count();
//      System.out.println("originCount originCount originCount:"+originCount);
      val mobileRDD = inputRDD.filter(line =>{
          val json = JSONUtils.getJson(line)



          val mobile = json.getString("mobile")

          if("13739879363".equals(mobile)  ) {
            true
          } else {
            false
          }
      })
//
//        val batchId = tuple._1
//
//        if("0".equals(batchId) ) {
//          true
//        } else {
//          false
//        }
//      }).map(tuple =>{
//          tuple._2
//      })
//      mobileRDD.persist(StorageLevel.MEMORY_ONLY)
//      val empty  =  mobileRDD.count()
//      System.out.println("empty empty empty:"+empty);
      mobileRDD.saveAsTextFile(hdfs+"/data/testTime/"+sf.format(startTime).replace("-","/")+"/")
      startTime = startTime+24*3600*1000l;
    }

  }
}