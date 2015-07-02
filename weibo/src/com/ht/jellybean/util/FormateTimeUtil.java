package com.ht.jellybean.util;

/**
 * Created by annuo on 2015/6/7.
 */
public class FormateTimeUtil {
    public static String[] getFormatTime(String rawTime) {
        String[] strings = rawTime.split(" ");

        //英文月份
        String[] engMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        //中文月份
        String[] zhMonths = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};

        //建立英文月份和中文月份之间的映射关系
        for (int i = 0; i < engMonths.length; i++) {
            if (strings[1].equals(engMonths[i])) {
                strings[1] = zhMonths[i];
                break;
            }
        }

        if (strings[2].startsWith("0")) {
            strings[2] = strings[2].split("0")[1];
        }
        return strings;
    }

}
