package com.upload;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
public class CopyFile {
    public static void main(String[] args)  {



        String readFile = System.getProperty("user.dir")+File.separator+"hdfsRead.txt";
        HashSet<String> set = getFileList(readFile);

//        String fileName = args[0];
//        String pre = args[1];
        String fileName = "F:\\download.txt";
        String pre = "1";

        if(!set.contains(fileName)) {
            Date date = new Date();
            SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH");
            String timePath = format.format(date).replace("-","/").replace(" ","/")+"/";
            try {

                String url = "hdfs://61.132.230.76:9000/flume/kemai/"+timePath;
                upload(fileName,url,timePath,pre);
            } catch (Exception e) {

                String url = "hdfs://61.132.230.78:9000/flume/kemai/"+timePath;
                try {
                    upload(fileName,url,timePath,pre);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            try {
                FileWriter fw = new FileWriter(readFile, true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println(fileName);
                pw.flush();
                pw.close();
                fw.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(fileName+"文件已经上传");
        }




    }

    private static HashSet<String> getFileList(String readFile) {
        HashSet<String> set = new HashSet<String>();
        try {
            File file = new File(readFile);
            if(!file.exists()) {
                file.createNewFile();
            }
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                set.add(line);
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return set;
    }

    public static void upload(String fileName,String url,String timePath,String pre) throws Exception{

        Configuration conf = new Configuration();

        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

        URI uri = new URI(url);
        FileSystem fs = FileSystem.get(uri,conf);
        Path resP = new Path(fileName);


        Path destP = new Path("/flume/kemai/"+timePath);
        if(!fs.exists(destP)){
            boolean flag = fs.mkdirs(destP);
            System.out.println(flag);
        }
        fs.copyFromLocalFile(resP, destP);
        String name[] = fileName.split("\\/");
        String s = name[name.length-1];

        fs.rename(new Path("/flume/kemai/"+timePath+s), new Path("/flume/kemai/"+timePath+pre+s));
        fs.close();
    }
}
