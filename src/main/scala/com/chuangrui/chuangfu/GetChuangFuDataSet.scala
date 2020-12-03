package com.chuangrui.chuangfu

import java.text.SimpleDateFormat
import java.util

import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ChuangFuConnectionUtils, JSONUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object GetChuangFuDataSet {

  def main(args: Array[String]): Unit = {

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("GetChuangFuDataSet")

    conf.set("spark.default.parallelism","66")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val timeStr = ChuangFuConnectionUtils.selectMaxTime()
    val sameModel = NaiveBayesModel.load(sc, hdfs+"/cfmodel/"+timeStr.replace("-","/")+"/")
    val array:Array[String] = ChuangFuConnectionUtils.getWords()
    val signMap:util.HashMap[Integer,String] =ChuangFuConnectionUtils.getSign2();
    System.out.println("array array array:"+array.length)
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)

    val startTimeStr = args.apply(0)
    val endTimeStr = args.apply(1)


    val partition = args.apply(2).toInt

    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(startTimeStr).getTime
    val endTime = sf.parse(endTimeStr).getTime
    while(startTime <= endTime) {


      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.txt.gz").repartition(partition)
      val tupleRDD = inputRDD.map(line =>{
        val json = JSONUtils.getJson(line)
        val content = json.getString("sign")+ json.getString("content")
        val ctime = json.getString("ctime")
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
          (msgSort,content)
        } else {
          null
        }
      })
      val fileterRDD:RDD[String] = tupleRDD.filter(tuple =>{
        if(tuple == null) {
          false
        } else {
          true
        }
      }).groupByKey().flatMap(tuple=>{
        val msgSort = tuple._1
        val iter = tuple._2.iterator
        var array:Array[String] = new Array[String](iter.length)
        var i=0
        for(s <- iter) {
          array(i) =msgSort+","+s
          i = i+1
        }
        array
      })
      fileterRDD.coalesce(5).saveAsTextFile(hdfs+"/data/chuangfu/msgSort/"+sf.format(startTime).replace("-","/")+"/")



      startTime = startTime+24*3600*1000l;
    }
  }

}
