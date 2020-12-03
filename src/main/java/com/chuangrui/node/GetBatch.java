package com.chuangrui.node;

import com.alibaba.fastjson.JSONObject;
import com.chuangrui.utils.SSHUtils;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetBatch {

    public static void main(String args[]) {

        String startDate = args[0];
        String endDate = args[1];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long start =format.parse(startDate).getTime();
            long end =format.parse(endDate).getTime();
            long day = 24*3600*1000l;
            while(start<=end) {
                getBatchMysql(format.format(new Date(start)));
                start = start+day;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void getBatchMysql(String startDate ) {
        String filename = "batch-"+startDate;
        try {



            String type[] = {"identify","marketing"};


                for(String t : type) {



                        FileWriter fw = new FileWriter("/batch/"+filename + ".txt", true);
                        PrintWriter pw = new PrintWriter(fw);

                        Connection conn =   getConn("3");

                        Statement st = conn.createStatement();
                        String sql = null;
                        String countSql = "";

                            sql = "SELECT id,customerId,count,mobileCount,unicomCount,telecomCount,smCount,template,sign,`categoryId` ,validateFlag, " +
                                    "validateTime,validateDesc,validateAuthor,`validateComment`,`sendFrom` ,`scheduleSendTime` ,`scheduleSendFlag` , " +
                                    "`mobileSuggestChannelId` ,`unicomSuggestChannelId` ,`telecomSuggestChannelId` , dePercent,deSuccessPercent " +
                                    ",   ctime,ltime,`destatus` ,`areaFlag` ,`personalFlag` ,`taskName` ,`pauseFlag` ,`tag` ,`tagFlag`  " +
                                    " from `crdb`.`tbl_app_" + t + "_batch` where ctime BETWEEN '" + startDate + " 00:00:00' and '" + startDate + " 23:59:59'  ";

                        System.out.println(sql);
                                ResultSet rs = st.executeQuery(sql);
                                while (rs.next()) {
                                    JSONObject json = new JSONObject();
                                    json.put("id", rs.getString("id"));
                                    json.put("customerId", rs.getString("customerId"));
                                    json.put("count", rs.getString("count"));
                                    json.put("mobileCount", rs.getString("mobileCount"));
                                    json.put("unicomCount", rs.getString("unicomCount"));
                                    json.put("telecomCount", replace(rs.getString("telecomCount") ));
                                    json.put("smCount", rs.getString("smCount"));
                                    json.put("template", rs.getString("template"));
                                    json.put("sign", rs.getString("sign"));
                                    json.put("categoryId", rs.getString("categoryId"));
                                    json.put("validateFlag", rs.getString("validateFlag"));
                                    json.put("validateTime", rs.getString("validateTime"));
                                    json.put("validateDesc", rs.getString("validateDesc"));
                                    json.put("validateAuthor", rs.getString("validateAuthor"));
                                    json.put("validateComment", rs.getString("validateComment"));
                                    json.put("sendFrom", rs.getString("sendFrom"));
                                    json.put("scheduleSendTIme", rs.getString("scheduleSendTIme"));
                                    json.put("scheduleSendFlag", rs.getString("scheduleSendFlag"));
                                    json.put("mobileSuggestChannelId", rs.getString("mobileSuggestChannelId"));
                                    json.put("unicomSuggestChannelId", rs.getString("unicomSuggestChannelId"));
                                    json.put("telecomSuggestChannelId", rs.getString("telecomSuggestChannelId"));
                                    json.put("dePercent", rs.getString("dePercent"));
                                    json.put("deSuccessPercent", rs.getString("deSuccessPercent"));
                                    json.put("ctime", rs.getString("ctime"));
                                    json.put("ltime", rs.getString("ltime"));
                                    json.put("destatus", rs.getString("destatus"));
                                    json.put("areaFlag", rs.getString("areaFlag"));
                                    json.put("personalFlag", rs.getString("personalFlag"));
                                    json.put("taskName", rs.getString("taskName"));
                                    json.put("pauseFlag", rs.getString("pauseFlag"));
                                    json.put("tag", rs.getString("tag"));
                                    json.put("tagFlag", rs.getString("tagFlag"));

                                    pw.println(json.toJSONString());
                                }


                        pw.flush();
                        fw.flush();
                        pw.close();
                        fw.close();

                }

            System.out.println("batch "+startDate+" 已经完成");

            //压缩 /home/test目录下的文件  gzip /home/test/a.txt 生成的压缩文件是/home/test/a.txt.gz
            SSHUtils.executeCmdOnNode4("gzip "+"/batch/"+filename+".txt");




        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String replace(String str) {
        String destination = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            destination = m.replaceAll("");
        }
        return destination;
    }

    public static Connection getConn(String type) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if("1".equals(type)) {
                conn = DriverManager.getConnection(GetMysqlByNode1.DB_URL1,GetMysqlByNode1.USER,GetMysqlByNode1.PASS);
            } else if("2".equals(type)) {
                conn = DriverManager.getConnection(GetMysqlByNode1.DB_URL2,GetMysqlByNode1.USER,GetMysqlByNode1.PASS);
            } else {
                conn = DriverManager.getConnection(GetMysqlByNode1.DB_URL,GetMysqlByNode1.USER,GetMysqlByNode1.PASS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
