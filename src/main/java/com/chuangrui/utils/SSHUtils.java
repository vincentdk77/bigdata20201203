package com.chuangrui.utils;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.chuangrui.export.ExportFromMysql;
import org.apache.commons.lang.StringUtils;


public class SSHUtils {


    /**
     * @param args
     */
    public static String executeCmd(String cmd){
        String rel = "";
        try {
            Connection connection = new Connection("node1");
            connection.connect();//连接
            connection.authenticateWithPassword("root", "password");//认证
            Session session = connection.openSession();
            session.execCommand(cmd);
            InputStream is = new StreamGobbler(session.getStdout());//获得标准输出流
            BufferedReader brs = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String temp = "";
            while ((temp = brs.readLine()) != null) {
               rel = rel+temp+"\n";
            }
            if (session != null) {
                session.close();
            }
            session.close();
            brs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return rel;
    }

    public static String executeCmdOnNode4(String cmd){
        String rel = "";
        try {
            Connection connection = new Connection("node4");
            connection.connect();//连接
            connection.authenticateWithPassword("root", "cz@2017");//认证
            Session session = connection.openSession();
            session.execCommand(cmd);
            InputStream is = new StreamGobbler(session.getStdout());//获得标准输出流
            BufferedReader brs = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String temp = "";
            while ((temp = brs.readLine()) != null) {
                rel = rel+temp+"\n";
            }
            if (session != null) {
                session.close();
            }
            session.close();
            brs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return rel;
    }

    public static String uploadToHdfs(String cmd,String filename,String timePath,String hdfsDirPre) {
        System.out.println("timePath:"+timePath+" hdfsDirPre:"+hdfsDirPre);
       String result = "";
       String shFile = "/ks/sh/upload.sh";
       executeCmd("rm -rf "+shFile);
        result += executeCmd("touch "+shFile);
        if(!StringUtils.isEmpty(timePath)) {
            //在hdfs上面创建日期目录
            String time[] = timePath.split("/");
            String tempPath = "";
            for(String t : time) {
                tempPath =tempPath+t+ "/";
                String createDirCmd = "echo /ks/hadoop/hadoop-2.7.7/bin/hdfs dfs -mkdir "+ hdfsDirPre+tempPath + ">> "+shFile;
                executeCmd(createDirCmd);
            }
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmd(cmd2);
         } else {
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmd(cmd2);
        }
        //删除node1上面/home/test/下当前文件
        executeCmd("echo rm -rf /home/test/"+filename +">> "+shFile);
        result += executeCmd("sudo sh /ks/sh/upload.sh");
        return result;
    }

    public static String uploadToHdfsByNode(String cmd,String filename,String timePath,String hdfsDirPre) {
        System.out.println("timePath:"+timePath+" hdfsDirPre:"+hdfsDirPre);
        String result = "";
        String shFile = "/ks/getFromMysql/sh/upload.sh";
        executeCmd("rm -rf "+shFile);
        result += executeCmd("touch "+shFile);
        if(!StringUtils.isEmpty(timePath)) {
            //在hdfs上面创建日期目录
            String time[] = timePath.split("/");
            String tempPath = "";
            for(String t : time) {
                tempPath =tempPath+t+ "/";
                String createDirCmd = "echo /ks/hadoop/hadoop-2.7.7/bin/hdfs dfs -mkdir "+ hdfsDirPre+tempPath + ">> "+shFile;
                executeCmd(createDirCmd);
            }
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmd(cmd2);
        } else {
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmd(cmd2);
        }
        //删除node1上面/home/test/下当前文件
       // executeCmd("echo rm -rf /ks/getFromMysql/"+filename +">> "+shFile);
        result += executeCmd("sudo sh /ks/getFromMysql/sh/upload.sh");
        return result;
    }

    public static String uploadToHdfsByNode4(String cmd,String filename,String timePath,String hdfsDirPre) {
        System.out.println("timePath:"+timePath+" hdfsDirPre:"+hdfsDirPre);
        String result = "";
        String shFile = "/home/getFromMysql/sh/upload.sh";
        executeCmdOnNode4("rm -rf "+shFile);
        result += executeCmdOnNode4("touch "+shFile);
        if(!StringUtils.isEmpty(timePath)) {
            //在hdfs上面创建日期目录
            String time[] = timePath.split("/");
            String tempPath = "";
            for(String t : time) {
                tempPath =tempPath+t+ "/";
                String createDirCmd = "echo /home/hadoop/hadoop-2.7.7/bin/hdfs dfs -mkdir "+ hdfsDirPre+tempPath + ">> "+shFile;
                executeCmdOnNode4(createDirCmd);
            }
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmdOnNode4(cmd2);
        } else {
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmdOnNode4(cmd2);
        }
        //删除node1上面/home/test/下当前文件
        // executeCmd("echo rm -rf /ks/getFromMysql/"+filename +">> "+shFile);
        result += executeCmdOnNode4("sudo sh /home/getFromMysql/sh/upload.sh");
        return result;
    }

    public static String uploadToHdfsByNode0507(String cmd,String filename,String timePath,String hdfsDirPre) {
        System.out.println("timePath:"+timePath+" hdfsDirPre:"+hdfsDirPre);
        String result = "";
        String shFile = "/home/getFromMysql/sh/upload2.sh";
        executeCmdOnNode4("rm -rf "+shFile);
        result += executeCmdOnNode4("touch "+shFile);
        if(!StringUtils.isEmpty(timePath)) {
            //在hdfs上面创建日期目录
            String time[] = timePath.split("/");
            String tempPath = "";
            for(String t : time) {
                tempPath =tempPath+t+ "/";
                String createDirCmd = "echo /home/hadoop/hadoop-2.7.7/bin/hdfs dfs -mkdir "+ hdfsDirPre+tempPath + ">> "+shFile;
                executeCmdOnNode4(createDirCmd);
            }
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmdOnNode4(cmd2);
        } else {
            String cmd2 = "echo "+cmd+" >> "+shFile;
            result += executeCmdOnNode4(cmd2);
        }
        //删除node1上面/home/test/下当前文件
        // executeCmd("echo rm -rf /ks/getFromMysql/"+filename +">> "+shFile);
        result += executeCmdOnNode4("sudo sh /home/getFromMysql/sh/upload2.sh");
        return result;
    }

    public static void main(String args[]) {
      System.out.println( executeCmd("gzip /ks/data/a.txt"));
//        String cmd = "/ks/hadoop/hadoop-2.7.7/bin/hdfs dfs -put /home/test/common-code.zip /data/message";
//        executeCmd("touch /ks/sh/upload.sh");
//        String cmd2 = "echo "+cmd+" > /ks/sh/upload.sh";
//        System.out.println(cmd2);
//        executeCmd(cmd2);
//        executeCmd("sudo sh /ks/sh/upload.sh");

    }
}
