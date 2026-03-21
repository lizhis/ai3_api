package com.ai.servicebase.controller;


import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.servicebase.config.db.DataSourceConfig;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rw-switch")
public class RwSwitchController {

    private final DataSourceConfig dataSourceConfig;

    public RwSwitchController(DataSourceConfig config) {
        this.dataSourceConfig = config;
    }

    private final String secret = "123456";

    @GetMapping(value = "/enable", produces = "application/json;charset=UTF-8")
    public BaseVO enableRw(@Parameter(description = "key", required = true, example = "") @RequestParam("key") String key) {
        if(StringUtil.isEmpty(key) || !key.equals(secret)){
            return BaseVO.error();
        }
        dataSourceConfig.tryInitSlaveIfAbsent();
        return BaseVO.ok("service_base 读写分离-开启");
    }

    @GetMapping(value = "/disable", produces = "application/json;charset=UTF-8")
    public BaseVO disableRw(@Parameter(description = "key", required = true, example = "") @RequestParam("key") String key) {
        if(StringUtil.isEmpty(key) || !key.equals(secret)){
            return BaseVO.error();
        }
        dataSourceConfig.getRwSwitch().set(false);
        return BaseVO.ok("service_base 读写分离-关闭");
    }

    @GetMapping(value = "/status", produces = "application/json;charset=UTF-8")
    public BaseVO getStatus(@Parameter(description = "key", required = true, example = "") @RequestParam("key") String key) {
        if(StringUtil.isEmpty(key) || !key.equals(secret)){
            return BaseVO.error();
        }
        return BaseVO.ok("service_base 读写分离当前状态：" + (dataSourceConfig.getRwSwitch().get() ? "开启" : "关闭"));
    }


}
