package com.ai.basecommon.core.dto.ad;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdBaiduDTO extends BaseDTO {

    private String imeiMd5; //标准32位md5
    private String androidIdMd5; //标准32位md5
    private String idfa; //IOS设备标识：原值
    private String oaidMd5; //标准32位md5
    private String oaid; //原值
    private String caid; //caid
    private String ip; //ip
    private String ua; //UserAgent
    private String osVersion; //操作系统版本
    private String osType; //操作系统 安卓：2；iOS：1 ，也可能是NULL
    private Long ts; //点击时间

    private String userId; //账户ID
    private String pid; //计划ID
    private String uid; //单元ID
    private String aid; //创意ID

    private String clickId; //点击或曝光唯一标识
    private String callbackUrl; //转化回调

}
