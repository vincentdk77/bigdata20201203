package com.chuangrui.version_3.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chuangrui.utils.JSONUtils;
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

public class HbaseUtilsVersion3 {

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

    public static  String[] columnFamilys = { "scores", "frequency" };
    public static String CUS_SCORE_Column = "cusScore";
    public static String REPLY_SCORE_Column = "repScore";
    public static String CHANNEL_BLACK_COLUMN="channel";
    public static String FREQUENCY_PRE = "f-";

    public static void main(String[] args) throws Exception {


        ArrayList<String> list2 =new ArrayList<String>();
        list2.clear();
        list2.add("15210011577@网贷@{\"2019-06-02\":1,\"2019-06-05\":1}");
        list2.add("15210011577@理财@{\"2019-06-07\":2,\"2019-06-08\":1}");
        list2.add("15210011579@理财@{\"2019-06-01\":2,\"2019-06-04\":1}");
//        saveOrUpdateBatchFrequencyHbase(list2);


    }

    public static void saveOrUpdateBatchFrequencyHbase(String signCode,ArrayList<String> list) {
        TableName tableName = HbaseTable.hbaseTable.get(signCode);
        Map<String,Integer> mobileMap = new HashMap<String, Integer>();
        Map<String,Integer> signMap = new HashMap<String, Integer>();
        Map<String,String> listMap = new HashMap<String, String>();
        List<String> rowKey = new ArrayList<String>();
        for(String line : list) {
            String ss [] = line.split("@");
            rowKey.add(ss[0]);
            listMap.put(ss[0] ,ss[1]);

        }

        Map<String, JSONObject > existMap = queryBatchFrequency(tableName,rowKey,columnFamilys[1]);

        //添加数据
        List<Map<String,Object>> insertListMap=new ArrayList<Map<String, Object>>();

        for(String line : list) {
            String ss [] = line.split("@");
            JSONObject frequencyJson =  existMap.get(ss[0]+"@"+FREQUENCY_PRE+ss[1]);

            //查看row是否存在、不存在需要插入，存在更新
            if(frequencyJson!= null ) {
                String existSignFrequency = frequencyJson.getString(ss[0]+"@"+FREQUENCY_PRE+ss[1]);
                JSONObject insertJson = JSONUtils.getJson(ss[2]);

                for(String dayTime : insertJson.keySet()) {
                    //已存在里面没有，需要添加
                    if(frequencyJson.getInteger(dayTime) != null  ) {
                        frequencyJson.put(dayTime,frequencyJson.getInteger(dayTime)+insertJson.getInteger(dayTime));
                    } else {
                        frequencyJson.put(dayTime,insertJson.getInteger(dayTime));
                    }
                }

                if(!frequencyJson.toJSONString().equals(existSignFrequency)) {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    map1.put("rowKey",ss[0]);
                    map1.put("columnFamily",columnFamilys[1]);
                    map1.put("columnName",FREQUENCY_PRE+ss[1]);
                    map1.put("columnValue",frequencyJson.toJSONString());
                    insertListMap.add(map1);
                }


            } else {
                Map<String,Object> map1=new HashMap<String,Object>();
                map1.put("rowKey",ss[0]);
                map1.put("columnFamily",columnFamilys[1]);
                map1.put("columnName",FREQUENCY_PRE+ss[1]);
                map1.put("columnValue",ss[2]);
                insertListMap.add(map1);
            }
        }

        insertMany(tableName,insertListMap);
    }

