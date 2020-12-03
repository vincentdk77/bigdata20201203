package com.chuangrui.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Utils {

    public static String md5(String mobile) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(mobile.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    public static void main(String args[]) {
        String mobile ="¹§Ï²£¡ÄúÒÑ»ñµÃ³¬µÍÀûÏ¢ÌØÈ¨£¬3000¶î¶È£¬30·ÖÖÓ¼«ËÙ·Å¿î£¬¿ìÀ´µÇÂ¼¼¤»îÁ¢¼´ÄÃÇ® http://t.cn/Rp8GxAg»ØTÍË¶©";
        String s2 = "¡¾¿ìÃ×½ðÈÚ¡¿";
        try {

            System.out.println(new String(mobile.getBytes("windows-1252"),"gbk"));
            System.out.println(new String(s2.getBytes("windows-1252"),"gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}
