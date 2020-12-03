package com.chuangrui.version_3.utils;

import org.apache.hadoop.hbase.TableName;

import java.util.HashMap;
import java.util.Map;

public class HbaseTable {

    //二级通知表
    public static TableName TZ_2_CYTZ = TableName.valueOf("cr:s3_2_cytz");
    public static TableName TZ_2_DSTZ = TableName.valueOf("cr:s3_2_dstz");
    public static TableName TZ_2_FDCTZ = TableName.valueOf("cr:s3_2_fdctz");
    public static TableName TZ_2_FWYTZ = TableName.valueOf("cr:s3_2_fwytz");
    public static TableName TZ_2_JKTZ = TableName.valueOf("cr:s3_2_jktz");
    public static TableName TZ_2_JRTZ = TableName.valueOf("cr:s3_2_jrtz");
    public static TableName TZ_2_QTTZ = TableName.valueOf("cr:s3_2_qttz");
    public static TableName TZ_2_YLTZ = TableName.valueOf("cr:s3_2_yltz");
    public static TableName TZ_2_YXTZ = TableName.valueOf("cr:s3_2_yxtz");
    public static TableName TZ_2_YSTZ = TableName.valueOf("cr:s3_2_ystz");
    public static TableName TZ_2_JYTZ = TableName.valueOf("cr:s3_2_jytz");
    public static TableName TZ_2_QCTZ = TableName.valueOf("cr:s3_2_qctz");

    //三级电商通知表
    public static TableName TZ_3_JYDQTZ = TableName.valueOf("cr:s3_3_jydqtz");
    public static TableName TZ_3_SJSMTZ = TableName.valueOf("cr:s3_3_sjsmtz");
    public static TableName TZ_3_JJYPTZ = TableName.valueOf("cr:s3_3_jjyptz");
    public static TableName TZ_3_FSYMTZ = TableName.valueOf("cr:s3_3_fsymtz");
    public static TableName TZ_3_MZGHTZ = TableName.valueOf("cr:s3_3_mzghtz");
    public static TableName TZ_3_MYWJTZ = TableName.valueOf("cr:s3_3_mywjtz");
    public static TableName TZ_3_SPSXTZ = TableName.valueOf("cr:s3_3_spsxtz");
    public static TableName TZ_3_JSYLTZ = TableName.valueOf("cr:s3_3_jsyltz");
    public static TableName TZ_3_YYBJTZ = TableName.valueOf("cr:s3_3_yybjtz");
    public static TableName TZ_3_TSWYTZ = TableName.valueOf("cr:s3_3_tswytz");
    public static TableName TZ_3_JDLYTZ = TableName.valueOf("cr:s3_3_jdlytz");
    public static TableName TZ_3_AZWHTZ = TableName.valueOf("cr:s3_3_azwhtz");
    public static TableName TZ_3_QTDSTZ = TableName.valueOf("cr:s3_3_qtdstz");

    //三级餐饮通知
    public static TableName TZ_3_FDTZ = TableName.valueOf("cr:s3_3_fdtz");
    public static TableName TZ_3_WMTZ = TableName.valueOf("cr:s3_3_wmtz");
    public static TableName TZ_3_JBTZ = TableName.valueOf("cr:s3_3_jbtz");

    //三级健康通知
    public static TableName TZ_3_JSTZ = TableName.valueOf("cr:s3_3_jstz");
    public static TableName TZ_3_YLYSTZ = TableName.valueOf("cr:s3_3_ylystz");

    //三级理财通知
    public static TableName TZ_3_GZFFTZ = TableName.valueOf("cr:s3_3_gzfftz");
    public static TableName TZ_3_LCTZ = TableName.valueOf("cr:s3_3_lctz");
    public static TableName TZ_3_XYKTZ = TableName.valueOf("cr:s3_3_xyktz");
    public static TableName TZ_3_YHTZ = TableName.valueOf("cr:s3_3_yhtz");
    public static TableName TZ_3_BXTZ = TableName.valueOf("cr:s3_3_bxtz");
    public static TableName TZ_3_DKTZ = TableName.valueOf("cr:s3_3_dktz");

    //4级贷款通知
    public static TableName TZ_4_WDTZ = TableName.valueOf("cr:s3_4_wdtz");
    public static TableName TZ_4_PAGDTZ = TableName.valueOf("cr:s3_4_pagdtz");
    public static TableName TZ_4_YHDKTZ = TableName.valueOf("cr:s3_4_yhdktz");

