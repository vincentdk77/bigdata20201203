package com.chuangrui.schema

import org.apache.spark.sql.types._

object ReplySchema {
 val row = StructType(
  Array(
   StructField("replyCustomerId",IntegerType,true),
   StructField("replyMobile",StringType,true),
   StructField("replyContent",StringType,true),
   StructField("replyCtime",TimestampType,true),
   StructField("replyChannelId",StringType,true)
  )
 )

}
