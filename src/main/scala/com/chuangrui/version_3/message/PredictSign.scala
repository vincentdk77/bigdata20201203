package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.node.GetMysqlByNode1
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.JSONUtils
import com.chuangrui.version_3.utils.{ConnectionSignMgrUtils, HttpUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object PredictSign {


//  var wordArray:Broadcast[Array[String]] =null
//  var model:Broadcast[NaiveBayesModel] = null
//  var sign:Broadcast[util.HashMap[Integer,String]] = null

//  var replyArray:Broadcast[Array[String]] = null
//  var replyModel:Broadcast[NaiveBayesModel] = null
//  var zcList:Broadcast[util.List[String]] = null
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("PredictSign")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)



    //短信模型加载
    val msgMaxTime = ConnectionSignMgrUtils.selectMaxTime(null,1)
    val sameModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/first/"+msgMaxTime.replace("-","/")+"/")
    val array:Array[String] = ConnectionSignMgrUtils.getWords(null,1)
    val signMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(1,0);
    System.out.println("array array array:"+array.length)
    System.out.println("signMap signMap signMap:"+signMap.size())
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)


    val filePath = args.apply(0)
    val partition = args.apply(1).toInt

      //短信内容
        val inputRDD = sc.textFile(hdfs+filePath).repartition(partition)
        val jsonRDD:RDD[String] = inputRDD.map(line =>{
          try {
            val ss = line.split(",")
            val content = ss(2)
            val batchId = ss(0)
            val uuid = ss(1)

            if (!StringUtils.isEmpty(content) ) {
              val word_array = wordArray.value
              val msgModel = model.value
              val signMap = sign.value

              val wordList = PaoDingCut.cutString(content)
              var crrentArray: Array[Double] = new Array[Double](word_array.length)
              for (i <- 0 to crrentArray.length - 1) {
                crrentArray(i) = 0
              }
              for (i <- 0 to wordList.size() - 1) {
                for (j <- 0 to word_array.length - 1) {
                  if (word_array(j).equals(wordList.get(i))) {
                    crrentArray(j) = crrentArray(j) + 1
                  }
                }
              }
              val result = msgModel.predict(Vectors.dense(crrentArray))
              val msgSort = signMap.get(result.toInt)
//              json.put("content",GetMysqlByNode1.replace(content))
              val s = msgSort+","+line
              s
            } else {
              null
            }
          } catch {
            case ex => {
              null
            }
          }
        }).filter(json =>{
          if(json !=null) {
            true
          } else {
            false
          }
        })
      jsonRDD.persist(StorageLevel.MEMORY_ONLY)
      jsonRDD.mapPartitions(savePredictSign).count()
      val batchStr :Array[String]= jsonRDD.take(1)
      val bs = batchStr(0).split(",")
      val batchId = Integer.parseInt(bs(1))
      val list = List(batchId)
      System.out.println(batchId);
      val rdd = sc.parallelize(list)
      rdd.map(batchId =>{
          HttpUtils.get(batchId)
      }).count()

  }

  def savePredictSign(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()

    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 5000 == 0) {
        ConnectionSignMgrUtils.savePredictSign(list)
        list.clear();
      }
    }
    if(list.size()>0) {
      ConnectionSignMgrUtils.savePredictSign(list )
      list.clear();
    }
    iter
  }





}
