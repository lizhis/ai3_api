package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.ad.AdVerifyParamDTO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.common.IpUtilX;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.service.IBaseAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdHandler {

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Value("${app.package}")
    private String appPackage;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private IBaseAdService baseAdService;


    //广告激活 巨量
    public BaseVO activeOceanengine() throws Exception{

        if(!profile.contains("prod")){
            return BaseVO.ok();
        }

        LogUtil.log("进入方法 巨量激活");

        String deviceId = userUtilX.getDvi();
        if(StringUtil.isEmpty(deviceId)){
            return BaseVO.ok();
        }

        String oaid = userUtilX.getOaid();
        String activeId = "";
        int platformType = CommonUtil.getDevicePlatform(deviceId);
        if(1 == platformType){
            activeId = oaid;
        }
        else if(2 == platformType){
            activeId = ipUtilX.getIp();
        }
        else{
            return BaseVO.ok();
        }

        AdVerifyParamDTO paramDTO = new AdVerifyParamDTO();
        paramDTO.setDeviceId(deviceId);
        paramDTO.setCheckId(activeId);
        paramDTO.setAppPackage(appPackage);

        String k = "ad_active_id_" + activeId;
        if(!redisUtilX.hasKey(k)){
            return BaseVO.ok();
        }

        LogUtil.log("调用广告服务 巨量激活：" + paramDTO);

        try{
            baseAdService.oceanengineActive(paramDTO);
            redisUtilX.delete(k);
        }catch (Exception e){
            LogUtil.log("调用广告服务 巨量激活 失败：" + e.getMessage());
        }
        return BaseVO.ok();
    }


    //广告激活
    public BaseVO active() throws Exception{
        if(!profile.contains("prod")){
            return BaseVO.ok();
        }
        String deviceId = userUtilX.getDvi();
        String activeId = "";
        int platformType = CommonUtil.getDevicePlatform(deviceId);
        if(1 == platformType){
            String oaid = userUtilX.getOaid();
            if(StringUtil.isEmpty(oaid)){
                return BaseVO.ok();
            }
            activeId = oaid;
        }
        else if(2 == platformType){
            activeId = ipUtilX.getIp();
        }
        else{
            return BaseVO.ok();
        }

        String k = "ad_active_id_" + activeId;

        LogUtil.log("app首次激活：" + k);

        if(redisUtilX.hasKey(k)){
            LogUtil.log("app首次激活 有标记 去广告服务：" + k);
            try{
                baseAdService.active(activeId);
                redisUtilX.delete(k);
            }catch (Exception e){
                LogUtil.log(e.getMessage());
            }
        }
        else{
            String m = EncryptUtil.md5(activeId).toLowerCase();
            k = "ad_active_id_" + m;
            LogUtil.log("app首次激活 有标记 去广告服务：" + k);
            if(redisUtilX.hasKey(k)){
                try{
                    baseAdService.active(m);
                    redisUtilX.delete(k);
                }catch (Exception e){
                    LogUtil.log(e.getMessage());
                }
            }
        }

        return BaseVO.ok();
    }




}
