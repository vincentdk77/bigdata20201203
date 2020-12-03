package com.chuangrui.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorCodeUtils {




    public static void convert(File src, File dest) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(src), "GBK"));
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest), "utf-8"));
        String line = null;
        while((line = br.readLine()) != null) {
            System.out.println(line);
            writer.write(line);
        }
        writer.flush();
        writer.close();
        br.close();
    }

    public static String convert(String text) throws UnsupportedEncodingException {
        return new String(text.getBytes("GBK"));
    }

    public static void main(String args[]) {
        String startDateStr = args[0];
        String endDateStr =args[1];
        String oldCode =args[2];
        String code =args[3];
//        String startDateStr = "2017-10-23";
//        String endDateStr = "2017-10-23";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long start = format.parse(startDateStr).getTime();
            long end = format.parse(endDateStr).getTime();
            while(start <=end) {

                FileWriter fw = new FileWriter("/home/luanma/msg-"+format.format(start)+"-new.txt", false);
//                FileWriter fw = new FileWriter("F:\\msg-"+format.format(start)+"-new.txt", false);
                PrintWriter pw = new PrintWriter(fw);

                File readfile = new File("/home/luanma/msg-"+format.format(start)+".txt" );
                String line;
                String lineStr = "" ;
                FileReader fr = new FileReader(readfile);
                BufferedReader br = new BufferedReader(fr);

                while ((line = br.readLine()) != null) {
                    lineStr = lineStr+line;
                    if(lineStr.endsWith("\"}")) {
                        try {
                            JSONObject json =  JSONUtils.getJson(lineStr);
                            String sign = json.getString("sign");
                            String content = json.getString("content");
                            json.put("sign",convert(sign).replace("?","").replace("�","")+"】");
                            json.put("content",replace(convert(content)).replace("?","").replace("?","").replace("�",""));
                            pw.println(json.toJSONString());
                            lineStr = "" ;
                        } catch (Exception e) {
                            System.out.println(lineStr);
                            e.printStackTrace();
                            lineStr = "" ;
                        }

                    } else {
                        System.out.println(lineStr);
                    }
                }
                pw.flush();
                pw.close();
                fw.close();
                br.close();
                fr.close();
                start = start+24*3600*1000l;
            }
//            String mobile ="¹§Ï²£¡ÄúÒÑ»ñµÃ³¬µÍÀûÏ¢ÌØÈ¨£¬3000¶î¶È£¬30·ÖÖÓ¼«ËÙ·Å¿î£¬¿ìÀ´µÇÂ¼¼¤»îÁ¢¼´ÄÃÇ® http://t.cn/Rp8GxAg»ØTÍË¶©";
//            String s2 = "¡¾¿ìÃ×½ðÈÚ¡¿";
//
//
//            System.out.println(new String(mobile.getBytes("windows-1252"),"gbk"));
//            System.out.println(new String(s2.getBytes("windows-1252"),"gbk"));
        } catch ( Exception e) {
            e.printStackTrace();
        }


    }

    public static String replace(String str) {
        String destination = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            destination = m.replaceAll("");
        }
        return destination;
    }
}
