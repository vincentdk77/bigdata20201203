package com.chuangrui.dataset;

import com.chuangrui.test.ContentTagMgr;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 从batch表查出来的数据，标签不对的数据通过关键字再次分类
*/
public class DistinctDataSet {
    public static void main(String args[]) {
        try {
            File dir = new File("F:\\dataset\\version3\\second\\yanzhengma\\");
            File[] txts = dir.listFiles();
            System.out.println("txts.size:"+txts.length);
            Map<String,String> signMap = new HashMap<String, String>();
            Map<String, ArrayList<String>>  array = new HashMap<String, ArrayList<String>>();
            for(File f :txts) {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                int index =0;
                while ((line = br.readLine()) != null ) {
                    try {

                        if(!StringUtils.isEmpty(line)) {
                            String ss[] = line.split(",");
                            if(!StringUtils.isEmpty(ss[1]) && ss[1].contains("【") &&ss[1].contains("】") ) {
                                String key = ss[1].substring(ss[1].indexOf("【"),ss[1].indexOf("】")+1);
                                if(signMap.get(key) == null){
                                    signMap.put(key,ss[0]);
                                    ArrayList<String> a = new ArrayList<String>();
                                    a.add(line.substring(0,line.indexOf("】")+1)+" "+f.getName());
                                    array.put(key,a);
                                } else {
                                    if(!signMap.get(key).equals(ss[0])) {
                                        ArrayList<String> a =array.get(key);
                                        int i = 0;
                                        for(;i<a.size();i++) {
                                            if(a.get(i).startsWith(line.substring(0,line.indexOf("】")+1))) {
                                                break;
                                            }
                                        }
                                        if(i == a.size()) {
                                            a.add(line.substring(0,line.indexOf("】")+1)+" "+f.getName());
                                            array.put(key,a);
                                        }


                                    }
                                }
                            }
                        }




                    } catch (Exception e) {
                        System.out.println(line);
                        e.printStackTrace();
                    }
                }
            }

            int index = 0;
            for(String key: array.keySet()) {
                if(array.get(key).size()>1) {
                    index++;
                    for(String l:array.get(key)) {
                        System.out.println(l);
                    }
                }
            }
            System.out.println(index);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
