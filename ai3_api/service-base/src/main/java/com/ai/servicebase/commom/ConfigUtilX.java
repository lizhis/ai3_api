package com.ai.servicebase.commom;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.core.po.base.SysConfigPO;
import com.ai.servicebase.mapper.SysConfApiMapper;
import com.ai.servicebase.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author
 */
@Component
public class ConfigUtilX {


    @Autowired
    private RedisUtilX redisUtilX;


    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private SysConfApiMapper sysConfApiMapper;




    public SysConfigPO loadConf() {
        String key = RedisKey.sys_config;
        SysConfigPO configPO = redisUtilX.getObj(key,SysConfigPO.class);
        if(null != configPO){
            return configPO;
        }
        configPO = sysConfigMapper.find();
        if(null == configPO){
            return null;
        }
        redisUtilX.setObj(key,configPO,600);
        return configPO;
    }


    public SysConfApiPO loadConfApi(){
        String key = RedisKey.conf_api;
        SysConfApiPO confPO = redisUtilX.getObj(key,SysConfApiPO.class);
        if(null != confPO){
            return confPO;
        }
        confPO = sysConfApiMapper.find();
        if(null == confPO){
            return null;
        }
        redisUtilX.setObj(key,confPO,600);
        return confPO;
    }





}
