package com.ai.basecommon.core.param.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdXingtuParam {

    @Schema(name = "os",title = "系统 0–Android 1–iOS 2–WP 3-Others ",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer os;

    @Schema(name = "ts",title = "时间戳",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ts;

    @Schema(name = "ua",title = "ua",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ua;

    @Schema(name = "ip",title = "ip",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    @Schema(name = "ipv4",title = "ipv4",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ipv4;

    @Schema(name = "model",title = "手机型号",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String model;

    @Schema(name = "demandId",title = "计划id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String demandId;

    @Schema(name = "itemId",title = "视频id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String itemId;

    @Schema(name = "callbackParam",title = "回调参数",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String callbackParam;

    @Schema(name = "callback",title = "回调地址",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String callback;

    @Schema(name = "imeiMd5",title = "imeiMd5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imeiMd5;

    @Schema(name = "oaidMd5",title = "oaidMd5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaidMd5;

    @Schema(name = "androidIdMd5",title = "androidIdMd5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androidIdMd5;




}
