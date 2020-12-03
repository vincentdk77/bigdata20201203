package com.chuangrui.version_3.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class ConnectionSignMgrUtils {

    public static Statement st = null ;
    public static Connection conn = null;

    public static void main(String args[]) {
            String s = "yanzhengma,1,123123dw,您的验证码是：123456";
            ArrayList a = new ArrayList();
            a.add(s);
        try {
            savePredictSign(a);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static HashMap<String,Integer> getLevelSign(Integer level ) {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        try{
            Statement st = getSt();
            ResultSet rs = st.executeQuery("select distinct code,level from sign where id in(select distinct parent_id from sign) and level ="+level);

            while(rs.next()) {
                Integer l =  rs.getInt("level");
                String code =  rs.getString("code");
                map.put(code,l);
            }
            rs.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void saveTestSuit(List<String> lists) throws  Exception{
            StringBuilder builder = new StringBuilder( "insert into test_unit (sign,content,status,add_time,update_time) values ");
            for(String ss:lists) {
               String sc[] = ss.split(",");
               builder.append("('"+sc[0]+"','"+sc[1].replace("'","")+"',0,now(),now()),");
            }
            String sql = builder.toString().substring(0,builder.toString().length()-1);
            Statement st = getSt();
             st.execute(sql);
    }

    public static void savePredictSign(List<String> lists) throws  Exception{
        StringBuilder builder = new StringBuilder( "insert into predict_sign(content,predict_sign,batch_id,add_time,uuid) values ");
        for(String ss:lists) {
            String sc[] = ss.split(",");
            builder.append("('"+sc[3]+"','"+sc[0]+"',"+sc[1]+",now(),'"+sc[2]+"'),");
        }
        String sql = builder.toString().substring(0,builder.toString().length()-1);
        System.out.println(sql);
        Statement st = getSt();
        st.execute(sql);
    }

    public static HashMap<String,Integer> getSign(Integer level, Integer parendId) {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        try{
            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id,code from sign where level="+level+" and  parent_id="+parendId);
            System.out.println();
            while(rs.next()) {
                Integer id =  rs.getInt("id");
                String sign =  rs.getString("code");
                map.put(sign,id);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<Integer,String> getSignId(Integer level, Integer parendId) {
        HashMap<Integer,String> map = new HashMap<Integer,String>();
        try{
            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id,code from sign where level="+level+" and  parent_id="+parendId);
            System.out.println();
            while(rs.next()) {
                Integer id =  rs.getInt("id");
                String sign =  rs.getString("code");
                map.put(id,sign);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<Integer,String> getAllSignId() {
        HashMap<Integer,String> map = new HashMap<Integer,String>();
        try{
            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id,code from sign  ");
            System.out.println();
            while(rs.next()) {
                Integer id =  rs.getInt("id");
                String sign =  rs.getString("code");
                map.put(id,sign);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static  Integer  getSignIdByCode(String code) {
        Integer id = null;
        try{
            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id  from sign where code='"+code+"'");
            System.out.println();
            while(rs.next()) {
                  id =  rs.getInt("id");
            }
            rs.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static String[] getWords(String signCode,Integer level) {
        String array[] = null;
        try {
            String timeStr = selectMaxTime( signCode, level);
            int number = 0;
            System.out.println("time time :"+timeStr);
            Statement st =  getSt();
            String countSql = "select count(1) number from word where sign_code='"+signCode+"' and level="+level+" and date_time ='"+timeStr+"'";
            ResultSet countRs = st.executeQuery(countSql);
            while (countRs.next()) {
                number = countRs.getInt("number");
            }
            array = new String[number];
            String sql ="select word from word where sign_code='"+signCode+"' and level="+level+" and  date_time = '"+timeStr+"' order by id asc";
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

    public static void saveWord(String array[],String timeStr,String signCode,Integer level) throws Exception{

            Statement st = getSt();
//            String sql ="delete from word";
//            st.execute(sql);
            String insertSql ="insert into word values ";
            for(int i=1;i<=array.length;i++) {
                insertSql = insertSql+"(null,'"+array[i-1]+"','"+timeStr+"',"+level+",'"+signCode+"'),";
            }
            insertSql = insertSql.substring(0,insertSql.length()-1);
            st.execute(insertSql);


    }

    public static String selectMaxTime(String signCode,Integer level) {
        String time = "";
        try{
           Statement st = getSt();
            Date date = new Date();
            String sql ="select max(date_time) date_time from word where sign_code='"+signCode+"' and level="+level;
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
            conn = DriverManager.getConnection("jdbc:mysql://61.132.230.78:3306/signMgr?autoReconnect=true&amp;autoReconnectForPools=true&amp;default-character-set=utf8mb4","chuangrui1","chuangrui@123");
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


}