    //5级平安个贷通知
    public static TableName TZ_5_PAGDCSTZ = TableName.valueOf("cr:s3_5_pagdcstz");
    public static TableName TZ_5_PAGDFKTZ = TableName.valueOf("cr:s3_5_pagdfktz");
    public static TableName TZ_5_PAGDHKTZ = TableName.valueOf("cr:s3_5_pagdhktz");
    public static TableName TZ_5_PAGDHKTXTZ = TableName.valueOf("cr:s3_5_pagdhktxtz");
    public static TableName TZ_5_PAGDSQTZ = TableName.valueOf("cr:s3_5_pagdsqtz");

    //5级银行贷款通知
    public static TableName TZ_5_YHDKCSTZ = TableName.valueOf("cr:s3_5_yhdkcstz");
    public static TableName TZ_5_YHDKFKTZ = TableName.valueOf("cr:s3_5_yhdkfktz");
    public static TableName TZ_5_YHDKHKTZ = TableName.valueOf("cr:s3_5_yhdkhktz");
    public static TableName TZ_5_YHDKHKTXTZ = TableName.valueOf("cr:s3_5_yhdkhktxtz");
    public static TableName TZ_5_YHDKSQTZ = TableName.valueOf("cr:s3_5_yhdksqtz");

    //5级网贷通知
    public static TableName TZ_5_WDCSTZ = TableName.valueOf("cr:s3_5_wdcstz");
    public static TableName TZ_5_WDFKTZ = TableName.valueOf("cr:s3_5_wdfktz");
    public static TableName TZ_5_WDHKTZ = TableName.valueOf("cr:s3_5_wdhktz");
    public static TableName TZ_5_WDHKTXTZ = TableName.valueOf("cr:s3_5_wdhktxtz");
    public static TableName TZ_5_WDSQTZ = TableName.valueOf("cr:s3_5_wdsqtz");


    //二级验证码表
    public static TableName YZM_2_CYYZM = TableName.valueOf("cr:s3_2_cyyzm");
    public static TableName YZM_2_DSYZM = TableName.valueOf("cr:s3_2_dsyzm");
    public static TableName YZM_2_FDCYZM = TableName.valueOf("cr:s3_2_fdcyzm");
    public static TableName YZM_2_FWYYZM = TableName.valueOf("cr:s3_2_fwyyzm");
    public static TableName YZM_2_JKYZM = TableName.valueOf("cr:s3_2_jkyzm");
    public static TableName YZM_2_JRYZM = TableName.valueOf("cr:s3_2_jryzm");
    public static TableName YZM_2_QTYZM = TableName.valueOf("cr:s3_2_qtyzm");
    public static TableName YZM_2_YLYZM = TableName.valueOf("cr:s3_2_ylyzm");
    public static TableName YZM_2_YXYZM = TableName.valueOf("cr:s3_2_yxyzm");
    public static TableName YZM_2_YSYZM = TableName.valueOf("cr:s3_2_ysyzm");
    public static TableName YZM_2_JYYZM = TableName.valueOf("cr:s3_2_jyyzm");
    public static TableName YZM_2_QCYZM = TableName.valueOf("cr:s3_2_qcyzm");

    //三级电商验证码表
    public static TableName YZM_3_JYDQYZM = TableName.valueOf("cr:s3_3_jydqyzm");
    public static TableName YZM_3_SJSMYZM = TableName.valueOf("cr:s3_3_sjsmyzm");
    public static TableName YZM_3_JJYPYZM = TableName.valueOf("cr:s3_3_jjypyzm");
    public static TableName YZM_3_FSYMYZM = TableName.valueOf("cr:s3_3_fsymyzm");
    public static TableName YZM_3_MZGHYZM = TableName.valueOf("cr:s3_3_mzghyzm");
    public static TableName YZM_3_MYWJYZM = TableName.valueOf("cr:s3_3_mywjyzm");
    public static TableName YZM_3_SPSXYZM = TableName.valueOf("cr:s3_3_spsxyzm");
    public static TableName YZM_3_JSYLYZM = TableName.valueOf("cr:s3_3_jsylyzm");
    public static TableName YZM_3_YYBJYZM = TableName.valueOf("cr:s3_3_yybjyzm");
    public static TableName YZM_3_TSWYYZM = TableName.valueOf("cr:s3_3_tswyyzm");
    public static TableName YZM_3_JDLYYZM = TableName.valueOf("cr:s3_3_jdlyyzm");
    public static TableName YZM_3_AZWHYZM = TableName.valueOf("cr:s3_3_azwhyzm");
    public static TableName YZM_3_QTDSYZM = TableName.valueOf("cr:s3_3_qtdsyzm");
    public static TableName YZM_3_DNBGYZM = TableName.valueOf("cr:s3_3_dnbgyzm");

