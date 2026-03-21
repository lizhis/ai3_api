package com.ai.servicebase.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.vo.base.ActivityVO;
import com.ai.basecommon.core.vo.base.NewbieChannelVO;
import com.ai.servicebase.commom.RedisUtilX;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.NewbieChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewbieChannelHandler {

    @Autowired
    private NewbieChannelMapper newbieChannelMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @ReadOnly
    public List<NewbieChannelVO> select() throws Exception{
        List<NewbieChannelVO> list = null;
        String key = RedisKey.newbie_channel_list;
        if(redisUtilX.hasKey(key)){
            list = redisUtilX.getObjList(key,NewbieChannelVO.class);
        }
        if(null == list){
            list = newbieChannelMapper.select();
            redisUtilX.setObj(key,list,3600);
        }
        return list;
    }





}
