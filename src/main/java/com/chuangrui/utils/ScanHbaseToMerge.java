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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanHbaseToMerge {

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
    public static TableName needInsertTable = TableName.valueOf("cr:mobile_scores_frequency_20181731");
    public static TableName receiveTable = TableName.valueOf("cr:mobile_scores_frequency_2");

    public static void main(String args[]) {
        try {

//    13687602840     19999999999
//            long startRow = Long.parseLong(args[0]);
//            long endRow = Long.parseLong(args[1]);
//            int pageSize = Integer.parseInt(args[2]);
//            String name = args[3];
            long startRow = 13421900000l;
            long endRow = 19999999999l;
              int pageSize = 100000;
              String name = "cr:mobile_scores_frequency_2";

            System.out.println("scanTable start startRow:"+startRow+" endRow:"+endRow);
            long time = System.currentTimeMillis();
            scanTable(TableName.valueOf(name),receiveTable ,startRow,endRow,pageSize);
            long endtime = System.currentTimeMillis();
            System.out.println((endtime - time) / 1000);
            System.out.println("scanTable end");



        }catch(Exception e) {
            e.printStackTrace();
        }
    }



    public static  void scanTable(TableName getTableName,TableName existTableName,long start,long end,Integer pageSize) throws Exception {
        Table getTable = conn.getTable(getTableName);
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
            for (Result rs : existRsacn) {
                List<Cell> cells = rs.listCells();
                String rowkey = Bytes.toString(rs.getRow());
                for (Cell cell : cells) {
                    String column = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());

                    if(column.startsWith("20")) {
//                        System.out.println(column+" "+value);
                        try {
                            if(!value.endsWith("\"}]")) {
                                value= value+"\"}]";
                            }
                            msgMap.put(rowkey+"-"+column,JSONObject.parseArray(value));
                        } catch (Exception e) {
                            System.out.println(column+" "+value);

                        }
                    } else if(column.startsWith("f-")) {
                        frequencyMap.put(rowkey+"-"+column,JSONObject.parseObject(value));
                    }else if(column.equals("channel")) {

                        blackMap.put(rowkey,JSONObject.parseObject(value));
                    } else if(column.equals("cusScore")){
                        try {
                            customerMap.put(rowkey, JSONObject.parseObject(value));
                        }catch(Exception e) {
                            System.out.println(column+" "+value);

                        }
                    } else if(column.equals("repScore")) {
                        replyMap.put(rowkey,JSONObject.parseObject(value));
                    }

                }
            }

            //最终需要插入到hbase的数据
            List<Put> puts = new ArrayList<Put>();

            //取需要插入的数据和已存在的数据进行对比
            ResultScanner getRsacn = getTable.getScanner(scan);
            //取已存在的数据
            for (Result rs : getRsacn) {
                List<Cell> cells = rs.listCells();

                String rowkey = Bytes.toString(rs.getRow());
                Put put = new Put(Bytes.toBytes(rowkey));
                for (Cell cell : cells) {
                    String column = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    //短信记录存在需要插入的数据
                    if(column.startsWith("20")) {
                        try {
                            //从已存在的记录中找,exitArray存放的当前column存放的短信数组
                            JSONArray exitArray = msgMap.get(rowkey + "-" + column);
                            if(!value.endsWith("\"}]")) {
                                value=value+"\"}]";
                            }
                            JSONArray insertArray = JSONObject.parseArray(value);
                            if (exitArray == null || exitArray.size() == 0) {
                                put.addColumn(Bytes.toBytes("msg"),
                                        Bytes.toBytes(column),
                                        Bytes.toBytes(value));
                            } else {
                                JSONArray finallArray = new JSONArray();
                                Map<String, Integer> distinctMap = new HashMap<String, Integer>();
                                for (int i = 0; i < exitArray.size(); i++) {
                                    JSONObject json = exitArray.getJSONObject(i);
                                    if (distinctMap.get(json.toJSONString()) == null) {
                                        finallArray.add(json);
                                        distinctMap.put(json.toJSONString(), 1);
                                    }
                                }
                                for (int i = 0; i < insertArray.size(); i++) {
                                    JSONObject json = insertArray.getJSONObject(i);
                                    if (distinctMap.get(json.toJSONString()) == null) {
                                        finallArray.add(json);
                                        distinctMap.put(json.toJSONString(), 1);
                                    }
                                }
                                put.addColumn(Bytes.toBytes("msg"),
                                        Bytes.toBytes(column),
                                        Bytes.toBytes(finallArray.toJSONString()));
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(column+" "+value);

                        }
                    } else if(column.startsWith("f-")) {
                        JSONObject existJSON = frequencyMap.get(rowkey+"-"+column);
                        JSONObject insertJSON = JSONObject.parseObject(value);
                        if(existJSON == null) {
                            put.addColumn(Bytes.toBytes("frequency"),
                                    Bytes.toBytes(column),
                                    Bytes.toBytes(insertJSON.toJSONString()));
                        } else {
                            JSONObject finallJson = new JSONObject();
                            for(String key: existJSON.keySet()) {
                                if(finallJson.get(key) == null) {
                                    finallJson.put(key,existJSON.getInteger(key));
                                } else {
                                    finallJson.put(key,finallJson.getInteger(key)+existJSON.getInteger(key));
                                }
                            }
                            for(String key: insertJSON.keySet()) {
                                if(finallJson.get(key) == null) {
                                    finallJson.put(key,insertJSON.getInteger(key));
                                } else {
                                    finallJson.put(key,finallJson.getInteger(key)+insertJSON.getInteger(key));
                                }
                            }
                            put.addColumn(Bytes.toBytes("frequency"),
                                    Bytes.toBytes(column),
                                    Bytes.toBytes(finallJson.toJSONString()));
                        }
                    }else if(column.equals("channel")) {
                        JSONObject existJSON = blackMap.get(rowkey);
                        JSONObject insertJson = JSONObject.parseObject(value);
                        if(existJSON == null) {
                            put.addColumn(Bytes.toBytes("scores"),
                                    Bytes.toBytes("channel"),
                                    Bytes.toBytes(insertJson.toJSONString()));
                        } else {
                            String existBlack = existJSON.getString("black");
                            String insertBlack = insertJson.getString("black");
                            String eb[] =existBlack.split("@");
                            String ib[] = insertBlack.split("@");
                            String fb = "";
                            if(Integer.parseInt(eb[0]) > Integer.parseInt(ib[0])) {
                                fb = eb[0]+"@";
                            } else {
                                fb = ib[0]+"@";
                            }
                            Map<String,Integer> companyMap = new HashMap<String,Integer>();
                            for(String c: eb[1].split(",")){
                                if(companyMap.get(c) == null) {
                                    fb = fb+c+",";
                                    companyMap.put(c,1);
                                }
                            }
                            for(String c: ib[1].split(",")){
                                if(companyMap.get(c) == null) {
                                    fb = fb+c+",";
                                    companyMap.put(c,1);
                                }
                            }
                            JSONObject j = new JSONObject();
                            j.put("black",fb.substring(0,fb.length()-1));
                            put.addColumn(Bytes.toBytes("scores"),
                                    Bytes.toBytes("channel"),
                                    Bytes.toBytes(j.toJSONString() ));
                        }
                    } else if(column.equals("cusScore")){
                        JSONObject existJson = customerMap.get(rowkey);
                        JSONObject insertJson = JSONObject.parseObject(value);
                        if(existJson == null) {
                            put.addColumn(Bytes.toBytes("scores"),
                                    Bytes.toBytes("cusScore"),
                                    Bytes.toBytes(insertJson.toJSONString()));
                        } else {
                            JSONObject finallJson = new JSONObject();
                            for(String key :existJson.keySet()) {
                                finallJson.put(key,existJson.getString(key));
                            }
                            for(String key: insertJson.keySet()) {
                                if(finallJson.getString(key) == null) {
                                    finallJson.put(key,insertJson.getString(key));
                                } else {
                                    String existCus[] = existJson.getString(key).split(",");
                                    String insertCus[] = insertJson.getString(key).split(",");
                                    String finalCus = "";
                                    Map<String,Integer> map = new HashMap<String,Integer>();
                                    for(String c : existCus) {
                                        if(map.get(c) == null) {
                                            finalCus = finalCus+c+",";
                                            map.put(c,1);
                                        }
                                    }
                                    for(String c : insertCus) {
                                        if(map.get(c) == null) {
                                            finalCus = finalCus+c+",";
                                            map.put(c,1);
                                        }
                                    }
                                    finallJson.put(key,finalCus.substring(0,finalCus.length()-1));
                                }
                            }
                            put.addColumn(Bytes.toBytes("scores"),
                                    Bytes.toBytes("cusScore"),
                                    Bytes.toBytes(finallJson.toJSONString()));
                        }
                    } else if(column.equals("repScore")) {
                        JSONObject existJson = replyMap.get(rowkey);
                        JSONObject insertJson = JSONObject.parseObject(value);
                        if(existJson == null) {
                            put.addColumn(Bytes.toBytes("scores"),
                                    Bytes.toBytes("repScore"),
                                    Bytes.toBytes(insertJson.toJSONString()));
                        }else {
                            JSONObject finallJson = new JSONObject();
                            for(String key :existJson.keySet()) {
                                finallJson.put(key,existJson.getString(key));
                            }
                            for(String key: insertJson.keySet()) {
                                finallJson.put(key, insertJson.getString(key));

                            }
                            put.addColumn(Bytes.toBytes("scores"),
                                    Bytes.toBytes("repScore"),
                                    Bytes.toBytes(finallJson.toJSONString()));
                        }
                    }
                }

                puts.add(put);
            }
            existTable.put(puts);
            existTable.close();

            getTable.close();
            System.out.println("use time:" + (System.currentTimeMillis() - time) / 1000);
            startRow = startRow + pageSize;
        }



    }


}
