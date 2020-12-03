package com.chuangrui.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeadNumberHbaseUtils {

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

    public static TableName MOBILE_STATUS = TableName.valueOf("cr:mobile_status");


    public static  String[] columnFamilys = { "mobile"};
    public static String COLUMN_CTIME = "ctime";
    public static String COLUMN_STATUS="status";

    public static void main(String[] args) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        list.add("17719499058@2019-08-04 00:00:00@1");

        saveOrUpdateBatchChannelBlackHbase(list);
    }


    public static void saveOrUpdateBatchChannelBlackHbase (ArrayList<String> list) {
        Map<String,String> listMap = new HashMap<String, String>();


        List<String> rowKey = new ArrayList<String>();
        for(String line : list) {
            String ss [] = line.split("@");
            rowKey.add(ss[0]);
            listMap.put(ss[0] ,ss[1]+"@"+ss[2]);

        }
        Map<String, String > existMap = queryBatch(rowKey,columnFamilys[0]);
        //添加数据
        List<Map<String,Object>> insertListMap=new ArrayList<Map<String, Object>>();

        for(String line : list) {
            String ss [] = line.split("@");
            String existMobileCtimeStatus =  existMap.get(ss[0]);

            //查看row是否存在、不存在需要插入，存在更新
            if(!StringUtils.isEmpty(existMobileCtimeStatus ) ) {
                String ctime=null;
                String cs[] = existMobileCtimeStatus.split("@");
                if(cs[0].compareTo(ss[1]) < 0) {
                    ctime = ss[1];
                }

                String status = null;
                if(!cs[1].equals(ss[2])) {
                    status = ss[2];
                }

                if(!StringUtils.isEmpty(ctime)) {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    map1.put("rowKey",ss[0]);
                    map1.put("columnFamily",columnFamilys[0]);
                    map1.put("columnName", COLUMN_CTIME);
                    map1.put("columnValue",ctime);
                    insertListMap.add(map1);
                }
                if(!StringUtils.isEmpty(status)) {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    map1.put("rowKey",ss[0]);
                    map1.put("columnFamily",columnFamilys[0]);
                    map1.put("columnName", COLUMN_STATUS);
                    map1.put("columnValue",status);
                    insertListMap.add(map1);
                }
            } else {
                Map<String,Object> map1=new HashMap<String,Object>();
                map1.put("rowKey",ss[0]);
                map1.put("columnFamily",columnFamilys[0]);
                map1.put("columnName", COLUMN_CTIME);
                map1.put("columnValue",ss[1]);
                insertListMap.add(map1);
                Map<String,Object> map2=new HashMap<String,Object>();
                map2.put("rowKey",ss[0]);
                map2.put("columnFamily",columnFamilys[0]);
                map2.put("columnName", COLUMN_STATUS);
                map2.put("columnValue",ss[2]);
                insertListMap.add(map2);
            }
        }
        insertMany(MOBILE_STATUS,insertListMap);
    }


    public static  Map<String, String> queryBatch(List<String> rowkeyList,String columFamily) {
        Map<String, String > rowMap = new HashMap<String, String>();
        List<Get> getList = new ArrayList<Get>();
        try {
            Table table = conn.getTable(MOBILE_STATUS);
            for(String rowkey:rowkeyList) {
                Get get = new Get(Bytes.toBytes(rowkey));
                getList.add(get);
            }
            Result[] results = table.get(getList);
            for(Result result : results) {

               String ctimeValue = Bytes.toString(result.getValue(Bytes.toBytes(columFamily),Bytes.toBytes(COLUMN_CTIME)));
                String statusValue = Bytes.toString(result.getValue(Bytes.toBytes(columFamily),Bytes.toBytes(COLUMN_STATUS)));
               String row = Bytes.toString(result.getRow());
                rowMap.put(row,ctimeValue+"@"+statusValue);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowMap;
    }


    /**
     * 添加数据（多个rowKey，多个列簇，适合由固定结构的数据）
     * @param tableName
     * @param list
     * @throws IOException
     */
    public static void insertMany(TableName tableName,List<Map<String,Object>> list)   {
        try {
            List<Put> puts = new ArrayList<Put>();
            Table table = conn.getTable(tableName);// Tabel负责跟记录相关的操作如增删改查等//
            if (list != null && list.size() > 0) {
                for (Map<String, Object> map : list) {
                    Put put = new Put(Bytes.toBytes(map.get("rowKey").toString()));
                    put.addColumn(Bytes.toBytes(map.get("columnFamily").toString()),
                            Bytes.toBytes(map.get("columnName").toString()),
                            Bytes.toBytes(map.get("columnValue").toString()));
                    puts.add(put);
                }
            }
            table.put(puts);
            table.close();
            System.out.println("add data Success!");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



}
