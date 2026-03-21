package com.ai.basecommon.core.param.ad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class AdOceanengineParam {

    @Schema(name = "promotionId",title = "广告ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long promotionId;

    @Schema(name = "promotionName",title = "广告名称",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String promotionName;

    @Schema(name = "projectId",title = "项目ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long projectId;

    @Schema(name = "projectName",title = "项目名称",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String projectName;

    @Schema(name = "advertiserId",title = "广告主id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long advertiserId;

    @Schema(name = "csite",title = "投放位置",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer csite;

    @Schema(name = "convertId",title = "转化ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long convertId;

    @Schema(name = "requestId",title = "请求下发的id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String requestId;

    @Schema(name = "trackId",title = "请求下发的id",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String trackId;

    @Schema(name = "idfa",title = "idfa",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfa;

    @Schema(name = "androidId",title = "安卓ID的md5",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String androidId;

    @Schema(name = "oaid",title = "oaid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaid;

    @Schema(name = "os",title = "平台 0安卓 1iOS 3其它",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer os;

    @Schema(name = "ip",title = "ip",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ip;

    @Schema(name = "time",title = "点击广告的时间戳",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long time;

    @Schema(name = "callbackParam",title = "回调参数",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String callbackParam;

    @Schema(name = "callbackUrl",title = "转化回调",requiredMode = Schema.RequiredMode.REQUIRED)
    private String callbackUrl;

    @Schema(name = "model",title = "手机型号",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String model;

    @Schema(name = "caid",title = "caid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String caid;

}
