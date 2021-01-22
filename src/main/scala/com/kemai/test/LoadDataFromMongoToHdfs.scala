package com.kemai.test

import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ArrayBuffer



object LoadDataFromMongoToHdfs {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME","root")
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("LoadDataFromMongoToHdfs")
      .getOrCreate()
    val sc = spark.sparkContext

    val collections = Array(
      "ent" // ES索引，统计字段
//      "ent_a_taxpayer", // ES索引，统计字段
//      "ent_abnormal_opt", // ES索引
//      "ent_annual_report", // 统计字段
//      "ent_apps", // ES索引，统计字段
//      "ent_bids", // ES索引
//      "ent_brand", // ES索引
//      "ent_cert", // ES索引，统计字段
//      "ent_contacts", // ES索引，统计字段
//      "ent_copyrights", // ES索引
//      "ent_court_notice", // 统计字段
//      "ent_court_operator", // ES索引
//      "ent_court_paper", // 统计字段
//      "ent_dishonesty_operator", //
//      "ent_ecommerce", // ES索引，统计字段
//      "ent_equity_pledged", // 统计字段
//      "ent_funding_event", // ES索引
//      "ent_goods", // ES索引
//      "ent_invest_company", // 统计字段
//      "ent_licence", // ES索引，统计字段
//      "ent_new_media", // ES索引，统计字段
//      "ent_news", // ES索引，统计字段
//      "ent_patent", // ES索引，统计字段
//      "ent_punishment", // ES索引，统计字段
//      "ent_recruit", // ES索引，统计字段
//      "ent_software", // ES索引，统计字段
//      "ent_trademark", // ES索引，统计字段
//      "ent_website", // ES索引，统计字段
//      "ent_maimai", //
//      "ent_zhaodao" //
    )
    import spark.implicits._
    for (tableName <- collections) {
      val mongoDF = spark.read
        .option("uri", "mongodb://foo-1:27017/ent")
        .option("collection", tableName)
        .format("com.mongodb.spark.sql")
        .load()
//          .rdd
//        .map(row=>tableName+"|"+row)
//        .mapPartitions(iter=>{
//          val array = ArrayBuffer[String]()
//          for (row <- iter) {
//            val str = row.toString()
//            array.append(str)
////            val tableName = str.substring(1,str.indexOf("|"))//去掉第一个[
////            val jsonStr = str.substring(str.indexOf("|")+1,str.length-1)//去掉最后一个]
//          }
//
//          array.iterator
//        })

//      mongoDF.foreach(ent=>println(ent.entName))
      mongoDF.show(truncate = false)
      println("开始写数据...")
//      mongoDF.groupBy()
//      mongoDF.saveAsTextFile("hdfs://foo-1:9000/newData")
      mongoDF.write.mode("overwrite").format("json").save("hdfs://foo-1:9000/newData")
      println("写入hdfs完毕！")


    }


    spark.close()


  }

}
