package com.upload;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class UploadFile {
    public static void main(String[] args) {
        File imagefile = new File("E:/03.txt");

        //创建ftp客户端
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        String hostname = "61.132.230.77";
        int port =  60021;
        String username = "kemaiftp";
        String password = "cz@2017";
        try {
            //链接ftp服务器
            ftpClient.connect(hostname, port);
            //登录ftp
            boolean login = ftpClient.login(username, password);
            int  reply = ftpClient.getReplyCode();
            System.out.println(reply);
            //如果reply返回230就算成功了，如果返回530密码用户名错误或当前用户无权限下面有详细的解释。
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return ;
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            ftpClient.changeWorkingDirectory("/home/ftpadmin");//在root目录下创建文件夹
            String remoteFileName = imagefile.getName();
            InputStream input = new FileInputStream(imagefile);
            boolean result = ftpClient.storeFile(remoteFileName, input);//文件你若是不指定就会上传到root目录下
            System.out.println(result);
            input.close();
            ftpClient.logout();

        } catch (Exception e) {

            e.printStackTrace();
        }  finally  {
            if (ftpClient.isConnected())
            {
                try
                {
                    ftpClient.disconnect();
                } catch (Exception ioe)
                {
                    ioe.printStackTrace();
                }
            }

        }
    }


}
