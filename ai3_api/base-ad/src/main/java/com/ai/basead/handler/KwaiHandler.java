package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basead.mapstruct.MapMapper;
import com.ai.basead.producer.AdActiveProducer;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.AdKwaiDTO;
import com.ai.basecommon.core.dto.msg.AdActiveMsgDTO;
import com.ai.basecommon.core.param.ad.AdKwaiParam;
import com.ai.basecommon.enums.UserChannelEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KwaiHandler {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private MapMapper mapMapper;

    @Autowired
    private AdActiveProducer adActiveProducer;


    public void listen(AdKwaiParam param) throws Exception{

        //LogUtil.log("快手广告 收到监听事件：" + param.toString());

        String id = null;
        if(1 == param.getOs()){
            //LogUtil.log("快手广告 收到IOS监听事件：" + param.toString());
            if(!StringUtil.isEmpty(param.getIp())){
                id = param.getIp();
            }
        }
        if(0 == param.getOs()){
            if(!StringUtil.isEmpty(param.getOaid())){
                id = param.getOaid().toLowerCase();
            }
        }

        if(null == id){
            //LogUtil.log("快手广告 没有设备ID 不处理");
            return;
        }


        AdKwaiDTO dto = mapMapper.adKwaiParamToDTO(param);

        id = id.toLowerCase();


        String key = RedisKey.ad_kwai_id_ + id;
        redisUtilX.setObj(key,dto,3600*48);

        String activeKey = RedisKey.ad_kwai_active_id_ + id;
        redisUtilX.setObj(activeKey,dto,3600*48);

        AdActiveMsgDTO msgDTO = new AdActiveMsgDTO();
        msgDTO.setActiveId(id);
        msgDTO.setChannel(UserChannelEnum.TENCENT2.getCode());
        adActiveProducer.produce(msgDTO);

    }






}
