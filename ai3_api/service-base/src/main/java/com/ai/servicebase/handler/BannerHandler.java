package com.ai.servicebase.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.BannerPO;
import com.ai.basecommon.core.vo.base.BannerVO;
import com.ai.servicebase.commom.RedisUtilX;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.BannerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BannerHandler {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @ReadOnly
    public List<BannerVO> select() throws Exception{
        List<BannerVO> list = null;
        String key = RedisKey.banner_list;
        if(redisUtilX.hasKey(key)){
            list = redisUtilX.getObjList(key,BannerVO.class);
        }
        if(null == list){
            list = bannerMapper.select();
            redisUtilX.setObj(key,list,3600);
        }
        return list;
    }





}
