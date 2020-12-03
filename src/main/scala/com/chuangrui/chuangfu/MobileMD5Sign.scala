package com.chuangrui.chuangfu

import java.text.SimpleDateFormat
import java.util

import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.{ConnectionUtils, JSONUtils, MD5Utils, MessageDataHandle}
import org.apache.commons.lang.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel

/**
  * 创富，5万+手机号通过md加密，跑一个月的数据找到这5万+个手机号在我们平台发送的短信，以及短信的类型
  *
  *
  * */
object MobileMD5Sign {
  def main(args: Array[String]): Unit = {


    val startTimeStr = args.apply(0)
    val endTimeStr = args.apply(1)
    val online = args.apply(2)
    var hdfs = "hdfs://cr"
    if(!online.equals("online")) {
        hdfs = "hdfs://node1:9000"
    }

    val conf = new SparkConf().setAppName("MobileMD5Sign")
    conf.set("spark.sql.shuffle.partitions","20")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val timeStr = ConnectionUtils.selectMaxTime()
    //加载模型
    var sameModel:NaiveBayesModel = null
    if(!online.equals("online")) {
      sameModel = NaiveBayesModel.load(sc, hdfs+"/naive_bayes_model/2019/05/06/")
    } else {
      sameModel  = NaiveBayesModel.load(sc, hdfs+"/model/"+timeStr.replace("-","/")+"/")
    }

    val array:Array[String] = ConnectionUtils.getWords()
    //获取所有标签
    val signMap:util.HashMap[Integer,String] =ConnectionUtils.getSign2();
    System.out.println("array array array:"+array.length)

    //获取5万+的md5加密的手机号
    val md5MobileArray = sc.textFile(hdfs+"/data/message/chuangfuMobileMD5.txt").collect()
    val md5MobileMap:util.HashMap[String,String] = new util.HashMap[String,String]()
    for(mobile <- md5MobileArray) {
      md5MobileMap.put(mobile,mobile)
    }
    val md5Map = sc.broadcast(md5MobileMap)

    val wordArray = sc.broadcast(array)
    val model = sc.broadcast(sameModel)
    val sign = sc.broadcast(signMap)

    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(startTimeStr).getTime
    val endTime = sf.parse(endTimeStr).getTime
    while(startTime <= endTime) {
        val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz")
        val resultRDD = inputRDD.map(line =>{
          val json = JSONUtils.getJson(line)
          val md5MobileMap = md5Map.value
          val mobile = json.getString("mobile")
          val md5 = MD5Utils.md5(mobile)
          try {
            if(!StringUtils.isEmpty(json.getString("content")) && !StringUtils.isEmpty(mobile) && !StringUtils.isEmpty(md5MobileMap.get(md5))) {
              //模型的词组
              val word_array = wordArray.value
              //模型
              val msgModel = model.value
              //短信词组
              val wordList = PaoDingCut.cutString(json.getString("content"))
              //标签
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
              md5+","+signMap.get(result.toInt)
            } else {
              ""
            }
          } catch {
            case ex => {
              println("line line line line line:" + line)
              ""
            }

          }

        })

        val filterRDD = resultRDD.filter(line =>{
          if(!StringUtils.isEmpty(line)) {
            true
          } else {
            false
          }
        })
         val rdd2 =  filterRDD.map(line => {
          val ss = line.split(",")
          (ss.apply(0),ss.apply(1))
        })
        val groupRDD =    rdd2.groupByKey().map(tuple => {
          val md5 = tuple._1
          var sign:String = ""
          val iterator = tuple._2.iterator
          for(index <- iterator){
            sign = sign+index+"@"
          }
          md5+","+sign
        })
      groupRDD.saveAsTextFile(hdfs+"/data/chuangfu/"+ sf.format(startTime).replace("-","/") )
      startTime = startTime+24*3600*1000l
    }


    sc.stop()
    //    val result = sameModel.predict(Vectors.dense(Array[Double](80,0,0)))
  }
}
