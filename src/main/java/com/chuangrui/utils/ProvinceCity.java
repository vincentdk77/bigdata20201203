package com.chuangrui.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class ProvinceCity {
    public static String city[] = {
            "河南省","郑州市","洛阳市","焦作市","商丘市","信阳市","周口市","鹤壁市","安阳市","濮阳市","驻马店市",
            "南阳市","开封市","漯河市","许昌市","新乡市","济源市","灵宝市","偃师市","邓州市","登封市","三门峡市","新郑市","禹州市",
            "巩义市","永城市","长葛市","义马市", "林州市项城市","汝州市","荥阳市","平顶山市","卫辉市","辉县市","舞钢市",
            "新密市","孟州市","沁阳市","郏县","安徽省","合肥市","亳州市","芜湖市","马鞍山市","池州市",
            "黄山市","滁州市","安庆市","淮南市","淮北市","蚌埠市","宿州市","宣城市","六安市","阜阳市","铜陵市","明光市",
            "天长市","宁国市","界首市","桐城市","潜山市","福建省","福州市","厦门市","泉州市","漳州市","南平市","三明市",
            "龙岩市","莆田市","宁德市建瓯市","武夷山市","长乐市","福清市","晋江市","南安市","福安市","龙海市邵武市","石狮市","福鼎市","建阳市","漳平市","永安市",
            "甘肃省","兰州市","白银市","武威市","金昌市","平凉市","张掖市","嘉峪关市","酒泉市","庆阳市定西市","陇南市",
            "天水市","玉门市","临夏市","合作市","敦煌市","甘南州","贵州省","贵阳市","安顺市","遵义市","六盘水市","兴义市",
            "都匀市","凯里市","毕节市","清镇市","铜仁市","赤水市","仁怀市福泉市","海南省","海口市","三亚市","万宁市",
            "文昌市","儋州市","琼海市","东方市","五指山市","河北省","石家庄市","保定市","唐山市","邯郸市邢台市",
            "沧州市","衡水市","廊坊市","承德市","迁安市","鹿泉市","秦皇岛市","南宫市","任丘市","叶城市","辛集市","涿州市",
            "定州市","晋州市","霸州市","黄骅市","遵化市","张家口市","沙河市","三河市","冀州市","武安市","河间市深州市","新乐市",
            "泊头市","安国市","双滦区","高碑店市","黑龙江省","哈尔滨市","伊春市","牡丹江市","大庆市","鸡西市","鹤岗市","绥化市","齐齐哈尔市",
            "黑河市","富锦市","虎林市","密山市","佳木斯市","双鸭山市","海林市","铁力市","北安市","五大连池市","阿城市",
            "尚志市","五常市","安达市","七台河市","绥芬河市","双城市","海伦市","宁安市","讷河市","穆棱市","同江市","肇东市",
            "湖北省","武汉市","荆门市","咸宁市","襄阳市","荆州市","黄石市","宜昌市","随州市","鄂州市","孝感市","黄冈市","十堰市",
            "枣阳市","老河口市","恩施市","仙桃市","天门市","钟祥市","潜江市","麻城市","洪湖市","汉川市","赤壁市","松滋市","丹江口市","武穴市",
            "广水市","石首市大冶市","枝江市","应城市","宜城市","当阳市","安陆市","宜都市","利川市","湖南省","长沙市","郴州市",
            "益阳市","娄底市","株洲市","衡阳市","湘潭市","岳阳市","常德市","邵阳市","永州市","张家界市","怀化市","浏阳市","醴陵市",
            "湘乡市","耒阳市","沅江市","涟源市","常宁市","吉首市","津市市","冷水江市","临湘市","汨罗市","武冈市","韶山市","湘西州",
            "吉林省","长春市","吉林市","通化市","白城市","四平市","辽源市","松原市","白山市","集安市","梅河口市","双辽市","延吉市",
            "九台市","桦甸市","榆树市","蛟河市","磐石市","大安市","德惠市","洮南市","龙井市","珲春市","公主岭市","图们市","舒兰市",
            "和龙市","临江市敦化市","江苏省","南京市","无锡市","常州市","扬州市","徐州市","苏州市","连云港市",
            "盐城市","淮安市","宿迁市","镇江市","南通市","泰州市","兴化市","东台市","常熟市","江阴市","张家港市","通州市","宜兴市",
            "邳州市","海门市","溧阳市","泰兴市","如皋市","昆山市","启东市","江都市","丹阳市","吴江市","靖江市","扬中市","新沂市",
            "仪征市","太仓市","姜堰市","高邮市","金坛市","句容市","灌南县","江西省","南昌市","赣州市","上饶市","宜春市",
            "景德镇市","新余市","九江市","萍乡市","抚州市","鹰潭市","吉安市","丰城市","樟树市","德兴市","瑞金市","井冈山市",
            "高安市","乐平市","南康市","贵溪市","瑞昌市","东乡县","广丰县","信州区","三清山","辽宁省","沈阳市","葫芦岛市","大连市",
            "盘锦市","鞍山市","铁岭市","本溪市","丹东市","抚顺市","锦州市","辽阳市","阜新市","调兵山市",
            "朝阳市","海城市","北票市","盖州市","凤城市","庄河市","凌源市","开原市","兴城市","新民市","大石桥市","东港市",
            "北宁市","瓦房店市","普兰店市","凌海市","灯塔市","营口市","青海省","西宁市","格尔木市","德令哈市","山东省","济南市",
            "青岛市","威海市","潍坊市","菏泽市","济宁市","东营市烟台市","淄博市","枣庄市","泰安市","临沂市","日照市","德州市","聊城市","滨州市","乐陵市",
            "兖州市","诸城市","邹城市","滕州市","肥城市","新泰市","胶州市","胶南市","即墨市","龙口市","平度市","莱西市","山西省",
            "太原市","大同市","阳泉市","长治市","临汾市","晋中市","运城市","忻州市","朔州市","吕梁市","古交市","高平市","永济市",
            "孝义市","侯马市","霍州市","介休市","河津市","汾阳市","原平市","晋城市","潞城市","陕西省","西安市",
            "咸阳市","榆林市","宝鸡市","铜川市","渭南市","汉中市","安康市","商洛市","延安市","韩城市","兴平市","华阴市","四川省","成都市","广安市","德阳市","乐山市","巴中市","内江市","宜宾市",
            "南充市","都江堰市","自贡市","泸州市","广元市达州市","资阳市","绵阳市","眉山市","遂宁市","雅安市",
            "阆中市","攀枝花市","广汉市","绵竹市","万源市","华蓥市","江油市","西昌市","彭州市","简阳市崇州市",
            "什邡市","峨眉山市","邛崃市","双流县","云南省","昆明市","玉溪市","大理市","曲靖市","昭通市","保山市",
            "丽江市","临沧市","楚雄市","开远市","个旧市","景洪市","安宁市","宣威市","浙江省","杭州市","宁波市","绍兴市",
            "温州市","台州市","湖州市","嘉兴市","金华市","舟山市","衢州市","丽水市","余姚市","乐清市","临海市","温岭市","永康市","瑞安市",
            "慈溪市","义乌市","上虞市","诸暨市","海宁市","桐乡市","兰溪市","龙泉市","建德市","富德市","富阳市",
            "平湖市","东阳市","嵊州市","奉化市","临安市","江山市","台湾省","台北市","台南市","台中市","高雄市",
            "桃源市","广东省","广州市","深圳市珠海市","汕头市","佛山市","韶关市","湛江市","肇庆市","江门市","茂名市","惠州市",
            "梅州市","汕尾市","河源市","阳江市","清远市","东莞市","中山市","潮州市","揭阳市","云浮市","自治区编辑",
            "广西壮族自治区","南宁市","贺州市","玉林市","桂林市","柳州市","梧州市","北海市","钦州市","百色市","防城港市","贵港市","河池市","崇左市","来宾市","东兴市",
            "桂平市","北流市","岑溪市","合山市","凭祥市","宜州市","内蒙古自治区","呼和浩特市","呼伦贝尔市","赤峰市",
            "扎兰屯市","鄂尔多斯市","乌兰察布市","巴彦淖尔市","二连浩特市","霍林郭勒市","包头市","乌海市","阿尔山市","乌兰浩特市","锡林浩特市","根河市",
            "满洲里市","额尔古纳市","牙克石市","临河市","丰镇市","通辽市","宁夏回族自治区",
            "银川市","固原市","石嘴山市","青铜峡市","中卫市","吴忠市","灵武市","西藏藏族自治区","拉萨市那曲市山南市林芝市昌都市",
            "阿里地区日喀则市","新疆维吾尔自治区","乌鲁木齐市","石河子市","喀什市","阿勒泰市","阜康市",
            "库尔勒市","阿克苏市","阿拉尔市","哈密市","克拉玛依市","昌吉市","奎屯市","米泉市","和田市","特别行政区","香港","澳门",
            "易县","都兰","鲁山","石首","宁武","临城","利津","伊川",
            "柞水","阳信","桐乡","铁岭","衡山","黄陵","宁安","龙岩","吴川","郫县",
            "铁法","商河","兴化","库伦旗","汶川","苗栗","六合","陕西省","浙江省","白朗",
            "连云港","湖口","丰顺","额敏","永善","龙泉","金堂","景东彝族自治县","徐州","陆川",
            "青浦","隆林各族自治县","绛县","桐庐","清涧","睢宁","印江土家族苗族自治县","隆昌","汶上","盐津",
            "莱州","平遥","铅山","麻城","蒙自","东乡族自治县","渑池","陇川","兰坪白族普米族自治县","筠连",
            "平邑","咸丰","江都","榆社","永新","青海","沅江","理县","珲春","柏乡",
            "拜城","海伦","澎湖","临桂","泽州","林甸","通海","抚远","嘉善","河曲",
            "泾川","长春","蕉岭","五河","侯马","盘山","竹山","武强","通州区","上海",
            "唐县","太和","阜南","高邑","吉木乃","萨迦","金塔","琼海","罗田","陆丰",
            "和县","木里藏族自治县","老河口","泸溪","庆云","兴县","昆明","高邮","凤阳","清丰",
            "来安","澄迈","聊城","务川仡佬族苗族自治县","罗甸","开远","青龙满族自治县","黟县","临颍","芦溪",
            "青川","贡觉","永昌","吉县","永嘉","绥化","青州","利川","永春","定襄",
            "成都","大田","屏南","东安","重庆市","东宁","乌鲁木齐　克拉玛依","龙海","巨野","茂县",
            "镇赉","岳普湖","云浮","来宾","晋州","山西省","白城","乌兰浩特","始兴","呼和浩特包头",
            "新邵","隆回","石家庄","范县","响水","维西傈僳族自治县","广德","安宁","通州","府谷",
            "平顶山","和林格尔","沧源佤族自治县","红安","措美","七台河","张家港","会泽","德令哈","塘沽区",
            "静海","新郑","尉氏","胶南","定西","甘南","共和","代县","天镇","万盛区",
            "武进","九台","泽库","滦南","息县","邵武","毕节","新都","通渭","万年",
            "许昌","玉环","郓城","当阳","哈巴河","永登","遵化","商州","临夏","宁河",
            "茂名","惠阳","泾源","万源","大新","鄂托克旗","琼中黎族苗族自治县　保亭黎族苗族自治县　陵水黎族自治县","孝感","文昌","岫岩满族自治县",
            "龙州","龙川","科尔沁左翼中旗　科尔沁左翼后旗","富县","大方","青海省","潼关","台湾省","天长","资阳",
            "莫力达瓦达斡尔族自治旗","宁波","贡山独龙族怒族自治县","舒兰","四川省","潍坊","兴和","滦县","友谊","牙克石",
            "商丘","漠河","番禺","富川瑶族自治县","镇巴","族自治县","蛟河","浚县","衢州","龙游",
            "射洪","彰化","都匀","崇左","乐昌","罗城仫佬族自治县","三亚","寻乌","黄石","崇州",
            "平凉","拉孜","临西","沾益","鄂温克自治旗","祥云","泗县","栖霞","盖州","新兴",
            "廊坊","洛宁","衡东","盐井","确山","盐源","迁西","宁津","丹棱","盐亭",
            "呈贡","天门","文登","彬县","玉屏侗族自治县","东山","广灵","当雄","樟树","鹿邑",
            "武邑","巴马瑶族自治县","陈巴尔虎旗","岐山","饶河","运城","华安","山东省","西宁","蔚县",
            "通什","平舆","华宁","西安","江北区","静乐","礼县","龙井","宣武区","新野",
            "永靖","房山区","莒南","长垣","灵台","水城","肥东","琼结","固始","元江哈尼族彝族傣族自治县",
            "宁海","平利","镇平","华容","沁源","清徐","吴忠","崇义","阜新蒙古族自治县　新宾满族自治县","五常",
            "和田","乐陵","策勒","黄浦区","易门","和政","辽宁省","桦川","武胜","封丘",
            "攀枝花","石嘴山","宾阳","安岳","镇康","魏县","沂源","门头沟区","互助土族自治县","会东",
            "顺义区","昭苏","黄梅","奉节","肥乡","妥坝","轮台","榆次","枞阳","武都",
            "涞水","万载","江苏","红河","凤城","溧水","谷城","突泉","尼木","四川",
            "霍城","涟水","信阳","恩施","莆田","清远","浦北","莒县","昌图","江华瑶族自治县",
            "微山","泰兴","惠来","萝北","西盟佤族自治县","逊克","祁县","崇仁","叶城","昔阳",
            "景德镇","东丽区","缙云","汝南","利辛","太白","兴文","康马","永城","疏附",
            "额济纳旗","沈丘","安泽","土默特左旗　土默特右旗","佳县","济阳","神农架","赣县","双峰","广元",
            "石屏","宣威","德安","朝阳","孟连傣族拉祜族佤族自治县","温宿","马龙","柳江","蚌埠","邻水",
            "玉田","浏阳","长顺","亚东","宜章","海兴","牡丹江","富锦","湘阴","东海",
            "黎城","石景山区","普安","华池","灵璧","竹溪","普宁","普定","米脂","淮北",
            "瑞安","奉化","盐边","泌阳","松桃苗族自治县","鸡泽","武冈","四平","秀山土家族苗族自治县","三门峡",
            "焦作","增城","枣阳","中宁","双流","南宁","将乐","阜新","汉阴","铜山",
            "秭归","宁乡","津南区","佳木斯","磁县","保靖","封开","阿荣旗","南安","隰县",
            "怀化","菏泽","河北省","合阳","元谋","云龙","富蕴","淮南","蓝山","定安",
            "元氏","岳西","松江区","韶山","柳河","临高","靖边","阿克陶","沙河","莘县",
            "石泉","斗门","南宫","容城","余杭","揭西","库车","沐川","西峰","西藏自治区",
            "卓尼","准格尔旗","荆州","东川","京山","永顺","平南","达孜","西峡","修文",
            "苍山","靖远","潼南","碧土","上思","温江","婺源","闵行区","佛坪","旌德",
            "镇远","镇宁布依族苗族自治县","漯河","江口","新化","麟游","阿拉善右旗","遂宁","玉门","岑巩",
            "辽宁","叙永","申扎","庆元","綦江","嵩县","宁蒗彝族自治县","德江","阜阳","大埔",
            "平原","歙县","大城","曲水","淳化","宿松","四会","弥渡","吉隆","兴隆",
            "东港","邳州","东丰","威海","即墨","江城哈尼族彝族自治县","连城","曲江","成县","垣曲",
            "安丘","吉木萨尔奇台","洛浦","北辰区","温岭","宜宾","饶平","耀县","崇信","治多",
            "汤原","昌江黎族自治县　乐东黎族自治县","临武","内蒙","固安","上海市","康定","东乡","温泉","宁强",
            "福安","延吉","南汇","六枝","中江","临安","曲沃","枝城","阳原","东平",
            "绥阳","南江","天柱","嘉定区","安义","富阳","中山","防城港","孙吴","安乡",
            "高县","武功","迁安","东源","安平","兴国","新县","杭锦旗","鸡东","濮阳",
            "高台","改则","蓬安","周宁","安庆","邵东","黔江土家族苗族自治县","杭州","开化","梁山",
            "荔波","望城","盐都","子长","金寨","于都","河西区","任县","渝中区","洛川",
            "泗阳","甘肃省","岑溪","民丰","岢岚","岗巴","清原满族自治县","潞城","光山","常宁",
            "安康","柳州","和静","万全","雷山","安溪","特克斯","达尔罕茂明安联合旗","营口","达拉特旗",
            "黄骅","民乐","安仁","金山区","噶尔","瓮安","无为","社旗","永福","卓资",
            "宁德","梁河","梨树","青冈","萧县","彭阳","景泰","五峰土家族自治县","宁远","恭城瑶族自治县",
            "平和","嘉祥","措勤","洪江","花都","商都","句容","红桥区","砀山","西丰",
            "荔浦","仙桃","石渠","剑河","寻甸回族彝族自治县","河南蒙古族自治县","潮阳","克山","尼勒克","铜川",
            "开原","马关","仪陇","景洪","巩留","漳浦","全椒","临江","关岭布依族苗族自治县","吉林",
            "蒙阴","聂拉木","勐海","郯城","崇文区","广南","开县","都昌","涞源","临汾",
            "贞丰","墨竹工卡工布江达","林西","西平","丹寨","临沂","祁门","邹平","海南","兴城",
            "新和","如东","西乡","满洲里","温州","雷波","涟源","光泽","嘉禾","阿勒泰",
            "卢氏","屏边苗族自治县","登封","辰溪","临沭","临河","赞皇","金沙","临沧","赵县",
            "陆良","稻城","奈曼旗","辉南","苏州","满城","海原","漳州","乾县","沙湾",
            "南海","德州","宣汉","凭祥","贵定","扶风","福泉","牟定","华亭","麻江",
            "娄底","莱芜","临泉","理塘","祁阳","巴里坤哈萨克自治县","洪泽","阜城","玉林","凌海",
            "寿光","德清","禄丰","顺平","东辽","高唐","莲花","河东区","克什克腾旗","常山",
            "盂县","布拖","黔西","洪洞","大厂回族自治县","禹城","安龙","建瓯","龙里","临泽",
            "海口","太仆寺旗","和硕","延长","辉县","义马","仁寿","白水","蒲县","安达",
            "安徽","清苑","拜泉","尚义","和顺","江门","榕江","雄县","郧西","榆中",
            "龙胜各族自治县","耿马傣族佤族自治县","南川","苍溪","津市","临洮","监利","道孚","铜仁","安远",
            "依兰","双辽","汝阳","新蔡","福贡","襄阳","丰润","亳州","乡城","攸县",
            "奉新","新田","融安","景宁畲族自治县","定州","夏河","泰和","瑞丽","台安","离石",
            "宁都","英吉沙","大姚","隆子","珠海","白山","波阳","丽水","武夷山","望奎",
            "乌鲁木齐托克逊","长子","德庆","铜鼓","酒泉","剑川","两当","玉树","泊头","余姚",
            "漳平","南丰","江阴","化隆回族自治县","肃北蒙古族自治县","郸城","福海","灯塔","南丹","宜州",
            "临海","遂川","泸县","宜川","达川","嵩明","阳新","隆安","绵阳","鄂伦春自治旗",
            "南乐","雷州","长武","白河","新疆","贵池","北碚区　双桥区","塔什库尔干塔吉克自治县","城口","类乌齐",
            "上犹","禄劝彝族苗族自治县","铁力","岱山","潞西","威远","长宁","桓台","平昌","虹口区",
            "梅州","南平","长安","和平区","夏津","双鸭山","环县","河池","辽中","吴县",
            "宝清","兰西","张家界","岳池","西乌珠穆沁旗","淮阳","湖北省","淮阴","威信","古浪",
            "渭源","灵石","冠县","水富","左权","卢湾区","道真仡佬族苗族自治县","沧州","陵川","宜丰",
            "梁平","福州","临川","高明","富顺","香河","平阳","宁冈","通化","平阴",
            "偃师","宝鸡","万荣","前郭尔罗斯蒙古族自治县","博湖","宝丰","南溪","邯郸","克东","桂阳",
            "博乐","黄山","金川","平陆","南京","阳春","孝昌","永宁","蒙城","高阳",
            "广东省","东胜","南康","陇县","重庆","凌源","凌云","新昌","伊金霍洛旗乌拉特前旗　乌拉特中旗","行唐",
            "舞钢","漾濞彝族自治县","临清","建昌","福清","永安","瓦房店","汤阴","南岸区","献县",
            "遂平","葫芦岛","洪湖","环江毛南族自治县","辽源","伽师","台江","清水河","慈溪","丹江口",
            "永定","顺德","鄞县","长寿","定结","康乐","隆尧","建阳","普陀区","伊吾",
            "原阳","郾城","富裕","麦盖提","思茅","柘荣","眉山","陶乐","台山","察布查尔锡伯自治县",
            "泾县","察隅","高陵","宣州","谢通门","鞍山","康平","宝应","得荣","金乡",
            "绥棱","宜黄","东光","惠安","沙坪坝区　九龙坡区　大渡口区　巴南区","吉首","常州","临湘","古丈","长汀",
            "南漳","繁峙","金湖","遂溪","大竹","玛曲","廉江","青县","三原","马尔康",
            "屯留","怀集","永寿","龙南","察雅","鹤壁","襄垣","朝阳区","东兴","思南",
            "丹巴","堆龙德庆林周","商南","东兰","河津","松溪","翁源","鱼台","长沙","金溪",
            "文安","长岛","云南","尚志","萨嘎","昌宁","赤城","高雄","松滋","衡南",
            "桃园","凤山","厦门","绿春","丹东","独山","阳曲","尤溪","田阳","德保",
            "大港区","长治","三台","颍上","来凤","霍山","施甸","沂南","襄城","长岭",
            "汝城","久治","政和","涪陵区","峨山彝族自治县","浦城","昆山","镇原","靖州苗族侗族自治县通道侗族自治县","罗定",
            "蓬溪","滁州","乐安","大宁","花莲","五华","壶关","南澳","博罗","忻城",
            "郑州","远安","惠民","正阳","开阳","平坝","兖州","格尔木","长泰","福建",
            "云县","齐齐哈尔黑河","大安","古交","天等","福鼎","沁县","阳朔","定边","保定",
            "贵州","惠水","从江","涉县","鄄城","栾城","海门","翼城","东至","阿尔山",
            "张家川回族自治县","穆棱","广昌","怀来","娄烦","高青","阿克塞哈萨克族自治县","仁布","深圳","齐河",
            "汕头","洱源","临漳","屯昌","龙口","张家口","文水","五原","平果","定远",
            "横山","贵港","衢县","郎溪","黑水","海阳","尉犁","五莲","松潘","公安",
            "嘉鱼","桂林","砚山","邢台","应县","长阳土家族自治县","图们","香港","马鞍山","南通",
            "炎陵","小金","炉霍","寿县","麻阳苗族自治县","英山","临潭","城步苗族自治县","武陟","永泰",
            "海晏","银川","六安","西充","呼图壁","桑日","闽清","精河","正镶白旗","桦南",
            "托克托","宁化","八宿","揭东","舞阳","文山","灌云","长海","勃利","赤壁",
            "讷河","信宜","黑山","五台","巴青","怀柔","新巴尔","冷水江","巴林右旗","台州",
            "电白","罗江","山阴","阳城","盘县","山阳","明水","舒城","武隆","清镇",
            "涿州","罗山","贺州","丘北","玛纳斯","阿拉善左旗","临澧","围场满族蒙古族自治县","张北","宁南",
            "济宁","德惠","贵溪","双牌","横峰","青田","濉溪","康保","井冈山","米易",
            "云和","陕县","洋县","荥经","永济","峨眉山","天水","比如","会理","台东",
            "抚松","乐山","吴旗","旬邑","余江","杞县","日照","韶关","河源","乃东",
            "海盐","台中","积石山保安族撒拉族东乡族自治县","会同","黄平","保山","阿图什","太谷","清新","弥勒",
            "平塘","连江","南郑","伊春","万州区","宁县","吐鲁番","宁夏回族自治区","上蔡","庆阳",
            "霍州","户县","徐闻","自贡","田林","望谟","平顺","于田","招远","枝江",
            "丹徒","勐腊","耒阳","咸阳","长清","常德","泰来","南部","景谷傣族彝族自治县","卢龙",
            "张掖","佛山","长丰","台湾","当涂","弋阳","仙居","宿豫","兴宁","金秀瑶族自治县",
            "潜江","赣榆","乌审旗","睢县","德兴","潜山","海淀区","洛扎","门源回族自治县","兴安",
            "乌海","长乐","舟曲","安化","焉耆回族自治县","益阳","楚雄","望江","永川","吉安",
            "敦煌","永州","涿鹿","莱阳","钟祥","淅川","大通回族土族自治县","内蒙古自治区","进贤","永清",
            "绥芬河","福建省","南充","鲁甸","闸北区","嵊泗","丰都","澄城","普兰","玛多",
            "天峨","临邑","汉寿","黎川","嘉峪关","额尔古纳根河","浮梁","西城区","扶沟","城固",
            "苏尼特右旗","阿坝","巴塘","惠州","昂仁","尖扎","三门","永丰","澧县","阆中",
            "阜宁","泾阳","天峻","大洼","扬州","民勤","东莞","江西","海林","虎右旗",
            "定兴","稷山","天津","泽普","星子","获嘉","武城","合水","凤庆","义乌",
            "林州","抚顺","磐石","三明","宣恩","惠东","昭觉","永年","修武","红原",
            "青阳","绥宁","贵德","永平","方城","天津市","海城","北票","瑞金","随州",
            "富宁","邱县","盘锦","梅河口","闽侯","吉水","梓潼","朔州","黄龙","多伦",
            "乡宁","志丹","甘孜","喀什","蒲城","扬中","加查","博兴","宜兴","哈尔滨",
            "石狮","紫云苗族布依族自治县","仁怀","襄樊","若羌","吉林省","东台","安县","合江","民和回族土族自治县",
            "宜兰","密山","磴口","合山","大足","夏邑","湛江","嘉义","唐山","浦东新区",
            "房县","都安瑶族自治县","霍林郭勒","永康","桐城","桐柏","三江侗族自治县","卫辉","黎平","兴山",
            "汾阳","资中","丁青","龙门","全州","宝兴","阿城","沽源","东营","鄢陵",
            "永仁","神木","伊通满族自治县","泰顺","诏安","米林","五大连池","洛南","渠县","上虞",
            "桦甸","灵武","诸暨","滕州","湄潭","东乌珠穆沁旗","唐河","安吉","九江","介休",
            "交口","高要","乐清","博野","阳西","西华","乐业","灵宝","昌乐","云阳",
            "富民","连州","革吉","新疆维吾尔自治区","宾川","威县","潮安","同江","中牟","资源",
            "习水","姜堰","嵊州","沁阳","二连浩特锡林浩特通辽","大丰","衡阳","隆德","九寨沟","昌平",
            "什邡","崇明","慈利","巴楚","嘉黎","茶陵","龙陵","西青区","塔城","罗平",
            "宿州","资溪","彰武","乌什","玉山","崇阳","紫金","屏山","修水","名山",
            "甘谷","杂多","南投","绥江","罗源","徐汇区","霞浦","建始","梧州","隆格尔",
            "勉县","博爱","寿阳","垫江","广西壮族自治区","鄯善","灵寿","乐平","浙江","余干",
            "泗水","大庆","德化","泉州","苍南","石柱土家族自治县","会昌","华县","藁城","连平",
            "余庆","湖州","株洲","沙县","藤县","夹江","昌黎","桐梓","镇雄","明溪",
            "石台","乐亭","合浦","宜良","分宜","井陉","沿河土家族自治县","兰州","信丰","太湖",
            "吴堡","阿巴嘎旗","常熟","唐海","岚县","保康","永德","拉萨","仙游","托里",
            "凤翔","西吉","敦化","阿合奇","周至","汕尾","洞口","沛县","兴海","新巴尔虎左旗",
            "灵山","肃南裕固族自治县","云霄","辛集","沅陵","正蓝旗","鹤山","鹤岗","四子王旗","射阳",
            "巧家","沈阳","宁明","郁南","扎赉特旗","上杭","绵竹","彭山","潢川","扎鲁特旗",
            "汉川","吴桥","永修","温县","金平苗族瑶族傣族自治县","简阳","那曲","甘泉","丹凤","长白朝鲜族自治县",
            "上林","赫章","贵州省","太康","诸城","渭南","蓟县","孟村回族自治县","江孜","桑植",
            "索县","中卫","宁阳","浠水","广饶","南华","合川","宁晋","漳县","太仓",
            "通城","蒙山","广西","双江拉祜族佤族布朗族傣族自治县","晋城","芮城","盐城","定南","英德","柘城",
            "西和","威宁彝族回族苗族自治县","宁陕","单县","花垣","礼泉","湘乡","江安","珙县","汉中",
            "敖汉旗","甘洛","偏关","江宁","济源","那坡","若尔盖","镇坪","东方","阳高",
            "虎林","大余","喀喇沁左翼蒙古族自治县","荣成","宁陵","鹤峰","融水苗族自治县","扶绥","山西","故城",
            "恩平","波密","兴业","丰南","云林","兰溪","驻马店","彭泽","景县","连山壮族瑶族自治县",
            "泗洪","北宁","赤水","商城","南县","上栗","新竹","禹州","安新","无锡",
            "枣庄","和布克赛尔蒙古自治县","肃宁","浑源","醴陵","淮安","霍邱","武威","黄冈","北安",
            "平武","延安","丽江纳西","成武","千阳","华蓥","兴义","锡山","大石桥","贺兰",
            "临猗","汉源","和平","饶阳","酉阳土家族苗族自治县","保德","内江","河口瑶族自治县","聂荣","荆门",
            "成安","兴平","白沙黎族自治县","东明","郴州","平安","南召","密云","怀安","滨海",
            "丰县","怀宁","泸西","平定","金华","皮山","喀喇沁旗","新平彝族傣族自治县","峡江","宁国",
            "大连","色达","东阳","且末","钟山","施秉","高安","湟中","长宁区","屏东",
            "胶州","东阿","永胜","徽县","襄汾","乌恰","扶余","应城","浦江","江永",
            "班戈","曲周","百色","安阳","延寿","武穴","德钦","西畴","阜平","肥城",
            "长兴","兴仁","六盘水","建宁","达县","新安","安陆","芷江侗族自治县","梅县","宣化",
            "新宁","河南省","蓝田","康县","洮南","萍乡","海南省","织金","绥中","宿迁",
            "子洲","曹县","松原","巫山","固原","左贡","宜君","沧县","陵县","枣强",
            "淳安","滨州","锦屏","高密","峨边彝族自治县","莱西","江山","壤塘","临县","灵川",
            "昌邑","阜康","陇西","邵阳","美姑","黑龙江","井研","湘潭","新密","彭州",
            "湟源","赤峰","青铜峡","南和","旺苍","西藏","象山","宝山区","抚宁","富平",
            "凤冈","平谷","永兴","江油","云梦","潮州","同仁","滦平","大邑","麻栗坡",
            "平江","渝北区","绍兴","玛沁","皋兰","灵丘","玉溪","富源","古县","巴林左旗",
            "法库","大悟","冀州","平山","蓬莱","乳山","台前","石门","青神","合作",
            "溧阳","遵义","宁城","阿瓦提","新民","周口","桃江","建水","正宁","洛阳",
            "阳谷","循化撒拉族自治县","仁化","西昌","昌都","呼兰","嘉兴","石阡","三都水族自治县","灌南",
            "凤凰","冕宁","和龙","正安","阳山","泰安","南开区","兰考","正定","澳门",
            "夏县","泰宁","安图","阳江","安国","资兴","白玉","中甸","璧山","上饶",
            "乐都","江津","绥滨","荣县","华阴","乌兰","惠农","鹤庆","磐安","日喀则",
            "平泉","通榆","巢湖","涡阳","顺昌","郏县","乾安","方正","洛隆","河北",
            "赣州","汾西","浮山","科尔沁右翼中旗　科尔沁右翼前旗","贵南","武宁","九龙","新沂","广安","阳泉",
            "延津","姚安","海宁","广宁","江浦","奎屯","武安","绩溪","奉贤","武宣",
            "本溪","广宗","个旧","祁东","明光","茌平","武定","靖西","营山","海安",
            "北流","公主岭","十堰","河南","开封","铜陵","鄂州","新河","刚察","承德",
            "韩城","新晃侗族自治县","贡嘎","宕昌","丰镇","崇礼","虞城","深泽","同德","越西",
            "中方","称多","大兴","大关","留坝","汝州","台北","内丘","萧山","岚皋",
            "同心","德昌","班玛","沙雅","隆化","如皋","犍为","忻州","北海","仲巴",
            "新泰","安徽省","望都","德阳","栾川","江川","桓仁满族自治县　宽甸满族自治县　本溪满族自治县","开江","天全","澄江",
            "柯坪","鹿寨","元阳","乌拉特后旗","民权","左云","瑞昌","略阳","大冶","文成",
            "农安","曲阜","三穗","集宁","锦州","肥西","台南","翁牛特旗","内乡","雅安",
            "东城区","新津","天祝藏族自治县","绥德","定日","化州","墨脱","佛冈","汉沽区","固镇",
            "双城","莎车","双柏","巩义","曲阳","象州","开鲁","新洲","集安","喜德",
            "甘德","合肥","巨鹿","杭锦后旗","邕宁","广水","南昌","广汉","馆陶","交城",
            "宁夏","乐至","木垒哈萨克自治县","中阳","含山","北川","延川","无极","孟津","武汉",
            "连南瑶族自治县","泸定","霸州","巍山彝族回族自治县","南阳","费县","凯里","布尔津","伊宁","咸宁",
            "界首","古田","武山","基隆","庆安","旬阳","裕民","镶黄旗","方山","内黄",
            "杜尔伯特蒙古族自治县","华坪","日土","陕西","眉县","剑阁","古蔺","道县","仪征","岷县",
            "西林","钦州","昭平","云南省","林芝","南陵","达日","镇沅彝族哈尼族拉祜族自治县","宜昌","右玉",
            "黑龙江省","蒲江","安塞","石河子","深州","从化","宜春","上高","石林彝族自治县","辽阳",
            "宽城满族自治县","柳林","高淳","乌苏","巫溪","沾化","高州","桂东","定陶","白银",
            "安顺","遂昌","淄博","江源","宜阳","广河","舟山","邹城","金门","团风",
            "雅江","扎囊","泸水","石城","平乐","柳城","肇州","鹿泉","儋州","札达",
            "南皮","南木林","马边彝族自治县","平乡","盱眙","南雄","平湖","金昌","南市区","凤县",
            "万宁","孟州","丰台区","延庆","阳东","丰宁满族自治县","息烽","甘肃","项城","紫阳",
            "博白","万安","秦安","杨浦区","墨江哈尼族自治县","生达","固阳","任丘","纳雍","芒康",
            "松阳","肇东","桂平","澄海","北京","库尔勒","秦皇岛","叶县","师宗","凤台",
            "孝义","高平","嘉荫","迭部","新丰","金阳","呼玛","册亨","建湖","囊谦",
            "平度","阿鲁科尔沁旗","休宁","盈江","晋宁","芦山","嫩江","晴隆","义县","桃源",
            "吴江","大英","巴东","庄河","曲靖","高碑店","新乐","普洱哈尼族彝族自治县","长葛","徐水",
            "建平","巴中","祁连","扎兰屯","淮滨","错那","邗江","米泉","安多","新乡",
            "原平","彝良","曲松","清水","湖南省","怀仁","神池","新干","通许","肇庆",
            "塔河","尼玛","南靖","淇县","边坝","汪清","洪雅","横县","永吉","榆林",
            "靖安","田东","靖宇","泰州","蕲春","全南","肇源","新源","鹰潭","畹町",
            "烟台","鄂托克前旗","滑县","大荔","集贤","无棣","丹阳","云安","乳源瑶族自治县","闻喜",
            "文县","江苏省","荣昌","安西","林口","新绛","芜湖","沭阳","安福","容县",
            "澜沧拉祜族自治县","疏勒","凉城","镇安","江达","广州","章丘","铜梁","石棉","武川",
            "陆河","三水","清河","都江堰","万山","木兰","榆树","新建","垦利","平罗",
            "平潭","武清","河北区","临朐","永和","静宁","浪卡子","南城","阿克苏","彭水苗族土家族自治县",
            "曲麻莱","新会","寿宁","灌阳","苍梧","化德","北京市","鸡西","庄浪","德格",
            "武鸣","昌吉","大化瑶族自治县","宝坻","广东","晋江","开平","荥阳","昭通","普格",
            "朗县","海拉尔","依安","通山","湖北","贵阳","海丰","通江","马山","丰城",
            "广丰","青岛","盐池","普兰店","商水","新余","江西省","静安区","郧县","河间",
            "溆浦","蠡县","武义","会宁","盐山","青河","大理","武乡","宜城","邓州",
            "金坛","琼山","太原","山东","武平","平远","三河","庐江","济南","启东",
            "通河","宾县","湖南","大名","大同","怀远","清流","忠县","邛崃","靖江",
            "洞头","五寨","沁水","广平","汨罗","衡水","山丹","繁昌","新龙","石楼",
            "碌曲","察哈尔右翼前旗　察哈尔右翼中旗　察哈尔右翼后旗　苏尼特左旗","镇江","揭阳","墨玉","岳阳","龙山","泸州","天台","黄陂",
            "南涧彝族自治县","哈密","龙江","建德","巴彦","腾冲","沂水"
    };

    public static void main(String args[]) {

        try {
            File readFile = new File("E:\\xian.txt");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int index = 0;

            Map<String, String> map = new HashMap<String, String>();

            while ((line = br.readLine()) != null) {
                String xian[] = line.split(" ");
                for(String x : xian) {
                    map.put(x,x);
                }
            }
            for(String key:map.keySet()) {
                if(index % 10 == 0) {
                    System.out.println("");
                }
                System.out.print("\""+key+"\",");

                index++;

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}