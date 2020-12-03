package com.chuangrui.utils;

import com.alibaba.fastjson.JSONObject;
import com.chuangrui.node.GetMysqlByNode1;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

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

    public static TableName CHUANG_FU_BLACK = TableName.valueOf("chuangfu:black");

    public static TableName tableName = TableName.valueOf("cr:mobile_scores_frequency");



    public static void main(String args[]) {

//        try{
//            long time = System.currentTimeMillis();
//            scanTable(tableName,"scores","repScore");
//            long endtime = System.currentTimeMillis();
//            System.out.println((endtime-time)/1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            java.sql.Connection conn = GetMysqlByNode1.getConn("0");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM `crdb`.`tbl_app_identify_batch` where id=11977372 ");
            while (rs.next()) {
                String template = rs.getString("template");
                System.out.println(template);

            }

        }catch (Exception e) {

        }

    }




    public static  void scanTable(TableName tableName,String columnFamily,String column) throws IOException {
        Table table = conn.getTable(tableName);
        Scan scan = new Scan();
//        scan.addFamily(Bytes.toBytes("scores"));
//        scan.addFamily(Bytes.toBytes("frequency"));
        scan.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(column));
//        scan.setMaxResultSize(10000);
        long startRow = 13000000000l;
        long endRow = 19999999999l;
        int pageSize = 100000;
        int index = 0;
        while(startRow<endRow) {
            System.out.println("startRow:"+startRow);
            scan.setStartRow(Bytes.toBytes(startRow+""));
            scan.setStopRow(Bytes.toBytes((startRow+pageSize)+""));
            ResultScanner rsacn = table.getScanner(scan);
            for(Result rs:rsacn) {
                String rowkey = Bytes.toString(rs.getRow());
                //System.out.println("row key :"+rowkey);
                Cell[] cells  = rs.rawCells();
                for(Cell cell : cells) {
                    String s = Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength())+"::"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())+"::"+
                            Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    System.out.println(s);
                }
                index++;

                // System.out.println("-----------------------------------------");
            }
            startRow = startRow+pageSize;
        }

        System.out.println(index);
    }

    private static void test(String file) {
        try {


            String columFamily[]  ={"frequency","scores","scores","scores"};
            String colum[]  ={"f-网贷","cusScore","repScore","channel"};
            File readFile = new File("F:\\a.txt");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int index =0;
         ;  List<String> list = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                list.add(line);
                index++;
                if(index % 10000 == 0) {
                    long time = System.currentTimeMillis();
                    queryBatch(list,columFamily,colum);
                    System.out.println(index+" use:"+(System.currentTimeMillis()-time)/1000);
                }
            }


            System.out.println("index:"+index);
        } catch (Exception e) {

        }
    }

    public static Map<String, JSONObject> queryBatch(List<String> rowkeyList, String columFamily[], String column[]) {
        Map<String, JSONObject > rowMap = new HashMap<String, JSONObject>();
        List<Get> getList = new ArrayList<Get>();
        try {
            Table table = conn.getTable(tableName);
            for(String rowkey:rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);
            for(Result result : results) {
                for(int i=0;i<columFamily.length;i++) {
                    String columnValue = Bytes.toString(result.getValue(Bytes.toBytes(columFamily[i]),Bytes.toBytes(column[i])));
                    if(!StringUtils.isEmpty(columnValue)) {
                        String row = Bytes.toString(result.getRow());
                        rowMap.put(row+"@"+result.getValue(Bytes.toBytes(columFamily[i]),Bytes.toBytes(column[i])),JSONObject.parseObject(columnValue));
                    }

                }


            }
            System.out.println(rowMap.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowMap;
    }
}
