package com.chuangrui.version_3.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chuangrui.test.PaoDingCut;
import com.chuangrui.utils.ConnectionUtils;
import com.chuangrui.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadCSVUtils {



    public static void main(String args[]) {
//        tongzhiErrorDataSet();
//        distinct();
//        checkYanzhengma();
//        checktongzhi();
        checkdataset();
    }

    private static void checkdataset() {
        try {


            HashMap<String, Integer> sign = ConnectionSignMgrUtils.getSign(4, 106);
            System.out.println(sign.size());
            File dir =  new File("F:\\dataset\\version3\\four\\yanzhengma-jinrongyzm-daikuanyzm");
           File[] txts = dir.listFiles();
           System.out.println("txts.size:"+txts.length);
           Map<String,String> signMap = new HashMap<String, String>();
           for(File f :txts) {
               FileReader fr = new FileReader(f);
               BufferedReader br = new BufferedReader(fr);
               String line = null;
               int index =0;
               while ((line = br.readLine()) != null) {
                   try {

                       String ss[] = line.split(",");
                        if(    sign.get(ss[0]) == null || ss.length !=2)
                           signMap.put(line,f.getName());

                   } catch (Exception e) {
                        System.out.println(line);
                       e.printStackTrace();
                   }
               }
           }
           System.out.println("size:"+" "+signMap.size());
            for(String key: signMap.keySet()) {
                System.out.println(key +" "+signMap.get(key));
            }
        }catch (Exception e) {

        }
    }

    private static void readcsv() {
        try {

            File newFile = new File("F:\\batch\\marketing_batch_sign.csv");
            FileWriter fw = new FileWriter("F:\\batch\\batchId_sign.txt", true);
            PrintWriter pw = new PrintWriter(fw);

            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);

            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                     String s = line.replace("\"","");
                    pw.println(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pw.flush();
            pw.close();
        }catch (Exception e) {

        }
    }

    private static void checktongzhi() {
        try {

            File newFile = new File("F:\\dataset\\version3\\first-test\\tongzhi.txt");
            FileWriter fw = new FileWriter("F:\\dataset\\version3\\first-test\\error-tongzhi.txt", false);
            PrintWriter pw = new PrintWriter(fw);

            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);

            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                    if(!line.contains("已逾期") && ! line.contains("生日") && ! line.contains("还款日") && ! line.contains("已通过") && ! line.contains("车展") && ! line.contains("会展") &&
                    !line.contains("脐血库") && ! line.contains("拖欠期") && ! line.contains("成功付款") && ! line.contains("已通过") && ! line.contains("车展") && ! line.contains("会展")

                            && ! line.contains("超过期限") && ! line.contains("已备好") && ! line.contains("售后维修")&& ! line.contains("已超期") ) {
                        pw.println(line);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pw.flush();
            pw.close();
        }catch (Exception e) {

        }
    }

    private static void checkYanzhengma() {
        try {

            File newFile = new File("F:\\dataset\\version3\\first-test\\yanzhengma.txt");
            FileWriter fw = new FileWriter("F:\\dataset\\version3\\first-test\\error-yanzhengma.txt", false);
            PrintWriter pw = new PrintWriter(fw);

            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);

            String line = null;
            while ((line = br.readLine()) != null) {
                try {
                    if(!line.contains("验证码") && ! line.contains("驗證碼") && ! line.contains("校验码") && ! line.contains("确认码") && ! line.contains("提现码") && ! line.contains("提现密码") && ! line.contains("操作码") && ! line.contains("取现码") ) {
                        pw.println(line);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pw.flush();
            pw.close();
        }catch (Exception e) {

        }
    }

    private static void distinct() {
        try {
            FileWriter fw = new FileWriter("F:\\dataset\\version3\\first-test\\distinct-part0.txt", false);
            PrintWriter pw = new PrintWriter(fw);
            File newFile = new File("F:\\dataset\\version3\\first-test\\part-00000");

            HashMap<String,ArrayList<String>> set = new HashMap<String,ArrayList<String>>();



                FileReader fr = new FileReader(newFile);
                BufferedReader br = new BufferedReader(fr);

                String line = null;
                while ((line = br.readLine()) != null) {
                    try {
                        if(line.contains("】")) {
                            String key = line.substring(0,line.indexOf("】")-1);
                            if(set.get(key) == null) {
                                ArrayList<String> list= new ArrayList<String>();
                                list.add(line);
                                set.put(key,list);
                            } else{
                                ArrayList<String> list=  set.get(key);
                                list.add(line);
                                set.put(key,list);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



            for(String key: set.keySet()){
                ArrayList<String> list = set.get(key);
                if(list.size()> 20) {
                    for(int i=0;i<20;i++) {
                        pw.println(list.get(i));
                    }
                } else {
                    for(int i=0;i<list.size();i++) {
                        pw.println(list.get(i));
                    }
                }
            }
            pw.flush();
            pw.close();

        }catch (Exception e) {

        }
    }

    private static void tongzhiErrorDataSet() {
        try {
            FileWriter fw = new FileWriter("F:\\dataset\\version3\\first-test\\yingxiao\\yingxiao-error-dataset.txt", false);
            PrintWriter pw = new PrintWriter(fw);
            File newFile = new File("F:\\dataset\\version3\\first-test\\yingxiao\\");
            String[] filelist = newFile.list();
            HashMap<String,Integer> set = new HashMap<String,Integer>();
            for (int i = 0; i < filelist.length; i++) {

                File readfile = new File("F:\\dataset\\version3\\first-test\\yingxiao\\" + filelist[i]);
                FileReader fr = new FileReader(readfile);
                BufferedReader br = new BufferedReader(fr);

                String line = null;
                while ((line = br.readLine()) != null) {
                    try {
                        set.put(line,new Random().nextInt(10000));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
            for(String key: set.keySet()){
                pw.println(key);
            }
            pw.flush();
            pw.close();

        }catch (Exception e) {

        }


    }

    private static void errorDataSet() {
        try {
            FileWriter fw = new FileWriter("F:\\dataset\\version3\\first-test\\yanzhengma\\yanzhengma-error-dataset.txt", false);
            PrintWriter pw = new PrintWriter(fw);
            File newFile = new File("F:\\dataset\\version3\\first-test\\yanzhengma\\");
            String[] filelist = newFile.list();
            for (int i = 0; i < filelist.length; i++) {

                File readfile = new File("F:\\dataset\\version3\\first-test\\yanzhengma\\" + filelist[i]);
                FileReader fr = new FileReader(readfile);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    try {
                       if(!line.contains("验证码")) {
                           pw.println(line);
                       }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                pw.flush();
                pw.close();

            }
        }catch (Exception e) {

        }


    }

    private static void tongzhi() {
        String line = "";
        try {


            FileWriter fw = new FileWriter("F:\\dataset\\version3\\first\\yingxiao-dataset.txt", false);
            PrintWriter pw = new PrintWriter(fw);


            File readFile = new File("F:\\dataset\\version3\\first\\营销短信.txt");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);

            int index =0;
            Map<String,ArrayList<String>> maps = new HashMap<String , ArrayList<String>>();
            while ((line = br.readLine()) != null) {
                try {
                 String s[] = line.split(",");
                 pw.println("tongzhi,"+s[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            System.out.println("index:"+index);
        } catch (Exception e) {
            System.out.println(line);
            e.printStackTrace();
        }
    }


    private static void fenkai() {
        String line = "";
        try {


            FileWriter fw = new FileWriter("F:\\dataset\\version3\\first-test\\yanzhengma\\yanzhengma-error-dataset.txt", false);
            PrintWriter pw = new PrintWriter(fw);


            File readFile = new File("F:\\dataset\\version3\\first\\yanzhengma.csv");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);

            int index =0;
            Map<String,ArrayList<String>> maps = new HashMap<String , ArrayList<String>>();
            while ((line = br.readLine()) != null) {
                try {
                    String ss[] = line.split(",");

                    if(maps.get(ss[0]) == null) {
                        ArrayList<String > list = new ArrayList<String>();
                        list.add(ss[1]);
                        maps.put(ss[0],list);
                    } else {
                        maps.get(ss[0]).add(ss[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println(maps.size());
            for(String key :maps.keySet()) {
                int i =0;
                for(String str : maps.get(key)) {
                    if(i<=10) {
                        pw.println("yanzhengma,"+key.replace("\"","")+str.replace(",","，").replace("\"",""));

                    } else {
                        break;
                    }
                    i++;
                }
            }

            System.out.println("index:"+index);
        } catch (Exception e) {
            System.out.println(line);
            e.printStackTrace();
        }
    }


}
