package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.utils.JSONUtils
import com.chuangrui.version_3.utils.JsonUtils
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object GetSign {



  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("GetSign")
    val sc = new SparkContext(conf)


  val batchSignRdd =  sc.textFile(hdfs+"/data/message/batchId_sign.txt") .map(line =>{
    try {
      val s = line.split(",")
      (s(0),s(1))
    } catch {
      case ex => {
        null
      }
    }
  }).filter(tuple => {
    if(tuple == null) {
      false
    } else {
      true
    }
  })
  batchSignRdd.persist(StorageLevel.MEMORY_ONLY)

    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime
    val smallTime = sf.parse("2018-09-30").getTime
    val maxTime = sf.parse("2019-10-18").getTime
    while (startTime <= endTime) {
      //短信内容
        val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val jsonRDD:RDD[JSONObject] = inputRDD.map(line =>{
        try {
          var json:JSONObject = JSONUtils.getJson(line)
          json
        } catch {
          case ex => {
            null
          }
        }
      }).filter(json =>{
          if(json == null) {
            false
          } else {
            true
          }
        })
      jsonRDD.persist(StorageLevel.MEMORY_ONLY)

      val batchIdArray:Array[String] = jsonRDD.map(json =>{
           val batchId = json.getString("batchId")
          (batchId,1)
        }).groupByKey().map(tuple=>{
          tuple._1
        }).collect()
//      System.out.println("batchIdArray batchIdArray batchIdArray:"+batchIdArray)

      var batchMap:util.HashMap[String,Integer] =new util.HashMap[String,Integer](batchIdArray.length);
      for( s <- batchIdArray) {
        batchMap.put(s,1)
      }
//      System.out.println("batchMap batchMap batchMap:"+batchMap.size())
      val ba = sc.broadcast(batchMap)

//      System.out.println("batchSignRdd batchSignRdd batchSignRdd:"+batchSignRdd.count())
      val filterBatchSignRDD = batchSignRdd.filter(tuple =>{
        val bm = ba.value
//        System.out.println("bm bm:"+bm.size()+" tuple._1:"+tuple._1);
        if(bm.get(tuple._1) != null) {
          true
        } else {
          false
        }
      })
//      filterBatchSignRDD.persist(StorageLevel.MEMORY_ONLY)
//      System.out.println("filterBatchSignRDD filterBatchSignRDD filterBatchSignRDD:"+filterBatchSignRDD.count());
      val bsArray:Array[(String,String)] = filterBatchSignRDD.collect()
      var bsMap:util.HashMap[String,String] =new util.HashMap[String,String]();
      for(t <- bsArray) {
        bsMap.put(t._1,t._2)
      }
//      System.out.println("bs bs bs:"+bsMap.size());
      val bs = sc.broadcast(bsMap)

      val finalRDD = jsonRDD.map(j =>{

        val bm = bs.value
        val s = JsonUtils.setSign(j,bm)
        s
      })

      finalRDD.coalesce(44).saveAsTextFile("hdfs://cr/data/getSign/"+sf.format(startTime).replace("-","/"),classOf[GzipCodec])
      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }





}
