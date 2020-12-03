package com.chuangrui.reply;

import com.chuangrui.test.PaoDingCut;
import com.chuangrui.utils.ConnectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HandleReply {

    public static void main(String args[]) {
        try {
//            File writeFile = new File("F:\\reply_dataset\\reply_dataset.txt");
//
//            FileWriter fw = new FileWriter(writeFile, false);
//            PrintWriter pw = new PrintWriter(fw);

            HashMap<String,Integer> set  = new HashMap<String,Integer>();
            File zcfile = new File("F:\\reply_dataset\\zhangci.txt");
            String line;
            FileReader zcfr = new FileReader(zcfile);
            BufferedReader zcbr = new BufferedReader(zcfr);
            while ((line = zcbr.readLine()) != null) {
                set.put(line.toLowerCase(),1);
            }

            Connection conn = ConnectionUtils.getConn();
            Statement st = conn.createStatement();
            for(String key : set.keySet()) {
                String sql = "insert into zangci values(null,'"+key+"')";
                st.execute(sql);
            }

//            File readfile = new File("F:\\reply_dataset\\reply_dataset.txt");
//            String line ;
//            FileReader fr = new FileReader(readfile);
//            BufferedReader br = new BufferedReader(fr);
//            while ((line = br.readLine()) != null) {
//                if(line.split(",").length!=2) {
//                    System.out.println(line);
//                }
//
//            }

//            sortReply("E:\\handle_reply.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sortReply(String filePath) {
        try {
            HashMap<String,ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
            File readfile = new File(filePath);
            String line ;
            FileReader fr = new FileReader(readfile);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {


                    String ss[] = line.split(",");
                    String result = "";
                    for(int i=1;i<ss.length;i++) {
                        result = result+ss[i]+"，";
                    }
                    if(!StringUtils.isEmpty(result))
                        result = result.substring(0,result.length()-1);
                    if(map.get(ss[0]) != null) {
                        map.get(ss[0]).add(result);
                    } else {
                        ArrayList<String> list=  new ArrayList<String>();
                        list.add(result);
                        map.put(ss[0], list);
                    }


            }

            File writeFile = new File("F:\\handle_reply_1.txt");

            FileWriter fw = new FileWriter(writeFile, false);
            PrintWriter pw = new PrintWriter(fw);
            for(String key : map.keySet()) {
               for(String s: map.get(key) ) {
                   pw.println(key+","+s);
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean chekcLetter(String line) {

        String s[] = {"t","td","no","y","n","ok","tmd","sb","bu"};

        boolean number  = true;
        for(int i=0;i<line.length();i++) {
            if((line.charAt(i) >='a' && line.charAt(i) <= 'z') || (line.charAt(i) >='A' && line.charAt(i) <= 'Z')) {

            } else {
                number = false;
            }
        }

        for(String str : s) {
            if(str.equals(line)) {
                number =  true;
            }
        }

        return number;
    }

    public static boolean checkNumber(String line) {


        boolean number  = true;
        for(int i=0;i<line.length();i++) {
            if(line.charAt(i) >='0' && line.charAt(i) <= '9') {

            } else {
                number = false;
            }
        }


        return number;
    }
}
