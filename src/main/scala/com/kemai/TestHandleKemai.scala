package com.kemai

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

case class InputData(tableName:String,jsonObj:JSONObject)

object TestHandleKemai {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("TestHandleKemai")
      .config("fs.defaultFS", "hdfs://jtb:9000")
      .config("dfs.nameservices", "jtb")
      .config("dfs.ha.namenodes.jtb", "nn1,nn2")
      .config("dfs.namenode.rpc-address.jtb.nn1", "node11:9000")
      .config("dfs.namenode.rpc-address.jtb.nn2", "node12:9000")
//      .config("dfs.client.failover.proxy.provider.jtb", "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider")
      .getOrCreate()
    //		val path_prefix = "hdfs://node11:9000/transform/"   //node11/61.132.230.81:9000
//    val path_prefix = "hdfs://jtb/transform/"   //jtb/61.132.230.81:8020
    val path_prefix = "transform/"   //jtb/61.132.230.81:8020

//    val path_prefix = "D:\\JavaRelation\\工作\\安徽创瑞\\mongoDatas\\transform\\"
    import spark.implicits._
    //path不支持传string，用逗号分隔，而textFile支持
    val inputDF = spark.read.format("json")
      .load(
//        path_prefix + "ent\\*",
        path_prefix + "ent_top500\\*"
      )
      .rdd
      .map(row=>{
//        println(row.toString())
        val strings: Array[String] = row.toString().split("\\|")
        val tableName = strings(0).substring(1)
        val jsonStr = strings(1).substring(0, strings(1).length - 1)
        val jsonObj: JSONObject = JSON.parseObject(jsonStr)
//        InputData(tableName,jsonObj)
        (tableName,jsonStr)
      })
      .toDF("tableName","jsonStr")

    println("=======================================================================")
    inputDF.show(truncate = false)
//    inputDF.collect.foreach(println(_))
    println("=======================================================================")




    spark.stop()
  }

}
