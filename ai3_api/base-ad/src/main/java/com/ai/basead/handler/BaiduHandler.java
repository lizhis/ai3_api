package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basead.producer.AdActiveProducer;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.AdBaiduDTO;
import com.ai.basecommon.core.dto.msg.AdActiveMsgDTO;
import com.ai.basecommon.core.param.ad.AdBaiduParam;
import com.ai.basecommon.enums.UserChannelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaiduHandler {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private AdActiveProducer adActiveProducer;

    public void listen(AdBaiduParam param) throws Exception{
        //LogUtil.log("百度广告 收到监听事件：" + param.toString());

        String id = null;
        if("2".equals(param.getOsType())){
            id = param.getOaid();
        }
        else{
            id = param.getIp();
        }

        if(null == id){
            //LogUtil.log("没有设备ID 不处理");
            return;
        }

        AdBaiduDTO dto = new AdBaiduDTO();
        dto.setImeiMd5(param.getImeiMd5());
        dto.setAndroidIdMd5(param.getAndroidIdMd5());
        dto.setIdfa(param.getIdfa());
        dto.setOaidMd5(param.getOaidMd5());
        dto.setOaid(param.getOaid());
        dto.setCaid(param.getCaid());
        dto.setIp(param.getIp());
        dto.setUa(param.getUa());
        dto.setOsVersion(param.getOsVersion());
        dto.setOsType(param.getOsType());
        dto.setTs(param.getTs());

        dto.setUserId(param.getUserId());
        dto.setPid(param.getPid());
        dto.setUid(param.getUid());
        dto.setAid(param.getAid());
        dto.setClickId(param.getClickId());
        dto.setCallbackUrl(param.getCallbackUrl());

        String key = RedisKey.ad_baidu_id_ + id;
        redisUtilX.setObj(key,dto,3600*48);

        String activeKey = RedisKey.ad_baidu_active_id_ + id;
        redisUtilX.setObj(activeKey,dto,3600*48);

        AdActiveMsgDTO msgDTO = new AdActiveMsgDTO();
        msgDTO.setActiveId(id);
        msgDTO.setChannel(UserChannelEnum.BAIDU.getCode());
        adActiveProducer.produce(msgDTO);

    }






}
