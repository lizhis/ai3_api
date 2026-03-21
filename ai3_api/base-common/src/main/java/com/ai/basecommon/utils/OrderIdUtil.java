package com.ai.basecommon.utils;

import com.ai.basecommon.enums.OrderPreEnum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;


public class OrderIdUtil {


    // 起始时间（避免暴露真实时间）
    private static final long START_TIME = 1672531200000L;
    private static final int MAX_SEQUENCE = 99;
    private static final AtomicInteger sequence = new AtomicInteger(0);
    private static long lastTimestamp = -1;


    public static synchronized String getProjectOrderId(Long userId) {
        return generate(userId,OrderPreEnum.PROJECT.getCode());
    }

    public static synchronized String getShopOrderId(Long userId) {
        return generate(userId,OrderPreEnum.SHOP.getCode());
    }

    //获取充值订单号
    public static String getRechargeId(Long userId) {
        return generate(userId,OrderPreEnum.RECHARGE.getCode());
    }

    //获取提现订单号
    public static String getWithdrawId(Long userId) {
        return generate(userId,OrderPreEnum.WITHDRAW.getCode());
    }





    private static String generate(Long userId, int bizType) {
        long now = System.currentTimeMillis();
        long timePart = now - START_TIME;

        int currentSeq;

        if (now == lastTimestamp) {
            currentSeq = sequence.getAndIncrement();
            if (currentSeq > MAX_SEQUENCE) {
                while (now <= lastTimestamp) {
                    now = System.currentTimeMillis();
                }
                timePart = now - START_TIME;
                sequence.set(0);
                currentSeq = 0;
            }
        } else {
            sequence.set(0);
            currentSeq = 0;
        }
        lastTimestamp = now;

        // 业务编号限制在 0-99
        int safeBiz = bizType % 10;

        // 用户ID只取后4位，不暴露完整信息
        long userPart = userId % 10000;

        int randomPart = ThreadLocalRandom.current().nextInt(10000);

        // 拼接订单号
        return String.format("%01d%04d%02d%08d%04d",
                safeBiz,
                userPart,
                currentSeq,
                timePart % 100000000,
                randomPart
        );
    }






}
