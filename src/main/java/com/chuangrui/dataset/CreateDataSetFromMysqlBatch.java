package com.chuangrui.dataset;

import com.alibaba.fastjson.JSONObject;
import com.chuangrui.utils.ConnectionUtils;
import com.chuangrui.utils.JSONUtils;
import com.chuangrui.utils.MessageDataHandle;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateDataSetFromMysqlBatch {

    public static void main(String args[]) {



        getBatch("identify");
        getBatch("marketing");
//        getSignNumber();
    }

    private static void getSignNumber() {
        try {

            HashMap<String, Integer> map = ConnectionUtils.getSign();

            File dir = new File("E:\\dataSet");
            File[] tempList = dir.listFiles();
            Map<String,Integer> signMap = new HashMap<String, Integer>();
            for(File file : tempList) {
                FileReader fr = new FileReader(file);
                System.out.println(file.getName());
                BufferedReader  br = new BufferedReader(fr);
                String line = "";

                while ((line = br.readLine()) != null) {
                    if(line.split(",").length !=2) {
                        System.out.println(line);
                    } else {
                        if(signMap.get(line.split(",")[0]) == null) {
                            signMap.put(line.split(",")[0],1);
                        } else {
                            signMap.put(line.split(",")[0],signMap.get(line.split(",")[0])+1);
                        }

                    }

                }

//                while ((line = br.readLine()) != null) {
//                    if(line.split(",").length !=2) {
//                        System.out.println(line);
//                    } else {
//                        if(StringUtils.isEmpty(line)) {
//                            System.out.println(line);
//                        }
//                        Integer type = map.get(line.split(",")[0]);
//                        if(type == null ) {
//                            System.out.println(line);
//                        }
//                    }
//
//                }
            }
            System.out.println("sum:"+signMap.keySet().size());
            for(String key : signMap.keySet()) {
                System.out.println(key+" "+signMap.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取某个文件夹下的所有文件
     *
     * @param fileNameList 存放文件名称的list
     * @param path 文件夹的路径
     * @return
     */
    public static void getAllFileName(String path, ArrayList<String> fileNameList) {
        //ArrayList<String> files = new ArrayList<String>();
        boolean flag = false;
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
//              System.out.println("文     件：" + tempList[i]);
                //fileNameList.add(tempList[i].toString());
                fileNameList.add(tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
//              System.out.println("文件夹：" + tempList[i]);
                getAllFileName(tempList[i].getAbsolutePath(),fileNameList);
            }
        }
        return;
    }


    private static void getBatch(String type) {
        try {
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

            FileWriter fw = new FileWriter("E:\\"+type+"-msgSortDataSet.txt",true);
            PrintWriter pw = new PrintWriter(fw);
            FileWriter errorfw = new FileWriter("E:\\"+type+"-errorMsgSortDataSet.txt",true);
            PrintWriter errorPw = new PrintWriter(errorfw);

            Connection conn = getConn();
            Statement st = conn.createStatement();
            String sql = "select id,template,mobileSuggestChannelId,unicomSuggestChannelId,telecomSuggestChannelId,validateFlag,sign  from tbl_app_"+type+"_batch";
            String countSql = "SELECT count(1) number FROM `crdb`.`tbl_app_"+type+"_batch`";
            System.out.println(countSql);
            System.out.println(sql);
            ResultSet countRs = st.executeQuery(countSql);
            int sum = 0;
            while (countRs.next()) {
                sum = countRs.getInt("number");
            }
            System.out.println("sum :"+sum);
            int pageSize = 100000;
            int pageCount = 0;
            if(sum % pageSize == 0) {
                pageCount = sum / pageSize;
            } else {
                pageCount = sum / pageSize+1;
            }
            int index = 0;
            for(int j=0;j<pageCount;j++) {
                String tempSql = sql +" limit "+j*pageSize+","+pageSize;
                System.out.println(" limit "+j*pageSize+","+pageSize);
                ResultSet rs = st.executeQuery(tempSql);
                while (rs.next()) {
                    index++;
                    Integer id = rs.getInt("id");
                    String template = rs.getString("template");
                    String mobileSuggestChannelId = rs.getString("mobileSuggestChannelId");
                    String unicomSuggestChannelId = rs.getString("unicomSuggestChannelId");
                    String telecomSuggestChannelId = rs.getString("telecomSuggestChannelId");
                    String validateFlag = rs.getString("validateFlag");
                    String sign = rs.getString("sign");
                    String mobileLabel = channelMap.get(mobileSuggestChannelId);
                    String unicomLabel = channelMap.get(unicomSuggestChannelId);
                    String telecomLabel = channelMap.get(telecomSuggestChannelId);
                    String label = getLabel(mobileLabel,unicomLabel,telecomLabel);
                    if("1".equals(validateFlag) || "4".equals(validateFlag)) {

                    }
                    if(!StringUtils.isEmpty(label)) {
                        if(!StringUtils.isEmpty(template)) {
                            pw.println(  label + "," +sign.replace("【","").replace("】","")+ template.replace(",", "，"));
                        }
                    } else {
                        if(!StringUtils.isEmpty(mobileSuggestChannelId) && !StringUtils.isEmpty(unicomSuggestChannelId) && !StringUtils.isEmpty(telecomSuggestChannelId)
                            && Integer.parseInt(mobileSuggestChannelId) >0 && Integer.parseInt(unicomSuggestChannelId) >0 && Integer.parseInt(telecomSuggestChannelId) >0)

                            if(!StringUtils.isEmpty(template)) {
                                errorPw.println(id+","+mobileSuggestChannelId+"-"+mobileLabel+","+unicomSuggestChannelId+"-"+unicomLabel+","+telecomSuggestChannelId+"-"+telecomLabel+","+template.replace(",","，"));
                        }

                    }

                }
            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getLabel(String mobileLabel, String unicomLabel, String telecomLabel) {
        String label = null;
        if(!StringUtils.isEmpty(mobileLabel)) {
            if(!StringUtils.isEmpty(unicomLabel)) {
                if(!StringUtils.isEmpty(telecomLabel)) {
                    if(mobileLabel.equals(unicomLabel ) && mobileLabel.equals(telecomLabel)) {
                        label = mobileLabel;
                    }
                } else {
                    if(mobileLabel.equals(unicomLabel ) ) {
                        label = mobileLabel;
                    }
                }
            } else {
                if(!StringUtils.isEmpty(telecomLabel)) {
                    if(mobileLabel.equals(telecomLabel ) ) {
                        label = mobileLabel;
                    }
                } else {
                    label = mobileLabel;
                }
            }
        } else {
            if(!StringUtils.isEmpty(unicomLabel)) {
                if(!StringUtils.isEmpty(telecomLabel)) {
                    if(unicomLabel.equals(telecomLabel ) ) {
                        label = unicomLabel;
                    }
                } else {
                    label = unicomLabel;
                }
            } else {
                if(!StringUtils.isEmpty(telecomLabel)) {
                    label = telecomLabel;
                } else {

                }
            }
        }
        return label;
    }

    public static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://cloudsp1.mysql.rds.aliyuncs.com:3306/crdb", "cr","Chuangrui@8254");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
