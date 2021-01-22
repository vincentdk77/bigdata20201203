import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object TestLoadFile {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]").appName("Test").getOrCreate()

    val sc = spark.sparkContext

//    val path = ""
//    val inputRDD: RDD[String] = sc.textFile(path, 4)
//    val makeRDD: RDD[Int] = sc.makeRDD(Array(1, 2, 3, 4), 4)
//    val frame: DataFrame = spark.read.format("jdbc").load(path)


    val path = "files/*"
    val inputRDD: RDD[String] = sc.textFile(path)
    inputRDD.collect().foreach(println(_))

  }


}
