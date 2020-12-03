package com.chuangrui.utils;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChuangFuConnectionUtils {

    public static Statement st = null ;
    public static Connection conn = null;

    public static void main(String args[]) {
//        String array[] = {"aa","bb"};
//        saveWord(array,"2019-04-19");
//        String msg =   "1@@@管娅菲您好，您已恶意违约，平台已多次电话催告，至今仍未收到还款。请立刻还清，否则平台将采取法律手段追回欠款！详询客服热线：0755-66866670，退订回T";
//        String ss[] = msg.split("@@@");
//        saveMessageSortResult(ss[1],"1","重度催收");
        ArrayList<String> list =new ArrayList<String>();
        list.add("13139077768@理财@1");
        list.add("13139077769@理财@1");
        list.add("15638707030@网贷@11132");
        list.add("13163072302@网贷@11132,10857");
        String timeStr="2017-12-08";


        String yearMonth = timeStr.substring(0,timeStr.lastIndexOf("-"));
        System.out.print(yearMonth);
    }

    public static HashMap<String,Integer> getSign() {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        try{

            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id,sign from sign");
            while(rs.next()) {
                Integer id =  rs.getInt("id");
                String sign =  rs.getString("sign");
                map.put(sign,id);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<Integer,String> getSign2() {
        HashMap<Integer,String> map = new HashMap<Integer,String>();
        try{

            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id,sign from sign");
            while(rs.next()) {
                Integer id =  rs.getInt("id");
                String sign =  rs.getString("sign");
                map.put(id,sign);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String[] getWords() {
        String array[] = null;
        try {
            String timeStr = selectMaxTime();
            int number = 0;

            Statement st =  getSt();
            String countSql = "select count(1) number from word where date_time ='"+timeStr+"'";
            ResultSet countRs = st.executeQuery(countSql);
            while (countRs.next()) {
                number = countRs.getInt("number");
            }
            array = new String[number];
            String sql ="select word from word where date_time = '"+timeStr+"' order by id asc";
            ResultSet rs = st.executeQuery(sql);
            int index =0;
            while(rs.next()) {
                String word =  rs.getString("word");
                array[index] = word;
                index ++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public static void saveWord(String array[],String timeStr) {
        try{

            Statement st = getSt();
//            String sql ="delete from word";
//            st.execute(sql);
            String insertSql ="insert into word values ";
            for(int i=1;i<=array.length;i++) {
                insertSql = insertSql+"(null,'"+array[i-1]+"','"+timeStr+"'),";
            }
            insertSql = insertSql.substring(0,insertSql.length()-1);
            st.execute(insertSql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getReplyWords() {
        String array[] = null;
        try {
            String timeStr = selectReplyMaxTime();
            int number = 0;

            Statement st = getSt();
            String countSql = "select count(1) number from reply_word where date_time ='"+timeStr+"'";
            ResultSet countRs = st.executeQuery(countSql);
            while (countRs.next()) {
                number = countRs.getInt("number");
            }
            array = new String[number];
            String sql ="select word from reply_word where date_time = '"+timeStr+"' order by id asc";
            ResultSet rs = st.executeQuery(sql);
            int index =0;
            while(rs.next()) {
                String word =  rs.getString("word");
                array[index] = word;
                index ++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }



    public static void saveReplyWord(String array[],String timeStr) {
        try{

            Statement st = getSt();
//            String sql ="delete from word";
//            st.execute(sql);
            String insertSql ="insert into reply_word values ";
            for(int i=1;i<=array.length;i++) {
                insertSql = insertSql+"(null,'"+array[i-1]+"','"+timeStr+"'),";
            }
            insertSql = insertSql.substring(0,insertSql.length()-1);
            st.execute(insertSql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveMessageSortResult(String msg,String uuid,String sign) {
        try{

            Statement st = getSt();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql ="insert into msg_sort_result(uuid,msg,sign,add_time) values ('"+uuid+"','"+msg+"','"+sign+"','"+format.format(date)+"')";

            st.execute(sql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveReplySortResult(String msg,String uuid,String sign) {
        try{

            Statement st = getSt();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql ="insert into reply_sort_result(uuid,msg,sign,add_time) values ('"+uuid+"','"+msg+"','"+sign+"','"+format.format(date)+"')";

            st.execute(sql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String selectReplyMaxTime() {
        String time = "";
        try{

            Statement st = getSt();
            Date date = new Date();

            String sql ="select max(date_time) date_time from reply_word";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                time = rs.getString("date_time");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String selectMaxTime() {
        String time = "";
        try{
           Statement st = getSt();
            Date date = new Date();

            String sql ="select max(date_time) date_time from word";
            System.out.println("sql:"+sql);
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                time = rs.getString("date_time");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }



    public static Connection getConn() {

        try{
            if(conn != null) {
                return conn;
            }
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://61.132.230.78:3306/cf_bigdata","chuangrui1","chuangrui@123");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Statement getSt() {
        if(st != null) {
            return st;
        }
        try{
            st = getConn().createStatement();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return st;
    }

    public static void saveTestDataSet(String sign, String msg, String uuid) {
        try{

            Statement st = getSt();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql ="insert into test_dataset(uuid,msg,origin_sort,add_time) values ('"+uuid+"','"+msg+"','"+sign+"','"+format.format(date)+"')";
            st.execute(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }





}
