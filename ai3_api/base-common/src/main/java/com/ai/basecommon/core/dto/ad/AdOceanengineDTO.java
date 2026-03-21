package com.ai.basecommon.core.dto.ad;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

@Data
public class AdOceanengineDTO extends BaseDTO {

    private Long promotionId; //广告ID
    private String promotionName; //广告名称
    private Long projectId; //项目ID
    private String projectName; //项目名称
    private Long advertiserId; //广告主id
    private Integer csite; //投放位置
    private Long convertId; //转化ID
    private String requestId; //请求下发的id
    private String trackId; //请求下发的id
    private String idfa; //idfa
    private String androidId; //安卓ID的md5
    private String oaid; //oaid
    private Integer os; //平台 0安卓 1iOS 3其它
    private String ip; //ip
    private Long time; //点击时间
    private String callbackParam; //回调参数
    private String callbackUrl; //转化回调
    private String model; //手机型号


}
