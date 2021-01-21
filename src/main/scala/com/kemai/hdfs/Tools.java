package com.kemai.hdfs;

import org.apache.hadoop.conf.Configuration;

public class Tools {

    public static final Configuration configuration = new Configuration();

    static {
        configuration.set("fs.defaultFS", "hdfs://node12:9000");
        System.setProperty("HADOOP_USER_NAME", "root");
    }
}