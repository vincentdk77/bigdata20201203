package com.chuangrui.label

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import java.util.Properties

object HandelLabel {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("HandelLabel")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    var props = new Properties();
    props.put("user","chuangrui1");
    props.put("password","chuangrui@123");
    val df = sqlContext.read.jdbc("jdbc:mysql://210.5.152.205:3306/historydata","tbl_app_identify_batch_record_result20181023",props).where("ctime between '2018-08-03 00:00:00' and '2018-08-03 23:59:59'").limit(1000)
    df.write.mode("overwrite").parquet("hdfs://");
    val sum =  df.rdd.count();
    println("sum"+sum);

    sc.stop();
    //    val connectionProperties = new Properties()
    //     connectionProperties.put("user","chuangrui1")
    //    connectionProperties.put("password","chuangrui@123")
    //     connectionProperties.put("driver","com.mysql.jdbc.Driver")
    //    val jdbc2DF = sqlContext.read.jdbc("jdbc:mysql://210.5.152.205:3306/historydata","tbl_app_identify_batch_record_result20181023",connectionProperties)
    //    val tempDf = sqlContext.sql("select * from tbl_app_identify_batch_record_result20181023   where ctime between '2018-10-20 00:00:00' and '2018-10-20 23:59:59'")
    //    //tempDf.write.mode("append").parquet("hdfs://data/temp")
    //    val sum = tempDf.rdd.count()
    //    println("sum"+sum);
  }
}
