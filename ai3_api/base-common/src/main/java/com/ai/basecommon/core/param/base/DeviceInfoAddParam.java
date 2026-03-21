package com.ai.basecommon.core.param.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description
 * @Author
 *
 */
@Data
public class DeviceInfoAddParam {


    @Schema(name = "deviceId",title = "设备ID",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String deviceId;

    @Schema(name = "oaid",title = "oaid",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String oaid;

    @Schema(name = "idfa",title = "idfa",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idfa;

    @Schema(name = "brand",title = "品牌",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String brand;

    @Schema(name = "model",title = "型号",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String model;

    @Schema(name = "version",title = "版本号",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String version;

    @Schema(name = "browserName",title = "浏览器名称",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String browserName;

    @Schema(name = "computerName",title = "计算机名称",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String computerName;

    @Schema(name = "hostName",title = "操作系统名称",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String hostName;
/*

    @Schema(name = "lng",title = "经度",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lng;

    @Schema(name = "lat",title = "纬度",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lat;
*/

}
