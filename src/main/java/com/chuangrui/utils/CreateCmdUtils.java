package com.chuangrui.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateCmdUtils {

    public static void main(String args[]) {

//        for(int i=0;i<360;i++) {
//            String pre = "";
//            String is = i+"";
//            if(is.length() ==1) {
//                pre = "0000"+is;
//            } else if(is.length() ==2) {
//                pre ="000"+is;
//            } else if(is.length() ==3){
//                pre ="00"+is;
//            } else {
//                pre = "0"+is;
//            }
//            String cmd = " cat ./part-"+pre +" >> ./03.txt";
//            System.out.println(cmd);
//        }

//        String startDateStr = "2019-01-30";
//        String endDateStr = "2019-03-06";
        String startDateStr = "2017-11-08";
        String endDateStr = "2017-12-19";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long start = format.parse(startDateStr).getTime();
            long end = format.parse(endDateStr).getTime();
            while(start <=end) {
                String timePath = format.format(start).replace("-","/");

//                    String gzipCmd = "gzip msg-"+format.format(start)+".txt";
//                    System.out.println(gzipCmd);
//                String rmCmd = "hdfs dfs -mkdir /data/message/msg/"+timePath;
//                System.out.println(rmCmd);
//////
//                String mkdirCmd = "hadoop fs -get /data/message/msg/"+format.format(start).replace("-","/" )+"/msg-"+format.format(start)+".txt.gz ";
//                System.out.println(mkdirCmd);

//                String mkdirCm1 = "mv msg-"+format.format(start)+"-new.txt   msg-"+format.format(start)+".txt";
//                System.out.println(mkdirCm1);

//                String mc = "unzip new-msg-"+format.format(start)+".zip";
//                System.out.println(mc);

//                String mkdirCm = "mv new-msg-"+format.format(start)+".txt msg-"+format.format(start)+".txt";
//                System.out.println(mkdirCm);
//                String cmd = "cd /home/luanma/"+format.format(start);
////                System.out.println(cmd);
////                String mkdirCm1 = "zip msg-"+format.format(start)+".zip ./*";
////                System.out.println(mkdirCm1);

//                String delete = "rm -rf /home/luanma/"+format.format(start)+"/msg-"+format.format(start)+".txt";
//                System.out.println(delete);

//                String mkdirCmd1 = "hdfs dfs -rm -r /data/message/msg/"+timePath+"/msg-"+format.format(start)+".txt.gz ";
//                System.out.println(mkdirCmd1);
                String mkdirCmd1 = "hdfs dfs -put /home/luanma/new/msg-"+format.format(start)+".txt.gz   /data/message/msg/"+timePath;
                System.out.println(mkdirCmd1);

//                String mkdirCmd = "hadoop fs -mv /data/getSign/"+format.format(start).replace("-","/")+"/* /data/message/msg/"+format.format(start).replace("-","/")+"/";
//                System.out.println(mkdirCmd);

//                String mkdirCmd = "hdfs dfs -mkdir /data/message/msg-beifen/"+format.format(start).replace("-","/");
//                System.out.println(mkdirCmd);
//                String mkdirCmd1 = "hdfs dfs -put /home/test/reply/reply-"+format.format(start)+".txt /data/message/reply/"+timePath;
//                System.out.println(mkdirCmd1);

//                String mkdirCmd = "hdfs dfs -mkdir /data/message/msg/"+format.format(start).replace("-","/");
//                System.out.println(mkdirCmd);

//                String cmd = "hadoop fs -get /data/message/reply/"+timePath+"/reply-"+format.format(start)+".txt    /home/getFromMysql/reply";
//                System.out.println(cmd);

//                String cmd = "mv /home/getFromMysql/msg/msg-"+format.format(start)+".txt.gz    /home/getFromMysql/msg/test-msg-"+format.format(start)+".txt.gz";
//                System.out.println(cmd);


                String type[] = {"identify","marketing"};
                String db[] = {"crdb_1","crdb_2"};
                for(String database : db) {
                    for (String t : type) {
                        for (int i = 0; i <= 10; i++) {
//                            //msg-2019-01-30-crdb_1-identify-0.txt.gz
//                            String ossCmd = "hadoop fs -get /data/message/msg/"+timePath+"/msg-"+format.format(start)+"-"+database+"-"+t+"-"+i+".txt.gz    /ks/getFromMysql/oss-msg";
//                            System.out.println(ossCmd);
//                            String mkdirCmd = "hdfs dfs -put /home/test/oss-msg/msg-"+format.format(start)+"-"+database+"-"+t+"-"+i+".txt.gz /data/message/msg/"+timePath;
//                            System.out.println(mkdirCmd);
//                            String mkdirCmd = "hdfs dfs -put /home/getFromMysql/oss-msg/msg-"+format.format(start)+"-"+database+"-"+t+"-"+i+".txt.gz /data/message/msg/"+timePath;
//                            System.out.println(mkdirCmd);
//                            String gzipCmd = "gzip /home/getFromMysql/oss-ms/msg-"+format.format(start)+"-"+database+"-"+t+"-"+i+".txt";
//                            System.out.println(gzipCmd);

                        }
                    }
                }



                start = start+24*3600*1000l;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
