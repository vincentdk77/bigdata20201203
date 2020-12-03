package com.chuangrui.export;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chuangrui.utils.FtpClientUtils;
import com.chuangrui.utils.SSHUtils;
import com.chuangrui.utils.ZIPUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 查询sql需要给机器ip 开白名单，本机器出网ip 为36.7.84.60 ，
 */

public class ExportFromMysql {

    public static String DB_URL ="jdbc:mysql://cloudsp1.mysql.rds.aliyuncs.com:3306/crdb";
    public static String DB_URL1 ="jdbc:mysql://cloudsp1.mysql.rds.aliyuncs.com:3306/crdb_1";
    public static String DB_URL2 ="jdbc:mysql://cloudsp1.mysql.rds.aliyuncs.com:3306/crdb_2";
    public static String USER=  "cr";
    public static String PASS ="Chuangrui@8254";
    public static String UPLOAD_PATH = "/home/test/";
    public static String COMMON_CODE_FILE_NAME="common-code";
    public static String LOCAL_COMMON_CODE_PATH = "D:\\historyMessage\\code\\";
    public static String LOCAL_REPLY_PATH="D:\\historyMessage\\reply\\";
    public static String LOCAL_MSG_PATH="D:\\historyMessage\\msg\\";
    public static String TXT = ".txt";
    public static String ZIP = ".zip";
    public static String HDFS_DIR_PRE = "/data/message";
    public static String HDFS_REPLY_DIR_PRE = "/data/message/reply/";
    public static String HDFS_MSG_DIR_PRE = "/data/message/msg/";
    public static String MSG_FILENAME_PRE = "msg-";
    public static String REPLY_FILENAME_PRE = "reply-";


    public String startDateStr;
    public String endDateStr;

    public ExportFromMysql(String startDateStr, String endDateStr) {
        this.startDateStr = startDateStr;
        this.endDateStr = endDateStr;
    }



    public static void main(String args[]) {


//        findReplyMsg("D:\\historyMessage\\msg\\msg-2019-04-14.zip");
        String startDateStr = "2019-05-06";
        String endDateStr = "2019-05-08";
        getLost(startDateStr,endDateStr);

//        String startDateStr =   "2019-04-20";
//        String endDateStr = "2019-04-20";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            long start = format.parse(startDateStr).getTime();
//            long end = format.parse(endDateStr).getTime();
//            while (start<=end) {
//                getReplyFromMysql(format.format(start));
//                start = start+24*3600*1000l;
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }

