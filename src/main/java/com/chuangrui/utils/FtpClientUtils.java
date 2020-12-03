package com.chuangrui.utils;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Date;


public class FtpClientUtils {

    public static String URL ="node1";
    public static Integer PORT = 21;
    public static String user = "test";
    public static String password = "chuangrui123";

    public static boolean uploadFile(
            String path, //FTP服务器保存目录
            String filename, //上传到FTP服务器上的文件名
            InputStream input // 输入流
    ) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.connect(URL, PORT);//连接FTP服务器
            ftp.enterLocalPassiveMode();
            ftp.setControlEncoding("UTF-8");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.login(user, password);//登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.out.println("FTP服务器 拒绝连接");
                return success;
            }
            ftp.changeWorkingDirectory(path);

            //上传前先删除这个文件
            delete(filename,ftp);
            ftp.storeFile(filename, input);
            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    /**
     * 删除文件
     *
     * @param fileName 要删除的文件地址
     * @return true/false
     * @throws IOException
     */
    public static boolean delete(String fileName, FTPClient ftpClient) throws IOException {
        return ftpClient.deleteFile(fileName);
    }


    /**
     * 下载文件到指定目录
     *
     * @param ftpFile 文件服务器上的文件地址
     * @param dstFile 输出文件的路径和名称
     * @throws Exception
     */
    public static void downLoad(String ftpFile, String dstFile, FTPClient ftpClient) throws Exception {
        if (StringUtils.isBlank(ftpFile)) {
            throw new RuntimeException("ftpFile为空");
        }
        if (StringUtils.isBlank(dstFile)) {
            throw new RuntimeException("dstFile为空");
        }
        File file = new File(dstFile);
        FileOutputStream fos = new FileOutputStream(file);
        ftpClient.retrieveFile(ftpFile, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从文件服务器获取文件流
     *
     * @param ftpFile 文件服务器上的文件地址
     * @return {@link InputStream}
     * @throws IOException
     */
    public static InputStream retrieveFileStream(String ftpFile, FTPClient ftpClient) throws IOException {
        if (StringUtils.isBlank(ftpFile)) {
            throw new RuntimeException("ftpFile为空");
        }
        return ftpClient.retrieveFileStream(ftpFile);
    }

    public static void main(String[] args) {
        try {
            FileInputStream in = new FileInputStream(new File("D:\\historyMessage\\code\\common-code.zip"));
            boolean flag = uploadFile( "/home/test/", "common-code.zip", in);
            System.out.println(flag);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}