    //三级餐饮验证码
    public static TableName YZM_3_FDYZM = TableName.valueOf("cr:s3_3_fdyzm");
    public static TableName YZM_3_WMYZM = TableName.valueOf("cr:s3_3_wmyzm");


    //三级健康验证码
    public static TableName YZM_3_JSYZM = TableName.valueOf("cr:s3_3_jsyzm");
    public static TableName YZM_3_YLYSYZM = TableName.valueOf("cr:s3_3_ylysyzm");

    //三级理财验证码

    public static TableName YZM_3_LCYZM = TableName.valueOf("cr:s3_3_lcyzm");
    public static TableName YZM_3_XYKYZM = TableName.valueOf("cr:s3_3_xykyzm");
    public static TableName YZM_3_YHYZM = TableName.valueOf("cr:s3_3_yhyzm");
    public static TableName YZM_3_BXYZM = TableName.valueOf("cr:s3_3_bxyzm");
    public static TableName YZM_3_DKYZM = TableName.valueOf("cr:s3_3_dkyzm");

    //4级贷款验证码
    public static TableName YZM_4_WDYZM = TableName.valueOf("cr:s3_4_wdyzm");
    public static TableName YZM_4_PAGDYZM = TableName.valueOf("cr:s3_4_pagdyzm");
    public static TableName YZM_4_YHDKYZM = TableName.valueOf("cr:s3_4_yhdkyzm");

    //二级营销
    public static TableName YX_2_CY = TableName.valueOf("cr:s3_2_cy");
    public static TableName YX_2_DS = TableName.valueOf("cr:s3_2_ds");
    public static TableName YX_2_FDC = TableName.valueOf("cr:s3_2_fdc");
    public static TableName YX_2_FWY = TableName.valueOf("cr:s3_2_fwy");
    public static TableName YX_2_JK = TableName.valueOf("cr:s3_2_jk");
    public static TableName YX_2_JR = TableName.valueOf("cr:s3_2_jr");
    public static TableName YX_2_QTYX = TableName.valueOf("cr:s3_2_qtyx");
    public static TableName YX_2_YL = TableName.valueOf("cr:s3_2_yl");
    public static TableName YX_2_YX = TableName.valueOf("cr:s3_2_yx");
    public static TableName YX_2_YS = TableName.valueOf("cr:s3_2_ys");
    public static TableName YX_2_JY = TableName.valueOf("cr:s3_2_jy");
    public static TableName YX_2_QC = TableName.valueOf("cr:s3_2_qc");
    public static TableName YX_2_ZXZS = TableName.valueOf("cr:s3_2_zxzs");
    public static TableName YX_2_PTYX = TableName.valueOf("cr:s3_2_ptyx");

    //三级电商
    public static TableName YX_3_JDYX = TableName.valueOf("cr:s3_3_jdyx");
    public static TableName YX_3_SJSM = TableName.valueOf("cr:s3_3_sjsm");
    public static TableName YX_3_DNBG = TableName.valueOf("cr:s3_3_dnbg");
    public static TableName YX_3_FSYM = TableName.valueOf("cr:s3_3_fsym");
    public static TableName YX_3_MZGH = TableName.valueOf("cr:s3_3_mzgh");
    public static TableName YX_3_JSYX = TableName.valueOf("cr:s3_3_jsyx");
    public static TableName YX_3_YYBJ = TableName.valueOf("cr:s3_3_yybj");
    public static TableName YX_3_TSWY = TableName.valueOf("cr:s3_3_tswy");
    public static TableName YX_3_JIUDYX = TableName.valueOf("cr:s3_3_jiudyx");
    public static TableName YX_3_LYYX = TableName.valueOf("cr:s3_3_lyyx");
    public static TableName YX_3_LSYX = TableName.valueOf("cr:s3_3_lsyx");
    public static TableName YX_3_YJYX = TableName.valueOf("cr:s3_3_yjyx");
    public static TableName YX_3_MYYX = TableName.valueOf("cr:s3_3_myyx");
    public static TableName YX_3_JJYP = TableName.valueOf("cr:s3_3_jjyp");

