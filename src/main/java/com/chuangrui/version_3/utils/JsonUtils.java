package com.chuangrui.version_3.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;


public class JsonUtils {

    public static String setSign(JSONObject json, HashMap<String,String> signMap) {
        if(StringUtils.isEmpty(json.getString("sign"))) {

            String sign = signMap.get(json.getString("batchId"));
//            System.out.println("sign:"+sign+" batchId:"+json.getString("batchId"));
            json.put("sign",sign);
        }
//        System.out.println("final sign:"+json.get("sign"));
        return json.toJSONString();
    }
}
