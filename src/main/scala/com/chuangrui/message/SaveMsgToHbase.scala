package com.chuangrui.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, HbaseUtils, JSONUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object SaveMsgToHbase {



  def main(args: Array[String]): Unit = {


    val startTimeStr = args.apply(0)
    val endTimeStr = args.apply(1)
    val partition = args.apply(2).toInt

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("SaveMsgToHbase")

    conf.set("spark.default.parallelism",""+partition)
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)





    val sf = new SimpleDateFormat("yyyy-MM-dd")
    val timeSf = new SimpleDateFormat("yyyy-MM")
    var startTime = sf.parse(startTimeStr).getTime
    val endTime = sf.parse(endTimeStr).getTime
    while(startTime <= endTime) {
      System.out.println("time time time time :"+sf.format(startTime));

      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.txt.gz").repartition(partition)

      inputRDD.map(line =>{


          val json = JSONUtils.getJson(line)

        if(!StringUtils.isEmpty(json.getString("mobile")) && !StringUtils.isEmpty(json.getString("ctime"))) {
          val mobile = json.getString("mobile")
          val ctime = json.getString("ctime")
          val yM = ctime.substring(0,7)
          (mobile+"@"+yM,json)
        } else {
          null
        }



      }).filter(tuple =>{
        if(tuple != null) {
          true
        } else {
          false
        }

      }).groupByKey().map(tuple =>{
        val mobile = tuple._1;
        val jsonArray = new JSONArray()
        for(json <- tuple._2.iterator) {
          jsonArray.add(json)
        }
        mobile+"@"+jsonArray.toJSONString
      }).mapPartitions(msgFunc).count()

       startTime = startTime+24*3600*1000l
    }
  }


  def msgFunc(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()

    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 100000 == 0) {

        HbaseUtils.saveOrUpdateBatchMsgHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      HbaseUtils.saveOrUpdateBatchMsgHbase(list )
      list.clear();
    }
    iter
  }
}
