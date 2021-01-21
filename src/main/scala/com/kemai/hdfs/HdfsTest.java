package com.kemai.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HdfsTest {

    public static void main(String[] args) throws IOException, Exception, URISyntaxException {
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://node8:9000");
        conf.set("dfs.client.use.datanode.hostname", "true");

        // 1 获取hdfs客户端对象
        FileSystem fs = FileSystem.get(conf );
//		FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), conf, "root");

//		-DHADOOP_USER_NAME=root
        // 2 在hdfs上创建路径
        fs.mkdirs(new Path("/test"));

        // 3 关闭资源
        fs.close();

        System.out.println("over");
    }

    // 5 文件详情查看
    @Test
    public void testListFiles() throws IOException, InterruptedException, URISyntaxException {

        // 1 获取对象
        Configuration conf = new Configuration();
//        conf.set("hadoop.rpc.protection", "privacy");
        conf.set("dfs.client.use.datanode.hostname", "true");
        FileSystem fs = FileSystem.get(new URI("hdfs://jtb"), conf , "root");

        // 2 查看文件详情
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);

        while(listFiles.hasNext()){
            LocatedFileStatus fileStatus = listFiles.next();

            // 查看文件名称、权限、长度、块信息
            System.out.println(fileStatus.getPath().getName());// 文件名称
            System.out.println(fileStatus.getPermission());// 文件权限
            System.out.println(fileStatus.getLen());// 文件长度

            BlockLocation[] blockLocations = fileStatus.getBlockLocations();

            for (BlockLocation blockLocation : blockLocations) {

                String[] hosts = blockLocation.getHosts();

                for (String host : hosts) {
                    System.out.println(host);
                }
            }

            System.out.println("------班长分割线--------");
        }

        // 3 关闭资源
        fs.close();
    }
}