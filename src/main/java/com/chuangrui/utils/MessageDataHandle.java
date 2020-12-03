package com.chuangrui.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

public class MessageDataHandle {



    public static void main(String args[]) {
        String line = "{\"customerId\":12319,\"mobile\":\"18152565235\",\"ctime\":1535299200000,\"content\":\"尊敬的客户，邀您申请一张额度最高5万元的信用卡（金卡），可取现！点击申请 0x0.biz/BDCNX7I 退订回T\",\"channelId\":429}";
        JSONObject json = JSONObject.parseObject(line);
        System.out.println(json.getString("mobile"));
    }

    public static void test(String[] args) {
        File file = new File("E:\\精确分类.txt");
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        File newFile = new File("E:\\精确分类2.txt");

        try {
            reader = new BufferedReader(new FileReader(file));
            FileWriter fw = new FileWriter(newFile, true);
            PrintWriter pw = new PrintWriter(fw);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\精确分类2.txt"),"GB2312")));

            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                boolean flag = true;
                System.out.println("line " + line + ": " + tempString);
                line++;
                String ss[] = tempString.split("\t");

                if(ss[1].length() < 16) {
                    flag = false;
                }
                if(ss[0].indexOf("^") >= 0) {
                    ss[0] = ss[0].substring(0,ss[0].indexOf("^"));
                }
                if("教育".equals(ss[0]) &&ss[1].startsWith( "【苏宁易购】")) {
                    flag = false;
                }
                if(flag) {
                    out.println(ss[0]+","+ss[1]);
                }
            }
            out.flush();
            reader.close();
        } catch (Exception e) {

        }
    }



}
