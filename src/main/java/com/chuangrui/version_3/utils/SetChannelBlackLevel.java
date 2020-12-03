package com.chuangrui.version_3.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SetChannelBlackLevel {



    public static void main(String args[]) {

        setChannelBlackLevel();
    }



    private static void setChannelBlackLevel() {
        try {

            File oldFile = new File("E:\\blacklevel.txt");
            FileReader fr = new FileReader(oldFile);
            BufferedReader br = new BufferedReader(fr);
            Map<String,Integer> oldMap = new HashMap<String,Integer>();
            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                    String s[] = line.split(",");
                     oldMap.put(s[0],Integer.parseInt(s[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            File newFile2 = new File("E:\\blacklevel2.txt");
            FileReader fr2 = new FileReader(newFile2);
            BufferedReader br2 = new BufferedReader(fr2);
            Map<String,Integer> oldMap2 = new HashMap<String,Integer>();
            String line2 = null;
            while ((line2 = br2.readLine()) != null) {
                try {
                    String s[] = line2.split(",");
                    if(oldMap.get(s[0] ) == null) {
                        oldMap2.put(s[0],1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(String key:oldMap2.keySet()) {
                System.out.println(key+",1");
            }

        }catch (Exception e) {

        }
    }



}
