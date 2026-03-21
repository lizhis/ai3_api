package com.ai.basecommon.core.param.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Data
public class AdUcParam {

    @Schema(name = "idfaSum",title = "iOS 设备唯一标识 md5 转大写",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfaSum;

    @Schema(name = "idfa1",title = "iOS 设备唯一标识",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfa1;

    @Schema(name = "caid",title = "iOS设备广协唯一标识",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String caid;



    @Schema(name = "imeiSum",title = "Android设备唯一标识的 md5 转大写",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imeiSum;

    @Schema(name = "imeiSum1",title = "Android设备唯一标识",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imeiSum1;

    @Schema(name = "oaid",title = "oaid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaid;

    @Schema(name = "oaidSum",title = "oaid的md5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaidSum;

    @Schema(name = "oaidSum1",title = "oaid的md5 转大写",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaidSum1;

    @Schema(name = "androididSum",title = "安卓ID的md5 转大写",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androididSum;

    @Schema(name = "androididSum1",title = "安卓ID的md5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androididSum1;



    @Schema(name = "ip",title = "ip",requiredMode = Schema.RequiredMode.REQUIRED)
    private String ip;

    @Schema(name = "uxTs",title = "点击时间",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long uxTs;


    @Schema(name = "callbackUrl",title = "转化回调",requiredMode = Schema.RequiredMode.REQUIRED)
    private String callbackUrl;


    @Schema(name = "acid",title = "广告账户ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long acid;

    @Schema(name = "gid",title = "广告组ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long gid;

    @Schema(name = "aid",title = "广告计划ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long aid;

    @Schema(name = "cid",title = "广告创意ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long cid;

    @Schema(name = "osId",title = "操作系统 0iOS 1安卓 100其他",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer osId;

    @Schema(name = "model1",title = "机型",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String model1;



}
