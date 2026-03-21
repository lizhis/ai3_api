package com.ai.basecommon.core.param.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdBianxianmaoParam {

    @Schema(name = "requestId",title = "请求ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String requestId;

    @Schema(name = "planId",title = "计划ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String planId;

    @Schema(name = "ip",title = "受众用户的IP地址",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    @Schema(name = "ua",title = "受众用户的UserAgent，进行urlencode编码",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ua;

    @Schema(name = "os",title = "0、Android 1、iOS",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String os;

    @Schema(name = "imei",title = "IMEI",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imei;

    @Schema(name = "imeiMd5",title = "md5sum(IMEI)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imeiMd5;

    @Schema(name = "androidId",title = "androidid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androidId;

    @Schema(name = "androidIdMd5",title = "md5sum(androidid)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androidIdMd5;

    @Schema(name = "oaid",title = "Android的oaid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaid;

    @Schema(name = "oaidMd5",title = "Android的md5sum(oaid)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaidMd5;

    @Schema(name = "idfa",title = "idfa",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfa;

    @Schema(name = "idfaMd5",title = "md5sum(idfa)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfaMd5;

    @Schema(name = "gaid",title = "gaid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gaid;

    @Schema(name = "gaidMd5",title = "md5sum(gaid)",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String gaidMd5;

    @Schema(name = "mediumLogicId",title = "流量平台逻辑ID，如：互动广告的bxm_id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String mediumLogicId;

    @Schema(name = "time",title = "UTC时间戳，自1970年开始的毫秒数",requiredMode = Schema.RequiredMode.REQUIRED)
    private String time;

    @Schema(name = "deviceId",title = "设备id，如果是 Android 则填写的是 gaid、如果是 iOS 则填写的是 idfa。",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String deviceId;

    @Schema(name = "callback",title = "效果回传地址，URL进行urlencode编码",requiredMode = Schema.RequiredMode.REQUIRED)
    private String callback;



}
