package com.ai.basewebsocket.task.config;


import com.ai.basewebsocket.common.SpringUtil;
import com.ai.basewebsocket.task.OnlineCheckTask;
import com.ai.basewebsocket.task.TestTask;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Description
 * @Author
 */
@Configuration
@EnableScheduling
public class Schedule {

    //秒-分-时-日-月-周

    //(*) 星号：可以理解为“每”的意思，每秒、没分
    //(?) 问好：只能出现在日期和星期这两个位置，表示这个位置的值不确定
    //(-) 表达一个范围，如在小时字段中使用 10-12 ，表示从10点到12点
    //(,) 逗号，表达一个列表值，如在星期字段中使用 1,2,4 ，则表示星期一、星期二、星期四
    //(/) 斜杠，如 x/y ，x是开始值，y是步长，如在第一位(秒)使用 0/15，表示从0秒开始，每15秒

    // "0 0 0 * * ?" 每天0点整执行
    // "0 30 1 * * ?" 每天1点30分执行
    // "0/10 * * * * ?" 每10秒钟执行一次
    // "0 0/3 * * * ?" 每3分钟执行一次
    // "0 * 0 * * ?" 每天0点到0点59分之间每分钟执行一次
    // "0 0/3 1,2 * * ?" 每天的凌晨1点和凌晨2点两个小时中每3分钟执行一次

    //定时器测试
    //@Scheduled(cron = "0/3 * * * * ?")
    public void testTask() {
        TestTask task = (TestTask) SpringUtil.getBean("TestTask");
        task.doTask();
    }

    //在线检查
    @Scheduled(cron = "4 0/10 * * * ?")
    public void onlineCheckTask() {
        OnlineCheckTask task = (OnlineCheckTask) SpringUtil.getBean("OnlineCheckTask");
        task.doTask();
    }




}
