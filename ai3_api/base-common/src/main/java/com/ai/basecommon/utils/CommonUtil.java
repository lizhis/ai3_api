package com.ai.basecommon.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {


    /**
     * 获取UUID  32位
     * @return
     */
    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }


    /**
     * 获取密码复杂度
     * @param pass
     * @return
     */
    public static int getPassLevel(String pass){
        int level = 0;
        if(null == pass || "".equals(pass)){
            return level;
        }
        String numReg = "[0-9]+";
        if(pass.matches(numReg)){
            level ++;
        }
        String lowerReg = "[a-z]+";
        if(pass.matches(lowerReg)){
            level ++;
        }
        String upperReg = "[A-Z]+";
        if(pass.matches(upperReg)){
            level ++;
        }
        return level;
    }



    /**
     * 判断字符串是否为纯数字
     * @param str
     * @return
     */
    public static boolean regIsNumber(String str) {
        if(null == str || "".equals(str)){
            return false;
        }
        String reg = "^[0-9]+$";
        return str.matches(reg);
    }


    /**
     * 隐藏手机号
     * @param tel
     * @return
     */
    public static String getHideTel(String tel) {
        if(StringUtil.isEmpty(tel)){
            return null;
        }
        if(tel.length() < 11){
            return tel;
        }
        return tel.substring(0, 3) + "****" + tel.substring(7);
    }

    /**
     * 隐藏邮箱
     * @param email
     * @return
     */
    public static String getHideEmail(String email) {
        if(StringUtil.isEmpty(email)){
            return null;
        }
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }


    /**
     * 隐藏身份证号码
     * @param idCard
     * @return
     */
    public static String getHideIdCard(String idCard) {
        if(StringUtil.isEmpty(idCard)){
            return null;
        }
        return idCard.substring(0, 3) + "****" + idCard.substring(idCard.length() - 3);
    }


    /**
     * 隐藏姓名
     * @param userName
     * @return
     */
    public static String getHideName(String userName) {
        if(null == userName || "".equals(userName)){
            return null;
        }
        if(!Pattern.compile("^[\u4e00-\u9fa5]+$").matcher(userName).matches()){
            return userName;
        }

        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(userName);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }

        if(count < 2){
            return userName;
        }

        String[] arr = {"欧阳", "太史", "端木", "上官", "司马", "东方", "独孤", "南宫",
                "万俟", "闻人", "夏侯", "诸葛", "尉迟", "公羊", "赫连", "澹台", "皇甫", "宗政", "濮阳",
                "公冶", "太叔", "申屠", "公孙", "慕容", "仲孙", "钟离", "长孙", "宇文", "司徒", "鲜于",
                "司空", "闾丘", "子车", "亓官", "司寇", "巫马", "公西", "颛孙", "壤驷", "公良", "漆雕", "乐正",
                "宰父", "谷梁", "拓跋", "夹谷", "轩辕", "令狐", "段干", "百里", "呼延", "东郭", "南门", "羊舌",
                "微生", "公户", "公玉", "公仪", "梁丘", "公仲", "公上", "公门", "公山", "公坚", "左丘", "公伯",
                "西门", "公祖", "第五", "公乘", "贯丘", "公皙", "南荣", "东里", "东宫", "仲长", "子书", "子桑",
                "即墨", "达奚", "褚师", "吴铭"};
        List<String> suffix = Arrays.asList(arr);



        String f = userName.substring(0,2);
        if(suffix.contains(f)){
            String x = "";
            for(int i=0;i<(count-2);i++){
                x += "*";
            }
            userName = f + x;
        }else{
            String x = "";
            for(int i=0;i<(count-1);i++){
                x += "*";
            }
            userName = userName.substring(0,1) + x;
        }
        return userName;
    }


    /**
     * 获取指定位数的随机数
     * @param count
     * @return
     */
    public static String getRandom(int count) {
        if(count < 1){
            count = 1;
        }
        double d = Math.random();
        String str = Double.toString(d);
        if(str.contains("-") || count > (str.length()-2)){
            boolean r = true;
            while (r){
                str = Double.toString(Math.random());
                if(!str.contains("-") && count < (str.length()-2)){
                    break;
                }
            }
        }
        long r = Long.parseLong(str.substring(str.length() - count));
        int len = Long.toString(r).length();
        if(len == count){
            return Long.toString(r);
        }
        Double w = Math.pow(10,count - len);
        long re = r * w.longValue();
        return Long.toString(re);
    }

    /**
     * 取两数之间的随机数
     * @param x
     * @param y
     * @return
     */
    public static int getRandom(int x, int y) {
        int num = -1;
        if (x < 0 || y < 0) {
            return num;
        } else {
            int max = Math.max(x, y);
            int min = Math.min(x, y);
            int mid = max - min;
            num = (int) (Math.random() * (mid + 1)) + min;
        }
        return num;
    }


    /**
     * 取两数之间的随机数
     * @param x
     * @param y
     * @return
     */
    public static BigDecimal getRandom(BigDecimal x, BigDecimal y) {
        BigDecimal min,max;
        if(null == x || null == y || x.compareTo(BigDecimal.ZERO) < 0 || y.compareTo(BigDecimal.ZERO) < 0){
            return BigDecimal.ZERO;
        }
        if(x.compareTo(y) >= 0){
            min = y;
            max = x;
        }else{
            min = x;
            max = y;
        }
        String[] arr1 = min.toString().split("\\.");
        String[] arr2 = max.toString().split("\\.");
        Integer digit = 0;
        if(2 == arr1.length){
            digit = arr1[1].length();
        }
        if(2 == arr2.length){
            digit = Math.max(digit,arr2[1].length());
        }

        Double point = Math.pow(10,digit);

        BigDecimal randomMin = min.multiply(new BigDecimal(point));
        BigDecimal randomMax = max.multiply(new BigDecimal(point));
        BigDecimal mid = randomMax.subtract(randomMin);
        BigDecimal num = new BigDecimal(String.valueOf(Math.random())).multiply(mid.add(BigDecimal.ONE)).add(randomMin);
        BigDecimal result = num.divide(new BigDecimal(point),digit, RoundingMode.DOWN).stripTrailingZeros();
        return result;
    }


    //生成随机字符串
    public static String getStringRandom(int length) {
        String charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charPool.length());
            sb.append(charPool.charAt(index));
        }
        return sb.toString();
    }



    /**
     * 判断设备平台
     * @param deviceId 输入的设备号字符串
     * @return 1 = Android ID, 2 = IDFV (iOS)
     */
    public static int getDevicePlatform(String deviceId) {
        if (deviceId == null) return -1;

        String trimmed = deviceId.trim();

        // Android ID: 16位十六进制小写
        if (trimmed.matches("^[0-9a-f]{16}$")) {
            return 1;
        }

        // IDFV: UUID 格式，包含大写+数字+固定连字符格式
        if (trimmed.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            return 2;
        }

        // 无法识别
        return -1;
    }



}
