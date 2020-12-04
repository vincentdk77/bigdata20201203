package com.kemai

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by JarvisKwok
 * Date :2020/10/9
 * Description : 日志转换
 */
object Transform {
	def main(args: Array[String]): Unit = {
		if (args.length != 1) {
			println(
				"""
				  |----------please enter params:----------
				  |- isRerun：是否重新执行之前数据（yes or no）
				  |- date：日志日期
				  |- tableName: 表名
                """.stripMargin)
			sys.exit()
		}
//		var Array(isRerun, date, tableName) = args
		var Array(tableName) = args

//		if (isRerun.equals("no")) {
//			val format = new SimpleDateFormat("yyyy-MM-dd")
//			date = format.format(new Date())
//		}

		val conf = new SparkConf().setAppName(s"${this.getClass.getSimpleName}")
		val sc = new SparkContext(conf)

		//源数据ent：{"_id":{"$oid":"5f856f0f8034b3c2d705f647"},"uniscId":"91211103MA10E6140W","entTypeCN":"有限责任公司（自然人投资或控股）","entName":"盘锦信朋缘劳务有限公司","corpStatusString":"存续（在营、开业、在册）","estDate":"2020-06-04","apprDate":"2020-06-04","regOrgCn":"盘锦市兴隆台区市场监督管理局","regCapCurCN":"人民币","regCaption":100.0,"regCap":"壹佰万元整","legalName":"张艳阳","opFrom":"2020-06-04","opTo":"2040-06-03","regProv":"辽宁省","regCity":"盘锦市","regDistrict":"兴隆台区","opScope":"许可项目：建筑劳务分包，住宅室内装饰装修，各类工程建设活动，消防设施工程（依法须经批准的项目，经相关部门批准后方可开展经营活动，具体经营项目以审批结果为准） 一般项目：园林绿化工程施工，家政服务，五金产品零售，日用百货销售，家用电器销售，建筑装饰材料销售，机械设备租赁，消防器材销售（除依法须经批准的项目外，凭营业执照依法自主开展经营活动）","dom":"辽宁省盘锦市兴隆台区泰山路东、大众街北王家荒回迁楼3#11003","source":"爱企查公众号","spider":"aiqichaapp","q":91,"hidden":0,"createdAt":1602580239,"entId":"5f856f0f8034b3c2d705f647","desc":"盘锦信朋缘劳务有限公司成立于2020年6月4日，法定代表人为张艳阳，注册资本为100.0万元,统一社会信用代码为91211103MA10E6140W，企业地址位于辽宁省盘锦市兴隆台区泰山路东、大众街北王家荒回迁楼3#11003，经营范围包括：许可项目：建筑劳务分包，住宅室内装饰装修，各类工程建设活动，消防设施工程（依法须经批准的项目，经相关部门批准后方可开展经营活动，具体经营项目以审批结果为准） 一般项目：园林绿化工程施工，家政服务，五金产品零售，日用百货销售，家用电器销售，建筑装饰材料销售，机械设备租赁，消防器材销售（除依法须经批准的项目外，凭营业执照依法自主开展经营活动）。盘锦信朋缘劳务有限公司目前的经营状态为存续（在营、开业、在册）。"}
		//目标数据ent: ent|{"_id":{"$oid":"5f856f0f8034b3c2d705f647"},"uniscId":"91211103MA10E6140W","entTypeCN":"有限责任公司（自然人投资或控股）","entName":"盘锦信朋缘劳务有限公司","corpStatusString":"存续（在营、开业、在册）","estDate":"2020-06-04","apprDate":"2020-06-04","regOrgCn":"盘锦市兴隆台区市场监督管理局","regCapCurCN":"人民币","regCaption":100.0,"regCap":"壹佰万元整","legalName":"张艳阳","opFrom":"2020-06-04","opTo":"2040-06-03","regProv":"辽宁省","regCity":"盘锦市","regDistrict":"兴隆台区","opScope":"许可项目：建筑劳务分包，住宅室内装饰装修，各类工程建设活动，消防设施工程（依法须经批准的项目，经相关部门批准后方可开展经营活动，具体经营项目以审批结果为准） 一般项目：园林绿化工程施工，家政服务，五金产品零售，日用百货销售，家用电器销售，建筑装饰材料销售，机械设备租赁，消防器材销售（除依法须经批准的项目外，凭营业执照依法自主开展经营活动）","dom":"辽宁省盘锦市兴隆台区泰山路东、大众街北王家荒回迁楼3#11003","source":"爱企查公众号","spider":"aiqichaapp","q":91,"hidden":0,"createdAt":1602580239,"entId":"5f856f0f8034b3c2d705f647","desc":"盘锦信朋缘劳务有限公司成立于2020年6月4日，法定代表人为张艳阳，注册资本为100.0万元,统一社会信用代码为91211103MA10E6140W，企业地址位于辽宁省盘锦市兴隆台区泰山路东、大众街北王家荒回迁楼3#11003，经营范围包括：许可项目：建筑劳务分包，住宅室内装饰装修，各类工程建设活动，消防设施工程（依法须经批准的项目，经相关部门批准后方可开展经营活动，具体经营项目以审批结果为准） 一般项目：园林绿化工程施工，家政服务，五金产品零售，日用百货销售，家用电器销售，建筑装饰材料销售，机械设备租赁，消防器材销售（除依法须经批准的项目外，凭营业执照依法自主开展经营活动）。盘锦信朋缘劳务有限公司目前的经营状态为存续（在营、开业、在册）。"}
		sc.textFile("hdfs://jtb/mongodata/" + tableName + "/2020-11-21")
			.map(tableName + "|" + _)
			.saveAsTextFile("hdfs://jtb/transform/" + tableName + "/2020-11-21")
		sc.stop()
	}
}
