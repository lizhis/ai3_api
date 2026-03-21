package com.ai.basecommon.utils;


public class RegUtil {



    /**
     * 判断字符串是否为纯数字
     * @param str
     * @return
     */
    public static boolean isAllNumber(String str) {
        if(null == str || "".equals(str)){
            return false;
        }
        String reg = "^[0-9]+$";
        return str.matches(reg);
    }

    /**
     * 校验邮箱格式
     * @param email
     * @return
     */
    public static boolean regEmail(String email) {
        if(null == email || "".equals(email)){
            return false;
        }
        String reg="^[A-z0-9_-]+\\@[A-z0-9]+\\.[A-z]+$";
        return email.matches(reg);
    }

    /**
     * 校验手机号格式
     * @param tel
     * @return
     */
    public static boolean regTel(String tel) {
        if(null == tel || "".equals(tel)){
            return false;
        }
        String reg = "^[1][23456789]\\d{9}$";
        return tel.matches(reg);
    }

    /**
     * 校验密码格式
     * @param pass
     * @return
     */
    public static boolean regPassword(String pass) {
        if(null == pass || "".equals(pass)){
            return false;
        }
        String reg = "^[0-9a-zA-Z]{6,20}$";
        return pass.matches(reg);
    }



}
