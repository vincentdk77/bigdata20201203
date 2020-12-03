package com.chuangrui.version_3.message

import java.util

import com.chuangrui.test.PaoDingCut
import org.apache.spark.mllib.classification.{LogisticRegressionWithLBFGS, NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}
import com.chuangrui.version_3.utils.ConnectionSignMgrUtils

object FirstMessageSortModel {

  def main(args: Array[String]): Unit = {

    val hdfsUrl = "hdfs://cr"

    val conf = new SparkConf().setAppName("FirstMessageSortModel")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    var timeStr = args.apply(0)
    var wordNumber = args.apply(1).toInt

    //获取一级分类
    val signMap:util.HashMap[String,Integer] = ConnectionSignMgrUtils.getSign(1,0);
    System.out.println("signMap signMap signMap signMap signMap:"+signMap.size())

    val inputRDD = sc.textFile(hdfsUrl+"/data/dataset/message/first/*.txt").repartition(12)
    inputRDD.persist(StorageLevel.MEMORY_ONLY_SER)

    val wordRDD = inputRDD.flatMap(line =>{
      val ss = line.split(",")
      val wordList = PaoDingCut.cutString(ss(1))

      var array:Array[String] = new Array[String](wordList.size())
      for(i <- 0 to wordList.size()-1) {
        array(i) = wordList.get(i)
      }
      array
    })

    //每个词出现次数
    val pairRDD = wordRDD.map(word =>{(word,1)}).groupByKey().map( tuple => {
      val word = tuple._1
      val iterator = tuple._2.iterator
      var sum =0
      for(index <- iterator){
        sum = sum+index
      }
      (word,sum)
    })
    pairRDD.persist(StorageLevel.MEMORY_ONLY)
    val maxNum = pairRDD.map(tuple=>{(tuple._2,tuple._1)}).sortByKey(false).take(1).apply(0)._1.toInt
    System.out.println("maxNum maxNum maxNum maxNum:"+maxNum);
    //pairRDD.map(tuple=>{(tuple._2,tuple._1)}).sortByKey(false).saveAsTextFile(hdfsUrl+"/data/pairRDD1")

    //计算每个词出现的篇幅数
    val numberRDD = inputRDD.flatMap(line => {
      val ss = line.split(",")
      val wordList = PaoDingCut.getDistictString(ss(1))
      var array:Array[String] = new Array[String](wordList.size())
      for(i <- 0 to wordList.size()-1) {
        array(i) = wordList.get(i)
      }
      array
    }).map(word =>{(word,1)}).groupByKey().map( tuple => {
      val word = tuple._1
      val iterator = tuple._2.iterator
      var sum =0
      for(index <- iterator){
        sum = sum+index
      }
      (word,sum)
    })


    val numberArray = numberRDD.collect()
    val map:util.HashMap[String,Double] = new util.HashMap[String,Double]()
    for(tuple <- numberArray) {
      map.put(tuple._1,tuple._2)
    }
    val broadcast = sc.broadcast(map)

   val handleRDD =  pairRDD.map(tuple => {
       val map = broadcast.value
       val word = tuple._1
       var num:Double = 0
       if(map.get(word) != null) {
          num = map.get(word)
       }
      if(num == 0) {
        (num,word)
      } else {
        (tuple._2/num+tuple._2/(maxNum*0.1),word)
      }
    }).sortByKey(false)

   // handleRDD.saveAsTextFile(hdfsUrl+"/data/handleRDD1")

   val wordVector:Array[String] = handleRDD.map(tuple=>{tuple._2}).take(wordNumber)
  //  wordVector.foreach(println)
    ConnectionSignMgrUtils.saveWord(wordVector,timeStr,null,1)

    val vector = sc.broadcast(wordVector)

    val parsedData = inputRDD.map(line => {
      val ss = line.split(",")
      val wordList = PaoDingCut.cutString(ss(1))
      val wordVector:Array[String] = vector.value
      var array:Array[Double] = new Array[Double](wordVector.length)
      for(i<-0 to array.length-1) {
          array(i) =0
      }
      for(i <- 0 to wordList.size()-1) {
        for(j<-0 to wordVector.length-1) {
          if(wordVector(j).equals( wordList.get(i))) {
            array(j) = array(j)+1
          }
        }

      }
      if(ss.length != 2 || signMap.get(ss(0)) == null ) {
        System.out.println("line line :"+line)
      }
      LabeledPoint(signMap.get(ss(0)).toDouble, Vectors.dense(array))
    })

    //样本数据划分训练样本与测试样本
    val splits = parsedData.randomSplit(Array(0.9, 0.1), seed = 20L)
    val training = splits(0)
    val test = splits(1)

    //新建贝叶斯分类模型模型，并训练 ,lambda 拉普拉斯估计
    val model = NaiveBayes.train(training, lambda = 1.0)

    //对测试样本进行测试
    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val print_predict = predictionAndLabel.take(100)
    println("prediction" + "\t" + "label")
    for (i <- 0 to print_predict.length - 1) {
      println(print_predict(i)._1 + "\t" + print_predict(i)._2)
    }

    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
    println(accuracy)

    //保存模型
    val ModelPath = "/model/version3/first/"+timeStr.replace("-","/")+"/"
//    model.save(sc, ModelPath)

//    val sameModel = NaiveBayesModel.load(sc, ModelPath)



//    val result = model.predict(Vectors.dense(Array[Double](80,0,0)))
//    println("result = "+result)











    sc.stop()
  }


}