    //三级餐饮
    public static TableName YX_3_FDYX = TableName.valueOf("cr:s3_3_fdyx");
    public static TableName YX_3_WMYX = TableName.valueOf("cr:s3_3_wmyx");


    //三级健康
    public static TableName YX_3_JIANSYX = TableName.valueOf("cr:s3_3_jiansyx");
    public static TableName YX_3_YLYSYX = TableName.valueOf("cr:s3_3_ylys");

    //三级理财
    public static TableName YX_3_LC = TableName.valueOf("cr:s3_3_lc");
    public static TableName YX_3_XYK = TableName.valueOf("cr:s3_3_xyk");
    public static TableName YX_3_BX = TableName.valueOf("cr:s3_3_bx");
    public static TableName YX_3_DK = TableName.valueOf("cr:s3_3_dk");

    //三级普通营销
    public static TableName YX_3_HBYX = TableName.valueOf("cr:s3_3_hbyx");
    public static TableName YX_3_HYYX = TableName.valueOf("cr:s3_3_hyyx");
    public static TableName YX_3_MC = TableName.valueOf("cr:s3_3_mc");
    public static TableName YX_3_SCBL = TableName.valueOf("cr:s3_3_scbl");
    public static TableName YX_3_ZH = TableName.valueOf("cr:s3_3_zh");

    //4级贷款
    public static TableName YX_4_WD = TableName.valueOf("cr:s3_4_wd");
    public static TableName YX_4_PAGD = TableName.valueOf("cr:s3_4_pagd");
    public static TableName YX_4_YHDK = TableName.valueOf("cr:s3_4_yhdk");



