package com.chuangrui.old;

import com.alibaba.fastjson.JSONObject;
import com.chuangrui.utils.SSHUtils;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;



public class GetChuangRuiHistoryData {
    //这里可以设置数据库名称
    private final static String URL = "jdbc:sqlserver://115.159.148.188:1433;DatabaseName=GenHis";
    private static final String USER="sqlw";
    private static final String PASSWORD="sqlw_user_789";

    public static String LOCAL_MSG_PATH="/home/getFromMysql/history/";
//    public static String LOCAL_MSG_PATH="D:\\historyMessage\\msg\\";

    private static Connection conn=null;
    //静态代码块（将加载驱动、连接数据库放入静态块中）
    static{
        try {
            //1.加载驱动程序
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //2.获得数据库的连接
            conn=(Connection) DriverManager.getConnection(URL,USER,PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //对外提供一个方法来获取数据库连接
    public static Connection getConnection(){
        return conn;
    }


    //测试用例
    public static void main(String[] args) throws Exception{

        String startTime = "2015-08-21";
//        String endTime = "2015-08-22";
        String endTime = "2017-08-11";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long start = format.parse(startTime).getTime();
            long end = format.parse(endTime).getTime();
            while(start <=end) {

               if(format.format(start).endsWith("1") && !format.format(start).endsWith("31")) {
                   String tableTime = format.format(start).substring(0,9).replace("-","");
                   System.out.println(tableTime);
                   getData(tableTime);

               }
                start = start+24*3600*1000l;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void getData(String tableTime) {
        try {
            SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            //通过数据库的连接操作数据库，实现增删改查
            Statement stmt = conn.createStatement();
            String maxSql = "select max(SendTime) endSend,min(SendTime) startSend from SmsApiDetail"+tableTime;
            System.out.println("maxSql:"+maxSql);
            ResultSet maxRs = stmt.executeQuery(maxSql);
            Integer number = 0;
            long startTime=0;
            long endTime = 0;
            while (maxRs.next()) {
               endTime = dayFormat.parse(dayFormat.format(format.parse(maxRs.getString("endSend")).getTime())).getTime();
                startTime = dayFormat.parse(dayFormat.format(format.parse(maxRs.getString("startSend")).getTime())).getTime();

            }
            System.out.println("startTime:"+dayFormat.format(startTime)+" endTime:"+dayFormat.format(endTime));
            while(startTime<=endTime) {
                System.out.println(dayFormat.format(startTime));

                FileWriter fw = new FileWriter(LOCAL_MSG_PATH+"msg-"+dayFormat.format(startTime) + ".txt", true);
                PrintWriter pw = new PrintWriter(fw);
                int sum = 0;
                String countSql = "select count(1) as number from SmsApiDetail"+tableTime+"  where SendTime >='"+dayFormat.format(startTime)+" 00:00:00' and SendTime <='"+dayFormat.format(startTime)+" 23:59:59'";
                ResultSet countRs = stmt.executeQuery(countSql);
                while (countRs.next()) {
                    sum = countRs.getInt("number");
                }
                System.out.println("sum :" + sum);
                int pageSize = 100000;
                int pageCount = 0;
                if (sum % pageSize == 0) {
                    pageCount = sum / pageSize;
                } else {
                    pageCount = sum / pageSize + 1;
                }
                int index = 0;
                for (int j = 0; j < pageCount; j++) {

                    String tempSql = "select  distinct top 100000  detail.ID,detail.CustomerId,detail.SubmitTime,detail.SendTime,detail.Mobile,detail.Content,report.MobileDesc,detail.DoneTime from SmsApiDetail"+tableTime+"  detail" +
                            " left join SmsApiReport"+tableTime+" report on report.SendId = detail.SendId and report.Mobile=detail.Mobile" +
                            "  where detail.id not in (select top "+(j*100000)+" id from SmsApiDetail"+tableTime+") and SendTime >='"+dayFormat.format(startTime)+" 00:00:00' and SendTime <='"+dayFormat.format(startTime)+" 23:59:59'";
                    System.out.println(tempSql);
                    ResultSet rs = stmt.executeQuery(tempSql);
                    while (rs.next()) {

                        JSONObject json = new JSONObject();
                        json.put("id",rs.getString("ID"));
                        json.put("batchId","");
                        json.put("customerId",rs.getString("CustomerId"));
                        json.put("mobile",rs.getString("Mobile"));
                        json.put("mobileOperator","");
                        json.put("content",rs.getString("Content"));
                        json.put("sign","");
                        json.put("smCount","");
                        json.put("categoryId","");
                        json.put("validateFlag","");
                        json.put("validateTime","");
                        json.put("validateAuthor","");
                        json.put("smUuid","");
                        json.put("scheduleSendTIme","");
                        json.put("scheduleSendFlag","");
                        json.put("suggestChannelId","");
                        json.put("ctime",rs.getString("SendTime"));
                        json.put("ltime","");
                        json.put("remoteIp","");
                        json.put("channelId","");
                        json.put("submitTime",rs.getString("SubmitTime"));
                        json.put("submitResult","");
                        json.put("deliverTime",rs.getString("DoneTime"));
                        json.put("deliverResult",rs.getString("MobileDesc"));
                        json.put("submitResultReal","");
                        json.put("deliverResultReal","");
                        json.put("status","");
                        pw.println(json.toJSONString());

                    }
                }
                pw.flush();
                pw.close();
                fw.close();

                startTime = startTime+24*3600*1000l;
                //压缩 /home/test目录下的文件  gzip /home/test/a.txt 生成的压缩文件是/home/test/a.txt.gz
                SSHUtils.executeCmdOnNode4("gzip "+LOCAL_MSG_PATH+"msg-"+dayFormat.format(startTime) + ".txt");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
