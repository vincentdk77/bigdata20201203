package com.chuangrui.test;

import java.util.Date;


public class MessageLog {
    public static void getConsoleLog(String type,String msg) {
        System.out.println(type + " " +getNowTime() + " " + msg);
    }

    public static String getNowTime() {
        long ts = System.currentTimeMillis();
        Date date = new Date(ts);
        return date.toString();
    }
}

