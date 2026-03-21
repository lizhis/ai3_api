package com.ai.basecommon.core.vo.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Data
public class SysConfSeasonVO {

    private BigDecimal seasonPrice;
    private BigDecimal yearPrice;

    private Long gift1;
    private Long gift2;
    private Long gift3;

    private String gift1Name;
    private String gift2Name;
    private String gift3Name;

    private String gift1Image;
    private String gift2Image;
    private String gift3Image;

}
