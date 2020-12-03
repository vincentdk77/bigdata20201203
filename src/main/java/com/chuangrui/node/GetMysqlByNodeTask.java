package com.chuangrui.node;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetMysqlByNodeTask {

    public static void main(String args[]) {
        Date date = new Date();
        long dateTime = date.getTime()-3*24*3600*1000l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(dateTime);
        System.out.println(dateStr+" 开始执行");
        System.out.println("开始拉取回复数据");
        GetMysqlByNode1.getReplyFromMysql(dateStr);
        System.out.println("回复数据拉取完成");
        System.out.println("开始拉取短信数据");
        GetMysqlByNode1.getDataFromMysql(dateStr);
        System.out.println("短信数据拉取完成");
    }

}
