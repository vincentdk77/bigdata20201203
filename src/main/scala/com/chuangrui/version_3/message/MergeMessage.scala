package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.JSONUtils
import com.chuangrui.version_3.utils.ConnectionSignMgrUtils
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object MergeMessage {


  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("MergeMessage")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)



    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val level = args.apply(2)
    val sign = args.apply(3)


    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime

    while (startTime <= endTime) {
      //短信内容
        val inputRDD = sc.textFile(hdfs+"/data/level/"+level+"/"+sign+"/"+sf.format(startTime).replace("-","/")+"/*.gz")

      inputRDD.coalesce(1).saveAsTextFile("hdfs://cr/data/newLevel/"+level+"/"+sign+"/"+sf.format(startTime).replace("-","/")+"/" ,classOf[GzipCodec])

      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }

}
