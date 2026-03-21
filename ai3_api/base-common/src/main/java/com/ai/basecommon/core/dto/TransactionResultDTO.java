package com.ai.basecommon.core.dto;

import lombok.Data;

@Data
public class TransactionResultDTO {
    private boolean success;
    private String message;

    public TransactionResultDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