    public static Map<String,TableName> hbaseTable = new HashMap<String, TableName>();
    static {

        //二级通知
        hbaseTable.put("canyintongzhi",TZ_2_CYTZ);
        hbaseTable.put("dianshangtongzhi",TZ_2_DSTZ);
        hbaseTable.put("fangdichantongzhi",TZ_2_FDCTZ);
        hbaseTable.put("fuwuyetongzhi",TZ_2_FWYTZ);
        hbaseTable.put("jiankangtongzhi",TZ_2_JKTZ);
        hbaseTable.put("jinrongtongzhi",TZ_2_JRTZ);
        hbaseTable.put("qitatongzhi",TZ_2_QTTZ);
        hbaseTable.put("yiliaotongzhi",TZ_2_YLTZ);
        hbaseTable.put("youxitongzhi",TZ_2_YXTZ);
        hbaseTable.put("yunshutongzhi",TZ_2_YSTZ);
        hbaseTable.put("jiaoyutongzhi",TZ_2_JYTZ);
        hbaseTable.put("qichetongzhi",TZ_2_QCTZ);

        //三级电商通知
        hbaseTable.put("jiayongdianqitongzhi",TZ_3_JYDQTZ);
        hbaseTable.put("shoujishumatongzhi",TZ_3_SJSMTZ);
        hbaseTable.put("jiajuyongpintongzhi",TZ_3_JJYPTZ);
        hbaseTable.put("fushiyimaotongzhi",TZ_3_FSYMTZ);
        hbaseTable.put("meizhuanggehutongzhi",TZ_3_MZGHTZ);
        hbaseTable.put("muyinwanjutongzhi",TZ_3_MYWJTZ);
        hbaseTable.put("shipinshengxiantongzhi",TZ_3_SPSXTZ);
        hbaseTable.put("jiushuiyinliaotongzhi",TZ_3_JSYLTZ);
        hbaseTable.put("yiyaobaojiantongzhi",TZ_3_YYBJTZ);
        hbaseTable.put("tushuwenyutongzhi",TZ_3_TSWYTZ);
        hbaseTable.put("jiudianlvyoutongzhi",TZ_3_JDLYTZ);
        hbaseTable.put("anzhuangweihutongzhi",TZ_3_AZWHTZ);
        hbaseTable.put("qitadianshangtongzhi",TZ_3_QTDSTZ);

        //三级餐饮通知
        hbaseTable.put("fandiantongzhi",TZ_3_FDTZ);
        hbaseTable.put("waimaitongzhi",TZ_3_WMTZ);
        hbaseTable.put("jiubatongzhi",TZ_3_JBTZ);

        //三级健康通知
        hbaseTable.put("jianshentongzhi",TZ_3_JSTZ);
        hbaseTable.put("yanglaoyangshengtongzhi",TZ_3_YLYSTZ);

        //三级理财通知
        hbaseTable.put("gongzifafangtongzhi",TZ_3_GZFFTZ);
        hbaseTable.put("licaitongzhi",TZ_3_LCTZ);
        hbaseTable.put("xinyongkatongzhi",TZ_3_XYKTZ);
        hbaseTable.put("yinhangtongzhi",TZ_3_YHTZ);
        hbaseTable.put("baoxiantongzhi",TZ_3_BXTZ);
        hbaseTable.put("daikuantongzhi",TZ_3_DKTZ);

        //4级贷款通知
        hbaseTable.put("wangdaitongzhi",TZ_4_WDTZ);
        hbaseTable.put("pingangedaitongzhi",TZ_4_PAGDTZ);
        hbaseTable.put("yinhangdaikuantongzhi",TZ_4_YHDKTZ);

        //5级平安个贷通知
        hbaseTable.put("pingangedaicuishoutongzhi",TZ_5_PAGDCSTZ);
        hbaseTable.put("pingangedaifangkuantongzhi",TZ_5_PAGDFKTZ);
        hbaseTable.put("pingangedaihuankuantongzhi",TZ_5_PAGDHKTZ);
        hbaseTable.put("pingangedaihuankuantixingtongzhi",TZ_5_PAGDHKTXTZ);
        hbaseTable.put("pingangedaishenqingtongzhi",TZ_5_PAGDSQTZ);


        //5级银行贷款通知
        hbaseTable.put("yinhangdaikuancuishoutongzhi",TZ_5_YHDKCSTZ);
        hbaseTable.put("yinhangdaikuanfangkuantongzhi",TZ_5_YHDKFKTZ);
        hbaseTable.put("yinhangdaikuanhuankuantongzhi",TZ_5_YHDKHKTZ);
        hbaseTable.put("yinhangdaikuanhuankuantixingtongzhi",TZ_5_YHDKHKTXTZ);
        hbaseTable.put("yinhangdaikuanshenqingtongzhi",TZ_5_YHDKSQTZ);

        //5级网贷通知
        hbaseTable.put("wangdaicuishoutongzhi",TZ_5_WDCSTZ);
        hbaseTable.put("wangdaifangkuantongzhi",TZ_5_WDFKTZ);
        hbaseTable.put("wangdaihuankuantongzhi",TZ_5_WDHKTZ);
        hbaseTable.put("wangdaihuankuantixingtongzhi",TZ_5_WDHKTXTZ);
        hbaseTable.put("wangdaishenqingtongzhi",TZ_5_WDSQTZ);

        //二级验证码
        hbaseTable.put("canyinyanzhengma",YZM_2_CYYZM);
        hbaseTable.put("dianshangyanzhengma",YZM_2_DSYZM);
        hbaseTable.put("fangdichanyanzhengma",YZM_2_FDCYZM);
        hbaseTable.put("fuwuyeyanzhengma",YZM_2_FWYYZM);
        hbaseTable.put("jiankangyanzhengma",YZM_2_JKYZM);
        hbaseTable.put("jinrongyanzhengma",YZM_2_JRYZM);
        hbaseTable.put("qitayanzhengma",YZM_2_QTYZM);
        hbaseTable.put("yiliaoyanzhengma",YZM_2_YLYZM);
        hbaseTable.put("youxiyanzhengma",YZM_2_YXYZM);
        hbaseTable.put("yunshuyanzhengma",YZM_2_YSYZM);
        hbaseTable.put("jiaoyuyanzhengma",YZM_2_JYYZM);
        hbaseTable.put("qicheyanzhengma",YZM_2_QCYZM);

        //三级电商验证码
        hbaseTable.put("jiayongdianqiyanzhengma",YZM_3_JYDQYZM);
        hbaseTable.put("shoujishumayanzhengma",YZM_3_SJSMYZM);
        hbaseTable.put("jiajuyongpinyanzhengma",YZM_3_JJYPYZM);
        hbaseTable.put("fushiyimaoyanzhengma",YZM_3_FSYMYZM);
        hbaseTable.put("meizhuanggehuyanzhengma",YZM_3_MZGHYZM);
        hbaseTable.put("muyingwanjuyanzhengma",YZM_3_MYWJYZM);
        hbaseTable.put("shipinshengxianyanzhengma",YZM_3_SPSXYZM);
        hbaseTable.put("jiushuiyinliaoyanzhengma",YZM_3_JSYLYZM);
        hbaseTable.put("yiyaobaojianyanzhengma",YZM_3_YYBJYZM);
        hbaseTable.put("tushuwenyuyanzhengma",YZM_3_TSWYYZM);
        hbaseTable.put("jiudianlvyouyanzhengma",YZM_3_JDLYYZM);
        hbaseTable.put("anzhuangweihuyanzhengma",YZM_3_AZWHYZM);
        hbaseTable.put("qitadianshangyanzhengma",YZM_3_QTDSYZM);
        hbaseTable.put("diannaobangongyanzhengma",YZM_3_DNBGYZM);

        //三级餐饮验证码
        hbaseTable.put("fandianyanzhengma",YZM_3_FDYZM);
        hbaseTable.put("waimaiyanzhengma",YZM_3_WMYZM);

        //三级健康通知
        hbaseTable.put("jianshenyanzhengma",YZM_3_JSYZM);
        hbaseTable.put("yanglaoyangshengyanzhengma",YZM_3_YLYSYZM);

        //三级理财验证码
        hbaseTable.put("licaiyanzhengma",YZM_3_LCYZM);
        hbaseTable.put("xinyongkayanzhengma",YZM_3_XYKYZM);
        hbaseTable.put("yinhangyanzhengma",YZM_3_YHYZM);
        hbaseTable.put("baoxianyanzhengma",YZM_3_BXYZM);
        hbaseTable.put("daikuanyanzhengma",YZM_3_DKYZM);

        //4级贷款验证码
        hbaseTable.put("wangdaiyanzhengma",YZM_4_WDYZM);
        hbaseTable.put("pingangedaiyanzhengma",YZM_4_PAGDYZM);
        hbaseTable.put("yinhangdaikuanyanzhengma",YZM_4_YHDKYZM);

        //二级营销
        hbaseTable.put("canyin",YX_2_CY);
        hbaseTable.put("dianshang",YX_2_DS);
        hbaseTable.put("fangdichan",YX_2_FDC);
        hbaseTable.put("fuwuye",YX_2_FWY);
        hbaseTable.put("jiankang",YX_2_JK);
        hbaseTable.put("jinrong",YX_2_JR);
        hbaseTable.put("qita",YX_2_QTYX);
        hbaseTable.put("yiliao",YX_2_YL);
        hbaseTable.put("youxi",YX_2_YX);
        hbaseTable.put("yunshu",YX_2_YS);
        hbaseTable.put("jiaoyu",YX_2_JY);
        hbaseTable.put("qiche",YX_2_QC);
        hbaseTable.put("zhuangxiuzhuangshi",YX_2_ZXZS);
        hbaseTable.put("putongyingxiao",YX_2_PTYX);

        //三级电商营销
        hbaseTable.put("jiadianyingxiao",YX_3_JDYX);
        hbaseTable.put("shoujishuma",YX_3_SJSM);
        hbaseTable.put("diannaobangong",YX_3_DNBG);
        hbaseTable.put("fushiyingxiao",YX_3_FSYM);
        hbaseTable.put("meizhuanggehu",YX_3_MZGH);
        hbaseTable.put("jiushuiyingxiao",YX_3_JSYX);
        hbaseTable.put("yiyaobaojian",YX_3_YYBJ);
        hbaseTable.put("tushuwenyu",YX_3_TSWY);
        hbaseTable.put("jiudianyingxiao",YX_3_JIUDYX);
        hbaseTable.put("lvyouyingxiao",YX_3_LYYX);
        hbaseTable.put("lingshiyingxiao",YX_3_LSYX);
        hbaseTable.put("yanjingyingxiao",YX_3_YJYX);
        hbaseTable.put("muyingyingxiao",YX_3_MYYX);
        hbaseTable.put("jiajuyongpin",YX_3_JJYP);

        //三级餐饮营销
        hbaseTable.put("fandian",YX_3_FDYX);
        hbaseTable.put("waimai",YX_3_WMYX);

        //三级健康通知
        hbaseTable.put("jianshen",YX_3_JIANSYX);
        hbaseTable.put("yanglaoyangsheng",YX_3_YLYSYX);

        //三级理财营销
        hbaseTable.put("licai",YX_3_LC);
        hbaseTable.put("xinyongka",YX_3_XYK);
        hbaseTable.put("baoxian",YX_3_BX);
        hbaseTable.put("daikuan",YX_3_DK);

        //4级贷款营销
        hbaseTable.put("wangdai",YX_4_WD);
        hbaseTable.put("pingangedai",YX_4_PAGD);
        hbaseTable.put("yinhangdaikuan",YX_4_YHDK);
    }




}