package  com.chuangrui.test

import java.text.SimpleDateFormat

import com.chuangrui.utils.{JSONUtils, MessageDataHandle}
import org.apache.commons.lang.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel

object Test {
  def main(args: Array[String]): Unit = {
    val hdfs = "hdfs://cr"
    val conf = new SparkConf().setAppName("Test")
    conf.set("spark.default.parallelism","40")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val startTimeStr = args.apply(0)
    val partition = args.apply(1).toInt
    val sf = new SimpleDateFormat("yyyy-MM-dd")
    var startTime = sf.parse(startTimeStr).getTime

    val inputRDD = sc.textFile(hdfs+"/data/message/msg/"+sf.format(startTime).replace("-","/")+"/*.txt.gz").repartition(partition)
    inputRDD.persist(StorageLevel.MEMORY_AND_DISK)
    val originCount:Long  = inputRDD.count();
    val count:Long = inputRDD.filter(line =>{
      val json = JSONUtils.getJson(line)
      val content = json.getString("content")
      if(StringUtils.isEmpty(content)) {
        false
      } else {
        true
      }
    }).count()

    System.out.println("originCount originCount originCount originCount originCount originCount:"+originCount )
    System.out.println("count count count count count count:"+count )
  }
}