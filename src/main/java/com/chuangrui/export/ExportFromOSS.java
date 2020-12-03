package com.chuangrui.export;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chuangrui.utils.FtpClientUtils;
import com.chuangrui.utils.SSHUtils;
import com.chuangrui.utils.ZIPUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

public class ExportFromOSS implements Runnable{

    public static String Node3Host = "210.5.152.206";
    public static String Node4Host = "210.5.152.207";
    public static String Node5Host = "210.5.152.208";
    public static String LocalHost = "localhost";

    public static String DB_URL ="jdbc:mysql://210.5.152.207:3306/crdb";
    public static String DB_URL1 ="jdbc:mysql://210.5.152.207:3306/crdb_1";
    public static String DB_URL2 ="jdbc:mysql://210.5.152.207:3306/crdb_2";

    public static String USER=  "root";
    public static String PASS ="root";
    public static String UPLOAD_PATH = "/home/test/";
    public static String COMMON_CODE_FILE_NAME="common-code";

    public static String LOCAL_REPLY_PATH="F:\\historyMessage\\reply\\";
    public static String LOCAL_MSG_PATH="F:\\historyMessage\\msg\\";
    public static String TXT = ".txt";
    public static String ZIP = ".zip";
    public static String HDFS_DIR_PRE = "/data/message";
    public static String HDFS_REPLY_DIR_PRE = "/data/message/reply/";
    public static String HDFS_MSG_DIR_PRE = "/data/message/msg/";
    public static String MSG_FILENAME_PRE = "msg-";
    public static String REPLY_FILENAME_PRE = "reply-";


    public String startDateStr;
    public String endDateStr;

    public ExportFromOSS(String startDateStr,String endDateStr) {
        this.startDateStr = startDateStr;
        this.endDateStr = endDateStr;
    }



    public static void main(String args[]) {

        //两个线程一起拉

//        Thread t1 = new Thread(new ExportFromMysql("2019-04-02","2019-04-02"));
//        t1.start();
//        Thread t2 = new Thread( new ExportFromMysql("2019-03-15","2019-03-16"));
//        t2.start();
//

        String host = Node5Host;
//        String startDateStr = "2019-02-08";
//        String endDateStr = "2019-03-06";
//        getLost(startDateStr,endDateStr,"identify","crdb_1",6,host);
//
//        String startDateStr1 = "2019-01-30";
//        String endDateStr1 = "2019-03-06";
//        getLost(startDateStr1,endDateStr1,"marketing","crdb_1",6,host);

//        String startDateStr2 = "2019-01-30";
//        String endDateStr2 = "2019-03-06";
//        getLost(startDateStr2,endDateStr2,"identify","crdb_2",6,host);

//        String startDateStr3 = "2019-02-26";
//        String endDateStr3 = "2019-03-06";
//        getLost(startDateStr3,endDateStr3,"marketing","crdb_2",6,host);


        getLost("2019-01-30","2019-03-06","marketing","crdb_1",10,Node4Host);
        getLost("2019-01-30","2019-03-06","identify","crdb_2",10,Node5Host);
        getLost("2019-01-30","2019-03-06","marketing","crdb_2",10,Node5Host);

//        getLost("2019-01-30","2019-03-06","identify","crdb_1",4,LocalHost);
//        getLost("2019-01-30","2019-03-06","marketing","crdb_1",4,LocalHost);
//        getLost("2019-01-30","2019-03-06","identify","crdb_2",4,LocalHost);
//        getLost("2019-01-30","2019-03-06","marketing","crdb_2",4,LocalHost);
//
//        getLost("2019-01-30","2019-03-06","identify","crdb_1",5,Node3Host);
//        getLost("2019-01-30","2019-03-06","marketing","crdb_1",5,Node3Host);
//        getLost("2019-01-30","2019-03-06","identify","crdb_2",5,Node3Host);
//        getLost("2019-01-30","2019-03-06","marketing","crdb_2",5,Node3Host);

//        findReplyMsg("F:\\historyMessage\\msg\\msg-2019-03-05-crdb_1-identify-0.zip");
//        findReplyMsg("F:\\historyMessage\\msg\\msg-2019-03-06-crdb_1-identify-0.zip");
//        ZIPUtil.compress("F:\\historyMessage\\msg\\msg-2019-03-29.txt","F:\\historyMessage\\msg\\msg-2019-03-29.zip");
//        getLast();


    }

