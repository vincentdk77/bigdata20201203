package com.chuangrui.message

import java.util

import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.ConnectionUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.mllib.linalg.Vectors

object PerdictMsgModel {
  def main(args: Array[String]): Unit = {

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("HandelLabel")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val timeStr = args.apply(0)
    val sameModel = NaiveBayesModel.load(sc, hdfs+"/model/"+timeStr.replace("-","/")+"/")
    val array:Array[String] = ConnectionUtils.getWords()
    val signMap:util.HashMap[Integer,String] =ConnectionUtils.getSign2();
    System.out.println("array array array:"+array.length)
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)

    val inputRDD = sc.textFile(hdfs+"/data/message/error.txt")
    val resultRDD = inputRDD.map(line =>{
      val word_array = wordArray.value
      val msgModel = model.value
      val wordList = PaoDingCut.cutString(line)
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
      signMap.get(result.toInt)+","+line
    })
    resultRDD.saveAsTextFile(hdfs+"/data/result/")

    sc.stop()
    //    val result = sameModel.predict(Vectors.dense(Array[Double](80,0,0)))
  }
}
