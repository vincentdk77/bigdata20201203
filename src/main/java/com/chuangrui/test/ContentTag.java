package com.chuangrui.test;

import java.util.Arrays;

public class ContentTag {
    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签的属性
     */
    private String tag;

    private Integer order = 99;

    /**
     * 签名包含关键词，逗号分隔
     */
    private String[] signContainArr;

    /**
     * 内容包含关键词1
     */
    private String[] contentContain1Arr;
    /**
     * 内容包含关键词2
     */
    private String[] contentContain2Arr;
    /**
     * 内容不包含关键词
     */
    private String[] notContainArr;

    /**
     * 是否包含链接：0不限，1包含，2不包含
     */
    private Integer containUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setSignContain(String signContain) {
        if(signContain == null){
            return;
        }
        this.signContainArr = signContain.split(",");
    }

    public void setContentContain1(String contentContain1) {
        if(contentContain1 == null){
            return;
        }
        this.contentContain1Arr = contentContain1.split(",");
    }

    public void setContentContain2(String contentContain2) {
        if(contentContain2 == null){
            return;
        }
        this.contentContain2Arr = contentContain2.split(",");
    }

    public void setNotContain(String notContain) {
        if(notContain == null){
            return;
        }
        this.notContainArr = notContain.split(",");
    }

    public Integer getContainUrl() {
        return containUrl == null ? 0 : containUrl;
    }

    public void setContainUrl(Integer containUrl) {
        this.containUrl = containUrl;
    }

    public String[] getSignContainArr() {
        return signContainArr;
    }

    public String[] getContentContain1Arr() {
        return contentContain1Arr;
    }

    public String[] getContentContain2Arr() {
        return contentContain2Arr;
    }

    public String[] getNotContainArr() {
        return notContainArr;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "ContentTag{" +
                "name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", order=" + order +
                ", signContainArr=" + Arrays.toString(signContainArr) +
                ", contentContain1Arr=" + Arrays.toString(contentContain1Arr) +
                ", contentContain2Arr=" + Arrays.toString(contentContain2Arr) +
                ", notContainArr=" + Arrays.toString(notContainArr) +
                ", containUrl=" + containUrl +
                '}';
    }
}
