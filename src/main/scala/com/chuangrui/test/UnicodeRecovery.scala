package com.chuangrui.test

import java.text.SimpleDateFormat

import com.chuangrui.utils.JSONUtils
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object UnicodeRecovery {
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("TestMobile")
    conf.set("spark.default.parallelism","40")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val startTimeStr = args.apply(0)
    val endTimeStr = args.apply(1)
    val partition = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(startTimeStr).getTime
    val endTime = sf.parse(endTimeStr).getTime
    while(startTime <= endTime) {
      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.txt.gz").repartition(partition)
//      inputRDD.persist(StorageLevel.MEMORY_AND_DISK)
//      val originCount  = inputRDD.count();
      val mobileRDD = inputRDD.map(line =>{
        val s = JSONUtils.recoverEncode(line)
        s
      })
      mobileRDD.coalesce(1).saveAsTextFile(hdfs+"/data/mobileResult/"+sf.format(startTime).replace("-","/")+"/")
      startTime = startTime+24*3600*1000l
    }

  }
}