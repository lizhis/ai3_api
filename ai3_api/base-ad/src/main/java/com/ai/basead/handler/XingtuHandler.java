package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.AdXingtuDTO;
import com.ai.basecommon.core.param.ad.AdXingtuParam;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XingtuHandler {

    @Autowired
    private RedisUtilX redisUtilX;


    public void listen(AdXingtuParam param) throws Exception{

        //LogUtil.log("星图广告  收到监听事件：" + param);

        String id = null;
        if(1 == param.getOs()){
            id = param.getIp();
        }
        if(0 == param.getOs()){
            if(!StringUtil.isEmpty(param.getOaidMd5())){
                id = param.getOaidMd5().toLowerCase();
            }
            else{
                id = param.getIp();
            }
        }

        if(null == id){
            LogUtil.log("星图广告 没有设备ID 不处理");
            return;
        }

        AdXingtuDTO dto = new AdXingtuDTO();
        dto.setOs(param.getOs());
        dto.setTs(param.getTs());
        dto.setUa(param.getUa());
        dto.setIp(param.getIp());
        dto.setIpv4(param.getIpv4());
        dto.setModel(param.getModel());
        dto.setDemandId(param.getDemandId());
        dto.setItemId(param.getItemId());
        dto.setCallbackParam(param.getCallbackParam());
        dto.setCallback(param.getCallback());
        dto.setImeiMd5(param.getImeiMd5());
        dto.setOaidMd5(param.getOaidMd5());
        dto.setAndroidIdMd5(param.getAndroidIdMd5());

        String key = RedisKey.ad_xingtu_id_ + id.toLowerCase();
        redisUtilX.setObj(key,dto,3600*48);

    }






}
