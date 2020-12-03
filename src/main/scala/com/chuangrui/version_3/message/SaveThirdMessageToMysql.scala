package com.chuangrui.version_3.message

import java.text.SimpleDateFormat
import java.util

import com.alibaba.fastjson.JSONObject
import com.chuangrui.node.GetMysqlByNode1
import com.chuangrui.test.PaoDingCut
import com.chuangrui.utils.JSONUtils
import com.chuangrui.version_3.utils.ConnectionSignMgrUtils
import org.apache.hadoop.io.compress.GzipCodec
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object SaveThirdMessageToMysql {

  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("SaveThirdMessageToMysql")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val canyinId = ConnectionSignMgrUtils.getSignIdByCode("canyin")
    val canyinSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,canyinId);
    val cyMap = sc.broadcast(canyinSignMap)

  val dianshangId = ConnectionSignMgrUtils.getSignIdByCode("dianshang")
  val dianshangSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,dianshangId);
  val dsMap = sc.broadcast(dianshangSignMap)

  val jinrongId = ConnectionSignMgrUtils.getSignIdByCode("jinrong")
  val jinrongSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,jinrongId);
  val jrMap = sc.broadcast(jinrongSignMap)

  val jiankangId = ConnectionSignMgrUtils.getSignIdByCode("jiankang")
  val jiankangSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,jiankangId);
  val jkMap = sc.broadcast(jiankangSignMap)

  val putongyingxiaoId = ConnectionSignMgrUtils.getSignIdByCode("putongyingxiao")
  val putongyingxiaoSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,putongyingxiaoId);
  val ptyxMap = sc.broadcast(putongyingxiaoSignMap)


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

  //三级普通营销加载  		putongyingxiao
  val thirdPtyxMaxTime = ConnectionSignMgrUtils.selectMaxTime("putongyingxiao",3)
  val thirdPtyxMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/putongyingxiao/"+thirdPtyxMaxTime.replace("-","/")+"/")
  val thirdPtyxWordArray:Array[String] = ConnectionSignMgrUtils.getWords("putongyingxiao",3)
  System.out.println("thirdPtyxWordArray thirdPtyxWordArray thirdPtyxWordArray:"+thirdPtyxWordArray.length)
  val thirdPtyxArray = sc.broadcast(thirdPtyxWordArray)
  val thirdPtyxModel = sc.broadcast(thirdPtyxMsgModel)


  val canyintongzhiId = ConnectionSignMgrUtils.getSignIdByCode("canyintongzhi")
  val canyintongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,canyintongzhiId);
  val cytzMap = sc.broadcast(canyintongzhiSignMap)

  val dianshangtongzhiId = ConnectionSignMgrUtils.getSignIdByCode("dianshangtongzhi")
  val dianshangtongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,dianshangtongzhiId);
  val dstzMap = sc.broadcast(dianshangtongzhiSignMap)

  val jinrongtongzhiId = ConnectionSignMgrUtils.getSignIdByCode("jinrongtongzhi")
  val jinrongtongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,jinrongtongzhiId);
  val jrtzMap = sc.broadcast(jinrongtongzhiSignMap)

  val jiankangtongzhiId = ConnectionSignMgrUtils.getSignIdByCode("jiankangtongzhi")
  val jiankangtongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,jiankangtongzhiId);
  val jktzMap = sc.broadcast(jiankangtongzhiSignMap)

  val qitatongzhiId = ConnectionSignMgrUtils.getSignIdByCode("qitatongzhi")
  val qitatongzhiSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,qitatongzhiId);
  val qttzMap = sc.broadcast(qitatongzhiSignMap)

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


  val canyinyanzhengmaId = ConnectionSignMgrUtils.getSignIdByCode("canyinyanzhengma")
  val canyinyanzhengmaSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,canyinyanzhengmaId);
  val cyyzmMap = sc.broadcast(canyinyanzhengmaSignMap)

  val dianshangyanzhengmaId = ConnectionSignMgrUtils.getSignIdByCode("dianshangyanzhengma")
  val dianshangyanzhengmaSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,dianshangyanzhengmaId);
  val dsyzmMap = sc.broadcast(dianshangyanzhengmaSignMap)

  val jinrongyanzhengmaId = ConnectionSignMgrUtils.getSignIdByCode("jinrongyanzhengma")
  val jinrongyanzhengmaSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,jinrongyanzhengmaId);
  val jryzmMap = sc.broadcast(jinrongyanzhengmaSignMap)

  val jiankangyanzhengmaId = ConnectionSignMgrUtils.getSignIdByCode("jiankangyanzhengma")
  val jiankangyanzhengmaSignMap:util.HashMap[Integer,String] =ConnectionSignMgrUtils.getSignId(3,jiankangyanzhengmaId);
  val jkyzmMap = sc.broadcast(jiankangyanzhengmaSignMap)

  //三级餐饮验证码加载  		canyinyanzhengma
  val thirdCyyzmMaxTime = ConnectionSignMgrUtils.selectMaxTime("canyinyanzhengma",3)
  val thirdCyyzmMsgModel = NaiveBayesModel.load(sc, hdfs+"/model/version3/third/canyinyanzhengma/"+thirdCyyzmMaxTime.replace("-","/")+"/")
  val thirdCyyzmWordArray:Array[String] = ConnectionSignMgrUtils.getWords("canyinyanzhengma",3)
  System.out.println("thirdCyyzmWordArray thirdCyyzmWordArray thirdCyyzmWordArray:"+thirdCyyzmWordArray.length)
  val thirdCyyzmArray = sc.broadcast(thirdCyyzmWordArray)
  val thirdCyyzmModel = sc.broadcast(thirdCyyzmMsgModel)

  //三级电商验证码加载  		dianshangyanzhengma
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

    val timeStr = args.apply(0)
    val endStr = args.apply(1)
    val partition = args.apply(2).toInt
    val executeCode = args.apply(3)
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(timeStr).getTime
    val endTime = sf.parse(endStr).getTime

    while (startTime <= endTime) {

      if(executeCode.charAt(0) == '1') {
        //餐饮
        val cyinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/canyin/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val cyRDD: RDD[String] = cyinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdCyArray.value
          val msgModel = thirdCyModel.value
          val signMap = cyMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        cyRDD.mapPartitions(saveMysql).count()

        //电商
        val dsinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/dianshang/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val dsRDD: RDD[String] = dsinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdDsArray.value
          val msgModel = thirdDsModel.value
          val signMap = dsMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        dsRDD.mapPartitions(saveMysql).count()

        //健康
        val jkinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/jiankang/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val jkRDD: RDD[String] = jkinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdJkArray.value
          val msgModel = thirdJkModel.value
          val signMap = jkMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        jkRDD.mapPartitions(saveMysql).count()



        //普通营销
        val ptyxinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/putongyingxiao/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val ptyxRDD: RDD[String] = ptyxinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdPtyxArray.value
          val msgModel = thirdPtyxModel.value
          val signMap = ptyxMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        ptyxRDD.mapPartitions(saveMysql).count()

        //金融
        val jrinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/jinrong/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val jrRDD: RDD[String] = jrinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdJrArray.value
          val msgModel = thirdJrModel.value
          val signMap = jrMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        jrRDD.mapPartitions(saveMysql).count()
      }

      if(executeCode.charAt(1) == '1') {
        //      餐饮通知
        val cytzinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/canyintongzhi/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val cytzRDD: RDD[String] = cytzinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdCytzArray.value
          val msgModel = thirdCytzModel.value
          val signMap = cytzMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        cytzRDD.mapPartitions(saveMysql).count()

        //      电商通知
        val dstzinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/dianshangtongzhi/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val dstzRDD: RDD[String] = dstzinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdDstzArray.value
          val msgModel = thirdDstzModel.value
          val signMap = dstzMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        dstzRDD.mapPartitions(saveMysql).count()

        //      健康通知
        val jktzinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/jiankangtongzhi/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val jktzRDD: RDD[String] = jktzinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdJktzArray.value
          val msgModel = thirdJktzModel.value
          val signMap = jktzMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        jktzRDD.mapPartitions(saveMysql).count()

        //      金融通知
        val jrtzinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/jinrongtongzhi/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val jrtzRDD: RDD[String] = jrtzinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdJrtzArray.value
          val msgModel = thirdJrtzModel.value
          val signMap = jrtzMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        jrtzRDD.mapPartitions(saveMysql).count()

        //      其他通知
        val qttzinputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/qitatongzhi/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val qttzRDD: RDD[String] = qttzinputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdQttzArray.value
          val msgModel = thirdQttzModel.value
          val signMap = qttzMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        qttzRDD.mapPartitions(saveMysql).count()
      }


      if(executeCode.charAt(2) == '1') {
        //      餐饮验证码
        val cyyzminputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/canyinyanzhengma/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val cyyzmRDD: RDD[String] = cyyzminputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdCyyzmArray.value
          val msgModel = thirdCyyzmModel.value
          val signMap = cyyzmMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        cyyzmRDD.mapPartitions(saveMysql).count()

        //      电商验证码
        val dsyzminputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/dianshangyanzhengma/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val dsyzmRDD: RDD[String] = dsyzminputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdDsyzmArray.value
          val msgModel = thirdDsyzmModel.value
          val signMap = dsyzmMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        dsyzmRDD.mapPartitions(saveMysql).count()

        //      健康验证码
        val jkyzminputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/jiankangyanzhengma/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val jkyzmRDD: RDD[String] = jkyzminputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdJkyzmArray.value
          val msgModel = thirdJkyzmModel.value
          val signMap = jkyzmMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        jkyzmRDD.mapPartitions(saveMysql).count()

        //      金融验证码
        val jryzminputRDD = Utils.handleInputRDD(sc.textFile(hdfs + "/data/level/second/jinrongyanzhengma/" + sf.format(startTime).replace("-", "/") + "/*.gz").repartition(partition))
        val jryzmRDD: RDD[String] = jryzminputRDD.map(line => {
          var json: JSONObject = JSONUtils.getJson(line)
          val content = json.getString("content")
          val signCode = json.getString("sign");
          val word_array = thirdJryzmArray.value
          val msgModel = thirdJryzmModel.value
          val signMap = jryzmMap.value
          val wordList = PaoDingCut.cutString(json.getString("sign") + content)
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
          val s = msgSort + "," + signCode + GetMysqlByNode1.replace(content).replace(",", "，").replace("'", "")
          s
        })
        jryzmRDD.mapPartitions(saveMysql).count()
      }

      System.out.println("end time time:"+sf.format(startTime));
      startTime = startTime+24*3600*1000l;

    }
  }

  def saveMysql(iter: Iterator[String]) :  Iterator[String] = {
    var index:Int = 0
    var list:util.ArrayList[String] = new util.ArrayList()

    while (iter.hasNext) {
      val e = iter.next;
      list.add(e)
      index = index+1
      if(index % 5000 == 0) {
        ConnectionSignMgrUtils.saveTestSuit(list)
        list.clear();
      }
    }
    if(list.size()>0) {
      ConnectionSignMgrUtils.saveTestSuit(list )
      list.clear();
    }
    iter
  }



}
