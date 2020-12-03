package com.chuangrui.schema

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object TempSchema {
 val row = StructType(
  Array(
   StructField("label",IntegerType,true),
   StructField("sentence",StringType,true)
  )
 )

}
