package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basead.mapstruct.MapMapper;
import com.ai.basead.producer.AdActiveProducer;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.AdTencentDTO;
import com.ai.basecommon.core.dto.msg.AdActiveMsgDTO;
import com.ai.basecommon.core.param.ad.AdTencentParam;
import com.ai.basecommon.enums.UserChannelEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TencentHandler {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private AdActiveProducer adActiveProducer;

    @Autowired
    private MapMapper mapMapper;


    public void listen(AdTencentParam param) throws Exception{
        LogUtil.log("腾讯广告 收到监听事件：" + param.toString());
        String id = null;
        if("android".equals(param.getDeviceOsType()) || "harmony".equals(param.getDeviceOsType())){
            if(!StringUtil.isEmpty(param.getHashOaid())){
                id = param.getHashOaid();
            }
        }
        else{
            if(!StringUtil.isEmpty(param.getIp())){
                id = param.getIp();
            }
            else if(!StringUtil.isEmpty(param.getIpv6())){
                id = param.getIpv6();
            }
        }

        if(null == id){
            LogUtil.log("没有设备ID 不处理");
            return;
        }

        //LogUtil.log("腾讯广告 收到监听事件 ID是：" + id);

        AdTencentDTO dto = mapMapper.adTencentParamToDTO(param);

        id = id.toLowerCase();

        String key = RedisKey.ad_tencent_id_ + id;
        redisUtilX.setObj(key,dto,3600*48);

        String activeKey = RedisKey.ad_tencent_active_id_ + id;
        redisUtilX.setObj(activeKey,dto,3600*48);

        AdActiveMsgDTO msgDTO = new AdActiveMsgDTO();
        msgDTO.setActiveId(id);
        msgDTO.setChannel(UserChannelEnum.TENCENT.getCode());
        adActiveProducer.produce(msgDTO);

    }

}
