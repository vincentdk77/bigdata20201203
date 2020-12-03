package com.chuangrui.schema

import org.apache.spark.sql.types._

object MessageSchema {
 val row = StructType(
  Array(
   StructField("customerId",IntegerType,true),
   StructField("mobile",StringType,true),
   StructField("content",StringType,true),
   StructField("ctime",TimestampType,true),
   StructField("channelId",StringType,true)
  )
 )

}
