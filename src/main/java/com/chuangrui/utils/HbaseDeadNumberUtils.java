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

public class HbaseDeadNumberUtils {

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

    public static TableName tableName = TableName.valueOf("cr:mobile_status_new");

    public static  String[] columnFamilys = { "mobile"};
    public static String COLUMN_CTIME = "ctime";
    public static String COLUMN_STATUS="status";

    public static void main(String[] args) throws Exception {
        ArrayList<String> list2 =new ArrayList<String>();
        list2.clear();
        list2.add("15210011577@2019-01-01@1");
        list2.add("15210011578@2019-01-01@0");

        saveOrUpdateBatchDeadNumberHbase(list2);


    }

    public static void saveOrUpdateBatchDeadNumberHbase(ArrayList<String> list) {
        //添加数据
        List<Map<String,Object>> insertListMap=new ArrayList<Map<String, Object>>();

        for(String line : list) {
            String ss [] = line.split("@");

            Map<String,Object> map1=new HashMap<String,Object>();
            map1.put("rowKey",ss[0]);
            map1.put("columnFamily",columnFamilys[0]);
            map1.put("columnName",COLUMN_CTIME);
            map1.put("columnValue",ss[1]);
            insertListMap.add(map1);
            Map<String,Object> map2=new HashMap<String,Object>();
            map2.put("rowKey",ss[0]);
            map2.put("columnFamily",columnFamilys[0]);
            map2.put("columnName",COLUMN_STATUS);
            map2.put("columnValue",ss[2]);
            insertListMap.add(map2);

        }

        insertMany(tableName,insertListMap);
    }






    /**
     * 添加数据（多个rowKey，多个列簇，适合由固定结构的数据）
     * @param tableName
     * @param list
     * @throws IOException
     */
    public static void insertMany(TableName tableName,List<Map<String,Object>> list)   {
        try {
            long start = System.currentTimeMillis();
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
            System.out.println("add data Success! size:"+list.size()+" time"+(System.currentTimeMillis()-start)/1000);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



}
