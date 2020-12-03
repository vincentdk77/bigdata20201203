package com.chuangrui.version_3.message



import com.alibaba.fastjson.JSONObject
import com.chuangrui.utils.JSONUtils
import org.apache.commons.lang.StringUtils
import org.apache.spark.rdd.RDD

object Utils {

  def handleInputRDD(rdd:RDD[String]):RDD[String] ={


       val newRDD =  rdd.map(line =>{
          var json:JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          (content,json)
        }).groupByKey().map(tuple=>{
          val iterator = tuple._2.iterator
          var json = iterator.next()
          val sign = json.getString("sign")
         (sign,json.toJSONString)
       }).groupByKey().map(tuple =>{
          val iterator = tuple._2.iterator
          var s = iterator.next()
          s
        })

     newRDD

  }




}
