package com.ai.basecommon.core.dto.map;

import lombok.Data;

/**
 * @Description
 * @Author
 *
 */
@Data
public class IpAddressDTO {

    private String ip;//IP
    private String country;//国家
    private String province;//省份
    private String city;//城市
    private String county;//区
    private String isp;//网络
    private String addressDetail;//详细地址
    private String address;//简要地址 用户端显示 省市
}
