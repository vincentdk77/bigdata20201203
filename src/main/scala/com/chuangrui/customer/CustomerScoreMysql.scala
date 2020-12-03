package com.chuangrui.customer

import java.text.SimpleDateFormat
import java.util

import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, JSONUtils, MessageDataHandle}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

object CustomerScoreMysql {



  def main(args: Array[String]): Unit = {

    val startTimeStr = args.apply(0)
    val endTimeStr = args.apply(1)
    //是否是第一次执行 1：是的，0：不是
    val isFirst = args.apply(2)

    val partition = args.apply(3).toInt

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("CustomerScoreMysql")

    conf.set("spark.default.parallelism",""+partition)
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val timeStr = ConnectionUtils.selectMaxTime()
    val sameModel = NaiveBayesModel.load(sc, hdfs+"/model/"+timeStr.replace("-","/")+"/")
    val array:Array[String] = ConnectionUtils.getWords()
    val signMap:util.HashMap[Integer,String] =ConnectionUtils.getSign2();
    System.out.println("array array array:"+array.length)
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)



    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(startTimeStr).getTime
    val endTime = sf.parse(endTimeStr).getTime
    while(startTime <= endTime) {


      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.txt.gz").repartition(partition)
      val tupleRDD = inputRDD.map(line =>{
        val json = JSONUtils.getJson(line)
        val content = json.getString("content")
        if(!StringUtils.isEmpty(content)) {
          val mobile = json.getString("mobile")
          val customerId = json.getString("customerId")

          val word_array = wordArray.value
          val msgModel = model.value
          val wordList = PaoDingCut.cutString(content)
          val signMap = sign.value
          var crrentArray:Array[Double] = new Array[Double](word_array.length)
          for(i<-0 to crrentArray.length-1) {
            crrentArray(i) =0
          }
          for(i <- 0 to wordList.size()-1) {
            for(j<-0 to word_array.length-1) {
              if(word_array(j).equals( wordList.get(i))) {
                crrentArray(j) = crrentArray(j)+1
              }
            }
          }
          val result = msgModel.predict(Vectors.dense(crrentArray))
          val msgSort = signMap.get(result.toInt)
          (mobile+"@"+msgSort,customerId)
        } else {
          null
        }
      })
      val fileterRDD:RDD[(String,String)] = tupleRDD.filter(tuple =>{
        if(tuple == null) {
          false
        } else {
          true
        }
      })

        val resultRDD = fileterRDD.groupByKey().map(tuple =>{
          val mobileSort = tuple._1
          val iterator = tuple._2.iterator
          var customerIds = ""
          for(s <- iterator) {
            if(!customerIds.contains(s)) {
              customerIds = customerIds+s+","
            }

          }
//          val ms = mobileSort.split("@")
//          ConnectionUtils.saveOrUpdateCustomerSocre(ms.apply(0),ms.apply(1),customerIds.substring(0,customerIds.length-1))
          mobileSort+"@"+customerIds.substring(0,customerIds.length-1)
        })

      def func(iter: Iterator[String]) :  Iterator[String] = {
        var index:Int = 0
        var list:util.ArrayList[String] = new util.ArrayList()
        while (iter.hasNext) {
          val e = iter.next;
          list.add(e)
          index = index+1
          if(index % 10000 == 0) {
            System.out.println("index index index index index index:"+index);
            ConnectionUtils.saveOrUpdateBatchCustomerSocre(list,sf.format(startTime))
            list.clear();
          }
        }
        if(list.size()>0) {
          System.out.println("index index index index index index:"+index);
          ConnectionUtils.saveOrUpdateBatchCustomerSocre(list,sf.format(startTime))
          list.clear();
        }
        iter
      }


       val finallRDD = resultRDD.mapPartitions(  func  )
       System.out.println("count count count count:"+finallRDD.count())
       startTime = startTime+24*3600*1000l
    }
  }



}
