package com.chuangrui.version_3.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CreateLevelCmd {

    public static HashMap<String,String> signWordNumber = new HashMap<String, String>();
    static {
//        signWordNumber.put("tongzhi","2-41000");
//        signWordNumber.put("yanzhengma","2-35000");
//        signWordNumber.put("yingxiao","2-40000");

//        signWordNumber.put("canyintongzhi","3-10000");
//        signWordNumber.put("dianshangtongzhi","3-20000");
//        signWordNumber.put("jiankangtongzhi","3-10000");
//        signWordNumber.put("jinrongtongzhi","3-20000");
//        signWordNumber.put("qitatongzhi","3-10000");
//        signWordNumber.put("canyinyanzhengma","3-10000");
//        signWordNumber.put("dianshangyanzhengma","3-10000");
//        signWordNumber.put("jiankangyanzhengma","3-10000");
//        signWordNumber.put("jinrongyanzhengma","3-10000");
//        signWordNumber.put("canyin","3-10000");
//        signWordNumber.put("dianshang","3-20000");
//        signWordNumber.put("jiankang","3-10000");
//        signWordNumber.put("jinrong","3-10000");
//        signWordNumber.put("putongyingxiao","3-10000");

        signWordNumber.put("daikuantongzhi","4-15000");
        signWordNumber.put("daikuanyanzhengma","4-15000");
//        signWordNumber.put("daikuan","4-10000");
//        signWordNumber.put("maichang","4-10000");

//        signWordNumber.put("pingangedaitongzhi","5-10000");
//        signWordNumber.put("wangdaitongzhi","5-10000");
//        signWordNumber.put("yinhangdaikuantongzhi","5-10000");
    }

    public static void main(String args[]) {
//        createLevelCmd();
        createSplitMessageCmd();
//
//        deleteYingxiao();

//        saveToHbase();
    }

    private static void saveToHbase() {


//        String startdate = "2019-02-18";
//        String endDate = "2019-05-30";

//        String startdate = "2019-07-13";
//        String endDate = "2019-10-19";

        String startdate = "2019-10-21";
        String endDate = "2019-11-06";

//        String startdate = "2020-01-18";
//        String endDate = "2020-03-16";

        String level = "four";
        String sign = "wangdai";
        Integer executor = 12;
        System.out.println("nohup spark-submit  --executor-memory 6G --executor-cores 4 --num-executors "+executor+"  --class com.chuangrui.version_3.hbase.SaveSignToHbase  --master yarn --deploy-mode client      /home/jar/bigdata-1.2-SNAPSHOT.jar  "+startdate+" "+endDate+"  "+executor*4+"  "+sign+"  "+level+" >> /home/SaveSignToHbase-"+sign+".log 2>&1 &");

    }


    private static void deleteYingxiao() {
        String date = "2019-03-23";
        String level = "4";
//        String level = "2";
        try {
//            HashMap<String, Integer> sign = ConnectionSignMgrUtils.getSign(2, 3);
            HashMap<String, Integer> sign = ConnectionSignMgrUtils.getSign(4, 106);

            sign.putAll(ConnectionSignMgrUtils.getSign(4, 154));
//            sign.putAll(ConnectionSignMgrUtils.getSign(3, 45));
//            sign.putAll(ConnectionSignMgrUtils.getSign(3, 74));
//            sign.putAll(ConnectionSignMgrUtils.getSign(3, 51));

            for(String key : sign.keySet()) {
                String cmd = "hdfs dfs -rm -r /data/level/";
                if(level.equals("2")) {
                    cmd = cmd+"second/";
                } else if(level.equals("3")) {
                    cmd = cmd+"third/";
                }else if(level.equals("4")) {
                    cmd = cmd+"four/";
                }else if(level.equals("5")) {
                    cmd = cmd+"five/";
                }
                cmd = cmd+key+"/"+date.replace("-","/")+"/";
                System.out.println(cmd);
            }
        } catch (Exception e) {

        }
    }


    public static void createSplitMessageCmd() {
        String startdate = "2020-03-17";
        String endDate = "2020-05-10";
        String ss [] = {"First","Second","Third","Four","Five"};

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long start = format.parse(startdate).getTime();
            long end = format.parse(endDate).getTime();
            for(String level:ss) {
                System.out.println("nohup spark-submit  --executor-memory 8G --executor-cores 4 --num-executors 8 --class com.chuangrui.version_3.message."+level+"SplitMessage  --master yarn --deploy-mode client      /home/jar/bigdata-1.2-SNAPSHOT.jar  "+startdate+" "+endDate+"  64  100 >> /home/getFromMysql/"+level+"SplitMessage.log 2>&1 &");
            }
            for(String level:ss) {
                System.out.println("nohup spark-submit  --executor-memory 4G --executor-cores 4 --num-executors 8  --class com.chuangrui.version_3.message.Save"+level+"MessageToMysql  --master yarn --deploy-mode client      /home/jar/bigdata-1.2-SNAPSHOT.jar  "+startdate+" "+endDate+"  128  100 >> /home/getFromMysql/Save"+level+"MessageToMysql.log 2>&1 &");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void  createLevelCmd() {
        String date = "2020-03-23";
        System.out.println("nohup spark-submit  --executor-memory 2G --executor-cores 2 --num-executors 3 --class com.chuangrui.version_3.message.FirstMessageSortModel  --master yarn --deploy-mode client      /home/jar/bigdata-1.2-SNAPSHOT.jar  "+date+"  36000   >> /home/getFromMysql/FirstMessageSortModel.log 2>&1 &");
        for(String key : signWordNumber.keySet()) {
            String ss[] = signWordNumber.get(key).split("-");
            System.out.println("nohup spark-submit  --executor-memory 2G --executor-cores 3 --num-executors 3 --class com.chuangrui.version_3.message.LevelMessageSortModel  --master yarn --deploy-mode client      /home/jar/bigdata-1.2-SNAPSHOT.jar  "+date+"  "+ss[1]+"  "+ss[0]+"  "+key+"  >> /home/getFromMysql/LevelMessageSortModel.log 2>&1 &");
        }
    }
}
