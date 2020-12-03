package com.chuangrui.utils;

import com.chuangrui.node.GetMysqlByNode1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DeadNumberUtils {
    public static Map<String,Integer> getDeadCode() {
        Map<String,Integer> map = new HashMap<String, Integer>();
        try {
            Connection conn = GetMysqlByNode1.getConn("0");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT code FROM `crdb`.`tbl_common_code` where `reason` like '%空号%' ");
            while (rs.next()) {
                String code = rs.getString("code");
                map.put(code,1);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
