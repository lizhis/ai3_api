package com.ai.basecommon.core.dto.ad;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdBianxianmaoDTO extends BaseDTO {

    private String requestId; //请求ID
    private String planId; //计划ID
    private String ip; //受众用户的IP地址
    private String ua; //受众用户的UserAgent，进行urlencode编码
    private String os; //0、Android 1、iOS
    private String imei; //IMEI
    private String imeiMd5; //md5sum(IMEI)
    private String androidId; //androidid
    private String androidIdMd5; //md5sum(androidid)
    private String oaid; //Android的oaid
    private String oaidMd5; //Android的md5sum(oaid)
    private String idfa; //idfa
    private String idfaMd5; //md5sum(idfa)
    private String gaid; //gaid
    private String gaidMd5; //md5sum(gaid)
    private String mediumLogicId; //流量平台逻辑ID，如：互动广告的bxm_id
    private String time; //UTC时间戳，自1970年开始的毫秒数
    private String deviceId; //设备id，如果是 Android 则填写的是 gaid、如果是 iOS 则填写的是 idfa
    private String callback; //效果回传地址



}
