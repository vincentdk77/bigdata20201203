package com.chuangrui.utils;

import com.alibaba.fastjson.JSONObject;
import com.chuangrui.model.ChannelBlack;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ConnectionUtils {

    public static Statement st = null ;
    public static Connection conn = null;

    public static void main(String args[]) throws Exception {
//        String array[] = {"aa","bb"};
//        saveWord(array,"2019-04-19");
//        String msg =   "1@@@管娅菲您好，您已恶意违约，平台已多次电话催告，至今仍未收到还款。请立刻还清，否则平台将采取法律手段追回欠款！详询客服热线：0755-66866670，退订回T";
//        String ss[] = msg.split("@@@");
//        saveMessageSortResult(ss[1],"1","重度催收");
//        ArrayList<String> list =new ArrayList<String>();
//        list.add("13139077768@理财@1");
//        list.add("13139077769@理财@1");
//        list.add("15638707030@网贷@11132");
//        list.add("13163072302@网贷@11132,10857");
//        String timeStr="2017-12-08";
//        saveOrUpdateBatchCustomerSocre(list,timeStr);
//
//        String yearMonth = timeStr.substring(0,timeStr.lastIndexOf("-"));
//        System.out.print(yearMonth);
        int index =0;
        try{
            File readfile1 = new File("F:\\02\\"  );
            File[] files = readfile1.listFiles();
            for(File readfile : files) {
                String line;
                FileReader fr = new FileReader(readfile);
                BufferedReader br = new BufferedReader(fr);
                List<String> list= new ArrayList<String>();
                while ((line = br.readLine()) != null) {
                    index++;
                    list.add(line);
                    if(list.size() > 1000) {
                        System.out.println("index:"+index);
                        saveChannelBlack(list);
                        list.clear();
                    }
                }
                saveChannelBlack(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        String s = "{\"smCount\":\"5\",\"replyCustomerId\":\"13806\",\"replyChannelId\":\"633\",\"sign\":\"【龙行翔宇】\",\"scheduleSendFlag\":\"\",\"replyLevel\":1.0,\"batchId\":\"0\",\"content\":\"乘机人信息\\n姓名\\t类型\\t证件号\\t舱位\\n马金寿\\t成人\\t身份证:652301197909076814\\t经济舱\\n乌鲁木齐-西安\\n\\n中国春秋航空公司9C8846\\n起飞 2018-09-24 16:10 地窝堡机场 T1到达 2018-09-24 19:30 咸阳机场 T1\\n飞行\\n约3小时20分钟  已出票，票号： SUXWEX \\n （请于航班起飞前120分钟到达机场办理登机手续,如需行程单请于航班起飞后七日内致电，如需开通延误险请至少于航班起飞24小时前来电激活。如有任何疑问请拨打贵宾专线4008588666。）\\n客服工号795竭诚为您服务，祝您生活愉快！\",\"validateFlag\":\"1\",\"smUuid\":\"13806_1_0_18699921888_1_CTwRZbQ_5\",\"ltime\":\"\",\"replyLtime\":\"2018-09-18 08:12:48.0\",\"customerId\":\"13806\",\"deliverResult\":\"DELIVRD\",\"replyId\":\"8906908\",\"ctime\":\"2018-09-18 08:25:10.0\",\"id\":\"1325596242\",\"replyContent\":\"怎么支付\",\"deliverResultReal\":\"DELIVRD\",\"channelId\":\"633\",\"replyCtime\":\"2018-09-18 08:12:48.0\",\"submitResult\":\"0\",\"deliverTime\":\"2018-09-18 08:49:56.0\",\"remoteIp\":\"\",\"mobile\":\"18699921888\",\"mobileOperator\":\"\",\"submitResultReal\":\"0\",\"validateAuthor\":\"\",\"replyDeliverTime\":\"2018-09-18 08:12:47.0\",\"replyExtNo\":\"035679\",\"validateTime\":\"\",\"submitTime\":\"2018-09-18 08:49:51.0\",\"suggestChannelId\":\"\",\"categoryId\":\"\",\"status\":\"1\"}";

    }

    public static String append(String s) {
        if(StringUtils.isEmpty(s)) {
            return "null";
        }
        return "'"+s.replace("'","").replace("\\","")+"'";
    }


    // mobile+"@"+level+"@"+companyName+"@"+ctime
    public static void saveChannelBlack(List<String> lists) throws Exception{
        if(lists == null || lists.size() == 0) {
            return ;
        }

            String sql ="";
            Statement st = getSt();
            String mobiles = "";
            Map<String, ChannelBlack>  needInsertMap = new HashMap<String, ChannelBlack>();
            for (String s : lists) {
                String[] ss = s.split("@");
                ChannelBlack cb = new ChannelBlack();
                cb.setLevel(Integer.parseInt(ss[1]));
                cb.setLastTime(ss[3]);
                String cps[] = ss[2].split(",");
                cb.setCompanyName(cps);
                mobiles= mobiles+"'"+ss[0]+"'"+",";
                needInsertMap.put(ss[0],cb);
            }
            mobiles = mobiles.substring(0,mobiles.length()-1);
            Map<String,ChannelBlack> existsMap = new HashMap<String, ChannelBlack>();
            ResultSet rs = st.executeQuery("select mobile,company_names,level from channel_black where mobile in ("+mobiles+")");
            while (rs != null && rs.next()) {
                ChannelBlack cb = new ChannelBlack();
                cb.setMobile(rs.getString("mobile"));
                if(!StringUtils.isEmpty(rs.getString("company_names"))) {
                    cb.setCompanyName(rs.getString("company_names").split(","));
                }
                cb.setLevel(rs.getInt("level"));
                existsMap.put(rs.getString("mobile"),cb);
            }

             List<String> updateList= new ArrayList<String>();
            StringBuilder builder = new StringBuilder("insert into channel_black" +
                    "(mobile,company_names,level,last_time ) values ");
            PreparedStatement ps = getConn().prepareStatement("update channel_black set level=?, company_names=?,last_time=? where mobile =?");
            for(String mobile :needInsertMap.keySet()) {
                if(existsMap.get(mobile) == null) {
                    ChannelBlack cb = needInsertMap.get(mobile);
                    String compNames = "";
                    for(String c: cb.getCompanyName()) {
                        compNames = compNames+c+",";
                    }
                    compNames = compNames.substring(0,compNames.length()-1);
                    builder.append("('"+mobile+"','"+compNames+"',"+cb.getLevel()+",'"+cb.getLastTime()+"'),");
                } else {
                    ChannelBlack existCb = existsMap.get(mobile);
                    ChannelBlack cb = needInsertMap.get(mobile);
                    Integer level = cb.getLevel();
                    if(existCb.getLevel() > level) {
                        level = existCb.getLevel();
                    }
                    Map<String,Integer> companys = new HashMap<String, Integer>();
                    for(String c: cb.getCompanyName()) {
                        companys.put(c,1);
                    }
                    for(String c: existCb.getCompanyName()) {
                        companys.put(c,1);
                    }

                    String compNames = "";
                    for(String c: companys.keySet()) {
                        compNames = compNames+c+",";
                    }
                    compNames = compNames.substring(0,compNames.length()-1);
                    ps.setInt(1,level);
                    ps.setString(2,compNames);
                    ps.setString(3,cb.getLastTime());
                    ps.setString(4,mobile);
                    ps.addBatch();

                }
            }

            sql = builder.toString().substring(0, builder.toString().length() - 1);

            if(sql.length()> 80) {
                st.execute(sql);
            }
            ps.executeBatch();
//            getConn().commit();


//        st.close();
    }


    public static void saveMsgReply(List<String> lists) throws Exception{
        String sql ="";

                Statement st = getSt();
                StringBuilder builder = new StringBuilder("insert into msg_reply" +
                        "(id,batch_id,customer_id,mobile,mobile_operator,content,sign," +
                        "sm_count,category_id,validate_flag,validate_time,validate_author,sm_uuid,schedule_send_time," +
                        "schedule_send_flag,suggest_channel_id,ctime,ltime,remote_ip,channel_id,submit_time," +
                        "submit_result,deliver_time,deliver_result,submit_result_real,deliver_result_real,status,reply_id,reply_customer_id," +
                        "reply_content,reply_ext_no,reply_deliver_time,reply_channel_id,reply_ctime,reply_ltime,reply_level) values ");
                for (String s : lists) {
                    JSONObject json = JSONUtils.getJson(s);
                    String id = json.getString("id");
                    if (StringUtils.isEmpty(id)) {
                        id = "null";
                    }
                    String batchId = json.getString("batchId");
                    if (StringUtils.isEmpty(batchId)) {
                        batchId = "null";
                    }
                    String customerId = json.getString("customerId");
                    if (StringUtils.isEmpty(customerId)) {
                        customerId = "null";
                    }
                    String mobile = append(json.getString("mobile"));
                    String mobileOperator = append(json.getString("mobileOperator"));
                    String content = append(json.getString("content"));
                    if (CodeUtils.isMessyCode(content)) {
                        content = null;
                    }
                    String sign = append(json.getString("sign"));
                    String smCount = append(json.getString("smCount"));
                    String categoryId = json.getString("categoryId");
                    if (StringUtils.isEmpty(categoryId)) {
                        categoryId = "null";
                    }
                    String validateFlag = append(json.getString("validateFlag"));
                    String validateTime = append(json.getString("validateTime"));
                    String validateAuthor = append(json.getString("validateAuthor"));
                    String smUuid = append(json.getString("smUuid"));
                    String scheduleSendTIme = append(json.getString("scheduleSendTIme"));
                    String scheduleSendFlag = append(json.getString("scheduleSendFlag"));
                    String suggestChannelId = json.getString("suggestChannelId");
                    if (StringUtils.isEmpty(suggestChannelId)) {
                        suggestChannelId = "null";
                    }
                    String ctime = append(json.getString("ctime"));
                    String ltime = append(json.getString("ltime"));
                    String remoteIp = append(json.getString("remoteIp"));
                    String channelId = json.getString("channelId");
                    if (StringUtils.isEmpty(channelId)) {
                        channelId = "null";
                    }
                    String submitTime = append(json.getString("submitTime"));
                    String submitResult = append(json.getString("submitResult"));
                    String deliverTime = append(json.getString("deliverTime"));
                    String deliverResult = append(json.getString("deliverResult"));
                    String submitResultReal = append(json.getString("submitResultReal"));
                    String deliverResultReal = append(json.getString("deliverResultReal"));
                    String status = append(json.getString("status"));
                    String replyId = json.getString("replyId");
                    if (StringUtils.isEmpty(replyId)) {
                        replyId = "null";
                    }
                    String replyCustomerId = json.getString("replyCustomerId");
                    if (StringUtils.isEmpty(replyCustomerId)) {
                        replyCustomerId = "null";
                    }
                    String replyContent = append(json.getString("replyContent"));
                    if (CodeUtils.isMessyCode(replyContent)) {
                        replyContent = null;
                    }
                    String replyExtNo = append(json.getString("replyExtNo"));
                    String replyDeliverTime = append(json.getString("replyDeliverTime"));
                    String replyChannelId = json.getString("replyChannelId");
                    if (StringUtils.isEmpty(replyChannelId)) {
                        replyChannelId = "null";
                    }
                    String replyCtime = append(json.getString("replyCtime"));
                    String replyLtime = append(json.getString("replyLtime"));
                    String replyLevel = json.getString("replyLevel");
                    String v = "(" + id + "," + batchId + "," + customerId + "," + mobile + "," + mobileOperator + "," + content + "," + sign + "," +
                            "" + smCount + "," + categoryId + "," + validateFlag + "," + validateTime + "," + validateAuthor + "," + smUuid + "," + scheduleSendTIme + "," +
                            scheduleSendFlag + "," + suggestChannelId + "," + ctime + "," + ltime + "," + remoteIp + "," + channelId + "," + submitTime + "," +
                            submitResult + "," + deliverTime + "," + deliverResult + "," + submitResultReal + "," + deliverResultReal + "," + status + "," + replyId + "," +
                            replyCustomerId + "," + replyContent + "," + replyExtNo + "," + replyDeliverTime + "," + replyChannelId + "," + replyCtime + "," + replyLtime + "," + replyLevel + " ),";
                    builder.append(v);
                }
                sql = builder.toString().substring(0, builder.toString().length() - 1);
                st.execute(sql);
    }

    public static HashMap<String,Integer> getSign() {
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        try{

            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id,sign from sign");
            while(rs.next()) {
                Integer id =  rs.getInt("id");
                String sign =  rs.getString("sign");
                map.put(sign,id);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<String,String> getSignCode() {
        HashMap<String,String> map = new HashMap<String,String>();
        try{

            Statement st = getSt();
            ResultSet rs = st.executeQuery("select sign,sign_english from sign");
            while(rs.next()) {

                String sign =  rs.getString("sign");
                String sign_english =  rs.getString("sign_english");
                map.put(sign_english,sign);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<Integer,String> getSign2() {
        HashMap<Integer,String> map = new HashMap<Integer,String>();
        try{

            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id,sign from sign");
            while(rs.next()) {
                Integer id =  rs.getInt("id");
                String sign =  rs.getString("sign");
                map.put(id,sign);
            }
            rs.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String[] getWords() {
        String array[] = null;
        try {
            String timeStr = selectMaxTime();
            int number = 0;

            Statement st =  getSt();
            String countSql = "select count(1) number from word where date_time ='"+timeStr+"'";
            ResultSet countRs = st.executeQuery(countSql);
            while (countRs.next()) {
                number = countRs.getInt("number");
            }
            array = new String[number];
            String sql ="select word from word where date_time = '"+timeStr+"' order by id asc";
            ResultSet rs = st.executeQuery(sql);
            int index =0;
            while(rs.next()) {
                String word =  rs.getString("word");
                array[index] = word;
                index ++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public static void saveWord(String array[],String timeStr) {
        try{

            Statement st = getSt();
//            String sql ="delete from word";
//            st.execute(sql);
            String insertSql ="insert into word values ";
            for(int i=1;i<=array.length;i++) {
                insertSql = insertSql+"(null,'"+array[i-1]+"','"+timeStr+"'),";
            }
            insertSql = insertSql.substring(0,insertSql.length()-1);
            st.execute(insertSql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getReplyWords() {
        String array[] = null;
        try {
            String timeStr = selectReplyMaxTime();
            int number = 0;

            Statement st = getSt();
            String countSql = "select count(1) number from reply_word where date_time ='"+timeStr+"'";
            ResultSet countRs = st.executeQuery(countSql);
            while (countRs.next()) {
                number = countRs.getInt("number");
            }
            array = new String[number];
            String sql ="select word from reply_word where date_time = '"+timeStr+"' order by id asc";
            ResultSet rs = st.executeQuery(sql);
            int index =0;
            while(rs.next()) {
                String word =  rs.getString("word");
                array[index] = word;
                index ++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }



    public static void saveReplyWord(String array[],String timeStr) {
        try{

            Statement st = getSt();
//            String sql ="delete from word";
//            st.execute(sql);
            String insertSql ="insert into reply_word values ";
            for(int i=1;i<=array.length;i++) {
                insertSql = insertSql+"(null,'"+array[i-1]+"','"+timeStr+"'),";
            }
            insertSql = insertSql.substring(0,insertSql.length()-1);
            st.execute(insertSql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveMessageSortResult(String msg,String uuid,String sign) {
        try{

            Statement st = getSt();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql ="insert into msg_sort_result(uuid,msg,sign,add_time) values ('"+uuid+"','"+msg+"','"+sign+"','"+format.format(date)+"')";
            System.out.println("sql:"+sql);
            st.execute(sql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveMessageSortResultBatch(List<String > list) throws Exception{
        try {
            Statement st = getSt();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder builder = new StringBuilder("insert into msg_sort_result(uuid,msg,sign,add_time) values ");

            for (String s : list) {
                String ss[] = s.split("@@@");
                builder.append("('" + ss[0] + "','" + EmojiFilter.filterEmoji2(ss[2].replace("'", "")) + "','" + ss[1] + "','" + format.format(date) + "'),");
            }
            String sql = builder.toString().substring(0, builder.toString().length() - 1) + " ON DUPLICATE KEY UPDATE add_time=add_time+1";
            System.out.println("sql:" + sql);
            st.execute(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveReplySortResult(String msg,String uuid,String sign) {
        try{

            Statement st = getSt();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql ="insert into reply_sort_result(uuid,msg,sign,add_time) values ('"+uuid+"','"+msg+"','"+sign+"','"+format.format(date)+"')";

            st.execute(sql);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String selectReplyMaxTime() {
        String time = "";
        try{

            Statement st = getSt();
            Date date = new Date();

            String sql ="select max(date_time) date_time from reply_word";

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                time = rs.getString("date_time");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String selectMaxTime() {
        String time = "";
        try{
           Statement st = getSt();
            Date date = new Date();

            String sql ="select max(date_time) date_time from word";
            System.out.println("sql:"+sql);
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                time = rs.getString("date_time");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }



    public static Connection getConn() {

        try{
            if(conn != null) {
                return conn;
            }
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://61.132.230.78:3306/bigdata?autoReconnect=true&autoReconnectForPools=true&rewriteBatchedStatements=true","chuangrui1","chuangrui@123");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static Statement getSt() {
        if(st != null) {
            return st;
        }
        try{
            st = getConn().createStatement();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return st;
    }

    public static void saveTestDataSet(String sign, String msg, String uuid) {
        try{

            Statement st = getSt();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql ="insert into test_dataset(uuid,msg,origin_sort,add_time) values ('"+uuid+"','"+msg+"','"+sign+"','"+format.format(date)+"')";
            st.execute(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getZangCi(){
        List<String> list = new ArrayList<String>();
        try{

            Statement st = getSt();
            Date date = new Date();
            String sql ="select distinct word from zangci ";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add( rs.getString("word"));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveOrUpdateCustomerSocre(String mobile,String sign,String customerIds) {

        try {
            Statement st = getSt();
            ResultSet rs = st.executeQuery("select id , customer_ids from  customer_score where mobile='"+mobile+"' and sign='"+sign+"' ");
            String existMobile = "";
            Integer id = null;
            String existCustomerIds = "";
            while (rs.next()) {
                id = rs.getInt("id");
                existCustomerIds = rs.getString("customer_ids");
            }
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(id != null) {
                Map<String,Integer> customerMap = new HashMap<String, Integer>();
                for(String c : customerIds.split(",")) {
                    customerMap.put(c,1);
                }
                for(String c : existCustomerIds.split(",")) {
                    customerMap.put(c,1);
                }
                String nowCustomerIds = "";
                int score = 0;
                for(String key : customerMap.keySet()) {
                    nowCustomerIds = nowCustomerIds+key+",";
                    score++;
                }
                nowCustomerIds = nowCustomerIds.substring(0,nowCustomerIds.length()-1);
                String updateSql = "update customer_score set update_time='"+format.format(date)+"',customer_ids='"+nowCustomerIds+"',score="+score+" where id= "+id;
               // System.out.println(updateSql);
                st.execute(updateSql);
            } else {
                int score = customerIds.split(",").length;
                String inssertSql = "insert into customer_score values (null,'"+mobile+"','"+sign+"','"+format.format(date)+"','"+format.format(date)+"','"+customerIds+"',"+score+") ";
                st.execute(inssertSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public static void saveOrUpdateBatchCustomerSocre(ArrayList<String> list,String timeStr) {
        try {
            String yearMonth = timeStr.substring(0,timeStr.lastIndexOf("-")).replace("-","_");
            Map<String,Integer> mobileMap = new HashMap<String, Integer>();
            Map<String,Integer> signMap = new HashMap<String, Integer>();
            Map<String,String> listMap = new HashMap<String, String>();
            for(String line : list) {
                String ss [] = line.split("@");
                mobileMap.put(ss[0],1);
                signMap.put(ss[1],1);
                listMap.put(ss[0]+"@"+ss[1],ss[2]);
            }
            String mobiles = "";
            for(String m : mobileMap.keySet()) {
                mobiles = mobiles +"'"+ m+"',";
            }
            mobiles = mobiles.substring(0,mobiles.length()-1);
            String signs = "";
            for(String s : signMap.keySet()) {
                signs = signs +"'"+s+"',";
            }
            signs = signs.substring(0,signs.length()-1);
            String sql ="select id,mobile,sign,customer_ids from customer_score_"+yearMonth+" where mobile in ("+mobiles+") and sign in ("+signs+") ";
            System.out.println(sql);
            Statement st = getSt();
            ResultSet rs = st.executeQuery(sql);

            Map<String,String> exitsMap = new HashMap<String, String>();
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String mobile = rs.getString("mobile");
                String sign = rs.getString("sign");
                String customer_ids = rs.getString("customer_ids");
                exitsMap.put(mobile+"@"+sign,id+"@"+customer_ids);
            }

            List<String> sqlList = new ArrayList<String>();
            for(String ms : listMap.keySet()) {
                if(exitsMap.get(ms) != null) {
                    String idCus[] = exitsMap.get(ms).split("@");
                    Integer id = Integer.parseInt(idCus[0]);
                    String existCustomerIds = idCus[1];
                    String currentCustomerIds[] = listMap.get(ms).split(",");
                    String needAdd = "";
                    for(String curr : currentCustomerIds) {
                        if(!existCustomerIds.contains(curr)) {
                            needAdd = needAdd+","+curr;
                        }
                    }
                    if(!StringUtils.isEmpty(needAdd)) {
                        String nowCustomerIds = existCustomerIds+needAdd;
                        int score = nowCustomerIds.split(",").length;
                        String updateSql =  "update customer_score_"+yearMonth+" set update_time='"+format.format(date)+"',customer_ids='"+nowCustomerIds+"',score="+score+" where id= "+id;
                        sqlList.add(updateSql);
                    }

                } else {
                    String ss [] = ms.split("@");
                    String customerIds = listMap.get(ms);
                    int score = customerIds.split(",").length;
                    String insertSql = "insert into customer_score_"+yearMonth+" values (null,'"+ss[0]+"','"+ss[1]+"','"+format.format(date)+"','"+format.format(date)+"','"+customerIds+"',"+score+") ";
                    sqlList.add(insertSql);
                }
            }

            //
            for(String s : sqlList) {
                st.addBatch(s);
            }
            st.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
