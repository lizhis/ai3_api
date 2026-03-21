package com.ai.basewebsocket.common;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.ClientRejectPO;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.basewebsocket.mapper.ClientRejectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description
 * @Author
 */
@Component
public class UserUtilX {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private ClientRejectMapper clientRejectMapper;


    /**
     * 检查dvi
     * @param dvi
     * @return true为正常 false为封禁
     * @throws Exception
     */
    public boolean checkDvi(String dvi){
        if(StringUtil.isEmpty(dvi)){
            return true;
        }
        String key = RedisKey.client_reject_list;
        List<ClientRejectPO> list = null;
        try{
            list = redisUtilX.getObjList(key,ClientRejectPO.class);
        }catch (Exception e){
            LogUtil.log(e.getMessage());
        }
        if(null == list || list.isEmpty()){
            list = clientRejectMapper.selectAll();
            if(null != list && !list.isEmpty()){
                redisUtilX.setObj(key,list,600);
            }
        }
        if(null == list || list.isEmpty()){
            return true;
        }
        ClientRejectPO po = list.stream().filter(v-> !StringUtil.isEmpty(v.getDeviceId()) && v.getDeviceId().equals(dvi)).findFirst().orElse(null);
        return null == po;
    }



}