    public static void saveOrUpdateBatchReplySocreHbase(String signCode,ArrayList<String> list) {
        TableName tableName = HbaseTable.hbaseTable.get(signCode);
        Map<String,Integer> mobileMap = new HashMap<String, Integer>();
        Map<String,Integer> signMap = new HashMap<String, Integer>();
        Map<String,String> listMap = new HashMap<String, String>();
        List<String> rowKey = new ArrayList<String>();
        for(String line : list) {
            String ss [] = line.split("@");
            rowKey.add(ss[0]);
            listMap.put(ss[0] ,ss[1]);

        }

        Map<String, JSONObject > existMap = queryBatch(tableName,rowKey,columnFamilys[0],REPLY_SCORE_Column);

        //添加数据
        List<Map<String,Object>> insertListMap=new ArrayList<Map<String, Object>>();

        for(String line : list) {
            String ss [] = line.split("@");
            JSONObject repScoreJson =  existMap.get(ss[0]);

            //查看row是否存在、不存在需要插入，存在更新
            if(repScoreJson!= null ) {
                String existSignReply = repScoreJson.getString(ss[0]);

                JSONObject finalJson = new JSONObject();

                JSONObject insertJson = JSONUtils.getJson(ss[1]);

                for(String sign : insertJson.keySet()) {
                    //已存在里面没有，需要添加
                    if(StringUtils.isEmpty(repScoreJson.getString(sign))  ) {
                        repScoreJson.put(sign,insertJson.getString(sign));
                    } else {
                        if(  !repScoreJson.getString(sign).contains(insertJson.getString(sign))) {
                            repScoreJson.put(sign,insertJson.getString(sign));
                        }
                    }
                }

                if(!repScoreJson.toJSONString().equals(existSignReply)) {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    map1.put("rowKey",ss[0]);
                    map1.put("columnFamily",columnFamilys[0]);
                    map1.put("columnName",REPLY_SCORE_Column);
                    map1.put("columnValue",repScoreJson.toJSONString());
                    insertListMap.add(map1);
                }


            } else {
                Map<String,Object> map1=new HashMap<String,Object>();
                map1.put("rowKey",ss[0]);
                map1.put("columnFamily",columnFamilys[0]);
                map1.put("columnName",REPLY_SCORE_Column);
                map1.put("columnValue",ss[1]);
                insertListMap.add(map1);
            }
        }

        insertMany(tableName,insertListMap);
    }




    public static void saveOrUpdateBatchChannelBlackHbase (String signCode, ArrayList<String> list) {
        TableName tableName = HbaseTable.hbaseTable.get(signCode);
        Map<String,String> listMap = new HashMap<String, String>();


        List<String> rowKey = new ArrayList<String>();
        for(String line : list) {
            String ss [] = line.split("@");
            rowKey.add(ss[0]);
            listMap.put(ss[0] ,ss[1]+"@"+ss[2]);

        }
        Map<String, JSONObject > existMap = queryBatch(tableName,rowKey,columnFamilys[0],CHANNEL_BLACK_COLUMN);
        //添加数据
        List<Map<String,Object>> insertListMap=new ArrayList<Map<String, Object>>();

        for(String line : list) {
            String ss [] = line.split("@");
            JSONObject existChannelBlackJson =  existMap.get(ss[0]);

            //查看row是否存在、不存在需要插入，存在更新
            if(existChannelBlackJson!= null ) {
                String channelBlack = existChannelBlackJson.getString("black");
                String levelAndCompany[] = channelBlack.split("@");

                String iString = "";
                String insertCompanys[] = ss[2].split(",");
                boolean isInsert = false;
                for(String i : insertCompanys) {
                    if(!levelAndCompany[1].contains(i)) {
                        iString = iString+i+",";
                    }
                }
                if(!StringUtils.isEmpty(iString)) {
                    iString = levelAndCompany[1]+","+iString.substring(0,iString.length()-1);
                } else {
                    iString = levelAndCompany[1];
                }
                if(  Integer.parseInt(levelAndCompany[0])>= Integer.parseInt(ss[1])) {
                    iString = levelAndCompany[0]+"@"+iString;
                } else {
                    iString = ss[1]+"@"+iString;
                }

                if(!channelBlack.equals(iString)) {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    map1.put("rowKey",ss[0]);
                    map1.put("columnFamily",columnFamilys[0]);
                    map1.put("columnName", CHANNEL_BLACK_COLUMN);
                    JSONObject json = new JSONObject();
                    json.put("black",iString);
                    map1.put("columnValue",json.toJSONString());
                    insertListMap.add(map1);
                }
            } else {
                Map<String,Object> map1=new HashMap<String,Object>();
                map1.put("rowKey",ss[0]);
                map1.put("columnFamily",columnFamilys[0]);
                map1.put("columnName", CHANNEL_BLACK_COLUMN);
                JSONObject json = new JSONObject();
                json.put("black",ss[1]+"@"+ss[2]);
                map1.put("columnValue",json.toJSONString());
                insertListMap.add(map1);
            }
        }
        insertMany(tableName,insertListMap);
    }

