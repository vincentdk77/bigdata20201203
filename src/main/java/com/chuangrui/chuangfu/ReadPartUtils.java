package com.chuangrui.chuangfu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chuangrui.utils.JSONUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadPartUtils {



    public static void main(String args[]) {

       checkDataSet("E:\\tmp\\");


    }

    private static void checkDataSet(String s) {

        Map<String,Integer> lineMap = new HashMap<String, Integer>();
        String dayTimes[] = {"22","23","24","25","26"};
        for(String dayTime : dayTimes){
            File newFile = new File("E:\\tmp\\"+dayTime+"\\");
            try{
                FileWriter fw = new FileWriter("E:\\cuishou.txt", true);
                PrintWriter pw = new PrintWriter(fw);
                List<String> yuqi = new ArrayList<String>();
                String[] filelist = newFile.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File("E:\\tmp\\"+dayTime+"\\"  + filelist[i]);
                    String line;
                    FileReader fr = new FileReader(readfile);
                    BufferedReader br = new BufferedReader(fr);
                    while ((line = br.readLine()) != null) {
                        if(line.split("@").length != 4 ) {
//                        System.out.println(filelist[i]+" "+line);
                        } else {
                            if(line.split("@")[1].contains("重度催收")) {
                             pw.println(line.split("@")[3]);
//                                lineMap.put(line.split("@")[0],1);
                            }

                        }

                    }

                    br.close();
                }


            }catch(Exception se){
                se.printStackTrace();
            }
            System.out.println("count:"+lineMap.size());
        }

        System.out.println("count:"+lineMap.size());
    }

    private static void youxi(String file) {
        try {
            Map<String,String> map = new HashMap<String, String>();

            FileWriter fw = new FileWriter("F:\\msg_dataset\\lingshiyingxiao.txt", false);
            PrintWriter pw = new PrintWriter(fw);


            File readFile = new File(file);
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int index =0;
            Map<String,ArrayList<String>> maps = new HashMap<String , ArrayList<String>>();
            while ((line = br.readLine()) != null) {
                String ss[] = line.split(",");
//
                String keys [] = {"三只松鼠","零食","饼","糕","小吃"};

                int jj = 0;
                for(String k : keys) {
                    if( line.contains(k)
//                            &&( line.contains("食")       ||  line.contains("吃")    ||  line.contains("餐"))
                            && !line.contains("移动")       &&  !line.contains("银")    &&  !line.contains("赚")
                            &&  !line.contains("教育")&& !line.contains("财")&& !line.contains("馅饼")&& !line.contains("贷")
                            &&  !line.contains("全友")&& !line.contains("泰康")&& !line.contains("平安")&& !line.contains("医")
                            &&  !line.contains("电信")&& !line.contains("营业厅")&& !line.contains("环球")&& !line.contains("城")
                            &&  !line.contains("青青E校")&& !line.contains("新东方")&& !line.contains("KTV")&& !line.contains("厅")
                            &&  !line.contains("学习")&& !line.contains("楚汉")&& !line.contains("教")&& !line.contains("三维")
//                            &&  !line.contains("审核")&& !line.contains("九阳")&& !line.contains("集团")&& !line.contains("面试")
//                            &&  !line.contains("苏宁")&& !line.contains("海尔")&& !line.contains("电")&& !line.contains("眼镜")
//                            &&  !line.contains("美团")&& !line.contains("饿了么")&& !line.contains("流量")&& !line.contains("ぐ")
//                            &&  !line.contains("鞋")&& !line.contains("健")&& !line.contains("联通")&& !line.contains("碗")
//                            &&  !line.contains("套餐")&& !line.contains("购物")&& !line.contains("资源")&& !line.contains("英语")
//                            &&  !line.contains("专家")&& !line.contains("联网")&& !line.contains("老师")&& !line.contains("魔力")

                    ) {
                        pw.println("零食营销," + ss[1].replace(",","，"));

//                        if(map.get(line.substring(0,5)) == null) {
//                           pw.println("通知," + ss[1]);
//                            map.put(line.substring(0,5),"1");
//                        }
                        break;
                    } else {
                        jj++;
                    }
                }
                if(jj == keys.length) {
//                    pw.println(line);
                }


//                if(maps.get(ss[0]) == null) {
//                    ArrayList<String> list = new ArrayList<String>();
//                    list.add(ss[1]);
//                    maps.put(ss[0],list);
//                } else {
//                    maps.get(ss[0]).add(ss[1]);
//                }
            }
//            for(String key : maps.keySet()) {
//               for(String s : maps.get(key)) {
//                   pw.println(key+","+s);
//               }
//            }


            System.out.println("index:"+index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fenkai(String file) {
        try {


            FileWriter fw = new FileWriter("F:\\msg_dataset\\sortdataset.txt", false);
            PrintWriter pw = new PrintWriter(fw);


            File readFile = new File(file);
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int index =0;
            Map<String,ArrayList<String>> maps = new HashMap<String , ArrayList<String>>();
            while ((line = br.readLine()) != null) {
                String ss[] = line.split(",");
                if(maps.get(ss[0]) == null) {
                    ArrayList<String > list = new ArrayList<String>();
                    list.add(ss[1]);
                    maps.put(ss[0],list);
                } else {
                    maps.get(ss[0]).add(ss[1]);
                }
            }

            for(String key :maps.keySet()) {
                for(String str : maps.get(key)) {
                    pw.println(key+","+str);
                }
            }

            System.out.println("index:"+index);
        } catch (Exception e) {

        }
    }

    private static void yanzhengma(String file) {
        try {
            HashMap<String,String> map = new HashMap<String, String>();

            FileWriter fw = new FileWriter("E:\\dataSet\\alldataset2.txt", false);
            PrintWriter pw = new PrintWriter(fw);


                File readFile = new File(file);
                FileReader fr = new FileReader(readFile);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                int index =0;
                while ((line = br.readLine()) != null) {
//                    String ss[] = line.split(",");
//                    ArrayList<String> a = PaoDingCut.cutString(ss[1]);
//                    System.out.println(ss[0]+"  "+a);
                    if(line.contains("网贷") && line.contains("验证码")) {
                       if(index <500) {
                           pw.write(line);
                       }
                    } else {
                        pw.write(line);
                    }
                }

                System.out.println("index:"+index);
            } catch (Exception e) {

        }
    }

    private static void distinctChuangFuFile() {
        try {
            HashMap<String,String> map = new HashMap<String, String>();

                File readFile = new File("F:\\msg_dataset\\baoxian-origin.txt");
                FileReader fr = new FileReader(readFile);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                while ((line = br.readLine()) != null) {
                    if(line.length()>13) {
                        map.put(line.substring(0,9),line);
                    }

                }


            FileWriter fw = new FileWriter("F:\\msg_dataset\\baoxian-origin-2.txt", false);
            PrintWriter pw = new PrintWriter(fw);
            for(String key : map.keySet()) {
                if( (!map.get(key).contains("验证码") && !map.get(key).contains("双控系统")  && !map.get(key).contains("欠款") && !map.get(key).contains("逾期")
                        && !map.get(key).contains("信用风险提醒")&& !map.get(key).contains("密码")  )

                ) {
                    pw.println("保险,"+map.get(key).replace(",","，"));
                }

            }
            pw.close();
            fw.close();
        }catch(Exception e){

        }
    }


    public static void chuangfu_xinyongka(){
        File writeFile = new File("F:\\xinyongka3_dataset.txt");
        File newFile = new File("F:\\chuangfu_xinyongka\\");
        try{
            FileWriter fw = new FileWriter(writeFile, false);
            PrintWriter pw = new PrintWriter(fw);

            Map<String,Integer> lineMap = new HashMap<String, Integer>();

            String[] filelist = newFile.list();
            for (int i = 0; i < filelist.length; i++) {

                File readfile = new File("F:\\chuangfu_xinyongka\\"  + filelist[i]);
                String line;
                FileReader fr = new FileReader(readfile);
                BufferedReader br = new BufferedReader(fr);
                while ((line = br.readLine()) != null) {
                    lineMap.put(line,1);
                }
            }
            for(String key :lineMap.keySet()) {

                if((key.contains("信用卡") || key.contains("申请") || key.contains("元")) && !key.contains("冰箱冷藏") && !key.contains("机场") && !key.contains("机票")  && !key.contains("东亚携程")) {
                    pw.println("信用卡,"+key.replace(",","，").replace("\"",""));
                }
            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            se.printStackTrace();
        }
    }

    /**
     * 将通道csv文件转为json格式的txt
     * @param writePath
     * @param readPath
     */
    public static void chuangfu_dataset() {
        File writeFile = new File("F:\\chuangfu_dataset\\dataset.txt");
        File newFile = new File("F:\\chuangfu_dataset\\");
        try{
            FileWriter fw = new FileWriter(writeFile, false);
            PrintWriter pw = new PrintWriter(fw);

            Map<String,Integer> lineMap = new HashMap<String, Integer>();

            String[] filelist = newFile.list();
            for (int i = 0; i < filelist.length; i++) {

                    File readfile = new File("F:\\chuangfu_dataset\\"  + filelist[i]);
                    String line;
                    FileReader fr = new FileReader(readfile);
                    BufferedReader br = new BufferedReader(fr);
                    while ((line = br.readLine()) != null) {
                        lineMap.put(line,1);
                    }
            }
            for(String key :lineMap.keySet()) {

//                if(key.contains("逾期")) {
//                    pw.println("逾期,"+key.replace(",","，").replace("\"",""));
//                }

//                if(key.contains("分期") && (key.contains("元") || key.contains("元"))) {
//                    pw.println("网络借贷现金分期,"+key.replace(",","，").replace("\"",""));
//                }

                if(key.contains("注册")  &&  !(key.contains("注册成功")||key.contains("感谢注册")||key.contains("成功注册"))  ) {
                    pw.println("网络借贷广告,"+key.replace(",","，").replace("\"",""));
                }

            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            se.printStackTrace();
        }
    }

    private static void distinctFile(String filePath,String writeFile) {
        try {
            File readFile = new File(filePath);
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int index = 0;
            String tempLine = "";
            Map<String,String> map = new HashMap<String, String>();
            String lastLine = "";
            while ((line = br.readLine()) != null) {
                if(StringUtils.isEmpty(lastLine)) {
                    lastLine = lastLine+line;
                } else {
                    tempLine = line;
                    if (tempLine.split(",").length == 2) {
                        String sign = lastLine.split(",")[0];
                        String str = lastLine.split(",")[1];
                        map.put(str,sign);
                        lastLine = tempLine;
                    } else {
                        lastLine = lastLine+line;
                    }
                }
            }
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);
            for(String key : map.keySet()) {
                pw.println(map.get(key)+","+key);
            }
        }catch(Exception e){

        }
    }

    private static void distinctErrorFile(String filePath,String writeFile) {
        try {
            File readFile = new File(filePath);
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int index = 0;
            String tempLine = "";
            Map<String,String> map = new HashMap<String, String>();
            String lastLine = "";
            while ((line = br.readLine()) != null) {
                if(StringUtils.isEmpty(lastLine)) {
                    lastLine = lastLine+line;
                } else {
                    tempLine = line;
                    if (tempLine.split(",").length == 5) {
                        String id = lastLine.substring(0,tempLine.indexOf(","));
                        String str = lastLine.substring(tempLine.indexOf(",")+1,lastLine.length());
                        map.put(str,id);
                        lastLine = tempLine;
                    } else {
                        lastLine = lastLine+line;
                    }
                }




            }
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);
            for(String key : map.keySet()) {
                pw.println(map.get(key)+","+key);
            }
        }catch(Exception e){

        }
    }


    public static void getNum() {
        int index = 0;
        try {
            File readFile = new File("E:\\a.txt");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";

            while ((line = br.readLine()) != null) {
                JSONObject json = JSONUtils.getJson(line);
                if(StringUtils.isEmpty(json.getString("content"))) {
                    System.out.println(index+"   "+json.getString("content"));
                    index++;
                }

            }
            System.out.println("index:"+index);
        } catch (Exception e) {

        }
        System.out.println("index:"+index);
    }

    public static void getPartNum() {
        try {
            for(int i=0;i<=3;i++) {
                File readFile = new File("E:\\message_all.txt");
                FileReader fr = new FileReader(readFile);
                BufferedReader br = new BufferedReader(fr);
                Map<String,Integer> map = new HashMap<String, Integer>();
                String line = "";
                int index = 0;
                int sum = 0;
                while ((line = br.readLine()) != null) {
                    if(map.get(line.split(",")[0]) !=null ) {
                        map.put(line.split(",")[0],map.get(line.split(",")[0])+1);
                    } else {
                        map.put(line.split(",")[0],1);
                    }
                }
                // System.out.println("index:"+index+" length3:"+sum);
                for(String key : map.keySet()) {
                    System.out.println(key+" "+map.get(key));
                }
            }

        } catch (Exception e) {

        }
    }

    /**
     * 在发送短信中，找回复短信的发送内容
     */
    public static void find() {

        try {
            //
            File msgReplyFile = new File("E:\\2019-04-06\\message-reply-customerId-mobile-2019-04-06.txt");
            FileWriter fw = new FileWriter(msgReplyFile, true);
            PrintWriter pw = new PrintWriter(fw);

            //回复
            File replyFile = new File("E:\\2019-04-06\\reply-2019-04-06.txt");
            FileReader replyFr = new FileReader(replyFile);
            BufferedReader replyBr = new BufferedReader(replyFr);
            String replyLine = "";
            Map<String,JSONObject> map = new HashMap<String, JSONObject>();
            while ((replyLine = replyBr.readLine()) != null) {
                JSONObject json = JSON.parseObject(replyLine);
                map.put(json.getString("customerId")+json.getString("mobile"),json);
            }

            //发送短信
            File readFile = new File("E:\\2019-04-06\\2019-04-06.txt");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                JSONObject json = JSON.parseObject(line);
                JSONObject replyContent = map.get(json.getString("customerId")+json.getString("mobile"));
                if(replyContent != null) {
                    json.put("replyContent",replyContent.getString("replyContent"));
                    json.put("replyChannelId",replyContent.getString("channelId"));
                    System.out.println(json.toJSONString());
                    pw.println(json.toJSONString());
                }
            }

            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
            replyBr.close();
            replyFr.close();
            br.close();
            fr.close();
        } catch (Exception e) {

        }
    }

    /**
     * 从回复短信记录里面抽取部分数据保存成txt文档
     * @param writePath
     * @param readPath
     */
    public static void reply(String writePath, String readPath) {
        File writeFile = new File(writePath);
        File newFile = new File(readPath);
        try{
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);

            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0;
            String tempLine = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date start = format.parse("2018-08-27 00:00:00");
//            Date end = format.parse("2018-08-27 23:59:59");
            while ((line = br.readLine()) != null) {
                if(!line.contains("customerId")) {
                    sum++;
                    //System.out.println("line:"+line);
                    tempLine = tempLine+ line;
                    if(tempLine.split("\",\"").length == 5) {
                        String array[] = tempLine.split("\",\"");
                        String ctime = array[3].replace("\"","");
                        Date cDate = format.parse(ctime);
                        //    if(cDate.getTime()>= start.getTime() && cDate.getTime() <= end.getTime() ) {
                        if(true){
                            index++;
                            JSONObject json  = new JSONObject();
                            json.put("customerId",array[0].replace("\"",""));
                            json.put("mobile",array[1].replace("\"",""));
                            json.put("replyContent",array[2].replace("\"",""));
                            json.put("ctime",array[3].replace("\"",""));
                            json.put("channelId",array[4].replace("\"",""));
                            pw.println(json.toJSONString());
                        }

                        tempLine = "";
                    }
                }
            }
            System.out.println("index:"+index+" sum:"+sum);
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    }

    /**
     * 将通道csv文件转为json格式的txt
     * @param writePath
     * @param readPath
     */
    public static void channel(String writePath, String readPath) {
        File writeFile = new File(writePath);
        File newFile = new File(readPath);
        try{
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);
            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0;
            String tempLine = "";
            while ((line = br.readLine()) != null) {
                if(!line.contains("id")) {
                    sum++;
                    //System.out.println("line:"+line);
                    tempLine = tempLine+ line;
                    if(tempLine.split("\",\"").length == 11) {
                        String array[] = tempLine.split("\",\"");
                        index++;
                        JSONObject json  = new JSONObject();
                        json.put("id",array[0].replace("\"",""));
                        json.put("name",array[1].replace("\"",""));
                        json.put("provider",array[2].replace("\"",""));
                        json.put("operator",array[3].replace("\"",""));
                        json.put("channelType",array[4].replace("\"",""));
                        json.put("status",array[5].replace("\"",""));
                        json.put("channelLabel",array[6].replace("\"",""));
                        json.put("channelCompany",array[7].replace("\"",""));
                        json.put("channelLabelNames",array[8].replace("\"",""));
                        json.put("label",array[9].replace("\"",""));
                        json.put("companyName",array[10].replace("\"",""));
                        pw.println(json.toJSONString());
                        tempLine = "";
                    }
                }
            }
            System.out.println("index:"+index+" sum:"+sum);
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            se.printStackTrace();
        }
    }

    /**
     * 将批次csv文件转为json格式的txt
     * @param writePath
     * @param readPath
     */
    public static void batch(String writePath, String readPath) {
        try{


            //读取channel.txt文件
            File channelFile = new File("E:\\channel.txt");
            FileReader channelfr = new FileReader(channelFile);
            BufferedReader channelbr = new BufferedReader(channelfr);
            String channelLine = "";
            Map<String,String> channelMap = new HashMap<String, String>();
            while ((channelLine = channelbr.readLine()) != null) {
                JSONObject json = JSONUtils.getJson(channelLine);
                String label = json.getString("label").trim();
                String id = json.getString("id").trim();
                channelMap.put(id,label);
            }


            File writeFile = new File(writePath);
            File newFile = new File(readPath);
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);
            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0,number = 0;
            String tempLine = "";
            while ((line = br.readLine()) != null) {
                if(!line.contains("mobileSuggestChannelId")) {
                    sum++;
                    System.out.println("sum:"+sum);
                    tempLine = tempLine+ line;
                    if(tempLine.split("\",\"").length == 4) {
                        String array[] = tempLine.split("\",\"");
                        index++;

                        String mobileLabel = channelMap.get(array[1].replace("\"",""));
                        String unicomLabel = channelMap.get(array[2].replace("\"",""));
                        String telecomLabel = channelMap.get(array[3].replace("\"",""));

                        if(!StringUtils.isEmpty(mobileLabel) && !StringUtils.isEmpty(unicomLabel) && !StringUtils.isEmpty(telecomLabel)
                                && mobileLabel.equals(unicomLabel) && mobileLabel.equals(telecomLabel)
                        ) {
                            number++;
                            pw.println(mobileLabel+","+array[0].replace("\"","").replace(",","，"));
                        }
                        tempLine = "";
                    }
                }
            }
            System.out.println("number:"+number+" index:"+index+" sum:"+sum);
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            se.printStackTrace();
        }
    }


    /**
     * 从发送短信中，抽取部分数据保存成txt文档
     * @param writePath
     * @param readPath
     */
    public static void message(String writePath, String readPath) {
        File writeFile = new File(writePath);
        File newFile = new File(readPath);
        try{
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);

            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0;
            String tempLine = "";
            while ((line = br.readLine()) != null) {
                if(!line.contains( "customerId")) {
                    sum++;
                    //System.out.println("line:"+line);
                    tempLine = tempLine+line;
                    if(tempLine.split("\",\"").length == 5) {
                        String array[] = tempLine.split("\",\"");
                        if(array.length != 5) {
                            System.out.println("line:"+tempLine);
                        } else {
                            index++;
                            JSONObject json  = new JSONObject();
                            json.put("customerId",array[0].replace("\"",""));
                            json.put("mobile",array[1].replace("\"",""));
                            json.put("content",array[2].replace("\"",""));
                            json.put("ctime",array[3].replace("\"",""));
                            json.put("channelId",array[4].replace("\"",""));
                            pw.println(json.toJSONString());
                        }
                        tempLine = "";
                    }
                }
            }
            System.out.println("index:"+index+" sum:"+sum);

            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    }

    public static void getLostMessage(String writePath, String readPath) {
        System.out.println(writePath+"   "+readPath);
        File writeFile = new File(writePath);
        File newFile = new File(readPath);
        try{
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);

            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0;
            String tempLine = "";
            while ((line = br.readLine()) != null) {
                if(!line.contains( "customerId")) {
                    sum++;
                    //System.out.println("line:"+line);
                    tempLine = tempLine+line;
                    if(tempLine.split("\",\"").length == 27) {
                        String array[] = tempLine.split("\",\"");
                        if(array.length != 27) {
                            System.out.println("line:"+tempLine);
                        } else {
                            index++;
                            JSONObject json = new JSONObject();
                            json.put("id",array[0].replace("\"",""));
                            json.put("batchId",array[1]);
                            json.put("customerId",array[2]);
                            json.put("mobile",array[3]);
                            json.put("mobileOperator",array[4]);
                            json.put("content",array[5]);
                            json.put("sign",array[6]);
                            json.put("smCount",array[7]);
                            json.put("categoryId",array[8]);
                            json.put("validateFlag",array[9]);
                            json.put("validateTime",array[10]);
                            json.put("validateAuthor",array[11]);
                            json.put("smUuid",array[12]);
                            json.put("scheduleSendTIme",array[13]);
                            json.put("scheduleSendFlag",array[14]);
                            json.put("suggestChannelId",array[15]);
                            json.put("ctime",array[16]);
                            json.put("ltime",array[17]);
                            json.put("remoteIp",array[18]);
                            json.put("channelId",array[19]);
                            json.put("submitTime",array[20]);
                            json.put("submitResult",array[21]);
                            json.put("deliverTime",array[22]);
                            json.put("deliverResult",array[23]);
                            json.put("submitResultReal",array[24]);
                            json.put("deliverResultReal",array[25]);
                            json.put("status",array[26].replace("\"",""));
                            pw.println(json.toJSONString());
                        }
                        tempLine = "";
                    } else {
                        //System.out.println("line:"+tempLine);
                    }
                }
            }
            System.out.println("index:"+index+" sum:"+sum);

            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    }


    public static void replyMsg(String writePath, String readPath) {
        System.out.println(writePath+"   "+readPath);
        File writeFile = new File(writePath);
        File newFile = new File(readPath);
        try{
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);

            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0;
            String tempLine = "";
            while ((line = br.readLine()) != null) {

                JSONObject rs = JSON.parseObject(line);
                JSONObject json = new JSONObject();
                if(index < 100) {
                    if(rs.getString("id").contains("\"")) {
                        System.out.println(rs.toJSONString());
                        index ++;
                    }
                }

                json.put("id",rs.getString("id").replace("\"",""));
                json.put("batchId",rs.getString("batchId"));
                json.put("customerId",rs.getString("customerId"));
                json.put("mobile",rs.getString("mobile"));
                json.put("mobileOperator",rs.getString("mobileOperator"));
                json.put("content",rs.getString("content"));
                json.put("sign",rs.getString("sign"));
                json.put("smCount",rs.getString("smCount"));
                json.put("categoryId",rs.getString("categoryId"));
                json.put("validateFlag",rs.getString("validateFlag"));
                json.put("validateTime",rs.getString("validateTime"));
                json.put("validateAuthor",rs.getString("validateAuthor"));
                json.put("smUuid",rs.getString("smUuid"));
                json.put("scheduleSendTIme",rs.getString("scheduleSendTIme"));
                json.put("scheduleSendFlag",rs.getString("scheduleSendFlag"));
                json.put("suggestChannelId",rs.getString("suggestChannelId"));
                json.put("ctime",rs.getString("ctime"));
                json.put("ltime",rs.getString("ltime"));
                json.put("remoteIp",rs.getString("remoteIp"));
                json.put("channelId",rs.getString("channelId"));
                json.put("submitTime",rs.getString("submitTime"));
                json.put("submitResult",rs.getString("submitResult"));
                json.put("deliverTime",rs.getString("deliverTime"));
                json.put("deliverResult",rs.getString("deliverResult"));
                json.put("submitResultReal",rs.getString("submitResultReal"));
                json.put("deliverResultReal",rs.getString("deliverResultReal"));
                json.put("status",rs.getString("status").replace("\"",""));
                pw.println(json.toJSONString());

            }
            System.out.println("index:"+index+" sum:"+sum);

            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }catch(Exception se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    }

    public static void insertBatchToOss(String writePath, String readFile,String tableName) {
        System.out.println( readFile);
        File newFile = new File(readFile);

        try{
            File writeFile = new File(writePath);
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);
            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0;
            String tempLine = "";
            String sql = "insert into "+tableName+" values ";
            while ((line = br.readLine()) != null) {
                if(!line.contains( "customerId")) {
                    sum++;
                    //System.out.println("line:"+line);
                    tempLine = tempLine+line;
                    if(tempLine.split("\",\"").length == 33) {
                        String array[] = tempLine.split("\",\"");
                        if(array.length != 33) {
                            System.out.println("line:"+tempLine);
                        } else {
                            index++;
                            if(index>=1360001) {
                                String str17 = null;
                                if (!StringUtils.isEmpty(array[17].replace("\"", ""))) {
                                    str17 = array[17].replace("\"", "");
                                }
                                String str16 = null;
                                if (!StringUtils.isEmpty(array[16].replace("\"", ""))) {
                                    str16 = "'" + array[16].replace("\"", "") + "'";
                                }
                                String str11 = null;
                                if (!StringUtils.isEmpty(array[11].replace("\"", ""))) {
                                    str11 = "'" + array[11].replace("\"", "") + "'";
                                }
                                String str22 = null;
                                if (!StringUtils.isEmpty(array[22].replace("\"", ""))) {
                                    str17 = array[22].replace("\"", "");
                                }
                                String str7 = array[7].replace("\"", "").replace("'", "’");
//                                if(index>=86310&& index<=86315) {
//                                    System.out.println(str7);
//                                }
//                                while (str7.contains("\\x")) {
//                                    str7 = str7.substring(0, str7.indexOf("\\x")) + str7.substring(str7.indexOf("\\x") + 4);
//                                }

//                            String sql = "insert into tbl_app_marketing_batch values ";
                                sql = sql + "(" + array[0].replace("\"", "") + "," + array[1].replace("\"", "") + "," + array[2].replace("\"", "") +
                                        "," + array[3].replace("\"", "") + "," + array[4].replace("\"", "") + "," + array[5].replace("\"", "") +
                                        "," + array[6].replace("\"", "") + ",'" + str7 + "','" + array[8].replace("\"", "") + "'," +
                                        array[9].replace("\"", "") + "," + array[10].replace("\"", "") + "," + str11 +
                                        ",'" + array[12].replace("\"", "") + "'," + array[13].replace("\"", "") + ",'" + array[14].replace("\"", "") +
                                        "','" + array[15].replace("\"", "") + "'," + str16 + "," + str17 + "," +
                                        array[18].replace("\"", "") + "," + array[19].replace("\"", "") + "," + array[20].replace("\"", "") +
                                        "," + array[21].replace("\"", "") + "," + str22 + "," + array[23].replace("\"", "") +
                                        "," + array[24].replace("\"", "") + ",'" + array[25].replace("\"", "") + "','" + array[26].replace("\"", "") + "'" +
                                        "," + array[27].replace("\"", "") + "," + array[28].replace("\"", "") + "," + array[29].replace("\"", "") +
                                        "," + array[30].replace("\"", "") + ",'" + array[31].replace("\"", "") + "'," + array[32].replace("\"", "") + "),";
//                            pw.println(sql);
                                if (index % 5000 == 0) {
                                    Connection conn = null;
                                    Class.forName("com.mysql.jdbc.Driver");
                                    conn = DriverManager.getConnection("jdbc:mysql://210.5.152.207:3306/crdb_1", "root", "root");
                                    Statement st = conn.createStatement();
                                    //  System.out.println(sql.substring(0,sql.length()-1));
                                    st.execute(sql.substring(0, sql.length() - 1));
                                    System.out.println(index + "已完成");
                                    sql = "insert into "+tableName+" values ";
                                }

                            }

                        }
                        tempLine = "";
                    } else {
                        //System.out.println("line:"+tempLine);
                    }
                }
            }

            Connection conn = null;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://210.5.152.207:3306/crdb_1","root","root");
            Statement st = conn.createStatement();
            st.execute(sql.substring(0,sql.length()-1));
            System.out.println(index +"已完成");
            System.out.println("index:"+index+" sum:"+sum);

        }catch(Exception se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    }

    public static void insertIdentifyBatchToOss(String writePath, String readFile,String tableName) {
        System.out.println( readFile);
        File newFile = new File(readFile);

        try{
            File writeFile = new File(writePath);
            FileWriter fw = new FileWriter(writeFile, true);
            PrintWriter pw = new PrintWriter(fw);
            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int index=0,sum=0;
            String tempLine = "";
            String sql = "insert into "+tableName+" values ";
            while ((line = br.readLine()) != null) {
                if(!line.contains( "customerId")) {
                    sum++;
                    //System.out.println("line:"+line);
                    tempLine = tempLine+line;
                    if(tempLine.split("\",\"").length == 31) {
                        String array[] = tempLine.split("\",\"");
                        if(array.length != 31) {
                            System.out.println("line:"+tempLine);
                        } else {
                            index++;
                            if(index>=1420001) {
                                String str17 = null;
                                if (!StringUtils.isEmpty(array[17].replace("\"", ""))) {
                                    str17 = array[17].replace("\"", "");
                                }
                                String str16 = null;
                                if (!StringUtils.isEmpty(array[16].replace("\"", ""))) {
                                    str16 = "'" + array[16].replace("\"", "") + "'";
                                }
                                String str11 = null;
                                if (!StringUtils.isEmpty(array[11].replace("\"", ""))) {
                                    str11 = "'" + array[11].replace("\"", "") + "'";
                                }
                                String str22 = null;
                                if (!StringUtils.isEmpty(array[22].replace("\"", ""))) {
                                    str17 = array[22].replace("\"", "");
                                }
                                String str7 = array[7].replace("\"", "").replace("'", "’");

                                sql = sql + "(" + array[0].replace("\"", "") + "," + array[1].replace("\"", "") + "," + array[2].replace("\"", "") +
                                        "," + array[3].replace("\"", "") + "," + array[4].replace("\"", "") + "," + array[5].replace("\"", "") +
                                        "," + array[6].replace("\"", "") + ",'" + str7 + "','" + array[8].replace("\"", "") + "'," +
                                        array[9].replace("\"", "") + "," + array[10].replace("\"", "") + "," + str11 +
                                        ",'" + array[12].replace("\"", "") + "'," + array[13].replace("\"", "") + ",'" + array[14].replace("\"", "") +
                                        "','" + array[15].replace("\"", "") + "'," + str16 + "," + str17 + "," +
                                        array[18].replace("\"", "") + "," + array[19].replace("\"", "") + "," + array[20].replace("\"", "") +
                                        "," + array[21].replace("\"", "") + "," + str22  + ",'" + array[23].replace("\"", "") + "','" + array[24].replace("\"", "") + "'" +
                                        "," + array[25].replace("\"", "") + "," + array[26].replace("\"", "") + "," + array[27].replace("\"", "") +
                                        ",'" + array[28].replace("\"", "") + "'," + array[29].replace("\"", "") + ",'" + array[30].replace("\"", "") + "'),";
//                            pw.println(sql);
                                if (index % 5000 == 0) {
                                    Connection conn = null;
                                    Class.forName("com.mysql.jdbc.Driver");
                                    conn = DriverManager.getConnection("jdbc:mysql://210.5.152.207:3306/crdb_1", "root", "root");
                                    Statement st = conn.createStatement();
//                                    System.out.println(sql.substring(0,sql.length()-1));
                                    st.execute(sql.substring(0, sql.length() - 1));
                                    System.out.println(index + "已完成");
                                    sql = "insert into "+tableName+" values ";
                                }

                            }

                        }
                        tempLine = "";
                    } else  {
//                         System.out.println("line:"+tempLine);
                    }
                }
            }

            Connection conn = null;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://210.5.152.207:3306/crdb_1","root","root");
            Statement st = conn.createStatement();
            System.out.println(sql);
            st.execute(sql.substring(0,sql.length()-1));
            System.out.println(index +"已完成");
            System.out.println("index:"+index+" sum:"+sum);

        }catch(Exception se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }
    }
}
