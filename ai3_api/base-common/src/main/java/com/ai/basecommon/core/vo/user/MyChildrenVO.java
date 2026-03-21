package com.ai.basecommon.core.vo.user;

import com.ai.basecommon.core.dto.BaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description
 * @Author
 */
@Data
public class MyChildrenVO extends BaseDTO {

    private BigDecimal rate1;
    private BigDecimal rate2;
    private BigDecimal rate3;

    private BigDecimal amount;
    private List<MyChildrenUserVO> list;

}
