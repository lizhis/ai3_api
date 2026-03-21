package com.ai.servicebase.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.ActivityPO;
import com.ai.basecommon.core.vo.base.ActivityVO;
import com.ai.basecommon.core.vo.base.BannerVO;
import com.ai.servicebase.commom.RedisUtilX;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityHandler {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @ReadOnly
    public List<ActivityVO> select() throws Exception{
        List<ActivityVO> list = null;
        String key = RedisKey.activity_list;
        if(redisUtilX.hasKey(key)){
            list = redisUtilX.getObjList(key,ActivityVO.class);
        }
        if(null == list){
            list = activityMapper.select();
            redisUtilX.setObj(key,list,3600);
        }
        return list;
    }





}
