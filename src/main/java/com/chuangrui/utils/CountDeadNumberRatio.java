package com.chuangrui.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountDeadNumberRatio {

    static Configuration conf = null;
    static Connection conn = null;

    static  java.sql.Connection mysqlConn;
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
    public static TableName Table = TableName.valueOf("cr:mobile_status_new");

    public static void main(String args[]) {
        try {

//    1300000000     19999999999
//            long startRow = Long.parseLong(args[0]);
//            long endRow = Long.parseLong(args[1]);
            long startRow = 13527980000l;
            long endRow = 19999999999l;

            System.out.println("scanTable start startRow:"+startRow+" endRow:"+endRow);
            long time = System.currentTimeMillis();
            scanTable(Table ,startRow,endRow);
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
        int pageSize = 10000;
        int index = 1;
        StringBuilder builder = new StringBuilder("insert into mobile_ratio(start_mobile,sum,real_sum,space_sum) values");
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
                String status = Bytes.toString(rs.getValue(Bytes.toBytes("mobile"),Bytes.toBytes("status")));
                String ctime =  Bytes.toString(rs.getValue(Bytes.toBytes("mobile"),Bytes.toBytes("ctime")));
                if(status.equals("1")) {
                    realSum++;
                } else {
                    spaceSum++;
                }
            }
            builder.append("('"+startRow+"', "+sum+","+realSum+","+spaceSum+"),");
            if(index%100 == 0) {
                String sql = builder.toString().substring(0,builder.toString().length()-1);
                System.out.println(sql);
                Statement st = getConn().createStatement();
                st.execute(sql);
                builder = new StringBuilder("insert into mobile_ratio(start_mobile ,sum,real_sum,space_sum) values");
            }

            System.out.println("use time:" + (System.currentTimeMillis() - time) / 1000);
            startRow = startRow + pageSize;
            index++;
        }
        if(builder.toString().length() > 90) {
            String sql = builder.toString().substring(0,builder.toString().length()-1);
            Statement st = getConn().createStatement();
            st.execute(sql);
        }



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
