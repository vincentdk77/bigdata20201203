package com.chuangrui.dataset;

import com.chuangrui.test.ContentTagMgr;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.message.internal.StringBuilderUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 从batch表查出来的数据，标签不对的数据通过关键字再次分类
*/
public class HandleErrorDataSet {
    public static void main(String args[]) {
        try {
            File readFile = new File("E:\\identify-distinct-errorMsgSortDataSet.txt");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);

            FileWriter fw = new FileWriter("E:\\identify-distinct-recovery-errorMsgSortDataSet.txt", true);
            PrintWriter pw = new PrintWriter(fw);


            FileWriter errorfw = new FileWriter("E:\\error.txt", true);
            PrintWriter errorPw = new PrintWriter(errorfw);

            String line = "";
            int index = 1;
            ContentTagMgr.getInstance().reload();
            while ((line = br.readLine()) != null) {
                String msg = line.split(",")[4];

                String tag = ContentTagMgr.getInstance().getTag("",msg);
                //System.out.println(index+" "+tag+" "+msg);
                if(!StringUtils.isEmpty(tag) && !tag.contains("^") && !"商超".equals(tag) && !"家装".equals(tag) && !"金融行业".equals(tag)) {
//                    pw.println(tag+","+msg);
                } else {
                    errorPw.println(msg);
                }
                index++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
