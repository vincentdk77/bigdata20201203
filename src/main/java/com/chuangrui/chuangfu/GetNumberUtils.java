package com.chuangrui.chuangfu;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

public class GetNumberUtils {

    public static void main(String args[]) {
        String line = "【瞬瞬】尊敬的张力文，瞬瞬已超期多天，请尽快还款，如已还款请忽略。回T退订";
        String line2 = "【小米钱包】尊敬的客户，您的合同已超期54天，应还3930.00元,小米钱包提醒您请及时通过客户端自助充值，避免超期产生更多费用，若已还款请忽略。回T退订";
        String line3 = "【至尊借款】尊敬的何虎迪，至尊借款已超期三天，请于中午十点前还款，回T退订";
        System.out.println(getDate(line3));
//        System.out.println(getMoney(line2));
    }

    public static String getDate(String line) {
        String date = null;
        if(StringUtils.isEmpty(line)) {
            return date;
        }
        if(line.contains("天")){
            int index = line.indexOf("天");
            String dateNumber = "";
            index--;
            while(line.charAt(index) >='0' && line.charAt(index) <='9') {
                dateNumber=line.charAt(index)+dateNumber;
                index --;
            }
            if(StringUtils.isEmpty(dateNumber)) {
                if(line.contains("多天")) {
                    date = "多天";
                } else if(line.contains("三天")){
                    date = "3";
                } else if(line.contains("明天")) {
                    date = "-1";
                }

            } else {
                date = dateNumber;
            }
        }

        return date;
    }

    public static String getMoney(String line) {
        String money = null;
        if(StringUtils.isEmpty(line)) {
            return money;
        }
        if(line.contains("元")){
            int index = line.indexOf("元");
            String moneyNumber = "";
            index--;
            while((line.charAt(index) >='0' && line.charAt(index) <='9') || line.charAt(index) == '.') {
                moneyNumber=line.charAt(index)+moneyNumber;
                index --;
            }
            money = moneyNumber;
        }


        return money;
    }

    /***
     *
     * @param json
     * @param line   dateTime+"@"+money+"@"+ctime+"@"+signFlag
     */
    public static void handle(JSONObject json ,String line) {
        String ss[] = line.split("@");

        json.put("last_collection_time", ss[2]);
        if(json.getString("max_money") != null) {
            if(Double.parseDouble(json.getString("max_money")) < Double.parseDouble(ss[1])) {
                json.put("max_money", ss[1]);
            }
        } else {
            json.put("max_money", ss[1]);
        }
        if(json.getString("max_collection_time") != null) {
            Double currentTime = null;
            Double existTime = null;
            try {
                currentTime = Double.parseDouble(ss[0]);
            } catch (Exception e) {
                json.put("max_collection_time", ss[0]);
            }
            try {
                existTime = Double.parseDouble(json.getString("max_collection_time"));
            } catch (Exception e) {

            }
            if(currentTime != null && existTime != null && currentTime>existTime) {
                json.put("max_collection_time", ss[0]);
            }
        } else {
            json.put("max_collection_time", ss[0]);
        }

        //最近一一个月催收次数
        if(json.getJSONObject("month_count") ==null) {
            JSONObject weekJson = new JSONObject();
            weekJson.put(ss[2],1);
            json.put("month_count",weekJson);
        } else {
            JSONObject existWeekJson = json.getJSONObject("month_count");
            if(existWeekJson.getInteger(ss[2]) == null) {
                existWeekJson.put(ss[2],1);
            } else {
                existWeekJson.put(ss[2],1+existWeekJson.getInteger(ss[2]));
            }
            json.put("month_count",existWeekJson);
        }

        if(json.getJSONObject("money_count") ==null) {
            JSONObject moneyJson = new JSONObject();
            moneyJson.put(ss[2],Double.parseDouble(ss[1]));
            json.put("money_count",moneyJson);
        } else {
            JSONObject existMoneyJson = json.getJSONObject("money_count");
            if(existMoneyJson.getDouble(ss[2]) == null) {
                existMoneyJson.put(ss[2],Double.parseDouble(ss[1]));
            } else {
                existMoneyJson.put(ss[2],Double.parseDouble(ss[1])+existMoneyJson.getDouble(ss[2]));
            }
            json.put("money_count",existMoneyJson);
        }

        if(json.getJSONObject("number_count") ==null) {
            JSONObject numberJson = new JSONObject();
            numberJson.put(ss[2],ss[3]);
            json.put(ss[2],numberJson);
        } else {
            JSONObject existNumberJson = json.getJSONObject("number_count");
            if(existNumberJson.getDouble(ss[2]) == null) {
                existNumberJson.put(ss[2],ss[3]);
            } else {
                if(!existNumberJson.getString(ss[2]).contains(ss[3])) {
                    existNumberJson.put(ss[2],existNumberJson.getString(ss[2])+","+ss[3]);
                }
            }
            json.put("number_count",existNumberJson);
        }

    }
}
