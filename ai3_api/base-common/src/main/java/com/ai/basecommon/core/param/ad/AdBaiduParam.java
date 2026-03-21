package com.ai.basecommon.core.param.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdBaiduParam {

    @Schema(name = "imeiMd5",title = "标准32位md5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imeiMd5;

    @Schema(name = "androidIdMd5",title = "标准32位md5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androidIdMd5;

    @Schema(name = "idfa",title = "IOS设备标识：原值",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfa;

    @Schema(name = "oaidMd5",title = "标准32位md5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaidMd5;

    @Schema(name = "oaid",title = "原值",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaid;

    @Schema(name = "caid",title = "caid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String caid;

    @Schema(name = "ip",title = "ip",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    @Schema(name = "ua",title = "UserAgent",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ua;


    @Schema(name = "osVersion",title = "操作系统版本",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String osVersion;

    @Schema(name = "osType",title = "操作系统 安卓：2；iOS：1 也可能是NULL",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String osType;


    @Schema(name = "ts",title = "点击时间",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ts;

    @Schema(name = "userId",title = "账户ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String userId;

    @Schema(name = "pid",title = "计划ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String pid;

    @Schema(name = "uid",title = "单元ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String uid;

    @Schema(name = "aid",title = "创意ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String aid;


    @Schema(name = "clickId",title = "点击或曝光唯一标识",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clickId;

    @Schema(name = "callbackUrl",title = "转化回调",requiredMode = Schema.RequiredMode.REQUIRED)
    private String callbackUrl;




}
