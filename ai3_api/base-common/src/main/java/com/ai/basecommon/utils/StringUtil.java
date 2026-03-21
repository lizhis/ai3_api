package com.ai.basecommon.utils;

import org.apache.commons.lang3.RandomStringUtils;


public class StringUtil {


    /**
     * 生成随机字符串
     * @param count
     * @return
     */
    public static String randomStr(int count){
        if(count <= 0){
            count = 6;
        }
        return RandomStringUtils.randomAlphanumeric(count);
    }

    /**
     * 字符串判空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }



}
