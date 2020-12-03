package com.chuangrui.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadMysqlUtils {

//    public static void main(String args[]) {
//        try {
//            File newFile = new File("E:\\historyMessage\\2018-08-27.txt");
//            int index = 0;
//            FileReader fw = new FileReader(newFile);
//            BufferedReader br = new BufferedReader(fw);
//            String line;
//            while ((line = br.readLine()) != null) {
//                index++;
//            }
//            System.out.println("index:"+index);
//        } catch (Exception e) {
//
//        }
//
//    }

    public static void main(String args[]) {
        String startDateStr = "2018-08-27";
        String endDateStr = "2018-08-27";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long startDate = format.parse(startDateStr).getTime();
            long endDate = format.parse(endDateStr).getTime();
            while(startDate <= endDate) {
               handleByDay( format.format(startDate));
               startDate = startDate+ 24*3600*1000l;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleByDay(String startDate) {
        String filename = startDate;
        System.out.println(new Date());
        System.out.println(filename);
        Connection conn = null;
        Statement stmt = null;
        File newFile = new File("E:\\historyMessage\\"+filename+".txt");
        try{

            // 注册 JDBC 驱动
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(Constant.DB_URL,Constant.USER,Constant.PASS);
            stmt = conn.createStatement();

            int start = 1;
            int number = 1000000;
            while ( start <=7) {
                FileWriter fw = new FileWriter(newFile, true);
                PrintWriter pw = new PrintWriter(fw);
                String sql  = "select customerId,mobile,content,ctime,channelId from tbl_app_identify_batch_record_result20181023 where ctime between "+ startDate+" 00:00:00 and "+ startDate+" 23:59:59 limit "+(start-1)*number+","+start*number+" ";
                System.out.println(sql);
                ResultSet rs = stmt.executeQuery(sql);
                int index =0;
                // 展开结果集数据库
                while(rs.next()){
                    JSONObject json = new JSONObject();
                    // 通过字段检索

                    json.put("customerId", rs.getInt("customerId"));
                    json.put("mobile",rs.getString("mobile"));
                    json.put("content", rs.getString("content"));
                    json.put("ctime", rs.getDate("ctime"));
                    json.put("channelId", rs.getInt("channelId"));
                    pw.println(json.toJSONString());
                }
                pw.flush();
                fw.flush();
                pw.close();
                fw.close();
                start ++;
            }

            // 完成后关闭

            stmt.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
}
