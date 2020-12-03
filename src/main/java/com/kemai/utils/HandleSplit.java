package com.kemai.utils;

import java.io.*;
import java.util.Map;

public class HandleSplit {

    public static void main(String args[]) {
//    val line = "2020-06-09 10:29:05,322 ent {\"regCap\": \"壹佰伍拾万元整\", \"regCapCurCN\": \"人民币\", \"nodeNum\": \"440000\", \"preEntType\": \"1130\", \"entName\": \"清远福元农业科技有限公司\", \"regNo\": \"441802000004126\", \"uniscId\": \"91441802351267997C\", \"regState\": \"4\", \"regStateCN\": \"注销企业\", \"entType\": \"1\", \"entTypeCN\": \"有限责任公司(自然人投资或控股)\", \"legalName\": \"李英\", \"industryPhy\": \"M\", \"regOrg\": \"441802\", \"regOrgCn\": \"广东省清远市清城区工商行政管理局\", \"estDate\": \"2015-06-15\", \"apprDate\": \"2019-09-02\", \"dom\": \"清远市清城区人民三路31号清远富荣农副产品批发中心8栋16号\", \"opFrom\": \"2015-06-15\", \"opTo\": null, \"opScope\": \"农业技术推广服务，批发零售业，农产品加工，贸易代理，网站建设，软件开发，种植农作物水果蔬菜。(依法须经批准的项目，经相关部门批准后方可开展经营活动)〓\", \"eGrpName\": null, \"eGrpShform\": null, \"ePubGroup\": \"unshow\", \"entTypeForAnnualReport\": 1, \"entTypeForQuery\": 1, \"linkmanName\": \"\", \"linkmanCerNo\": \"\", \"linkmanPhone\": \"\", \"updatedAt\": 1588476351.85796, \"keyperson\": [{\"perId\": \"d7d14569-016c-1000-e005-f7390a130116441800\", \"pripId\": \"f5a65f99-014d-1000-e000-0b3d0a130116\", \"name\": \"黄海林\", \"positionCN\": \"监事\", \"createdAt\": 1588476402.434772}, {\"perId\": \"d7d14569-016c-1000-e005-f7370a130116441800\", \"pripId\": \"f5a65f99-014d-1000-e000-0b3d0a130116\", \"name\": \"李英\", \"positionCN\": \"执行董事\", \"createdAt\": 1588476402.451218}, {\"perId\": \"d7d14569-016c-1000-e005-f7380a130116441800\", \"pripId\": \"f5a65f99-014d-1000-e000-0b3d0a130116\", \"name\": \"王凡\", \"positionCN\": \"经理\", \"createdAt\": 1588476402.464797}], \"shareholder\": [{\"invId\": \"6E2B48C760368135AD1A951B8334A77DB77D5D7D5D7DD808AD0EC4D9A83C0D2D8EFFCFFE5D7D5D7D4C4C6C4C5160CB1B9D5777208305258625549E54F7052505-1588475872400\", \"inv\": \"王海林\", \"invTypeCN\": \"自然人股东\", \"cerTypeCN\": \"中华人民共和国居民身份证\", \"cerNo\": \"\", \"blicTypeCN\": \"\", \"liSubConAm\": null, \"liAcConAm\": null, \"countryCN\": \"\", \"dom\": \"\", \"respFormCN\": \"\", \"respForm\": \"\", \"sConForm\": \"货币出资\", \"sConFormCN\": null, \"conDate\": null, \"detailCheck\": \"false\", \"url\": \"http://192.168.199.194:8082/gsxt/corp-query-entprise-info-shareholderDetail-6E2B48C760368135AD1A951B8334A77DB77D5D7D5D7DD808AD0EC4D9A83C0D2D8EFFCFFE5D7D5D7D4C4C6C4C5160CB1B9D5777208305258625549E54F7052505-1588475872400.html?entType=1\", \"createdAt\": 1588476405.101828}, {\"invId\": \"175231BE194FF84CD463EC62FA4DDE04CE0424042404A171D477BDA0D1457454F786B68724042404353515352819B262E4624215B63010B31061AB61C2301030-1588475872400\", \"inv\": \"李英\", \"invTypeCN\": \"自然人股东\", \"cerTypeCN\": \"中华人民共和国居民身份证\", \"cerNo\": \"\", \"blicTypeCN\": \"\", \"liSubConAm\": null, \"liAcConAm\": null, \"countryCN\": \"\", \"dom\": \"\", \"respFormCN\": \"\", \"respForm\": \"\", \"sConForm\": \"货币出资\", \"sConFormCN\": null, \"conDate\": null, \"detailCheck\": \"false\", \"url\": \"http://192.168.199.194:8082/gsxt/corp-query-entprise-info-shareholderDetail-175231BE194FF84CD463EC62FA4DDE04CE0424042404A171D477BDA0D1457454F786B68724042404353515352819B262E4624215B63010B31061AB61C2301030-1588475872400.html?entType=1\", \"createdAt\": 1588476405.114481}, {\"invId\": \"F1B4D758FFA91EAA32850A841CAB38E228E2C2E2C2E2479732915B4637A392B211605061C2E2C2E2D3D3F3D3CEFF5484021F3F68CB4D6DCE6D1CD61CBF4D6D4D-1588475872400\", \"inv\": \"王凡\", \"invTypeCN\": \"自然人股东\", \"cerTypeCN\": \"中华人民共和国居民身份证\", \"cerNo\": \"\", \"blicTypeCN\": \"\", \"liSubConAm\": null, \"liAcConAm\": null, \"countryCN\": \"\", \"dom\": \"\", \"respFormCN\": \"\", \"respForm\": \"\", \"sConForm\": \"货币出资\", \"sConFormCN\": null, \"conDate\": null, \"detailCheck\": \"false\", \"url\": \"http://192.168.199.194:8082/gsxt/corp-query-entprise-info-shareholderDetail-F1B4D758FFA91EAA32850A841CAB38E228E2C2E2C2E2479732915B4637A392B211605061C2E2C2E2D3D3F3D3CEFF5484021F3F68CB4D6DCE6D1CD61CBF4D6D4D-1588475872400.html?entType=1\", \"createdAt\": 1588476405.128661}]}";
        handleFile();
    }

    private static void handleFile( ) {

        try{



            File readfile = new File("E:\\spidermain_data.log");
            String line;
            FileReader fr = new FileReader(readfile);
            BufferedReader br = new BufferedReader(fr);
            int index = 0;
            FileWriter fw = new FileWriter("E:\\spidermain_data_new.log", false);
            PrintWriter pw = new PrintWriter(fw);
            while ((line = br.readLine()) != null) {
                String s1 = line.substring(0, 23);
                String ss = line.substring(24, line.length()-1);
                String table = ss.substring(0,ss.indexOf("{")-1);
                String content =  ss.substring( ss.indexOf("{"),ss.length()-1);
               String newLine =  s1 + "|" + table + "|" + content;
                pw.println(newLine);
            }



            pw.flush();
            pw.close();
            fw.close();


        }catch(Exception se){
            se.printStackTrace();
        }

    }
}