    public static void saveOrUpdateBatchCustomerSocreHbase(String signCode,ArrayList<String> list) {
        TableName tableName = HbaseTable.hbaseTable.get(signCode);
        Map<String,Integer> mobileMap = new HashMap<String, Integer>();
        Map<String,Integer> signMap = new HashMap<String, Integer>();
        Map<String,String> listMap = new HashMap<String, String>();
        List<String> rowKey = new ArrayList<String>();
        for(String line : list) {
            String ss [] = line.split("@");
            rowKey.add(ss[0]);
            listMap.put(ss[0] ,ss[1]);

        }

        Map<String, JSONObject > existMap = queryBatch(tableName,rowKey,columnFamilys[0],CUS_SCORE_Column);

        //添加数据
        List<Map<String,Object>> insertListMap=new ArrayList<Map<String, Object>>();

        for(String line : list) {
            String ss [] = line.split("@");
            JSONObject cusScoreJson =  existMap.get(ss[0]);

            //查看row是否存在、不存在需要插入，存在更新
            if(cusScoreJson!= null ) {
                String existSignCustomerIds = cusScoreJson.getString(ss[0]);

                JSONObject finalJson = new JSONObject();

                JSONObject insertJson = JSONUtils.getJson(ss[1]);

                for(String sign : insertJson.keySet()) {
                    //已存在里面没有，需要添加
                    if(StringUtils.isEmpty(cusScoreJson.getString(sign))  ) {
                        cusScoreJson.put(sign,insertJson.getString(sign));
                    } else {
                        if(  !cusScoreJson.getString(sign).contains(insertJson.getString(sign))) {
                            cusScoreJson.put(sign,cusScoreJson.getString(sign)+","+insertJson.getString(sign));
                        }
                    }
                }

                if(!cusScoreJson.toJSONString().equals(existSignCustomerIds)) {
                    Map<String,Object> map1=new HashMap<String,Object>();
                    map1.put("rowKey",ss[0]);
                    map1.put("columnFamily",columnFamilys[0]);
                    map1.put("columnName",CUS_SCORE_Column);
                    map1.put("columnValue",cusScoreJson.toJSONString());
                    insertListMap.add(map1);
                }


            } else {
                Map<String,Object> map1=new HashMap<String,Object>();
                map1.put("rowKey",ss[0]);
                map1.put("columnFamily",columnFamilys[0]);
                map1.put("columnName",CUS_SCORE_Column);
                map1.put("columnValue",ss[1]);
                insertListMap.add(map1);
            }
        }

        insertMany(tableName,insertListMap);
    }


    public static  Map<String, JSONObject> queryBatchFrequency(TableName tableName, List<String> rowkeyList,String columFamily) {
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
                    if(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()).startsWith(FREQUENCY_PRE) ) {
                        JSONObject json = JSONObject.parseObject(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                        rowMap.put(row+"@"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),json);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowMap;
    }




    public static  Map<String, JSONObject> queryBatch(TableName tableName, List<String> rowkeyList,String columFamily,String column) {
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
               String columnValue = Bytes.toString(result.getValue(Bytes.toBytes(columFamily),Bytes.toBytes(column)));
               String row = Bytes.toString(result.getRow());
                rowMap.put(row,JSONObject.parseObject(columnValue));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowMap;
    }

    /**
     * 创建只有一个列簇的表
     * @throws IOException
     */

