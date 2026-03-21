package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.AdBianxianmaoDTO;
import com.ai.basecommon.core.param.ad.AdBianxianmaoParam;
import com.ai.basecommon.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BianxianmaoHandler {

    @Autowired
    private RedisUtilX redisUtilX;


    public void listen(AdBianxianmaoParam param) throws Exception{
        //LogUtil.log("变现猫广告 收到监听事件：" + param.toString());

        String id = null;
        if("1".equals(param.getOs())){
            if(!StringUtil.isEmpty(param.getIdfa())){
                id = param.getIdfa().toLowerCase();
            }
        }
        if("0".equals(param.getOs())){
            if(!StringUtil.isEmpty(param.getOaid())){
                id = param.getOaid().toLowerCase();
            }
        }
        if(null == id && !StringUtil.isEmpty(param.getIp())){
            id = param.getIp();
        }

        if(null == id){
            //LogUtil.log("变现猫 没有设备ID 不处理");
            return;
        }

        AdBianxianmaoDTO dto = new AdBianxianmaoDTO();
        dto.setRequestId(param.getRequestId());
        dto.setPlanId(param.getPlanId());
        dto.setIp(param.getIp());
        dto.setUa(param.getUa());
        dto.setOs(param.getOs());
        dto.setImei(param.getImei());
        dto.setImeiMd5(param.getImeiMd5());
        dto.setAndroidId(param.getAndroidId());
        dto.setAndroidIdMd5(param.getAndroidIdMd5());
        dto.setOaid(param.getOaid());
        dto.setOaidMd5(param.getOaidMd5());
        dto.setIdfa(param.getIdfa());
        dto.setIdfaMd5(param.getIdfaMd5());
        dto.setGaid(param.getGaid());
        dto.setGaidMd5(param.getGaidMd5());
        dto.setMediumLogicId(param.getMediumLogicId());
        dto.setTime(param.getTime());
        dto.setDeviceId(param.getDeviceId());
        dto.setCallback(param.getCallback());


        String key = RedisKey.ad_bianxianmao_id_ + id.toLowerCase();
        redisUtilX.setObj(key,dto,3600*48);


    }






}
