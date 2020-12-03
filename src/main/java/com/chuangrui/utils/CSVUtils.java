package com.chuangrui.utils;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *  
 * 文件操作 
 */
public class CSVUtils {

    /**
     *  
     * 生成为CVS文件 
     *
     * @param exportData  源数据List 
     * @param map         csv文件的列表头map 
     * @param outPutPath  文件路径 
     * @param fileName    文件名称 
     * @return 
     */
    @SuppressWarnings("rawtypes")
    public static File createCSVFile(List exportData, LinkedHashMap map, String outPutPath,
                                     String fileName) {
        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        try {
            File file = new File(outPutPath);
            if (!file.exists()) {
                file.mkdir();
            }
            //定义文件名格式并创建
            csvFile = File.createTempFile(fileName, ".csv", new File(outPutPath));
            System.out.println("csvFile：" + csvFile);
            // UTF-8使正确读取分隔符","
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "UTF-8"), 1024);
            System.out.println("csvFileOutputStream：" + csvFileOutputStream);
            // 写入文件头部
            for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext(); ) {
                java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();
                csvFileOutputStream.write((String) propertyEntry.getValue() != null ? (String) propertyEntry.getValue() : "");
                if (propertyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();
            // 写入文件内容
            for (Iterator iterator = exportData.iterator(); iterator.hasNext(); ) {
                Object row = (Object) iterator.next();
                for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext(); ) {
                    java.util.Map.Entry propertyEntry = (java.util.Map.Entry) propertyIterator.next();
                    csvFileOutputStream.write((String) BeanUtils.getProperty(row,
                            (String) propertyEntry.getKey()));
                    if (propertyIterator.hasNext()) {
                        csvFileOutputStream.write(",");
                    }
                }
                if (iterator.hasNext()) {
                    csvFileOutputStream.newLine();
                }
            }
            csvFileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     *  
     * 下载文件 
     *
     * @param response 
     * @param csvFilePath  文件路径 
     * @param fileName     文件名称 
     * @throws IOException 
     */
    public static void exportFile(HttpServletResponse response, String csvFilePath, String fileName)
            throws IOException {
        response.setContentType("application/csv;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

        InputStream in = null;
        try {
            in = new FileInputStream(csvFilePath);
            int len = 0;
            byte[] buffer = new byte[1024];
            response.setCharacterEncoding("UTF-8");
            OutputStream out = response.getOutputStream();
            while ((len = in.read(buffer)) > 0) {
                out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
                out.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     *  
     * 删除该目录filePath下的所有文件 
     *
     * @param filePath  文件目录路径 
     */
    public static void deleteFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
    }

    /**
     *  
     * 删除单个文件 
     *
     * @param filePath  文件目录路径 
     * @param fileName  文件名称 
     */
    public static void deleteFile(String filePath, String fileName) {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].getName().equals(fileName)) {
                        files[i].delete();
                        return;
                    }
                }
            }
        }
    }

    /**
     *  
     * 测试数据 
     *
     * @param args 
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {

        List exportData = new ArrayList<Map>();

        try {
            File newFile = new File("F:\\test\\");
            String[] filelist = newFile.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File("F:\\test\\"+ filelist[i]);
                String line;
                FileReader fr = new FileReader(readfile);
                BufferedReader br = new BufferedReader(fr);
                int index = 0;
                while ((line = br.readLine()) != null) {
                    JSONObject json = JSONObject.parseObject(line);
                    Map row1 = new LinkedHashMap<String, String>();
                    row1.put("1", "\""+json.getString("id")+"\"");
                    row1.put("2", "\""+json.getString("batchId")+"\"");
                    row1.put("3", "\""+json.getString("customerId")+"\"");
                    row1.put("4", "\""+json.getString("mobile")+"\"");
                    row1.put("5", "\""+json.getString("mobileOperator")+"\"");
                    row1.put("6", "\""+json.getString("content")+"\"");
                    row1.put("7", "\""+json.getString("sign")+"\"");
                    row1.put("8", "\""+json.getString("smCount")+"\"");
                    row1.put("9", "\""+json.getString("categoryId")+"\"");
                    row1.put("10", "\""+json.getString("validateFlag")+"\"");
                    row1.put("11", "\""+json.getString("validateTime")+"\"");
                    row1.put("12", "\""+json.getString("validateAuthor")+"\"");
                    row1.put("13", "\""+json.getString("smUuid")+"\"");
                    if(StringUtils.isEmpty(json.getString("scheduleSendTIme"))) {
                        row1.put("14", "");
                    } else {
                        row1.put("14", "\""+json.getString("scheduleSendTIme")+"\"");
                    }

                    row1.put("15", "\""+json.getString("scheduleSendFlag")+"\"");
                    row1.put("16", "\""+json.getString("suggestChannelId")+"\"");
                    row1.put("17", "\""+json.getString("ctime")+"\"");
                    row1.put("18", "\""+json.getString("ltime")+"\"");
                    row1.put("19", "\""+json.getString("remoteIp")+"\"");
                    row1.put("20", "\""+json.getString("channelId")+"\"");
                    row1.put("21", "\""+json.getString("submitTime")+"\"");
                    row1.put("22", "\""+json.getString("submitResult")+"\"");
                    row1.put("23", "\""+json.getString("deliverTime")+"\"");
                    row1.put("24", "\""+json.getString("deliverResult")+"\"");
                    row1.put("25", "\""+json.getString("submitResultReal")+"\"");
                    row1.put("26", "\""+json.getString("deliverResultReal")+"\"");
                    row1.put("27", "\""+json.getString("status")+"\"");
                    exportData.add(row1);
                }
            }

            LinkedHashMap map = new LinkedHashMap();
            //设置列名
            map.put("1", "id");
            map.put("2", "batchId");
            map.put("3", "customerId");
            map.put("4", "mobile");
            map.put("5", "mobileOperator");
            map.put("6", "content");
            map.put("7", "sign");
            map.put("8", "smCount");
            map.put("9", "categoryId");
            map.put("10", "validateFlag");
            map.put("11", "validateTime");
            map.put("12", "validateAuthor");
            map.put("13", "smUuid");
            map.put("14", "scheduleSendTIme");
            map.put("15", "scheduleSendFlag");
            map.put("16", "suggestChannelId");
            map.put("17", "ctime");
            map.put("18", "ltime");
            map.put("19", "remoteIp");
            map.put("20", "channelId");
            map.put("21", "submitTime");
            map.put("22", "submitResult");
            map.put("23", "deliverTime");
            map.put("24", "deliverResult");
            map.put("25", "submitResultReal");
            map.put("26", "deliverResultReal");
            map.put("27", "status");

            //这个文件上传到路径，可以配置在数据库从数据库读取，这样方便一些！
            String path = "F:/reateCSVFile/";

            //文件名=生产的文件名称+时间戳
            String fileName = "46323";
            System.out.println("fileName:"+fileName);
            File file = CSVUtils.createCSVFile(exportData, map, path, fileName);
            String fileName2 = file.getName();
            System.out.println("文件名称：" + fileName2);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
