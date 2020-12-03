package com.chuangrui.version_3.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SplitFile {


    public static void main(String args[]) {
//        String line = "���������";
//        try {
//            line = new String(line.getBytes("windows-1252"),"gbk");
//            System.out.println(line);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String startDateStr = "2017-10-14";
//        String endDateStr = "2017-10-14";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            long start = format.parse(startDateStr).getTime();
//            long end = format.parse(endDateStr).getTime();
//            while (start <= end) {
//                String timePath = format.format(start);
//                fenkai(timePath);
//                start = start+24*3600*1000l;
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

//        ConvertEncoding ce = new ConvertEncoding();
//        ce.convertEncoding("GBK", "UTF-8", "D:\\乱码-10\\2017-10-14\\msg-2017-10-14.txt",
//                "D:\\乱码-10\\2017-10-14\\new-msg-2017-10-14.txt", "", new FileFilter()
//                {
//                    public boolean accept(File pathname)
//                    {
//                        return pathname.isDirectory() || pathname.getName().endsWith("java");
//                    }
//                });
        try {
            String startDateStr = "2017-11-27";
            String endDateStr = "2017-11-30";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            long start = format.parse(startDateStr).getTime();
            long end = format.parse(endDateStr).getTime();
            while(start <=end) {

                FileWriter fw = new FileWriter("F:\\乱码-12\\"+format.format(start)+"\\new-msg-"+format.format(start)+".txt", false);
                PrintWriter pw = new PrintWriter(fw);
                File fileDir = new File("F:\\乱码-12\\"+format.format(start)+"\\msg-"+format.format(start)+".txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(  new FileInputStream(fileDir), "GBK"));
                String str;
                int index=0;
                while ((str = in.readLine()) != null  ) {
//                System.out.println(str);// java内部只有unicode编码 所以str是unicode编码
                    String str2 = new String(str.getBytes("utf-8"), "utf-8");// str.getBytes("GBK")是gbk编码，但是str2是unicode编码
                    pw.println(str2);
                    index ++;
                }
                pw.flush();
                pw.close();
                fw.close();
                in.close();
                System.out.println(format.format(start)+" index:"+index);
                start = start+24*3600*1000l;
            }



//
//            FileWriter fw = new FileWriter("D:\\乱码-10\\2017-10-14\\new-msg-2017-10-14.txt", false);
//            PrintWriter pw = new PrintWriter(fw);
//            File fileDir = new File("D:\\乱码-10\\2017-10-14\\msg-2017-10-14.txt");
//            BufferedReader in = new BufferedReader(new InputStreamReader(  new FileInputStream(fileDir), "GBK"));
//            String str;
//            int index=0;
//            while ((str = in.readLine()) != null  ) {
////                System.out.println(str);// java内部只有unicode编码 所以str是unicode编码
//                String str2 = new String(str.getBytes("utf-8"), "utf-8");// str.getBytes("GBK")是gbk编码，但是str2是unicode编码
//                pw.println(str2);
//                index ++;
//            }
//            pw.flush();
//            pw.close();
//            in.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }




}
