package com.ai.basecommon.core.dto.map;

import lombok.Data;

/**
 * @Description
 * @Author
 */
@Data
public class MapAddressDTO {

    private String nation;//国家
    private String province;//省份
    private String city;//城市
    private String district;//辖区
    private String street;//街道
    private String streetNumber;//门牌
    private Integer cityCode;//城市代码
    private Integer adcode;//行政区划代码

}
