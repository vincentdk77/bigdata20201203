package com.kemai

import java.util

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.chuangrui.utils.JSONUtils
import com.kemai.mango.{MangoHandle, MongoUtils}
import com.kemai.recommend.{GetCategory, RecommendUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.bson.types.ObjectId

/**
 * Created by JarvisKwok
 * Date :2020/10/22
 * Description :
 */
object RecommendTest {

	def main(args: Array[String]): Unit = {
		Logger.getLogger("org").setLevel(Level.ERROR)

		val conf: SparkConf = new SparkConf().setAppName(s"${this.getClass.getSimpleName}").setMaster("local[*]")
		val sc = new SparkContext(conf)

		// 广播dirver端的变量
		val bc_score = sc.broadcast(60)
		val bc_categoryList = sc.broadcast(GetCategory.getFinalCategoryList)

		val inputRDD: RDD[String] = sc.textFile("bigdata/convert/*")

		val tupleRDD: RDD[(String, JSONObject)] = inputRDD.map(line => {
			try {
				val tableName = line.substring(0, line.indexOf("|")).trim()
				val content = line.substring(line.indexOf("{"), line.length())
				val json = JSONUtils.getNotNullJson(content) // 简单的数据格式处理
				json.put("tableName", tableName);
				val entId = json.getString("entId")
				(entId, json)
			} catch {
				case ex: Exception => (null, null)
			}
		})
			.filter(t => !StringUtils.isEmpty(t._1))

		import scala.collection.JavaConversions._

		/**
		 * 对entId进行聚合，然后遍历迭代器，将同一企业在不同表的JSON数据（需去重）放入JSONArray中
		 * 再根据表名为key，JSONArray为值，构建map集合
		 * 最后map转成一个大的JSONObject
		 * 格式：{"ent": [{"entId": "5eaa3d2d581f7afded866558",...}，...}]，"ent_recruit": [{"entId": "5eaa3d2d581f7afded866558",...}],...}
		 */
		val jsonRDD: RDD[JSONObject] = tupleRDD.groupByKey().map(t => {
			var set = new util.HashSet[String]()
			var map = new util.HashMap[String, JSONArray]()

			for (json <- t._2) {
				val tableName = json.getString("tableName")
				if (map.get(tableName) == null) {
					val array = new JSONArray()
					array.add(json)
					set.add(json.toJSONString)
					map.put(tableName, array)
				} else {
					val array = map.get(tableName)
					if (!set.contains(json.toJSONString)) {
						array.add(json)
						set.add(json.toJSONString)
					}
					map.put(tableName, array)
				}
			}

			var finalJson = new JSONObject();
			for (key <- map.keySet()) {
				finalJson.put(key, map.get(key))
			}
			finalJson
		})
			.filter(_.getJSONArray("ent") != null)
			.map(MangoHandle.merge)
			.map(MangoHandle.customCategory(_, bc_categoryList.value))
			.map(MangoHandle.products)
			.map(MangoHandle.corpStatusString)
			.map(MangoHandle.agentType)
			.map(MangoHandle.check)
			.map(MangoHandle.setMangoId)
			.map(_.getJSONArray("ent").getJSONObject(0))
			.filter(json => StringUtils.isNotEmpty(json.getString("opScope")))
			.filter(json => {
				val corpStatusString = json.getString("corpStatusString")
				if (StringUtils.isNotEmpty(corpStatusString)) {
					if (corpStatusString.startsWith("注销") || corpStatusString.startsWith("吊销")) {
						false
					} else {
						true
					}
				} else {
					true
				}
			})


		val mongoRDD: RDD[(String, JSONObject)] = jsonRDD.map(json => {

			val industryCategory = json.getString("industryCategory")
			if (StringUtils.isNotEmpty(industryCategory)) {
				val words: Array[String] = industryCategory.split(",")
				if (words(0).equals("批发和零售业")) {
					val newKey = scala.util.Random.nextInt(10) + "_" + words(0)
					(newKey, json)
				} else if (words(0).equals("制造业")) {
					val newKey = scala.util.Random.nextInt(10) + "_" + words(0)
					(newKey, json)
				}
				else {
					(words(0), json)
				}
			} else {
				(null, null)
			}
		})
			.filter(t => !StringUtils.isEmpty(t._1))

		val category2count: collection.Map[String, Long] = mongoRDD.countByKey()
		category2count.foreach(println)
		val size = mongoRDD.countByKey().size
		println(size)


		val similarRDD = mongoRDD.groupByKey().map(t => {

			val list = new util.ArrayList[JSONObject]()
			// 将迭代器的数据全部存在list集合中
			for (json <- t._2) {
				list.add(json)
			}

			// 指定EntId对应的相似企业：（EntId1，EntId2|finalSimilar）
			val map: util.Map[String, util.HashSet[String]] = RecommendUtil.getRecommend(list, bc_score.value)
			(t._1, map)
		})
		similarRDD.collect().foreach(println)

		val value1 = similarRDD.map(t => {
			var key = ""
			if (t._1.contains("_")) {
				key = t._1.split("_")(1)
			} else {
				key = t._1
			}

			val value = t._2
			(key, value)
		})

		val value2 = value1.groupByKey().flatMap(t => {

			val map = new util.HashMap[String, util.HashSet[String]]()
			for (e <- t._2) {
				for (key <- e.keySet()) {
					map.put(key, e.get(key))
				}
			}

			val array = new Array[(String, util.HashSet[String])](map.size());
			var i = 0;
			for (key <- map.keySet()) {
				array(i) = (key, map.get(key))
				i = i + 1
			}
			array
		})
		value2.collect().foreach(println)

		//		similarRDD.take(1).foreach(print)
		//		similarRDD.mapPartitions(similarFunc).count()
		sc.stop()
	}


	def similarFunc(iter: Iterator[(String, util.HashSet[String])]): Iterator[(String, util.HashSet[String])] = {
		var index = 0
		var map = new util.HashMap[String, util.Collection[JSONObject]]
		var list = new util.ArrayList[JSONObject]

		while (iter.hasNext) {
			val e = iter.next;
			val entId = e._1
			val similarId = e._2.toString
			var json = new JSONObject()
			json.put("_id", new ObjectId(entId)) // 将entId作为主键
			json.put("entId", entId);
			json.put("similarId", similarId)
			list.add(json)
			index = index + 1

			if (index % 500 == 0) {
				map.put("ent_similar", list)
				MongoUtils.batchInsert(map)
				map.clear();
				list.clear()
			}
		}
		if (list.size() > 0) {
			map.put("ent_similar", list)
			MongoUtils.batchInsert(map)
			map.clear();
			list.clear()
		}
		iter
	}
}
