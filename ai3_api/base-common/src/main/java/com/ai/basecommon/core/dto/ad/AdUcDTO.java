package com.ai.basecommon.core.dto.ad;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class AdUcDTO extends BaseDTO {



    private String idfaSum; //iOS 设备唯一标识 md5 转大写
    private String idfa1; //iOS 设备唯一标识
    private String caid;//iOS设备广协唯一标识


    private String imeiSum; //Android设备唯一标识的 md5 转大写
    private String imeiSum1; //Android设备唯一标识
    private String oaid; //oaid
    private String oaidSum; //oaid的md5
    private String oaidSum1; //oaid的md5 转大写
    private String androididSum; //安卓ID的md5 转大写
    private String androididSum1; //安卓ID的md5
    private String ip; //ip
    private Long uxTs; //点击时间
    private String callbackUrl; //转化回调

    private Long acid; //广告账户ID
    private Long gid; //广告组ID
    private Long aid; //广告计划ID
    private Long cid; //广告创意ID
    private Integer osId; //操作系统 0iOS 1安卓 100其他
    private String model1; //机型



}
