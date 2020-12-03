package com.kemai.utils;
public class Test {
    public static void main(String[] args) {
        String s = "abcd";
        String news = "";
        for(int i=s.length()-1;i>=0;i--) {
            news = news+s.charAt(i);
        }
        int j;
        for(j=1;j<=news.length();j++) {
            String sub = news.substring(0,news.length()-j);
            if(isHuiwen(sub)) {
                break;
            }
        }
        String buchong  = news.substring(news.length()-j,news.length());
        System.out.println(buchong);
    }

    private static boolean isHuiwen(String sub) {
        boolean flag= true;
        for(int i=0;i<sub.length();i++) {
            if(sub.charAt(i) != sub.charAt(sub.length()-i-1)) {
                return false;
            }
        }
        return flag;
    }
}
