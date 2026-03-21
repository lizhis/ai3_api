package com.ai.basewebsocket.task;
import com.ai.basecommon.utils.LogUtil;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description
 * @Author
 */
@Component("TestTask")
public class TestTask{



    public void doTask() {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        LogUtil.log("定时器："+df.format(new Date()));



    }




}