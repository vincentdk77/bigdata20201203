package com.chuangrui.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DoFile {
    public static ArrayList<String> getFileContentList(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ArrayList<String> resList = new ArrayList<String>();
        for(File f:files){
            try {
                FileInputStream fis = new FileInputStream(f.getPath());
                String s = "";
                byte[] buff = new byte[1024];
                int len = 0;
                while((len = fis.read(buff)) != -1){
                    s += new String(buff, 0, len);
                }
                resList.add(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return resList;

    }
}