    private static void getReplyFromMysql(String startDate) {
        System.out.println(startDate+" 开始");
        try {
            String filename = REPLY_FILENAME_PRE+startDate;
            FileWriter fw = new FileWriter(LOCAL_REPLY_PATH + filename + TXT, true);
            PrintWriter pw = new PrintWriter(fw);
            Connection conn = getConn("0");
            Statement st = conn.createStatement();
            String sql = " SELECT id,`customerId` ,`mobile` ,`content` ,`extNo` ,`deliverTime` ,`channelId` ,`ctime`,`ltime`   FROM `crdb`.`tbl_customer_mo_record`where ctime BETWEEN '"+startDate+" 00:00:00' and '"+startDate+" 23:59:59' ";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("id",rs.getString("id"));
                json.put("customerId",rs.getString("customerId"));
                json.put("mobile",rs.getString("mobile"));
                json.put("content",rs.getString("content"));
                json.put("extNo",rs.getString("extNo"));
                json.put("deliverTime",rs.getString("deliverTime"));
                json.put("channelId",rs.getString("channelId"));
                json.put("ctime",rs.getString("ctime"));
                json.put("ltime",rs.getString("ltime"));
                pw.println(json.toJSONString());
            }
            System.out.println("reply "+startDate+" 已经完成");
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();

            //上传到node1 /home/test目录下
            FileInputStream in = new FileInputStream(new File(LOCAL_REPLY_PATH+filename+TXT));
            boolean flag = FtpClientUtils.uploadFile( UPLOAD_PATH, filename+TXT, in);

            //压缩
            SSHUtils.executeCmd("gzip /home/test/"+filename+TXT);
            //上传到hdfs上面
            String timePath = startDate.replace("-","/")+"/";
            String cmd = "/ks/hadoop/hadoop-2.7.7/bin/hdfs dfs -put "+UPLOAD_PATH+filename+TXT+".gz"+" "+HDFS_REPLY_DIR_PRE+timePath;
            System.out.println(cmd);
            String result = SSHUtils.uploadToHdfs(cmd,filename+TXT+".gz",timePath,HDFS_REPLY_DIR_PRE);
            System.out.println(result);
            //压缩文件
//            ZIPUtil.compress(LOCAL_REPLY_PATH + filename + TXT,LOCAL_REPLY_PATH + filename + ZIP);
            //删除本地当前txt文件
//            File deleteFile = new File(LOCAL_REPLY_PATH + filename + TXT);
//            if(deleteFile.exists()) {
//                deleteFile.delete();
//            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getLost(String startDateStr, String endDateStr) {
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            long startDate = format.parse(startDateStr).getTime();
            long endDate = format.parse(endDateStr).getTime();
            while(startDate <= endDate) {
                long start = System.currentTimeMillis();
                //从mysql下载回复短信数据
                getReplyFromMysql(format.format(startDate));

                //从mysql下载发送短信数据
//                getDataFromMysql(format.format(startDate));
//                //找发出去的短信的回复短信
//                System.out.println("get-reply-msg "+format.format(startDate)+" start");
//                findReplyMsg(LOCAL_MSG_PATH+MSG_FILENAME_PRE+format.format(startDate)+ZIP);
//                System.out.println("get-reply-msg "+format.format(startDate)+" end");
//                System.out.println("use time"+(System.currentTimeMillis() - start));
                startDate = startDate+24*3600*1000l;
            }
//            getCommonCodeFromMysql();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDataFromMysql(String startDate ) {
        String filename = MSG_FILENAME_PRE+startDate;
        try {

            //从44张表里查询当天的数据，写入同一个文件中
//            String type[] = {"marketing","identify"};
//            String db[] = {"crdb_1","crdb_2"};

            String type[] = {"identify","marketing"};
            String db[] = {"crdb_1","crdb_2"};
            for(String database : db) {
                for(String t : type) {
                    for(int i=0;i<=10;i++) {

                        if(t.equals("identify") && database.equals("crdb_1")){

                        } else if(t.equals("marketing") && database.equals("crdb_1") && i<=7) {

                        } else    {

                            FileWriter fw = new FileWriter("D:\\historyMessage\\msg\\msg-" + startDate + ".txt", true);
                            PrintWriter pw = new PrintWriter(fw);

                            Connection conn = null;
                            if ("crdb_1".equals(database)) {
                                conn = getConn("1");
                            } else {
                                conn = getConn("2");
                            }
                            Statement st = conn.createStatement();
                            String sql = null;
                            String countSql = "";
                            if (i == 0) {
                                sql = "select id,batchId,customerId,mobile,mobileOperator,content,sign,smCount,categoryId,validateFlag," +
                                        "validateTime,validateAuthor,smUuid,scheduleSendTIme,scheduleSendFlag,suggestChannelId,ctime,ltime,remoteIp," +
                                        "channelId,submitTime,submitResult,deliverTime,deliverResult,submitResultReal,deliverResultReal,status from " +
                                        "`" + database + "`.`tbl_app_" + t + "_batch_record_" + i + "` where ctime BETWEEN '" + startDate + " 00:00:00' and '" + startDate + " 23:59:59'  ";
                                countSql = "select count(1) number from " + "`" + database + "`.`tbl_app_" + t + "_batch_record_" + i + "` where ctime BETWEEN '" + startDate + " 00:00:00' and '" + startDate + " 23:59:59'";
                            } else {
                                String joinTable = null;
                                if ("identify".equals(t)) {
                                    joinTable = "`crdb`.`tbl_app_identify_batch`";
                                } else {
                                    joinTable = "`crdb`.`tbl_app_marketing_batch`";
                                }
                                sql = "select r1.id,r1.batchId,r1.customerId,r1.mobile,r1.mobileOperator,batch.template   content,r1.sign,r1.smCount,r1.categoryId,r1.validateFlag," +
                                        "r1.validateTime,r1.validateAuthor,r1.smUuid,r1.scheduleSendTIme,r1.scheduleSendFlag,r1.suggestChannelId,r1.ctime,r1.ltime,r1.remoteIp," +
                                        "r1.channelId,r1.submitTime,r1.submitResult,r1.deliverTime,r1.deliverResult,r1.submitResultReal,r1.deliverResultReal,r1.status from " +
                                        "`" + database + "`.`tbl_app_" + t + "_batch_record_" + i + "` r1 left join " + joinTable + " batch on batch.id=r1.`batchId` where r1.ctime BETWEEN '" + startDate + " 00:00:00' and '" + startDate + " 23:59:59'  ";

                                countSql = "select count(1)  number from " + "`" + database + "`.`tbl_app_" + t + "_batch_record_" + i + "` where ctime BETWEEN '" + startDate + " 00:00:00' and '" + startDate + " 23:59:59'";
                            }
                            System.out.println(countSql);
                            System.out.println(sql);

                            ResultSet countRs = st.executeQuery(countSql);
                            int sum = 0;
                            while (countRs.next()) {
                                sum = countRs.getInt("number");
                            }
                            System.out.println("sum :" + sum);
                            int pageSize = 10000;
                            int pageCount = 0;
                            if (sum % pageSize == 0) {
                                pageCount = sum / pageSize;
                            } else {
                                pageCount = sum / pageSize + 1;
                            }
                            Integer index = 0;
                            for (int j = 0; j < pageCount; j++) {
                                if (j >= 0) {
                                    String tempSql = sql + " limit " + j * pageSize + "," + pageSize;
                                    System.out.println(" limit " + j * pageSize + "," + pageSize);
                                    write10000(tempSql, st, index, pw, t, conn);
                                }
                            }

                            pw.flush();
                            fw.flush();
                            pw.close();
                            fw.close();
                            System.out.println("num:" + index);

                        }
                    }
                }
            }
            System.out.println("message "+startDate+" 已经完成");

            //上传到node1 /home/test目录下
            FileInputStream in = new FileInputStream(new File(LOCAL_MSG_PATH+filename+TXT));
            boolean flag = FtpClientUtils.uploadFile( UPLOAD_PATH, filename+TXT, in);

            //压缩 /home/test目录下的文件  gzip /home/test/a.txt 生成的压缩文件是/home/test/a.txt.gz
            SSHUtils.executeCmd("gzip /home/test/"+filename+TXT);

            //上传到hdfs上面
            String timePath = startDate.replace("-","/")+"/";
            String cmd = "/ks/hadoop/hadoop-2.7.7/bin/hdfs dfs -put "+UPLOAD_PATH+filename+TXT+".gz"+" "+HDFS_MSG_DIR_PRE+timePath;
            System.out.println(cmd);
            String result = SSHUtils.uploadToHdfs(cmd,filename+TXT+".gz",timePath,HDFS_MSG_DIR_PRE);
            System.out.println(result);

            //压缩文件
            ZIPUtil.compress(LOCAL_MSG_PATH+filename+TXT,LOCAL_MSG_PATH+filename+ZIP);
            //删除本地当前txt文件
            File deleteFile = new File(LOCAL_MSG_PATH + filename + TXT);
            if(deleteFile.exists()) {
                deleteFile.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write10000(String sql,Statement st,Integer index,PrintWriter pw,String type,Connection conn) {
         boolean flag = true;
         try {
             ResultSet rs = st.executeQuery(sql);
             while (rs.next()) {
                 index++;
                 JSONObject json = new JSONObject();
                 json.put("id", rs.getString("id"));
                 json.put("batchId", rs.getString("batchId"));
                 json.put("customerId", rs.getString("customerId"));
                 json.put("mobile", rs.getString("mobile"));
                 json.put("mobileOperator", rs.getString("mobileOperator"));
                 json.put("content", rs.getString("content"));
                 json.put("sign", rs.getString("sign"));
                 json.put("smCount", rs.getString("smCount"));
                 json.put("categoryId", rs.getString("categoryId"));
                 json.put("validateFlag", rs.getString("validateFlag"));
                 json.put("validateTime", rs.getString("validateTime"));
                 json.put("validateAuthor", rs.getString("validateAuthor"));
                 json.put("smUuid", rs.getString("smUuid"));
                 json.put("scheduleSendTIme", rs.getString("scheduleSendTIme"));
                 json.put("scheduleSendFlag", rs.getString("scheduleSendFlag"));
                 json.put("suggestChannelId", rs.getString("suggestChannelId"));
                 json.put("ctime", rs.getString("ctime"));
                 json.put("ltime", rs.getString("ltime"));
                 json.put("remoteIp", rs.getString("remoteIp"));
                 json.put("channelId", rs.getString("channelId"));
                 json.put("submitTime", rs.getString("submitTime"));
                 json.put("submitResult", rs.getString("submitResult"));
                 json.put("deliverTime", rs.getString("deliverTime"));
                 json.put("deliverResult", rs.getString("deliverResult"));
                 json.put("submitResultReal", rs.getString("submitResultReal"));
                 json.put("deliverResultReal", rs.getString("deliverResultReal"));
                 json.put("status", rs.getString("status"));
                 pw.println(json.toJSONString());
             }
         } catch (Exception e) {
             e.printStackTrace();
             flag = false;
         } finally {
                //处理失败
              if(!flag) {
                  try {
                      Thread.sleep(5000);
                      if(st != null) {
                          st.close();
                      }
                      if(conn != null) {
                          conn.close();;
                      }
                      conn = getConn(type);
                      st =conn.createStatement();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
                  write10000(sql, st, index, pw, type,conn);
              }
         }

    }

    public static Connection getConn(String type) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if("1".equals(type)) {
                conn = DriverManager.getConnection(DB_URL1,USER,PASS);
            } else if("2".equals(type)) {
                conn = DriverManager.getConnection(DB_URL2,USER,PASS);
            } else {
                conn = DriverManager.getConnection(DB_URL,USER,PASS);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 在发送短信中，找回复短信的发送内容
     */
    public static void findReplyMsg(String msgFile) {
        try {
            Charset gbk = Charset.forName("utf-8");
            String fileName = new File(msgFile).getName().split("\\.")[0];
            System.out.println(fileName);
            String dateStr = fileName.substring(MSG_FILENAME_PRE.length());
            //发送出去的短信，回复短信可能在几天之后，从当前时间向后推5天找回复的短信
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            long startDate = format.parse(dateStr).getTime();
            long endDate = startDate+5*24*3600*1000l;
            Map<String,JSONObject> map = new HashMap<String, JSONObject>();
            while(startDate <= endDate) {
                //回复
                String replyFilePath = LOCAL_REPLY_PATH+REPLY_FILENAME_PRE+format.format(startDate)+TXT;

                File file = new File(replyFilePath);
                BufferedReader reader = null;
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    JSONObject json = JSON.parseObject(line);
                    map.put(json.getString("customerId")+json.getString("mobile")+json.getString("channelId"),json);
                }
                reader.close();
                startDate = startDate+24*3600*1000l;
            }
            //回复短信找到发送短信，写入的文件
            File msgReplyFile = new File("D:\\historyMessage\\msg-reply\\msg-reply-"+dateStr+TXT);
            FileWriter fw = new FileWriter(msgReplyFile, false);
            PrintWriter pw = new PrintWriter(fw);

            //发送短信
            ZipFile msgZf = new ZipFile(msgFile);
            InputStream msgIn = new BufferedInputStream(new FileInputStream(msgFile));
            ZipInputStream msgZin = new ZipInputStream(msgIn, gbk);
            ZipEntry ze;
            while ((ze = msgZin.getNextEntry()) != null) {
                if (ze.toString().endsWith("txt")) {
                    BufferedReader br = new BufferedReader( new InputStreamReader(msgZf.getInputStream(ze)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        JSONObject json = JSON.parseObject(line);
                        JSONObject replyContent = map.get(json.getString("customerId")+json.getString("mobile")+json.getString("channelId"));
                        if(replyContent != null) {
                            json.put("replyContent",replyContent.getString("content"));
                            json.put("replyChannelId",replyContent.getString("channelId"));
                            pw.println(json.toJSONString());
                        }
                    }
                    br.close();
                }
            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
            msgZin.close();
            msgIn.close();
            msgZf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
