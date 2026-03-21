package com.ai.basecommon.core.dto.ad;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdXingtuDTO extends BaseDTO {

    private Integer os; //系统 0–Android 1–iOS 2–WP 3-Others
    private String ts; //时间戳
    private String ua; //ua
    private String ip; //ip
    private String ipv4; //ipv4
    private String model; //手机型号
    private String demandId; //计划id
    private String itemId; //视频id
    private String callbackParam; //回调参数
    private String callback; //回调地址
    private String imeiMd5; //imeiMd5
    private String oaidMd5; //oaidMd5
    private String androidIdMd5; //androidIdMd5



}
