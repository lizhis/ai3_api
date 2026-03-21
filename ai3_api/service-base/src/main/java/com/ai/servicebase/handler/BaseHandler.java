package com.ai.servicebase.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.param.PlatformTypeParam;
import com.ai.basecommon.core.po.base.SysConfigPO;
import com.ai.basecommon.core.vo.base.AppVersionVO;
import com.ai.servicebase.commom.RedisUtilX;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.AppVersionMapper;
import com.ai.servicebase.mapper.SysConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseHandler {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private AppVersionMapper appVersionMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;



    //查询app版本号
    @ReadOnly
    public AppVersionVO appVersion(PlatformTypeParam param) throws Exception{
        if(null == param){
            param = new PlatformTypeParam();
        }
        if(null == param.getPlatform()){
            param.setPlatform(1);
        }
        String key = RedisKey.app_version_last_ + param.getPlatform();
        AppVersionVO vo = redisUtilX.getObj(key,AppVersionVO.class);
        if(null == vo){
            vo = appVersionMapper.findLastVersion(param.getPlatform());
            if(null != vo){
                redisUtilX.setObj(key,vo,3600);
            }
        }
        return vo;
    }


    @ReadOnly
    public SysConfigPO loadConf() {
        String key = RedisKey.sys_config;
        SysConfigPO configPO = redisUtilX.getObj(key,SysConfigPO.class);
        if(null == configPO){
            configPO = sysConfigMapper.find();
            redisUtilX.setObj(key,configPO,1800);
        }
        return configPO;
    }



}
