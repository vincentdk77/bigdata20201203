package com.chuangrui.frequency

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, HbaseUtils, JSONUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object SaveMsgFrequencyToHbase {



  def main(args: Array[String]): Unit = {


    val startTimeStr = args.apply(0)
    val endTimeStr = args.apply(1)
    val partition = args.apply(2).toInt

    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("SaveMsgFrequencyToHbase")

    conf.set("spark.default.parallelism",""+partition)
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)


    //短信模型加载
    val msgMaxTime = ConnectionUtils.selectMaxTime()
    val sameModel = NaiveBayesModel.load(sc, hdfs+"/model/"+msgMaxTime.replace("-","/")+"/")
    val array:Array[String] = ConnectionUtils.getWords()
    val signMap:util.HashMap[Integer,String] =ConnectionUtils.getSign2();
    System.out.println("array array array:"+array.length)
    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)


    val sf = new SimpleDateFormat("yyyy-MM-dd")
    val timeSf = new SimpleDateFormat("yyyy-MM")
    var startTime = sf.parse(startTimeStr).getTime
    val endTime = sf.parse(endTimeStr).getTime
    while(startTime <= endTime) {
      System.out.println("time time time time :"+sf.format(startTime));

      val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.txt.gz").repartition(partition)

      inputRDD.map(line =>{
          val json = JSONUtils.getJson(line)

        if(!StringUtils.isEmpty(json.getString("mobile")) && !StringUtils.isEmpty(json.getString("ctime")) && !StringUtils.isEmpty(json.getString("content"))) {
          val mobile = json.getString("mobile")
          val ctime = json.getString("ctime")
          val content = json.getString("content")

          val word_array = wordArray.value
          val msgModel = model.value
          val wordList = PaoDingCut.cutString(content)
          if(wordList != null && wordList.size()> 0) {
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

            val day = ctime.substring(0,10)

            (mobile+"@"+msgSort+"@"+day,1)
          } else {
            null
          }

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
        val mobileSortDay = tuple._1.split("@");
        var num = 0
        for(t <- tuple._2.iterator) {
          num = num +t
        }
        val json = new JSONObject()
        json.put(mobileSortDay.apply(2),num)
        mobileSortDay.apply(0)+"@"+mobileSortDay.apply(1)+"@"+json.toJSONString
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

        HbaseUtils.saveOrUpdateBatchFrequencyHbase(list)
        list.clear();
      }
    }
    if(list.size()>0) {

      HbaseUtils.saveOrUpdateBatchFrequencyHbase(list )
      list.clear();
    }
    iter
  }
}
