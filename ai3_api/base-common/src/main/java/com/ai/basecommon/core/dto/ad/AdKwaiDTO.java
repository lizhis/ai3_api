package com.ai.basecommon.core.dto.ad;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdKwaiDTO extends BaseDTO {

    private String accountId; //广告账户ID
    private String missionId; //任务ID
    private String orderId; //订单ID
    private String cid; //广告创意ID
    private String did; //广告计划ID
    private String imei2; //对15位数字的 IMEI （比如860576038225452）进行 MD5
    private String oaid; //Android设备标识
    private String oaid2; //Android设备标识计算MD5
    private String idfa2; //iOS下的idfa计算MD5
    private String androidId2; //对 ANDROIDID（举例:8f6581815307be28） 进行 MD5
    private Long ts; //时间戳
    private String ip; //ip
    private Integer os; //OS系统 1-iOS，0-安卓
    private String model; //手机型号
    private String callback; //回调信息




}
