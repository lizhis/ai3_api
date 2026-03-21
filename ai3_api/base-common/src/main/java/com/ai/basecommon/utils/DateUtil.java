package com.ai.basecommon.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {


    /**
     * 获取今天日期
     * @return
     */
    public static Integer todayDate(){
        return Integer.parseInt(timestampToDate(System.currentTimeMillis(),"yyyyMMdd"));
    }

    public static Integer ym(){
        return Integer.parseInt(timestampToDate(System.currentTimeMillis(),"yyyyMM"));
    }


    /**
     * 日期时间格式转时间戳
     * @param dateStr  20220704213518  到秒
     * @return
     */
    public static Long dateStrToTimestamp(String dateStr) throws Exception{
        //大写HH：24小时制，小写hh：12小时制
        //毫秒：SSS
        //指定转化前的格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //转化后为Date日期格式
        Date date = sdf.parse(dateStr);
        //Date转为时间戳long
        return date.getTime();
    }

    /**
     * 获取昨天日期
     * @return
     */
    public static Integer yesterdayIntDate(){
        Long d = System.currentTimeMillis() - 3600 * 24 * 1000;
        return Integer.parseInt(timestampToDate(d,"yyyyMMdd"));
    }

    /**
     * 时间戳转日期格式
     * @param time 13位时间戳
     * @param format 默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String timestampToDate(Long time,String format) {
        if(time == null || 0 == time){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }


    /**
     * 10位时间戳转日期格式
     * @param seconds 精确到秒的字符串
     * @param format 默认yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String timestamp10ToDate(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }


    /**
     * 计算两时间的时间差
     * @param startTime
     * @param endTime
     * @return n天n小时n分钟
     */
    public static String diff(Long startTime,Long endTime){
        if(null == startTime || null == endTime){
            System.out.println("计算时间差的参数不能为空");
            return null;
        }
        if(startTime >= endTime){
            System.out.println("计算时间差的结束时间必须大于开始时间");
            return null;
        }
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;

        long diff = endTime - startTime;

        String result = "";

        long day = diff / nd;
        if(day > 0){
            result += day + "天";
        }
        long hour = diff % nd / nh;
        if(hour > 0){
            result += hour + "小时";
        }
        long min = diff % nd % nh / nm;
        if(min > 0){
            result += min + "分钟";
        }
        if("".equals(result)){
            result = "小于1分钟";
        }
        return result;
    }


    /**
     * 返回当前日期 格式：2020-11-11
     * @return
     */
    public static String currentDate(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }


    /**
     * 返回当前日期 数组
     * @return
     */
    public static String[] currentDateArr(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(date);
        return dateStr.split("-");
    }


    /**
     * 获取昨天日期 格式：2020-03-17
     * @return
     */
    public static String yesterdayDate(){
        Calendar cal=Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        cal.add(Calendar.DATE,-1);
        Date d=cal.getTime();
        SimpleDateFormat sp=new SimpleDateFormat("yyyy-MM-dd");
        return sp.format(d);
    }


    /**
     * 获取昨天日期号数 格式：31
     * @return
     */
    public static String yesterdayNum(){
        Calendar cal=Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        cal.add(Calendar.DATE,-1);
        Date d=cal.getTime();
        SimpleDateFormat sp=new SimpleDateFormat("dd");
        return sp.format(d);
    }

    /**
     * 获取今天0点时间戳
     * @return
     */
    public static Long getTodayStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取明天0点时间戳
     * @return
     */
    public static Long getTomorrowStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 传入时间戳，返回字符串类型时间（ISO8601标准时间）
     * @param time
     * @return
     */
    public static String getISO8601Time(Long time){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date(time));
        return nowAsISO;
    }

    /**
     * ISO8601标准时间 转日期时间
     * @param date ISO8601标准时间 如：2019-12-06T11:11:14.328+0800
     * @param format 返回的格式 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String ISO8601ToDate(String date,String format){
        String result = "";
        if(null == date || "".equals(date) || null == format || "".equals(format)){
            return result;
        }
        try{
            TimeZone tz = TimeZone.getTimeZone("UTC");
            TimeZone tz2 = TimeZone.getTimeZone("Asia/Shanghai");
            SimpleDateFormat mat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            mat.setTimeZone(tz);
            Date date2 = mat.parse(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            simpleDateFormat.setTimeZone(tz2);
            result = simpleDateFormat.format(date2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ISO8601标准时间转时间戳
     * @param date ISO8601标准时间 如：2019-12-06T11:11:14.328+0800
     * @return
     */
    public static Long ISO8601ToTimestamp(String date){
        Long result = 0L;
        if(null == date || "".equals(date)){
            return result;
        }
        try{
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat mat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            mat.setTimeZone(tz);
            Date date2 = mat.parse(date);
            result = date2.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 返回当前年  格式：2020
     * @return
     */
    public static String currentNian(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(date);
    }


    /**
     * 日期格式字符串转换成时间戳
     * @param dateStr 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Long dateToTimeSeconds(String dateStr,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return Long.valueOf(String.valueOf(sdf.parse(dateStr).getTime()/1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /**
     * 日期格式字符串转换成时间戳  毫秒
     * @param dateStr 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Long dateToMillisecond(String dateStr,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return Long.valueOf(String.valueOf(sdf.parse(dateStr).getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /**
     * 日期格式字符串转换成时间戳  毫秒
     * @param dateStr 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Long dateToTimeStamp(String dateStr,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return Long.valueOf(String.valueOf(sdf.parse(dateStr).getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /**
     * 判断是否是闰年：
     */
    public static boolean isLeap( int year){
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
            return true;
        else
            return false;
    }


    /**
     * 获取指定年月有多少天
     * @param year
     * @param month
     * @return
     */
    public static int getDays(int year,int month){
        int days;
        int FebDay=28;
        if(isLeap(year))
            FebDay=29;

        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days=31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                days=30;
                break;
            case 2:
                days=FebDay;
                break;
            default:
                days=0;
                break;
        }
        return days;
    }


    /**
     * 返回当前时分秒 格式：15:28:27
     * @return
     */
    public static String currentTime(){
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }


    /**
     * 获取指定日期所在的周一时间戳
     * @param date 时间
     * @param format 参数格式 如yyyy-MM-dd
     * @return
     */
    public static Long getMondayTime(String date,String format){
        if(null == date || "".equals(date)){
            return null;
        }
        if(null == format || "".equals(format)){
            return null;
        }
        Long mondayTime = null;
        try{
            SimpleDateFormat sdf=new SimpleDateFormat(format); //设置时间格式
            Calendar cal = Calendar.getInstance();
            Date time = sdf.parse(date);
            cal.setTime(time);
            cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            if(1 == dayWeek) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }

            cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

            int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            mondayTime = cal.getTimeInMillis();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mondayTime;
    }


    /**
     * 获取指定日期的下一个月的开始时间戳
     * @param dateStr 时间
     * @param format 参数格式 如yyyy-MM-dd
     * @return
     */
    public static Long getNextMonthStartTime(String dateStr,String format){
        Long time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            time = calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }


    /**
     * 获取指定日期的当月的开始时间戳
     * @param dateStr 时间
     * @param format 参数格式 如yyyy-MM-dd
     * @return
     */
    public static Long getMonthStartTime(String dateStr,String format){
        Long time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            time = calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }


    //指定时间的年开始时间戳
    public static Long getYearStartTime(String dateStr,String format){
        Long time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            calendar.set(Calendar.DAY_OF_YEAR,1);
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);


            time = calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;


    }

    /**
     * 获取这个月开始时间
     * @return
     */
    public static Long getMonthStartTime(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Long time = calendar.getTimeInMillis();
        return time;
    }

    /**
     * 获取指定时间戳的当月结束时间
     * @param time
     * @return
     */
    public static Long getMonthEndTime(Long time){
        if(null == time || time < 1){
            return null;
        }
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Long lastTime = calendar.getTimeInMillis() - 1000;
        return lastTime;
    }


    //本周结束时间
    public static Long currentWeekEndTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        int dayOfWeek = now.getDayOfWeek().getValue();
        //LocalDateTime weekStart = now.minusDays(dayOfWeek - 1).with(LocalTime.MIN);
        LocalDateTime weekEnd = now.plusDays(7 - dayOfWeek).with(LocalTime.MAX);
        //System.out.println("当前周的开始时间:" + weekStart.format(fmt));
        System.out.println("当前周的结束时间:" + weekEnd.format(fmt));
        Long ss = weekEnd.toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
        System.out.println("当前周的结束时间戳:" + weekEnd.format(fmt));
        return ss;
    }

    public static Long currentMONTHEndTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

        //LocalDateTime monthStart = now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime monthEnd = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        Long ss = monthEnd.toEpochSecond(ZoneOffset.ofHours(8)) * 1000;
        //System.out.println("当前月的开始时间:" + monthStart.format(fmt));
        System.out.println("当前月的结束时间:" + monthEnd.format(fmt));
        System.out.println("当前月的结束时间戳:" + ss);
        return ss;
    }

}
