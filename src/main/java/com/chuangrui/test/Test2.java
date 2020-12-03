package com.chuangrui.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Test2 {
    public static void main(String args[]) {
        try {
            String url = "E:\\blacklevel.txt";
            //BufferedReader是可以按行读取文件
            FileInputStream inputStream = new FileInputStream(new File(url));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while((str = bufferedReader.readLine()) != null)
            {
                String [] temp = str.split("\\,");
                if(temp.length != 2) {
                    System.out.println(str);

                }


            }
            inputStream.close();
            bufferedReader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
