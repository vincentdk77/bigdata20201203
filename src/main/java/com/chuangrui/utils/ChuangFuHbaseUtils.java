package com.chuangrui.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChuangFuHbaseUtils {

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
    public static String COLUMN_FAMILY = "count";



    public static void main(String[] args) throws Exception {


//
        ArrayList<String> list2 =new ArrayList<String>();
        list2.clear();
        list2.add("15210011577@网贷逾期@{\"last_collection_time\":\"2019-07-10 12:12:11\",\"max_money\":\"125.45\",\"max_collection_time\":54,\"month_count\":\"{\\\"2019-07-10\\\":3}\",\"money_count\":\"{\\\"2019-07-10\\\":12143.45}\",\"number_count\":\"{\\\"2019-07-10\\\":\\\"你我贷\\\"}\"}");
//        list2.add("15210011577@理财@{""\"2019-06-07\":2,\"2019-06-08\":1}");
//        list2.add("15210011579@理财@{\"2019-06-01\":2,\"2019-06-04\":1}");
        saveOrUpdateBatchChuangfuBlackHbase(list2);

    }


    public static void saveOrUpdateBatchChuangfuBlackHbase(ArrayList<String> list) {
        Map<String,Integer> mobileMap = new HashMap<String, Integer>();
        Map<String,Integer> signMap = new HashMap<String, Integer>();
        List<String> rowKey = new ArrayList<String>();
        for(String line : list) {
            String ss [] = line.split("@");
            rowKey.add(ss[0]);
        }

        Map<String, JSONObject > existMap = queryBatchChuangFuBlack(CHUANG_FU_BLACK,rowKey,"");

        //添加数据
        List<Map<String,Object>> insertListMap=new ArrayList<Map<String, Object>>();

        // mobile+"@"+msgSort+"@"{'last_collection_time':'2019-05-12'}
        for(String line : list) {
            String ss [] = line.split("@");
            JSONObject blackJson =  existMap.get(ss[0]+"@"+ss[1]);

            //查看row是否存在、不存在需要插入，存在更新
            if(blackJson!= null ) {
                String existSignFrequency = blackJson.toJSONString();

                JSONObject insertJson = JSONUtils.getJson(ss[2]);
                if(!StringUtils.isEmpty(insertJson.getString("last_collection_time"))){
                    if(!StringUtils.isEmpty(blackJson.getString("last_collection_time") )  ) {
                        if(insertJson.getString("last_collection_time").compareTo( blackJson.getString("last_collection_time")) > 0) {
                            blackJson.put("last_collection_time",insertJson.getString("last_collection_time"));
                        }
                    } else {
                        blackJson.put("last_collection_time",insertJson.getString("last_collection_time"));
                    }
                }
                //max_money
                if(!StringUtils.isEmpty(insertJson.getString("max_money"))){
                    if(!StringUtils.isEmpty(blackJson.getString("max_money") )  ) {
                        if(Double.parseDouble(insertJson.getString("max_money")) > Double.parseDouble(blackJson.getString("max_money"))) {
                            blackJson.put("max_money",insertJson.getString("max_money"));
                        }
                    } else {
                        blackJson.put("max_money",insertJson.getString("max_money"));
                    }
                }
                //max_collection_time 最长逾期天数
                if(!StringUtils.isEmpty(insertJson.getString("max_collection_time"))){
                    if(!StringUtils.isEmpty(blackJson.getString("max_collection_time") )  ) {
                        if(insertJson.getString("max_collection_time").compareTo(blackJson.getString("max_collection_time")) > 0) {
                            blackJson.put("max_collection_time",insertJson.getString("max_collection_time"));
                        }
                    } else {
                        blackJson.put("max_collection_time",insertJson.getString("max_collection_time"));
                    }
                }

                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDateStr  = insertJson.getString("last_collection_time");
                    String monthStr = format.format(format.parse(currentDateStr).getTime()- 30*24*3600*1000l);
                    //month_count 先删除超过30天的数据，然后再追加
                    JSONObject monthJson = new JSONObject();
                    if(blackJson.getJSONObject("month_count") != null) {
                        for(String key: blackJson.getJSONObject("month_count").keySet()) {
                            if(monthStr.compareTo(key) <= 0) {
                                monthJson.put(key,blackJson.getJSONObject("month_count").getInteger(key));
                            }
                        }
                    }
                    blackJson.put("month_count",monthJson);

                    for(String key:insertJson.getJSONObject("month_count").keySet()){
                        if(blackJson.getJSONObject("month_count") ==null || blackJson.getJSONObject("month_count").getInteger(key) == null) {
                            JSONObject newJson = blackJson.getJSONObject("month_count");
                            if(newJson == null) {
                                newJson = new JSONObject();
                            }
                            newJson.put(key,insertJson.getJSONObject("month_count").getInteger(key));
                            blackJson.put("month_count",newJson);
                        } else {
                            blackJson.put("month_count",blackJson.getJSONObject("month_count").put(key,insertJson.getJSONObject("month_count").getInteger(key)+blackJson.getJSONObject("month_count").getInteger(key)));
                        }
                    }
                    //money_count 先删除超过30天的数据，然后再追加
                    JSONObject moneyJson = new JSONObject();
                    if( blackJson.getJSONObject("money_count") != null) {
                        for(String key:  blackJson.getJSONObject("money_count").keySet()) {
                            if(monthStr.compareTo(key) <= 0) {
                                moneyJson.put(key, blackJson.getJSONObject("money_count").getDouble(key));
                            }
                        }
                    }
                    blackJson.put("money_count",moneyJson);

                    for(String key:insertJson.getJSONObject("money_count").keySet()){
                        if(blackJson.getJSONObject("money_count") ==null || blackJson.getJSONObject("money_count").getDouble(key) == null) {
                            JSONObject newJson = blackJson.getJSONObject("money_count");
                            if(newJson == null) {
                                newJson = new JSONObject();
                            }
                            newJson.put(key,insertJson.getJSONObject("money_count").getDouble(key));
                            blackJson.put("money_count",newJson);
                        } else {
                            blackJson.put("money_count",blackJson.getJSONObject("money_count").put(key,insertJson.getJSONObject("money_count").getDouble(key)+blackJson.getJSONObject("month_count").getDouble(key)));
                        }
                    }
                    //number_count 先删除超过30天的数据，然后再追加
                    JSONObject numberJson = new JSONObject();
                    if(blackJson.getJSONObject("number_count")!= null) {
                        for(String key: blackJson.getJSONObject("number_count").keySet()) {
                            if(monthStr.compareTo(key) <= 0) {
                                numberJson.put(key, blackJson.getJSONObject("number_count").getString(key));
                            }
                        }
                    }
                    blackJson.put("number_count",numberJson);

                    for(String key:insertJson.getJSONObject("number_count").keySet()){
                        JSONObject newJson = blackJson.getJSONObject("number_count");
                        if(newJson == null) {
                            newJson = new JSONObject();
                        }
                        newJson.put(key,insertJson.getJSONObject("number_count").getString(key));
                        blackJson.put("number_count",newJson);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if(!blackJson.toJSONString().equals(existSignFrequency)) {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    map1.put("rowKey",ss[0]);
                    map1.put("columnFamily",COLUMN_FAMILY);
                    map1.put("columnName",ss[1]);
                    map1.put("columnValue",blackJson.toJSONString());
                    insertListMap.add(map1);
                }


            } else {

                Map<String,Object> map1=new HashMap<String,Object>();
                map1.put("rowKey",ss[0]);
                map1.put("columnFamily",COLUMN_FAMILY);
                map1.put("columnName",ss[1]);
                map1.put("columnValue",ss[2]);
                insertListMap.add(map1);
            }
        }

        insertMany(CHUANG_FU_BLACK,insertListMap);
    }

    public static  Map<String, JSONObject> queryBatchChuangFuBlack(TableName tableName,List<String> rowkeyList,String columFamily) {
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
                String row = Bytes.toString(result.getRow());
                Cell[] cells  = result.rawCells();
                for(Cell cell : cells) {
                    String s = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    System.out.println(s);
                        JSONObject json = JSONObject.parseObject(s);
                        rowMap.put(row+"@"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),json);

                }
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
