package com.chuangrui.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class XYKHbase {

    static Configuration conf = null;
    static Connection conn = null;
    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "node4,node5,node6");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        try {
            conn = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   public static TableName receiveTable = TableName.valueOf("cr:mobile_scores_frequency_2");

    public static void main(String args[]) {

        get20();
//        get220();

        try {


//            long startRow = Long.parseLong(args[0]);
//            long endRow = Long.parseLong(args[1]);
//            int pageSize = Integer.parseInt(args[2]);
//            String name = args[3];
//            long startRow = 13000000000l;
//            long endRow = 13100000000l;
//              int pageSize = 100000;
//              String name = "cr:mobile_scores_frequency_2";
//
//            System.out.println("scanTable start startRow:"+startRow+" endRow:"+endRow);
//            long time = System.currentTimeMillis();
//            scanTable( receiveTable ,startRow,endRow,pageSize);
//            long endtime = System.currentTimeMillis();
//            System.out.println((endtime - time) / 1000);
//            System.out.println("scanTable end");



        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void get220() {
        HashMap<String,Integer> set = new HashMap<String,Integer>();
        double score = 10.0;
        try {
            File readfile = new File("F:\\test\\wangdaifk.txt" );
            FileReader fr = new FileReader(readfile);
            BufferedReader br = new BufferedReader(fr);
            File readfile2 = new File("F:\\test\\wangdaihk.txt" );
            FileReader fr2 = new FileReader(readfile2);
            BufferedReader br2 = new BufferedReader(fr2);
            FileWriter fw = new FileWriter("F:\\test\\wdfinal.txt", false);
            PrintWriter pw = new PrintWriter(fw);
            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                    String ss[] =  line.split(",");
                    if(ss.length == 2) {
                        if(Double.parseDouble(ss[1]) >=score) {
                           set.put(ss[0],1);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            while ((line = br2.readLine()) != null) {
                try {
                    String ss[] =  line.split(",");
                    if(ss.length == 2) {
                        if(Double.parseDouble(ss[1]) >=score) {
                            set.put(ss[0],1);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(String s:set.keySet()) {
                pw.println(s);
            }
            pw.flush();
            pw.close();
            fw.close();
        }catch (Exception e) {

        }
    }

    private static void get20() {
        String dianxin[] = {"133","149","153","173","177","180","181","189","199","1700","1701","1702","191","162"};
        try {
            File readfile = new File("F:\\test\\xinyongka.txt" );
            FileReader fr = new FileReader(readfile);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter("F:\\test\\xykfinal.txt", false);
            PrintWriter pw = new PrintWriter(fw);
            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                   String ss[] =  line.split(",");
                   if(ss.length == 2) {

                       if(Double.parseDouble(ss[1]) >=12) {
                           for(String d:dianxin) {
                               if(ss[0].startsWith(d)) {
                                   pw.println(ss[0]);
                                   break;
                               }
                           }

                       }
                   }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pw.flush();
            pw.close();
            fw.close();
        }catch (Exception e) {

        }
    }


    public static  void scanTable( TableName existTableName,long start,long end,Integer pageSize) throws Exception {

        FileWriter fw = new FileWriter("/home/xinyongka.txt", false);
        PrintWriter pw = new PrintWriter(fw);

        FileWriter wdfw = new FileWriter("/home/wangdaifk.txt", false);
        PrintWriter wdpw = new PrintWriter(wdfw);

        FileWriter hkfw = new FileWriter("/home/wangdaihk.txt", false);
        PrintWriter hkpw = new PrintWriter(hkfw);

        Table existTable = conn.getTable(existTableName);
        Scan scan = new Scan();

        long startRow = start;
        long endRow = end;
//        int pageSize = 10000;
        while (startRow < endRow) {
            long time = System.currentTimeMillis();
            System.out.println("startRow:" + startRow);
            scan.setStartRow(Bytes.toBytes(startRow + ""));
            scan.setStopRow(Bytes.toBytes((startRow + pageSize) + ""));
            Map<String, JSONArray> msgMap = new HashMap<String,JSONArray>();
            Map<String,JSONObject> frequencyMap = new HashMap<String,JSONObject>();
            Map<String, JSONObject> blackMap = new HashMap<String,JSONObject>();
            Map<String, JSONObject> customerMap = new HashMap<String,JSONObject>();
            Map<String, JSONObject> replyMap = new HashMap<String,JSONObject>();

            ResultScanner existRsacn = existTable.getScanner(scan);
            //取已经存在的数据的数据
            Map<String ,Double> moblieScoreMap = new HashMap<String, Double>();
            StringBuilder builder = new StringBuilder();
            StringBuilder  wdbuilder = new StringBuilder();
            StringBuilder fkbuilder = new StringBuilder();
            for (Result rs : existRsacn) {

                List<Cell> cells = rs.listCells();
                String rowkey = Bytes.toString(rs.getRow());
                int customer=0,channel=0;
                double reply=0,frequency=0;

                int wdcustomer=0,wdchannel=0;
                double wdreply=0,wdfrequency=0;

                int fkcustomer=0,fkchannel=0;
                double fkreply=0,fkfrequency=0;
                try {
                    for (Cell cell : cells) {
                        String column = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                        String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

                        if(column.equals("channel")) {
                            if(!StringUtils.isEmpty(value)) {
                                String bs[] =  JSONObject.parseObject(value).getString("black").split("@");
                                channel = Integer.parseInt(bs[0])+bs[1].split(",").length;


                            }
                        } else if(column.equals("cusScore")){
                            if(!StringUtils.isEmpty(value)) {
                                JSONObject cusJson = JSONObject.parseObject(value);
                                if(cusJson != null && cusJson.getString("信用卡") != null ){
                                    customer =  cusJson.getString("信用卡").split(",").length  ;
                                }
                                if(cusJson != null && (cusJson.getString("网络借贷还款") != null ) ){
                                    wdcustomer =  cusJson.getString("网络借贷还款").split(",").length  ;
                                }
                                if(cusJson != null && (cusJson.getString("网络借贷放款") != null ) ){
                                    fkcustomer =  cusJson.getString("网络借贷放款").split(",").length  ;
                                }
                            }
                        } else if(column.equals("repScore")) {
                            if(!StringUtils.isEmpty(value)) {
                                JSONObject repJson = JSONObject.parseObject(value);
                                if(repJson != null && repJson.getString("信用卡") != null ){
                                    reply = Double.parseDouble(repJson.getString("信用卡")   );
                                }
                                if(repJson != null && repJson.getString("网络借贷还款") != null ){
                                    wdreply = Double.parseDouble(repJson.getString("网络借贷还款")   );
                                }
                                if(repJson != null && repJson.getString("网络借贷放款") != null ){
                                    fkreply = Double.parseDouble(repJson.getString("网络借贷放款")   );
                                }
                            }


                        }

                    }
                    double finalScore = (reply+3)*10/6.0*0.25 + (customer+3)*0.25 + (5-channel)/5.0*10*0.5 ;
                    if(finalScore != 7.0) {
                        builder.append(rowkey+","+finalScore+"\n");
                    }
                    double wdScore = (wdreply+3)*10/6.0*0.25 + (wdcustomer+3)*0.25 + (5-channel)/5.0*10*0.5 ;
                    if(wdScore != 7.0) {
                        wdbuilder.append(rowkey+","+finalScore+"\n");
                    }
                    double fkScore = (fkreply+3)*10/6.0*0.25 + (fkcustomer+3)*0.25 + (5-channel)/5.0*10*0.5 ;
                    if(fkScore != 7.0) {
                        fkbuilder.append(rowkey+","+finalScore+"\n");
                    }

                } catch (Exception e) {

                }

            }
            pw.print(builder.toString());
            wdpw.print(wdbuilder.toString());
            hkfw.append(fkbuilder.toString());
            System.out.println("use time:" + (System.currentTimeMillis() - time) / 1000);
            startRow = startRow + pageSize;
        }
        pw.close();
        fw.close();
        wdpw.close();
        wdfw.close();
        hkpw.close();
        hkfw.close();



    }


}