    public void run() {
        System.out.println(startDateStr+" "+endDateStr+" 开启");
//        getLost(startDateStr,endDateStr);
//        System.out.println(startDateStr+" "+endDateStr+" 结束");
    }

    public static void getLost(String startDateStr, String endDateStr,String type ,String dbname, Integer tableIndex,String host) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            long startDate = format.parse(startDateStr).getTime();
            long endDate = format.parse(endDateStr).getTime();
            while(startDate <= endDate) {
                long start = System.currentTimeMillis();
                //从mysql下载发送短信数据
                getDataFromMysql(format.format(startDate),type,dbname,tableIndex,host);
                //找发出去的短信的回复短信
                System.out.println("get-reply-msg "+format.format(startDate)+" start");
                findReplyMsg(LOCAL_MSG_PATH+MSG_FILENAME_PRE+format.format(startDate)+"-"+dbname+"-"+type+"-"+tableIndex+ZIP);
                System.out.println("get-reply-msg "+format.format(startDate)+" end");
                System.out.println("use time"+(System.currentTimeMillis() - start));
                startDate = startDate+24*3600*1000l;
            }
//            getCommonCodeFromMysql();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDataFromMysql(String startDate, String type,String dbname,Integer table,String host) {
        String filename = MSG_FILENAME_PRE+startDate+"-"+dbname+"-"+type+"-"+table;
        try {
            FileWriter fw = new FileWriter("F:\\historyMessage\\msg\\msg-"+startDate+"-"+dbname+"-"+type+"-"+table+".txt",true);
            PrintWriter pw = new PrintWriter(fw);
            Connection conn = null;
            if("crdb_1".equals(dbname)) {
                conn = getConn("1",host);
            } else {
                conn = getConn("2",host);
            }
            Statement st = conn.createStatement();
            String sql = null;
            String countSql = "";
            if(table == 0) {
                sql = "select id,batchId,customerId,mobile,mobileOperator,content,sign,smCount,categoryId,validateFlag," +
                        "validateTime,validateAuthor,smUuid,scheduleSendTIme,scheduleSendFlag,suggestChannelId,ctime,ltime,remoteIp," +
                        "channelId,submitTime,submitResult,deliverTime,deliverResult,submitResultReal,deliverResultReal,status from " +
                        "`"+dbname+"`.`tbl_app_"+type+"_batch_record_"+table+"` where ctime BETWEEN '"+startDate+" 00:00:00' and '"+startDate+" 23:59:59' ";
                countSql = "select count(1) number from " +  "`"+dbname+"`.`tbl_app_"+type+"_batch_record_"+table+"` where ctime BETWEEN '"+startDate+" 00:00:00' and '"+startDate+" 23:59:59'";
            } else {
                String joinTable = null;
                if("identify".equals(type)) {
                    joinTable = "crdb_1.tbl_app_identify_batch";
                } else {
                    joinTable = "crdb_1.tbl_app_marketing_batch";
                }
                sql = "select r1.id,r1.batchId,r1.customerId,r1.mobile,r1.mobileOperator,batch.template   content,r1.sign,r1.smCount,r1.categoryId,r1.validateFlag," +
                        "r1.validateTime,r1.validateAuthor,r1.smUuid,r1.scheduleSendTIme,r1.scheduleSendFlag,r1.suggestChannelId,r1.ctime,r1.ltime,r1.remoteIp," +
                        "r1.channelId,r1.submitTime,r1.submitResult,r1.deliverTime,r1.deliverResult,r1.submitResultReal,r1.deliverResultReal,r1.status from " +
                        "`"+dbname+"`.`tbl_app_"+type+"_batch_record_"+table+"` r1 left join "+joinTable+" batch on batch.id=r1.`batchId` where r1.ctime BETWEEN '"+startDate+" 00:00:00' and '"+startDate+" 23:59:59' ";
                countSql = "select count(1) number from " +  "`"+dbname+"`.`tbl_app_"+type+"_batch_record_"+table+"` where ctime BETWEEN '"+startDate+" 00:00:00' and '"+startDate+" 23:59:59'";
            }
            System.out.println(countSql);
            System.out.println(sql);

            ResultSet countRs = st.executeQuery(countSql);
            int sum = 0;
            while (countRs.next()) {
                sum = countRs.getInt("number");
            }
            System.out.println("sum :"+sum);
            int pageSize = 10000;
            int pageCount = 0;
            if(sum % pageSize == 0) {
                pageCount = sum / pageSize;
            } else {
                pageCount = sum / pageSize+1;
            }
            int index = 0;
            for(int j=0;j<pageCount;j++) {
                String tempSql = sql +" limit "+j*pageSize+","+pageSize;
                System.out.println(" limit "+j*pageSize+","+pageSize);
                ResultSet rs = st.executeQuery(tempSql);
                while (rs.next()) {
                    index++;
                    JSONObject json = new JSONObject();
                    json.put("id",rs.getString("id"));
                    json.put("batchId",rs.getString("batchId"));
                    json.put("customerId",rs.getString("customerId"));
                    json.put("mobile",rs.getString("mobile"));
                    json.put("mobileOperator",rs.getString("mobileOperator"));
                    json.put("content",rs.getString("content"));
                    json.put("sign",rs.getString("sign"));
                    json.put("smCount",rs.getString("smCount"));
                    json.put("categoryId",rs.getString("categoryId"));
                    json.put("validateFlag",rs.getString("validateFlag"));
                    json.put("validateTime",rs.getString("validateTime"));
                    json.put("validateAuthor",rs.getString("validateAuthor"));
                    json.put("smUuid",rs.getString("smUuid"));
                    json.put("scheduleSendTIme",rs.getString("scheduleSendTIme"));
                    json.put("scheduleSendFlag",rs.getString("scheduleSendFlag"));
                    json.put("suggestChannelId",rs.getString("suggestChannelId"));
                    json.put("ctime",rs.getString("ctime"));
                    json.put("ltime",rs.getString("ltime"));
                    json.put("remoteIp",rs.getString("remoteIp"));
                    json.put("channelId",rs.getString("channelId"));
                    json.put("submitTime",rs.getString("submitTime"));
                    json.put("submitResult",rs.getString("submitResult"));
                    json.put("deliverTime",rs.getString("deliverTime"));
                    json.put("deliverResult",rs.getString("deliverResult"));
                    json.put("submitResultReal",rs.getString("submitResultReal"));
                    json.put("deliverResultReal",rs.getString("deliverResultReal"));
                    json.put("status",rs.getString("status"));
                    pw.println(json.toJSONString());
                }
            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
            System.out.println("num:"+index);
            System.out.println("message "+startDate+" 已经完成");
            //上传到node1 /home/test目录下
            FileInputStream in = new FileInputStream(new File(LOCAL_MSG_PATH+filename+TXT));
            boolean flag = FtpClientUtils.uploadFile( UPLOAD_PATH, filename+TXT, in);
//
//            //压缩 /home/test目录下的文件  gzip /home/test/a.txt 生成的压缩文件是/home/test/a.txt.gz
            SSHUtils.executeCmd("gzip /home/test/"+filename+TXT);
//
//            //上传到hdfs上面
            String timePath = startDate.replace("-","/")+"/";
            String cmd = "/ks/hadoop/hadoop-2.7.7/bin/hdfs dfs -put "+UPLOAD_PATH+filename+TXT+".gz"+" "+HDFS_MSG_DIR_PRE+timePath;
            System.out.println(cmd);
            String result = SSHUtils.uploadToHdfs(cmd,filename+TXT+".gz",timePath,HDFS_MSG_DIR_PRE);
            System.out.println(result);
//
//            //压缩文件
            ZIPUtil.compress(LOCAL_MSG_PATH+filename+TXT,LOCAL_MSG_PATH+filename+ZIP);
//            //删除本地当前txt文件
            File deleteFile = new File(LOCAL_MSG_PATH + filename + TXT);
            if(deleteFile.exists()) {
                deleteFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn(String type,String host) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if("1".equals(type)) {
                conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/crdb_1",USER,PASS);
            } else if("2".equals(type)) {
                conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/crdb_2",USER,PASS);
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
            String dateStr = fileName.substring(MSG_FILENAME_PRE.length(),MSG_FILENAME_PRE.length()+10);
            //发送出去的短信，回复短信可能在几天之后，从当前时间向后推5天找回复的短信
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            long startDate = format.parse(dateStr).getTime();
            long endDate = startDate+5*24*3600*1000l;
            Map<String,JSONObject> map = new HashMap<String, JSONObject>();
            while(startDate <= endDate) {
                //回复
                String replyFilePath = "D:\\historyMessage\\reply\\"+REPLY_FILENAME_PRE+format.format(startDate)+TXT;

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
            File msgReplyFile = new File("F:\\historyMessage\\msg-reply\\msg-reply-"+dateStr+TXT);
            FileWriter fw = new FileWriter(msgReplyFile, true);
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
