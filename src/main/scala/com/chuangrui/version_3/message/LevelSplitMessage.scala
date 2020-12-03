package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.node.GetMysqlByNode1
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.JSONUtils
import com.chuangrui.version_3.utils.ConnectionSignMgrUtils
import org.apache.commons.lang3.StringUtils
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object LevelSplitMessage {


  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("LevelSplitMessage")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)



    //一级模型加载
    val firstMaxTime = ConnectionSignMgrUtils.selectMaxTime(null,1)
    val firstMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/first/"+firstMaxTime.replace("-","/")+"/")
    val firstWordArray:Array[String] = ConnectionSignMgrUtils.getWords(null,1)
    val signMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getAllSignId();
    System.out.println("firstWordArray firstWordArray firstWordArray:"+firstWordArray.length)
    val firstArray = sc.broadcast(firstWordArray)
    val firstModel = sc.broadcast(firstMsgModel)
    val sign = sc.broadcast(signMap)

    //二级验证码加载  yanzhengma
    val secondYzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("yanzhengma",2)
    val secondYzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/second/yanzhengma/"+secondYzmMaxTime.replace("-","/")+"/")
    val secondYzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("yanzhengma",2)
    System.out.println("secondYzmArray secondYzmArray secondYzmArray:"+secondYzmWordArray.length)
    val secondYzmArray = sc.broadcast(secondYzmWordArray)
    val secondYzmModel = sc.broadcast(secondYzmMsgModel)

    //二级通知加载  tongzhi
    val secondTzMaxTime = ConnectionSignMgrUtils.selectMaxTime("tongzhi",2)
    val secondTzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/second/tongzhi/"+secondTzMaxTime.replace("-","/")+"/")
    val secondTzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("tongzhi",2)
    System.out.println("secondTzWordArray secondTzWordArray secondTzWordArray:"+secondTzWordArray.length)
    val secondTzArray = sc.broadcast(secondTzWordArray)
    val secondTzmModel = sc.broadcast(secondTzMsgModel)

    //二级营销加载  yingxiao
    val secondYxMaxTime = ConnectionSignMgrUtils.selectMaxTime("yingxiao",2)
    val secondYxMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/second/yingxiao/"+secondYxMaxTime.replace("-","/")+"/")
    val secondYxWordArray:Array[String] = ConnectionSignMgrUtils.getWords("yingxiao",2)
    System.out.println("secondYxWordArray secondYxWordArray secondYxWordArray:"+secondYxWordArray.length)
    val secondYxArray = sc.broadcast(secondYxWordArray)
    val secondYxModel = sc.broadcast(secondYxMsgModel)

    //三级餐饮加载  canyin
    val thirdCyMaxTime = ConnectionSignMgrUtils.selectMaxTime("canyin",3)
    val thirdCyMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/canyin/"+thirdCyMaxTime.replace("-","/")+"/")
    val thirdCyWordArray:Array[String] = ConnectionSignMgrUtils.getWords("canyin",3)
    System.out.println("thirdCyWordArray thirdCyWordArray thirdCyWordArray:"+thirdCyWordArray.length)
    val thirdCyArray = sc.broadcast(thirdCyWordArray)
    val thirdCyModel = sc.broadcast(thirdCyMsgModel)

    //三级电商加载  dianshang
    val thirdDsMaxTime = ConnectionSignMgrUtils.selectMaxTime("dianshang",3)
    val thirdDsMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/dianshang/"+thirdDsMaxTime.replace("-","/")+"/")
    val thirdDsWordArray:Array[String] = ConnectionSignMgrUtils.getWords("dianshang",3)
    System.out.println("thirdDsWordArray thirdDsWordArray thirdDsWordArray:"+thirdDsWordArray.length)
    val thirdDsArray = sc.broadcast(thirdDsWordArray)
    val thirdDsModel = sc.broadcast(thirdDsMsgModel)

    //三级健康加载  jiankang
    val thirdJkMaxTime = ConnectionSignMgrUtils.selectMaxTime("jiankang",3)
    val thirdJkMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/jiankang/"+thirdJkMaxTime.replace("-","/")+"/")
    val thirdJkWordArray:Array[String] = ConnectionSignMgrUtils.getWords("jiankang",3)
    System.out.println("thirdJkWordArray thirdJkWordArray thirdJkWordArray:"+thirdJkWordArray.length)
    val thirdJkArray = sc.broadcast(thirdJkWordArray)
    val thirdJkModel = sc.broadcast(thirdJkMsgModel)

    //三级金融加载  	jinrong
    val thirdJrMaxTime = ConnectionSignMgrUtils.selectMaxTime("jinrong",3)
    val thirdJrMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/jinrong/"+thirdJrMaxTime.replace("-","/")+"/")
    val thirdJrWordArray:Array[String] = ConnectionSignMgrUtils.getWords("jinrong",3)
    System.out.println("thirdJrWordArray thirdJrWordArray thirdJrWordArray:"+thirdJrWordArray.length)
    val thirdJrArray = sc.broadcast(thirdJrWordArray)
    val thirdJrModel = sc.broadcast(thirdJrMsgModel)

    //三级金融加载  		putongyingxiao
    val thirdPtyxMaxTime = ConnectionSignMgrUtils.selectMaxTime("putongyingxiao",3)
    val thirdPtyxMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/putongyingxiao/"+thirdPtyxMaxTime.replace("-","/")+"/")
    val thirdPtyxWordArray:Array[String] = ConnectionSignMgrUtils.getWords("putongyingxiao",3)
    System.out.println("thirdPtyxWordArray thirdPtyxWordArray thirdPtyxWordArray:"+thirdPtyxWordArray.length)
    val thirdPtyxArray = sc.broadcast(thirdPtyxWordArray)
    val thirdPtyxModel = sc.broadcast(thirdPtyxMsgModel)

    //三级餐饮通知加载  		canyintongzhi
    val thirdCytzMaxTime = ConnectionSignMgrUtils.selectMaxTime("canyintongzhi",3)
    val thirdCytzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/canyintongzhi/"+thirdCytzMaxTime.replace("-","/")+"/")
    val thirdCytzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("canyintongzhi",3)
    System.out.println("thirdCytzWordArray thirdCytzWordArray thirdCytzWordArray:"+thirdCytzWordArray.length)
    val thirdCytzArray = sc.broadcast(thirdCytzWordArray)
    val thirdCytzModel = sc.broadcast(thirdCytzMsgModel)

    //三级电商通知加载  		dianshangtongzhi
    val thirdDstzMaxTime = ConnectionSignMgrUtils.selectMaxTime("dianshangtongzhi",3)
    val thirdDstzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/dianshangtongzhi/"+thirdDstzMaxTime.replace("-","/")+"/")
    val thirdDstzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("dianshangtongzhi",3)
    System.out.println("thirdDstzWordArray thirdDstzWordArray thirdDstzWordArray:"+thirdDstzWordArray.length)
    val thirdDstzArray = sc.broadcast(thirdDstzWordArray)
    val thirdDstzModel = sc.broadcast(thirdDstzMsgModel)

    //三级健康通知加载  		jiankangtongzhi
    val thirdJktzMaxTime = ConnectionSignMgrUtils.selectMaxTime("jiankangtongzhi",3)
    val thirdJktzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/jiankangtongzhi/"+thirdJktzMaxTime.replace("-","/")+"/")
    val thirdJktzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("jiankangtongzhi",3)
    System.out.println("thirdJktzWordArray thirdJktzWordArray thirdJktzWordArray:"+thirdJktzWordArray.length)
    val thirdJktzArray = sc.broadcast(thirdJktzWordArray)
    val thirdJktzModel = sc.broadcast(thirdJktzMsgModel)

    //三级金融通知加载  		jinrongtongzhi
    val thirdJrtzMaxTime = ConnectionSignMgrUtils.selectMaxTime("jinrongtongzhi",3)
    val thirdJrtzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/jinrongtongzhi/"+thirdJrtzMaxTime.replace("-","/")+"/")
    val thirdJrtzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("jinrongtongzhi",3)
    System.out.println("thirdJrtzWordArray thirdJrtzWordArray thirdJrtzWordArray:"+thirdJrtzWordArray.length)
    val thirdJrtzArray = sc.broadcast(thirdJrtzWordArray)
    val thirdJrtzModel = sc.broadcast(thirdJrtzMsgModel)

    //三级其他通知加载  		qitatongzhi
    val thirdQttzMaxTime = ConnectionSignMgrUtils.selectMaxTime("qitatongzhi",3)
    val thirdQttzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/qitatongzhi/"+thirdQttzMaxTime.replace("-","/")+"/")
    val thirdQttzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("qitatongzhi",3)
    System.out.println("thirdQttzWordArray thirdQttzWordArray thirdQttzWordArray:"+thirdQttzWordArray.length)
    val thirdQttzArray = sc.broadcast(thirdQttzWordArray)
    val thirdQttzModel = sc.broadcast(thirdQttzMsgModel)

    //三级餐饮验证码加载  		canyinyanzhengma
    val thirdCyyzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("canyinyanzhengma",3)
    val thirdCyyzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/canyinyanzhengma/"+thirdCyyzmMaxTime.replace("-","/")+"/")
    val thirdCyyzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("canyinyanzhengma",3)
    System.out.println("thirdCyyzmWordArray thirdCyyzmWordArray thirdCyyzmWordArray:"+thirdCyyzmWordArray.length)
    val thirdCyyzmArray = sc.broadcast(thirdCyyzmWordArray)
    val thirdCyyzmModel = sc.broadcast(thirdCyyzmMsgModel)

    //三级餐饮验证码加载  		dianshangyanzhengma
    val thirdDsyzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("dianshangyanzhengma",3)
    val thirdDsyzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/dianshangyanzhengma/"+thirdDsyzmMaxTime.replace("-","/")+"/")
    val thirdDsyzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("dianshangyanzhengma",3)
    System.out.println("thirdDsyzmWordArray thirdDsyzmWordArray thirdDsyzmWordArray:"+thirdDsyzmWordArray.length)
    val thirdDsyzmArray = sc.broadcast(thirdDsyzmWordArray)
    val thirdDsyzmModel = sc.broadcast(thirdDsyzmMsgModel)

    //三级健康验证码加载  		jiankangyanzhengma
    val thirdJkyzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("jiankangyanzhengma",3)
    val thirdJkyzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/jiankangyanzhengma/"+thirdJkyzmMaxTime.replace("-","/")+"/")
    val thirdJkyzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("jiankangyanzhengma",3)
    System.out.println("thirdJkyzmWordArray thirdJkyzmWordArray thirdJkyzmWordArray:"+thirdJkyzmWordArray.length)
    val thirdJkyzmArray = sc.broadcast(thirdJkyzmWordArray)
    val thirdJkyzmModel = sc.broadcast(thirdJkyzmMsgModel)

    //三级金融验证码加载  		jinrongyanzhengma
    val thirdJryzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("jinrongyanzhengma",3)
    val thirdJryzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/jinrongyanzhengma/"+thirdJryzmMaxTime.replace("-","/")+"/")
    val thirdJryzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("jinrongyanzhengma",3)
    System.out.println("thirdJryzmWordArray thirdJryzmWordArray thirdJryzmWordArray:"+thirdJryzmWordArray.length)
    val thirdJryzmArray = sc.broadcast(thirdJryzmWordArray)
    val thirdJryzmModel = sc.broadcast(thirdJryzmMsgModel)

    //四级贷款加载  		daikuan
    val fourDkMaxTime = ConnectionSignMgrUtils.selectMaxTime("daikuan",4)
    val fourDkMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/daikuan/"+fourDkMaxTime.replace("-","/")+"/")
    val fourDkWordArray:Array[String] = ConnectionSignMgrUtils.getWords("daikuan",4)
    System.out.println("fourDkWordArray fourDkWordArray fourDkWordArray:"+fourDkWordArray.length)
    val fourDkArray = sc.broadcast(fourDkWordArray)
    val fourDkModel = sc.broadcast(fourDkMsgModel)

    //四级贷款通知加载  		daikuantongzhi
    val fourDktzMaxTime = ConnectionSignMgrUtils.selectMaxTime("daikuantongzhi",4)
    val fourDktzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/daikuantongzhi/"+fourDktzMaxTime.replace("-","/")+"/")
    val fourDktzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("daikuantongzhi",4)
    System.out.println("fourDktzWordArray fourDktzWordArray fourDktzWordArray:"+fourDktzWordArray.length)
    val fourDktzArray = sc.broadcast(fourDktzWordArray)
    val fourDktzModel = sc.broadcast(fourDktzMsgModel)

    //四级贷款验证码加载  		daikuanyanzhengma
    val fourDkyzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("daikuanyanzhengma",4)
    val fourDkyzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/daikuanyanzhengma/"+fourDkyzmMaxTime.replace("-","/")+"/")
    val fourDkyzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("daikuanyanzhengma",4)
    System.out.println("fourDkyzmWordArray fourDkyzmWordArray fourDkyzmWordArray:"+fourDkyzmWordArray.length)
    val fourDkyzmArray = sc.broadcast(fourDkyzmWordArray)
    val fourDkyzmModel = sc.broadcast(fourDkyzmMsgModel)

    //四级卖场加载  		maichang
    val fourMcMaxTime = ConnectionSignMgrUtils.selectMaxTime("maichang",4)
    val fourMcMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/four/maichang/"+fourMcMaxTime.replace("-","/")+"/")
    val fourMcWordArray:Array[String] = ConnectionSignMgrUtils.getWords("maichang",4)
    System.out.println("fourMcWordArray fourMcWordArray fourMcWordArray:"+fourMcWordArray.length)
    val fourMcArray = sc.broadcast(fourMcWordArray)
    val fourMcModel = sc.broadcast(fourMcMsgModel)

    //五级平安个贷通知加载  		pingangedaitongzhi
    val fivePagdtzMaxTime = ConnectionSignMgrUtils.selectMaxTime("pingangedaitongzhi",5)
    val fivePagdtzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/five/pingangedaitongzhi/"+fivePagdtzMaxTime.replace("-","/")+"/")
    val fivePagdtzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("pingangedaitongzhi",5)
    System.out.println("fivePagdtzWordArray fivePagdtzWordArray fivePagdtzWordArray:"+fivePagdtzWordArray.length)
    val fivePagdtzArray = sc.broadcast(fivePagdtzWordArray)
    val fivePagdtzModel = sc.broadcast(fivePagdtzMsgModel)

    //五级网贷通知加载  		wangdaitongzhi
    val fiveWdtzMaxTime = ConnectionSignMgrUtils.selectMaxTime("wangdaitongzhi",5)
    val fiveWdtzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/five/wangdaitongzhi/"+fiveWdtzMaxTime.replace("-","/")+"/")
    val fiveWdtzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("wangdaitongzhi",5)
    System.out.println("fiveWdtzWordArray fiveWdtzWordArray fiveWdtzWordArray:"+fiveWdtzWordArray.length)
    val fiveWdtzArray = sc.broadcast(fiveWdtzWordArray)
    val fiveWdtzModel = sc.broadcast(fiveWdtzMsgModel)

    //五级银行贷款通知加载  		yinhangdaikuantongzhi
    val fiveYhdktzMaxTime = ConnectionSignMgrUtils.selectMaxTime("yinhangdaikuantongzhi",5)
    val fiveYhdktzMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/five/yinhangdaikuantongzhi/"+fiveYhdktzMaxTime.replace("-","/")+"/")
    val fiveYhdktzWordArray:Array[String] = ConnectionSignMgrUtils.getWords("yinhangdaikuantongzhi",5)
    System.out.println("fiveYhdktzWordArray fiveYhdktzWordArray fiveYhdktzWordArray:"+fiveYhdktzWordArray.length)
    val fiveYhdktzArray = sc.broadcast(fiveYhdktzWordArray)
    val fiveYhdktzModel = sc.broadcast(fiveYhdktzMsgModel)

    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime

    while (startTime <= endTime) {
      //短信内容
        val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.gz").repartition(partition)
        val jsonRDD:RDD[(String,String)] = inputRDD.map(line =>{
          try {
            var json:JSONObject = JSONUtils.getJson(line)
            val content = json.getString("content")
            var signCode = json.getString("sign")
            if (!StringUtils.isEmpty(content) ) {
              val word_array = firstArray.value
              val msgModel = firstModel.value
              val signMap = sign.value
              val wordList = PaoDingCut.cutString(json.getString("sign")+content)
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
              (msgSort,signCode+GetMysqlByNode1.replace(content))
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

      //-----------------------------------------------------------------------------------------------------
      //------------------------------------------------------------验证码-----------------------------------
      //-----------------------------------------------------------------------------------------------------
      //一级验证码
      val originYzmRDD  = jsonRDD.filter(tuple => {
        if(tuple._1.equals("yanzhengma")) {
          true
        } else {
          false
        }
      }).map(tuple =>{
        tuple._1+","+tuple._2.replace(",","，")
      })
      originYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      originYzmRDD.coalesce(30).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/first/yanzhengma/")
      val yzmRDD = originYzmRDD.map(line => {
        val content = line.split(",").apply(1)
        val yzmModel = secondYzmModel.value
        val word_array = secondYzmArray.value
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
        val result = yzmModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      yzmRDD.persist(StorageLevel.MEMORY_ONLY)

      //二级餐饮验证码
      val originCyYzmRDD = yzmRDD.filter(tuple => {
        if(tuple._1.equals("canyinyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  })
      originCyYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      originCyYzmRDD.coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/canyinyanzhengma/")
      val cyYzmRDD = originCyYzmRDD.map(line =>{
        val content = line.split(",").apply(1)
        val cyYzmModel = thirdCyyzmModel.value
        val word_array = thirdCyyzmArray.value
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
        val result = cyYzmModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      cyYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      //三级饭店验证码
      cyYzmRDD.filter(tuple => {
        if(tuple._1.equals("fandianyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/fandianyanzhengma/")
      //三级外卖验证码
      cyYzmRDD.filter(tuple => {
        if(tuple._1.equals("waimaiyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/waimaiyanzhengma/")
      cyYzmRDD.unpersist()
      originCyYzmRDD.unpersist()

      //二级电商验证码
      val originDsYzmRDD = yzmRDD.filter(tuple => {
        if(tuple._1.equals("dianshangyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  })
      originDsYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      originDsYzmRDD .coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/dianshangyanzhengma/")
      val dsYzmRDD = originDsYzmRDD.map(line =>{
        val content = line.split(",").apply(1)
        val dsYzmModel = thirdDsyzmModel.value
        val word_array = thirdDsyzmArray.value
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
        val result = dsYzmModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      dsYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      //三级安装维护验证码  anzhuangweihuyanzhengma
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("anzhuangweihuyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/anzhuangweihuyanzhengma/")
      //三级电脑办公验证码  diannaobangongyanzhengma
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("diannaobangongyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/diannaobangongyanzhengma/")
      //三级服饰衣帽验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("fushiyimaoyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/fushiyimaoyanzhengma/")
      //三级家居用品验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("jiajuyongpinyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/jiajuyongpinyanzhengma/")
      //三级家用电器验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("jiayongdianqiyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/jiayongdianqiyanzhengma/")
      //三级酒店旅游验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("jiudianlvyouyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/jiudianlvyouyanzhengma/")
      //三级酒水饮料验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("jiushuiyinliaoyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/jiushuiyinliaoyanzhengma/")
      //三级美妆个护验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("meizhuanggehuyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/meizhuanggehuyanzhengma/")
      //三级母婴玩具验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("muyingwanjuyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/muyingwanjuyanzhengma/")
      //三级食品生鲜验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("shipinshengxianyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/shipinshengxianyanzhengma/")
      //三级手机数码验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("shoujishumayanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/shoujishumayanzhengma/")
      //三级图书文娱验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("tushuwenyuyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/tushuwenyuyanzhengma/")
      //三级医疗保健验证码
      dsYzmRDD.filter(tuple => {
        if(tuple._1.equals("yiyaobaojianyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/yiyaobaojianyanzhengma/")
      dsYzmRDD.unpersist()
      originDsYzmRDD.unpersist()

      //二级房地产验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("fangdichanyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/fangdichanyanzhengma/")
      //二级服务业验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("fuwuyeyanzhengma")) {true} else {false}
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level"+sf.format(startTime).replace("-","/")+"/second/fuwuyeyanzhengma/")
      //二级健康验证码
      val originJkYzmRDD = yzmRDD.filter(tuple => {
        if(tuple._1.equals("jiankangyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  })
      originJkYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      originJkYzmRDD.coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/jiankangyanzhengma/")
      val jkYzmRDD = originJkYzmRDD.map(line =>{
        val content = line.split(",").apply(1)
        val jkYzmModel = thirdJkyzmModel.value
        val word_array = thirdJkyzmArray.value
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
        val result = jkYzmModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      jkYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      //三级健身验证码
      jkYzmRDD.filter(tuple => {
        if(tuple._1.equals("jianshenyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level"+sf.format(startTime).replace("-","/")+"/third/jianshenyanzhengma/")
      //三级养老养生验证码
      jkYzmRDD.filter(tuple => {
        if(tuple._1.equals("yanglaoyangshengyanzhengma,")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level"+sf.format(startTime).replace("-","/")+"/third/yanglaoyangshengyanzhengma,/")
      jkYzmRDD.unpersist()
      originJkYzmRDD.unpersist()

      //二级教育验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("jiaoyuyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level"+sf.format(startTime).replace("-","/")+"/second/jiaoyuyanzhengma/")
      //二级金融验证码
      val originJrYzmRdd = yzmRDD.filter(tuple => {
        if(tuple._1.equals("jinrongyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  })
      originJrYzmRdd.persist(StorageLevel.MEMORY_ONLY)
      originJrYzmRdd.coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/jinrongyanzhengma/")
      val jrYzmRDD = originJrYzmRdd.map(line=>{
        val content = line.split(",").apply(1)
        val jrYzmModel = thirdJryzmModel.value
        val word_array = thirdJryzmArray.value
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
        val result = jrYzmModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      jrYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      //三级保险验证码
      jrYzmRDD.filter(tuple => {
        if(tuple._1.equals("baoxianyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/baoxianyanzhengma/")
      //三级贷款验证码
      val orginDkYzmRdd = jrYzmRDD.filter(tuple => {
        if(tuple._1.equals("daikuanyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  })
      orginDkYzmRdd.persist(StorageLevel.MEMORY_ONLY)
      orginDkYzmRdd.coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/daikuanyanzhengma/")
      val dkYzmRDD = orginDkYzmRdd.map(line =>{
        val content = line.split(",").apply(1)
        val dkYzmModel = fourDkyzmModel.value
        val word_array = fourDkyzmArray.value
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
        val result = dkYzmModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      dkYzmRDD.persist(StorageLevel.MEMORY_ONLY)
      //四级平安个贷验证码
      dkYzmRDD.filter(tuple => {
        if(tuple._1.equals("pingangedaiyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/four/pingangedaiyanzhengma/")
      //四级网贷验证码
      dkYzmRDD.filter(tuple => {
        if(tuple._1.equals("wangdaiyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/four/wangdaiyanzhengma/")
      //四级银行贷款验证码
      dkYzmRDD.filter(tuple => {
        if(tuple._1.equals("yinhangdaikuanyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/four/yinhangdaikuanyanzhengma/")
      dkYzmRDD.unpersist()
      orginDkYzmRdd.unpersist()

      //三级理财验证码
      jrYzmRDD.filter(tuple => {
        if(tuple._1.equals("licaiyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/licaiyanzhengma/")
      //三级信用卡验证码
      jrYzmRDD.filter(tuple => {
        if(tuple._1.equals("xinyongkayanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/xinyongkayanzhengma/")
      jrYzmRDD.unpersist()
      originJrYzmRdd.unpersist()

      //二级汽车验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("qicheyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/qicheyanzhengma/")
      //二级其他验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("qitayanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/qitayanzhengma/")
      //二级医疗验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("yiliaoyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/yiliaoyanzhengma/")
      //二级游戏验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("youxiyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/youxiyanzhengma/")
      //二级运输验证码
      yzmRDD.filter(tuple => {
        if(tuple._1.equals("yunshuyanzhengma")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/yunshuyanzhengma/")
      yzmRDD.unpersist()
      originYzmRDD.unpersist()

      //-----------------------------------------------------------------------------------------------------
      //-------------------------通知--通知---通知--通知--通知--通知--通知--通知--通知--通知-------------------
      //-----------------------------------------------------------------------------------------------------
      //一级通知
      val originTzRDD  = jsonRDD.filter(tuple => {
        if(tuple._1.equals("tongzhi")) {
          true
        } else {
          false
        }
      }).map(tuple =>{
        tuple._1+","+tuple._2.replace(",","，")
      })
      originTzRDD.persist(StorageLevel.MEMORY_ONLY)
      originTzRDD.coalesce(30).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/first/tongzhi/")
      val tzRDD = originTzRDD.map(line => {
        val content = line.split(",").apply(1)
        val tzModel = secondTzmModel.value
        val word_array = secondTzArray.value
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
        val result = tzModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      tzRDD.persist(StorageLevel.MEMORY_ONLY)
      //二级餐饮通知
      val originCyTzRDD = tzRDD.filter(tuple => {
        if(tuple._1.equals("canyintongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  })
      originCyTzRDD.persist(StorageLevel.MEMORY_ONLY)
      originCyTzRDD.coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/canyintongzhi/")
      val cyTzRDD = originCyTzRDD.map(line =>{
        val content = line.split(",").apply(1)
        val cyModel = thirdCytzModel.value
        val word_array = thirdCytzArray.value
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
        val result = cyModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      cyTzRDD.persist(StorageLevel.MEMORY_ONLY)
      //三级外卖通知
      cyTzRDD.filter(tuple => {
        if(tuple._1.equals("waimaitongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/waimaitongzhi/")
      //三级饭店通知
      cyTzRDD.filter(tuple => {
        if(tuple._1.equals("fandiantongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/fandiantongzhi/")
      cyTzRDD.unpersist()
      originCyTzRDD.unpersist()

      //二级电商通知
      val originDsTzRDD = tzRDD.filter(tuple => {
        if(tuple._1.equals("dianshangtongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  })
      originDsTzRDD.persist(StorageLevel.MEMORY_ONLY)
      originDsTzRDD.coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/dianshangtongzhi/")
      val dsTzRDD = originDsTzRDD.map(line =>{
        val content = line.split(",").apply(1)
        val dsModel = thirdDstzModel.value
        val word_array = thirdDstzArray.value
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
        val result = dsModel.predict(Vectors.dense(crrentArray))
        val msgSort = signMap.get(result.toInt)
        (msgSort,content)
      })
      dsTzRDD.filter(tuple => {
        if(tuple._1.equals("fandiantongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/third/fandiantongzhi/")


      //二级房地产通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("fangdichantongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/fangdichantongzhi/")
      //二级服务业通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("fuwuyetongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/fuwuyetongzhi/")
      //二级健康通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("jiankangtongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/jiankangtongzhi/")
      //二级教育通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("jiaoyutongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/jiaoyutongzhi/")
      //二级金融通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("jinrongtongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/jinrongtongzhi/")
      //二级汽车通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("qichetongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/qichetongzhi/")
      //二级其他通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("qitatongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/qitatongzhi/")
      //二级医疗通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("yiliaotongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/yiliaotongzhi/")
      //二级游戏通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("youxitongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/youxitongzhi/")
      //二级运输通知
      tzRDD.filter(tuple => {
        if(tuple._1.equals("yunshutongzhi")) {  true } else {  false  }
      }).map(tuple =>{  tuple._1+","+tuple._2  }).coalesce(10).saveAsTextFile("hdfs://cr/data/level/"+sf.format(startTime).replace("-","/")+"/second/yunshutongzhi/")
      tzRDD.unpersist()
      originTzRDD.unpersist()


      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }





}
