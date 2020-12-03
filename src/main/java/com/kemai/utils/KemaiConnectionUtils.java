package com.kemai.utils;

import com.alibaba.fastjson.JSONObject;
import com.chuangrui.model.ChannelBlack;
import com.chuangrui.utils.CodeUtils;
import com.chuangrui.utils.EmojiFilter;
import com.chuangrui.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class KemaiConnectionUtils {

    public static Statement st = null ;
    public static Connection conn = null;

    public static void main(String args[]) throws Exception {

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



    public static void saveKemaiTime(String timeStr) {
        try{

            Statement st = getSt();

            String insertSql ="insert into kemai_date values (null,'"+timeStr+"') ";

            st.execute(insertSql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String selectMaxTime() {
        String time = "";
        try{
           Statement st = getSt();
            Date date = new Date();

            String sql ="select max(add_time) date_time from kemai_date";
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
            conn = DriverManager.getConnection("jdbc:mysql://61.132.230.78:3306/bigdata?autoReconnect=true&autoReconnectForPools=true&rewriteBatchedStatements=true","chuangrui1","chuangrui@123");
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
