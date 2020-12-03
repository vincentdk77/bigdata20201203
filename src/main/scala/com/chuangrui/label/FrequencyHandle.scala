package com.chuangrui.label

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object FrequencyHandle {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("LogAnalysisStream")

    //设置反压机制
    conf.set("spark.streaming.backpressure.enabled","true")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.streaming.blockInterval", "5000ms")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val ssc = new StreamingContext(sc,Seconds(60))
   val df= sqlContext.read.parquet("hdfs://node1:9000/naive_bayes_model/data/part-00000-476ea0d5-74ec-4ba8-a66e-1cad420974a5-c000.snappy.parquet")
df.rdd.collect().foreach(println)
    ssc.start()
    ssc.awaitTerminationOrTimeout(3*24*3600*1000)
    sc.stop()
  }
}
