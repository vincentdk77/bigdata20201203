package com.chuangrui.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentTagMgr {
    private static ContentTagMgr instance = new ContentTagMgr();
    public static ContentTagMgr getInstance() {
        return instance;
    }

    static final String domainSuffix = ".xin;.com;.cn;.net;.com;.cn;.vip;.top;.cc;.shop;.club;.wang;.xyz;.luxe;.site;.news;.pub;.fun;.online;.win;.red;.loan;.ren;.mom;.net;.cn;.org;.link;.biz;.bid;.help;.tech;.date;.mobi;.so;.me;.tv;.co;.vc;.pw;.video;.party;.pics;.website;.store;.ltd;.ink;.trade;.live;.wiki;.name;.space;.gift;.lol;.work;.band;.info;.click;.photo;.market;.tel;.social;.press;.game;.kim;.org;.cn;.games;.pro;.men;.love;.studio;.rocks;.asia;.group;.science;.design;.software;.engineer;.lawyer;.beer;.im;.gg";
    static final String[] domainSuffixArr = domainSuffix.split(";");

    static final String bankListSuffix = "包商银行,东莞银行,工商银行,光大银行,广发银行,广州银行,湖北银行,花旗银行,华夏银行,建设银行,交通银行,民生银行,南京银行,南粤银行,宁波银行,农商银行,农业银行,平安银行,浦发银行,上海银行,温州银行,新疆银行,兴业银行,邮储银行,渣打银行,招商银行,浙商银行,中国银行,中信银行,中兴银行,中原银行,重庆银行";
    static final String[] bankListSuffixArr = bankListSuffix.split(",");

    static List<ContentTag>  contentTagList = new ArrayList<ContentTag>();

    public void reload(){
        ContentTag contentTag1 = new ContentTag();
        contentTag1.setOrder(1);
        contentTag1.setName("金融四类-信用卡");
        contentTag1.setTag("信用卡");
        contentTag1.setContainUrl(1);
        contentTag1.setSignContain("银行,信用卡");
        contentTag1.setContentContain1("信用卡,元信用,金卡,卡#额度,额度#领取,额度#取现,信用#额度,额度#办理,卡#领取,额度#申领,额度#下卡,额度#元,额度#万,额度#000,额度#获取");
        contentTag1.setNotContain("贷,抵押,理财,培训,课程,教育,辅导,授课,成绩,名师,上课,老师,托管,存管,回报");
        contentTagList.add(contentTag1);

        ContentTag contentTag2 = new ContentTag();
        contentTag2.setOrder(2);
        contentTag2.setName("金融四类-网贷");
        contentTag2.setTag("网贷");
        contentTag2.setContainUrl(1);
        contentTag2.setContentContain1("借款,借钱,取钱,放款,下款,金融,贷,额度,額度,现金,借款,授信,到账,提现,取现,分期,领钱,取款,周转金,万#申请,拿#钱,领#钱,元#申请,000#申请,万#查看,元#查看,000#查看,万#领取,元#领取,000#领取,分#期,000#借,万#借,额#元,信用#元,白条,提#钱");
        contentTag2.setNotContain("房,平米,按揭,理财,培训,课程,教育,辅导,授课,成绩,名师,上课,老师,托管,存管,回报");
        contentTagList.add(contentTag2);

        ContentTag contentTag20 = new ContentTag();
        contentTag20.setOrder(3);
        contentTag20.setName("金融四类-网贷");
        contentTag20.setTag("个贷");
        contentTag20.setSignContain("中国平安,平安银行");
        contentTag20.setContainUrl(2);
        contentTag20.setContentContain1("借款,借钱,取钱,放款,下款,资金,金融,贷,额度,现金,借款,授信,到账,提现,取现,分期,领钱,取款");
        contentTag20.setNotContain("房,平米,按揭,,理财,培训,课程,教育,辅导,授课,成绩,名师,上课,老师,托管,存管,回报");
        contentTagList.add(contentTag20);

        ContentTag contentTag3 = new ContentTag();
        contentTag3.setOrder(5);
        contentTag3.setName("金融四类-理财");
        contentTag3.setTag("理财");
        contentTag3.setContentContain1("收益,货币,债券,信托,保监会,加息,年化,投资,理财,定期,分红,银行托管,银行存管,回报");
        contentTag3.setNotContain("精装,平米,按揭,平方,㎡,房,学区,公寓,精装,面积,产权,按揭,样板,逾期,信用,出借方,微信");
        contentTagList.add(contentTag3);

        ContentTag contentTag5 = new ContentTag();
        contentTag5.setOrder(6);
        contentTag5.setName("非金融四类-地产");
        contentTag5.setTag("地产");
        contentTag5.setContentContain1("商铺,租金,房,学区,公寓,精装,面积,产权,按揭,样板");
        contentTag5.setContentContain2("平米,平方,㎡");
        contentTag5.setNotContain("包工,包料,全包,人工,辅料,家装,装饰");
        contentTagList.add(contentTag5);

        ContentTag contentTag4 = new ContentTag();
        contentTag4.setOrder(7);
        contentTag4.setName("非金融四类-家居,装饰,装修,会展,建材");
        contentTag4.setTag("家装");
        contentTag4.setContentContain1("家私,木门,包工,包料,全包,家居,装饰,装修,装潢,会展,建材,灯,窗帘,定制,橱柜,卫浴,床,床垫,马桶,茶几,沙发,建材,电视柜,全屋定制,家具,装饰,装修,主材,辅材,家电,冰箱,瓷砖,全友家居,居然之家,红星美凯龙,电视,家装");
        contentTag4.setNotContain("贷,现金");
        contentTagList.add(contentTag4);

        ContentTag contentTag71 = new ContentTag();
        contentTag71.setOrder(8);
        contentTag71.setName("非金融四类-整形美容");
        contentTag71.setTag("整形美容");
        contentTag71.setContainUrl(2);
        contentTag71.setContentContain1("美容,整形,玻尿酸");
        contentTagList.add(contentTag71);

        ContentTag contentTag6 = new ContentTag();
        contentTag6.setOrder(9);
        contentTag6.setName("非金融四类-移动卖场");
        contentTag6.setTag("移动卖场");
        contentTag6.setSignContain("4G,营业厅,移动,联通,电信,通讯");
        contentTag6.setContentContain1("4G,营业厅,话费,号码,移动,联通,电信,手机");
        contentTag6.setNotContain("贷,借,款");
        contentTagList.add(contentTag6);

        ContentTag contentTag61 = new ContentTag();
        contentTag61.setOrder(10);
        contentTag61.setName("非金融四类-教育");
        contentTag61.setTag("教育");
        contentTag61.setContentContain1("书院,幼儿园,小学,中学,初中,高中,学英语,中考,高考,年级,学习,学校,校园,学院,培训,课程,教育,辅导,授课,成绩,名师,上课,老师,驾校");
        contentTagList.add(contentTag61);

        ContentTag contentTag7 = new ContentTag();
        contentTag7.setOrder(11);
        contentTag7.setName("金融行业");
        contentTag7.setTag("金融行业");
        contentTag7.setContainUrl(2);
        contentTag7.setContentContain1("还款,扣款,逾期,违约,欠款");
        contentTag7.setNotContain("房,学区,公寓,精装,面积,产权,按揭,样板,平米,平方,㎡");
        contentTagList.add(contentTag7);

        ContentTag contentTag8 = new ContentTag();
        contentTag8.setOrder(12);
        contentTag8.setName("商超");
        contentTag8.setTag("商超");
        contentTag8.setContainUrl(2);
        contentTag8.setContentContain1("专卖店,车展,电器,超市");
        contentTagList.add(contentTag8);

        contentTagList.sort(new Comparator<ContentTag>() {

            public int compare(ContentTag o1, ContentTag o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        System.out.println(contentTagList.toString());
    }

    public String getTag(String sign,String content){
        content = replaceBlank(content);
        String newContent = replaceBlank(sign) + content;

        StringBuffer stringBuffer = new StringBuffer();
        for(ContentTag contentTag : contentTagList){
            String signContainArr[] = contentTag.getSignContainArr();
            //签名包含
            if(signContainArr!=null && signContainArr.length > 0){
                boolean isOk = false;
                for(String s : signContainArr){
                    if(s.indexOf("#") <= 0){
                        if(sign.contains(s)){
                            isOk = true;
                            break;
                        }
                    }else{
                        boolean okFlag = true;
                        String[] arr = s.split("#");
                        for(String a : arr){
                            if(!sign.contains(a)){
                                okFlag = false;
                                break;
                            }
                        }
                        if(okFlag){
                            isOk = true;
                            break;
                        }
                    }
                }
                if(!isOk){
                    continue;
                }
            }
            //内容包含
            String contentContain1Arr[] = contentTag.getContentContain1Arr();
            if(contentContain1Arr!=null && contentContain1Arr.length > 0){
                boolean isOk = false;
                ok:for(String s : contentContain1Arr){
                    if(s.indexOf("#") <= 0){
                        if(newContent.contains(s)){
                            isOk = true;
                            break;
                        }
                    }else{
                        boolean okFlag = true;
                        String[] arr = s.split("#");
                        for(String a : arr){
                            if(!newContent.contains(a)){
                                okFlag = false;
                                break;
                            }
                        }
                        if(okFlag){
                            isOk = true;
                            break;
                        }
                    }
                }
                if(!isOk){
                    continue;
                }
            }
            //内容包含
            String contentContain2Arr[] = contentTag.getContentContain2Arr();
            if(contentContain2Arr!=null && contentContain2Arr.length > 0){
                boolean isOk = false;
                ok:for(String s : contentContain2Arr){
                    if(s.indexOf("#") <= 0){
                        if(newContent.contains(s)){
                            isOk = true;
                            break;
                        }
                    }else{
                        boolean okFlag = true;
                        String[] arr = s.split("#");
                        for(String a : arr){
                            if(!newContent.contains(a)){
                                okFlag = false;
                                break;
                            }
                        }
                        if(okFlag){
                            isOk = true;
                            break;
                        }
                    }
                }
                if(!isOk){
                    continue;
                }
            }
            //不包含
            String notContain2Arr[] = contentTag.getNotContainArr();
            if(notContain2Arr!=null && notContain2Arr.length > 0){
                boolean isOk = true;
                for(String s : notContain2Arr){
                    if(newContent.contains(s)){
                        isOk = false;
                        break;
                    }
                }
                if(!isOk){
                    continue;
                }
            }
            if(contentTag.getContainUrl() == 1 && !containUrl(content)){
                continue;
            }
            if(contentTag.getContainUrl() == 2 && containUrl(content)){
                continue;
            }
            if(stringBuffer.length() > 0){
                stringBuffer.append("^");
            }
            boolean isOk = true;
            if(contentTag.getTag().equals("信用卡") && newContent.contains("银行")){
                isOk = false;
                for(String bank : bankListSuffixArr){
                    if(newContent.contains(bank)){
                        isOk = true;
                        break;
                    }
                }
            }
            if(isOk){
                stringBuffer.append(contentTag.getTag());
            }
        }
        return stringBuffer.toString();
    }

    public static boolean containUrl(String content){
        for (String ds : domainSuffixArr){
            if(content.contains(ds) && !content.contains("@")){
                return true;
            }
        }
        return false;
    }

    public String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static void main(String[] args) {
        ContentTagMgr.getInstance().reload();
        String sign = "";
        String content = "新年之际，众发驾校全包班4300！！冬天报名，春天拿证！联系方式:15960281750（微信同号）地址:华天学院斜对面回T退订";
        String tag = ContentTagMgr.getInstance().getTag(sign,content);
        System.out.println("" + tag + "," + sign + content + "");
    }
}
