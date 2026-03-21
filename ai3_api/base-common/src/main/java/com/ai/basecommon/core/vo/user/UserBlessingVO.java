package com.ai.basecommon.core.vo.user;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserBlessingVO {
    private List<Integer> cardTypes;
    private BigDecimal amount;
    private Integer inviteCardNum;
}
