package com.ai.basecommon.core.param;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TestParam {


    @Schema(name = "name", title = "姓名")
    private String name;



}