    public static void createTable() throws IOException {
        Admin admin = conn.getAdmin();
        if(!admin.tableExists(TableName.valueOf("test"))) {
            TableName tableName = TableName.valueOf("test");
            //表描述器构造器
            TableDescriptorBuilder  tdb  =TableDescriptorBuilder.newBuilder(tableName)  ;
            //列族描述起构造器
            ColumnFamilyDescriptorBuilder cdb =  ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("user"));
            //获得列描述起
            ColumnFamilyDescriptor  cfd = cdb.build();
            //添加列族
            tdb.setColumnFamily(cfd);
            //获得表描述器
            TableDescriptor td = tdb.build();
            //创建表
            //admin.addColumnFamily(tableName, cfd); //给标添加列族
            admin.createTable(td);
        }else {
            System.out.println("表已存在");
        }
        //关闭链接
    }
    /**
     * 创建表（包含多个列簇）
     *
     * @tableName 表名
     *
     * @family 列族列表
     */
    public static void createTable(TableName tableName, String[] columnFamilys) throws IOException {
        Admin admin = conn.getAdmin();
        if(!admin.tableExists(tableName)) {
            //表描述器构造器
            TableDescriptorBuilder  tdb  =TableDescriptorBuilder.newBuilder(tableName)  ;
            //列族描述器构造器
            ColumnFamilyDescriptorBuilder cdb ;
            //获得列描述器
            ColumnFamilyDescriptor  cfd ;
            for (String columnFamily:columnFamilys) {
                cdb =  ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily));
                cfd = cdb.build();
                //添加列族
                tdb.setColumnFamily(cfd);
            }
            //获得表描述器
            TableDescriptor td = tdb.build();
            //创建表
            admin.createTable(td);
        }else {
            System.out.println("表已存在！");
        }
        //关闭链接
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

    /**
     * 添加数据（多个rowKey，多个列簇）
     * @throws IOException
     */
    public static void insertMany() throws IOException {
        Table table = conn.getTable(TableName.valueOf("test"));
        List<Put> puts = new ArrayList<Put>();
        Put put1 = new Put(Bytes.toBytes("rowKey1"));
        put1.addColumn(Bytes.toBytes("user"),Bytes.toBytes("name") , Bytes.toBytes("wd"));

        Put put2 = new Put(Bytes.toBytes("rowKey2"));
        put2.addColumn(Bytes.toBytes("user"),Bytes.toBytes("age") , Bytes.toBytes("25"));

        Put put3 = new Put(Bytes.toBytes("rowKey3"));
        put3.addColumn(Bytes.toBytes("user"),Bytes.toBytes("weight") , Bytes.toBytes("60kg"));

        Put put4 = new Put(Bytes.toBytes("rowKey4"));
        put4.addColumn(Bytes.toBytes("user"),Bytes.toBytes("sex") , Bytes.toBytes("男"));
        puts.add(put1);
        puts.add(put2);
        puts.add(put3);
        puts.add(put4);
        table.put(puts);
        table.close();
    }

    /**
     * 添加数据（一个rowKey,一个列簇）
     * @throws IOException
     */
    public void insertSingle() throws IOException {
        Table table = conn.getTable(TableName.valueOf("test"));

        Put put1 = new Put(Bytes.toBytes("rowKey5"));

        put1.addColumn(Bytes.toBytes("user"),Bytes.toBytes("name") , Bytes.toBytes("cm"));
        put1.addColumn(Bytes.toBytes("user"),Bytes.toBytes("age") , Bytes.toBytes("22"));
        put1.addColumn(Bytes.toBytes("user"),Bytes.toBytes("weight") , Bytes.toBytes("88kg"));
        put1.addColumn(Bytes.toBytes("user"),Bytes.toBytes("sex") , Bytes.toBytes("男"));

        table.put(put1);
        table.close();
    }

    /**
     * 根据RowKey，列簇，列名修改值
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnName
     * @param columnValue
     * @throws IOException
     */
    public static void updateData(TableName tableName,String rowKey,String columnFamily,String columnName,String columnValue)  {
        try {
            Table table = conn.getTable(tableName);
            Put put1 = new Put(Bytes.toBytes(rowKey));
            put1.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName), Bytes.toBytes(columnValue));
            table.put(put1);
            table.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据rowKey删除一行数据
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    public static void deleteData(TableName tableName,String rowKey) throws IOException {
        Table table = conn.getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        table.delete(delete);
        table.close();
    }

    /**
     * 删除某一行的某一个列簇内容
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @throws IOException
     */
    public static void deleteData(TableName tableName,String rowKey,String columnFamily) throws IOException {
        Table table = conn.getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addFamily(Bytes.toBytes(columnFamily));
        table.delete(delete);
        table.close();
    }

    /**
     * 删除某一行某个列簇某列的值
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnName
     * @throws IOException
     */
    public static void deleteData(TableName tableName,String rowKey,String columnFamily,String columnName) throws IOException {
        Table table = conn.getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
        table.delete(delete);
        table.close();
    }

    /**
     * 删除某一行某个列簇多个列的值
     * @param tableName
     * @param rowKey
     * @param columnFamily
     * @param columnNames
     * @throws IOException
     */
    public static void deleteData(TableName tableName,String rowKey,String columnFamily,List<String> columnNames) throws IOException {
        Table table = conn.getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        for(String columnName:columnNames){
            delete.addColumns(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName));
        }
        table.delete(delete);
        table.close();
    }
    /**
     *根据rowKey查询数据
     * @throws IOException
     */
    public static void getResult(TableName tableName, String rowKey) throws IOException {
        Table table = conn.getTable(tableName);
        //获得一行
        Get get = new Get(Bytes.toBytes(rowKey));
        Result set = table.get(get);
        Cell[] cells  = set.rawCells();
        for(Cell cell : cells) {
            System.out.println(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())+"::"+
                    Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
        table.close();
    }

    /**
     * 全表扫描
     * @param tableName
     * @throws IOException
     */
    public static  void scanTable(TableName tableName) throws IOException {
        Table table = conn.getTable(tableName);
        Scan scan = new Scan();
        ResultScanner rsacn = table.getScanner(scan);
        for(Result rs:rsacn) {
            String rowkey = Bytes.toString(rs.getRow());
            System.out.println("row key :"+rowkey);
            Cell[] cells  = rs.rawCells();
            for(Cell cell : cells) {
                System.out.println(Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength())+"::"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())+"::"+
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            }
            System.out.println("-----------------------------------------");
        }
    }


    //过滤器 LESS <  LESS_OR_EQUAL <=  EQUAL =   NOT_EQUAL <>  GREATER_OR_EQUAL >=   GREATER >   NO_OP 排除所有

    /**
     * rowKey过滤器
     * @param tableName
     * @throws IOException
     */
    public static  void rowkeyFilter(TableName tableName) throws IOException {
        Table table = conn.getTable(tableName);
        Scan scan = new Scan();
        RowFilter filter = new RowFilter(CompareOperator.EQUAL,new RegexStringComparator("Key1$"));//str$ 末尾匹配，相当于sql中的 %str  ^str开头匹配，相当于sql中的str%
        scan.setFilter(filter);
        ResultScanner scanner  = table.getScanner(scan);
        for(Result rs:scanner) {
            String rowkey = Bytes.toString(rs.getRow());
            System.out.println("row key :"+rowkey);
            Cell[] cells  = rs.rawCells();
            for(Cell cell : cells) {
                System.out.println(Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength())+"::"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())+"::"+
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            }
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * 列值过滤器
     * @throws IOException
     */
    public static void singColumnFilter(TableName tableName) throws IOException {
        Table table = conn.getTable(tableName);
        Scan scan = new Scan();
        //下列参数分别为，列族，列名，比较符号，值
        SingleColumnValueFilter filter =  new SingleColumnValueFilter( Bytes.toBytes("author"),  Bytes.toBytes("name"),
                CompareOperator.EQUAL,  Bytes.toBytes("spark")) ;
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for(Result rs:scanner) {
            String rowkey = Bytes.toString(rs.getRow());
            System.out.println("row key :"+rowkey);
            Cell[] cells  = rs.rawCells();
            for(Cell cell : cells) {
                System.out.println(Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength())+"::"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())+"::"+
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            }
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * 列名前缀过滤器
     * @throws IOException
     */
    public static void columnPrefixFilter(TableName tableName) throws IOException {
        Table table = conn.getTable(tableName);
        Scan scan = new Scan();
        ColumnPrefixFilter filter = new ColumnPrefixFilter(Bytes.toBytes("name"));
        scan.setFilter(filter);
        ResultScanner scanner  = table.getScanner(scan);
        for(Result rs:scanner) {
            String rowkey = Bytes.toString(rs.getRow());
            System.out.println("row key :"+rowkey);
            Cell[] cells  = rs.rawCells();
            for(Cell cell : cells) {
                System.out.println(Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength())+"::"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())+"::"+
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            }
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * 过滤器集合
     * @throws IOException
     */
    public static void filterSet(TableName tableName) throws IOException {
        Table table = conn.getTable(tableName);
        Scan scan = new Scan();
        FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        SingleColumnValueFilter filter1 =  new SingleColumnValueFilter( Bytes.toBytes("author"),  Bytes.toBytes("name"),
                CompareOperator.EQUAL,  Bytes.toBytes("spark")) ;
        ColumnPrefixFilter filter2 = new ColumnPrefixFilter(Bytes.toBytes("name"));
        list.addFilter(filter1);
        list.addFilter(filter2);

        scan.setFilter(list);
        ResultScanner scanner  = table.getScanner(scan);
        for(Result rs:scanner) {
            String rowkey = Bytes.toString(rs.getRow());
            System.out.println("row key :"+rowkey);
            Cell[] cells  = rs.rawCells();
            for(Cell cell : cells) {
                System.out.println(Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength())+"::"+Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength())+"::"+
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            }
            System.out.println("-----------------------------------------");
        }

    }

}
