package com.chuangrui.test;


import com.amazonaws.util.StringUtils;
import com.chuangrui.utils.CodeUtils;
import com.chuangrui.utils.ProvinceCity;
import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PaoDingCut {

    public static boolean check(String word) {
        boolean flag= true;
        if(word.length() == 1) {
            return false;
        }
        String useWords[] = {"http"};
        for(String w: useWords) {
            if(word.equals(w)) {
                return true;
            }
        }

        //部分词停用
        String stopWords[] = {"退订","一些","一个","一份","一天","一年","一次","一点","举升机","决不允许","之兵","库容","五龙","自闭症",
                "互助","电线管","屯昌","本溪","滦平","仙湖","青贮","三三四","油菜","涿州市","黔南","黔南州","土默特左旗","斯巴达","松江",
                "齐白石","獭祭","敦睦","木樨","老山","八角街","用兵","中菲","沐阳","苗寨","木樨园","卡迪亚","两湖","慈溪",
                "您好","攀枝","江北","唐镇","人民日","内障","佳城",
                "腰间盘"};
        for(String w : stopWords) {
            if(word.equals(w)) {
                return false;
            }
        }
        //城市地名去掉
        for(String city : ProvinceCity.city) {
            if(word.equals(city)) {
                return false;
            }
        }

        //如果是全英文，过滤掉
        boolean english = true;
        for(int i=0;i<word.length();i++) {
            if((word.charAt(i) >='a' && word.charAt(i)<='z') || (word.charAt(i) >='A' && word.charAt(i)<='Z')) {

            } else {
                english = false;
            }
        }
        if(english) {
            return false;
        }

        char fuhao[] = {'!','@','#','$','%','&','*','-','_','+','=','?','.','`','ン','呜','噢','哈','リ','기','0','1','2','3','4','5','6','7','8','9',
               'の','시','다','お','哦','啊','니','하','は','ま','ら','で','ご','ち','丨','に','미','세','り','적','唰','나','華','개','い','が','く',
               'ぐ','こ','さ','し','す','た','だ','て','と','な','び','る','を','イ','キ','ゲ','シ','タ','ッ','ト','ペ','ポ','ャ','ー','신','입','최','￥',
                '￣','ス','丶'};

       for(int i=0;i<word.length();i++) {
           for(int j=0;j<fuhao.length;j++) {
               if(word.charAt(i) == fuhao[j]) {
                   flag = false;
                   return false;
               }
           }

       }
       return  flag;
    }

    public static ArrayList<String> cutString(String text){

        // 定义返回结果列表
        ArrayList<String> tokenList = new ArrayList<String>();
        tokenList = analysisByIK3Point2(text);
        if(text != null && text.indexOf("【")>=0 && text.indexOf("】")>=0 ) {

                tokenList.add(text.substring(text.indexOf("【"),text.indexOf("】")+1).replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "").replaceAll("'",""));

        }
        return tokenList;
    }

    public static ArrayList<String> getDistictString(String text) {
        // 定义返回结果列表
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        ArrayList<String> list = analysisByIK3Point2(text);
        for(String word: list ) {
            map.put(word,1);
        }
        ArrayList<String> tokenList = new ArrayList<String>();
        for(String key : map.keySet()) {
            tokenList.add(key);
        }
        if(text != null && text.indexOf("【")>=0 && text.indexOf("】")>=0 ) {
            tokenList.add(text.substring(text.indexOf("【"),text.indexOf("】")+1).replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "").replaceAll("'",""));
        }
        return tokenList;
    }

    public static ArrayList<String> analysisByIK3Point2(  String content) {
        if(StringUtils.isNullOrEmpty(content)){
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        IKAnalyzer ss = new IKAnalyzer();
        StringReader reader = new StringReader(content);
        try {
            TokenStream tokenStream = ss.tokenStream("", reader);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                CharTermAttribute termAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                if(check(termAttribute.toString())){
                    list.add(termAttribute.toString());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String args[]) {
//        String text ="【旭辉吴门府】环线开通，板块融合，大苏州时代来临！最后一次享受大涨红利的机会，投资紧跟旭辉，即买即涨，抢占吴江价格洼地，太湖新城约115-175?铂悦精装臻品，本周开盘！68085888退订回T回T退订";
//        String text = "http://t.cn/EyMC0rH";
//        System.out.println(cutString(text));

        String str = "【柳州车展】恭喜您成功报名领取05月01日-05月02日2019桂中夏季车展暨柳州第二届电商购车节门票，客服将24小时内联系您送票事宜，";
       System.out.println( cutString(str));
        System.out.println( getDistictString(str));



//        List<String> ikList = analysisByIK3Point2(  str);
//        System.out.println(ikList);

//        String x = "a";
//        System.out.println(check(x));
    }
}

