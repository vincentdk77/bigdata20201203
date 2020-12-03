package com.chuangrui.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountSignNumberDateUtils {

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
    static  java.sql.Connection mysqlConn;

    public static TableName tableName = TableName.valueOf("cr:mobile_scores_frequency_2");
    //    测试
//    public static TableName tableName = TableName.valueOf("test");
    public static  String[] columnFamilys = { "scores","msg","frequency" };
    public static String CUS_SCORE_Column = "cusScore";
    public static String REPLY_SCORE_Column = "repScore";
    public static String CHANNEL_BLACK_COLUMN="channel";
    public static String FREQUENCY_PRE = "f-";

    public static void main(String args[]) {
        try {

//    1300000000     19999999999
//            long startRow = Long.parseLong(args[0]);
//            long endRow = Long.parseLong(args[1]);
            long startRow = 13000000000l;
            long endRow = 19999999999l;

            System.out.println("scanTable start startRow:"+startRow+" endRow:"+endRow);
            long time = System.currentTimeMillis();
            scanTable(tableName ,startRow,endRow);
            long endtime = System.currentTimeMillis();
            System.out.println((endtime - time) / 1000);
            System.out.println("scanTable end");



        }catch(Exception e) {
            e.printStackTrace();
        }
    }



    public static  void scanTable(TableName tableName, long start,long end) throws Exception {
        Table getTable = conn.getTable(tableName);

        Scan scan = new Scan();

        long startRow = start;
        long endRow = end;
        int pageSize = 10000000;
        int index = 1;
        Map<String,Integer> signMap = new HashMap<String, Integer>();

        while (startRow < endRow) {

            long time = System.currentTimeMillis();
            System.out.println("startRow:" + startRow);
            scan.setStartRow(Bytes.toBytes(startRow + ""));

            scan.setStopRow(Bytes.toBytes((startRow + pageSize-1) + ""));

            ResultScanner existRsacn = getTable.getScanner(scan);
            //取已经存在的数据的数据
            int sum = 0;
            int realSum = 0;
            int spaceSum = 0;
            for (Result rs : existRsacn) {
                sum++;
                List<Cell> cells = rs.listCells();

                String rowkey = Bytes.toString(rs.getRow());
                for(Cell cell : cells) {
                    if(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()).startsWith(FREQUENCY_PRE) ) {
                        String fsign = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                        JSONObject json = JSONObject.parseObject(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                        for(String key:json.keySet()) {
                            String sign = fsign.substring(2,fsign.length());
                            Integer num = json.getInteger(key);
                            if(signMap.get(sign+"@"+key) == null) {
                                signMap.put(sign+"@"+key,num);
                            } else {
                                signMap.put(sign+"@"+key,signMap.get(sign+"@"+key)+num);
                            }
                        }
                    }
                }

            }
            System.out.println("use time:" + (System.currentTimeMillis() - time) / 1000);
            startRow = startRow + pageSize;

        }


        int i = 0;
        StringBuilder builder = new StringBuilder("insert into sign_send_num_date(sign,number,add_time,ctime) values");
        for(String key:signMap.keySet()) {
            String ss[] = key.split("@");
            builder.append("( '"+ss[0]+"',"+signMap.get(key)+",now(),'"+ss[1]+"'),");
            i++;
            if(i % 2000 == 0) {
                System.out.println("i:"+i+" 插入");
                String sql = builder.toString().substring(0,builder.toString().length()-1);
                //            System.out.println(sql);
                Statement st = getConn().createStatement();
                st.execute(sql);
                builder = new StringBuilder("insert into sign_send_num_date(sign,number,add_time,ctime) values");
            }
        }

        if(builder.toString().length()> 70) {
            String sql = builder.toString().substring(0,builder.toString().length()-1);
//            System.out.println(sql);
            Statement st = getConn().createStatement();
            st.execute(sql);
            builder = new StringBuilder("insert into sign_send_num_date(sign,number,add_time,ctime) values");
        }


//



    }


    public static java.sql.Connection getConn() {

        try{
            if(mysqlConn != null) {
                return mysqlConn;
            }
            Class.forName("com.mysql.jdbc.Driver");
            mysqlConn = DriverManager.getConnection("jdbc:mysql://61.132.230.78:3306/dataplatform?autoReconnect=true&amp;autoReconnectForPools=true","chuangrui1","chuangrui@123");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return mysqlConn;
    }

}
