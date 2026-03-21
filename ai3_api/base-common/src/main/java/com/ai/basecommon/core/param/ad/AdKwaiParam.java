package com.ai.basecommon.core.param.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class AdKwaiParam {

    @Schema(name = "accountId",title = "广告账户ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String accountId;

    @Schema(name = "missionId",title = "任务ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String missionId;

    @Schema(name = "orderId",title = "订单ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String orderId;

    @Schema(name = "cid",title = "创意ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String cid;

    @Schema(name = "did",title = "计划ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String did;

    @Schema(name = "imei2",title = "对15位数字的 IMEI （比如860576038225452）进行 MD5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imei2;

    @Schema(name = "oaid",title = "Android设备标识",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaid;

    @Schema(name = "oaid2",title = "Android设备标识计算MD5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaid2;

    @Schema(name = "idfa2",title = "iOS下的idfa计算MD5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfa2;

    @Schema(name = "androidId2",title = "对 ANDROIDID（举例:8f6581815307be28） 进行 MD5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androidId2;

    @Schema(name = "ts",title = "时间戳",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long ts;

    @Schema(name = "ip",title = "ip",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    @Schema(name = "os",title = "OS系统 1-iOS，0-安卓",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer os;

    @Schema(name = "model",title = "手机型号",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String model;

    @Schema(name = "callback",title = "回调信息",requiredMode = Schema.RequiredMode.REQUIRED)
    private String callback;



}
