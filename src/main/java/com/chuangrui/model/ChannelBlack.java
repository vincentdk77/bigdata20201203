package com.chuangrui.model;

import java.util.Arrays;
import java.util.Date;

public class ChannelBlack{
    private String mobile;
    private Integer level;
    private String[] companyName;
    private String lastTime;

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String[] getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String[] companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "ChannelBlack{" +
                "mobile='" + mobile + '\'' +
                ", level=" + level +
                ", companyName=" + Arrays.toString(companyName) +
                ", lastTime=" + lastTime +
                '}';
    }
}
