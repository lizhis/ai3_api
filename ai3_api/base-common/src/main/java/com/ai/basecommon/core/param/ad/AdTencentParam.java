package com.ai.basecommon.core.param.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class AdTencentParam {

    @Schema(name = "clickId",title = "点击ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clickId;

    @Schema(name = "clickTime",title = "点击时间",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long clickTime;

    @Schema(name = "adgroupId",title = "广告组ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long adgroupId;

    @Schema(name = "adPlatformType",title = "广告投放平台",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer adPlatformType;

    @Schema(name = "adType",title = "广告类型",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer adType;

    @Schema(name = "accountId",title = "广告主ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long accountId;

    @Schema(name = "agencyId",title = "代理商ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long agencyId;

    @Schema(name = "clickSkuId",title = "点击sku",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String clickSkuId;

    @Schema(name = "billingEvent",title = "计费类型",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer billingEvent;

    @Schema(name = "deviceOsType",title = "设备类型 ios, android",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String deviceOsType;

    @Schema(name = "processTime",title = "请求时间",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long processTime;

    @Schema(name = "promotedObjectId",title = "应用ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String promotedObjectId;

    @Schema(name = "requestId",title = "请求ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String requestId;

    @Schema(name = "callback",title = "回传地址",requiredMode = Schema.RequiredMode.REQUIRED)
    private String callback;

    @Schema(name = "adgroupName",title = "广告组名称",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String adgroupName;

    @Schema(name = "hashAndroidId",title = "安卓ID做md5加密后的小写",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String hashAndroidId;

    @Schema(name = "hashOaid",title = "安卓Q以及更高版本的设备号，64位及以下原值，后做md5加密",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String hashOaid;

    @Schema(name = "muid",title = "设备ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private String muid;

    @Schema(name = "ip",title = "ip",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    @Schema(name = "ipv6",title = "ip",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ipv6;


}
