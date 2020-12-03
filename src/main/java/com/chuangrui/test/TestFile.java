package com.chuangrui.test;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestFile {

    public static void main(String[] args) throws Exception{
        String url = "E:\\sqlResult_2314374.csv";
        //BufferedReader是可以按行读取文件
        FileInputStream inputStream = new FileInputStream(new File(url));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        ContentTagMgr.getInstance().reload();

        String str = null;
        String csvSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        List<String> list = new ArrayList<String>();
        while((str = bufferedReader.readLine()) != null)
        {
            String [] temp = str.split(csvSplitBy);
            if(temp.length != 2){
                continue;
            }
            try {
                String sign = temp[1].replaceAll("\\\"","");
                String content = temp[0].replaceAll("\\\"","").replaceAll(",","，");

                String tag = ContentTagMgr.getInstance().getTag(sign,content);
                list.add(tag +"," + sign + content);

            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
        }
        inputStream.close();
        bufferedReader.close();
        writeCSV(list,"短信分类");

    }

    public static void writeCSV(List<String> dataList,String fileName){
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            File finalCSVFile = new File("E:\\"+fileName+".csv");
            out = new FileOutputStream(finalCSVFile);
            osw = new OutputStreamWriter(out, "UTF-8");
            // 手动加上BOM标识
            osw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }));
            bw = new BufferedWriter(osw);

            if (dataList != null && !dataList.isEmpty()) {
                for (String data : dataList) {
                    bw.append(data).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static String formatDate(String time) {
        time = time.replace("\"","");
        if(time!=null){
            SimpleDateFormat sdf = null;
                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            try {
                Date date = sdf.parse(time);
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                String formatDate = sdf.format(date);
                return formatDate;
            } catch (ParseException e) {
                return "";
            }
        }else{
            return "";
        }
    }
}


