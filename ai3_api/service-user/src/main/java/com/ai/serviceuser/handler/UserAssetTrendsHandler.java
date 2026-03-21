package com.ai.serviceuser.handler;

import com.ai.basecommon.core.param.user.UserAssetTrendsParam;
import com.ai.basecommon.core.vo.user.UserAssetTrendsVO;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.UserAssetTrendsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAssetTrendsHandler {

    @Autowired
    private UserAssetTrendsMapper userAssetTrendsMapper;

    @Autowired
    private UserUtilX userUtilX;



    //资产动态
    @ReadOnly
    public List<UserAssetTrendsVO> select(UserAssetTrendsParam param) throws Exception{
        if(null == param){
            param = new UserAssetTrendsParam();
        }
        param.setUserId(userUtilX.getUserId());
        if(null == param.getType() || param.getType() < 0){
            param.setType(0);
        }
        Long time = System.currentTimeMillis() - 86400000L * 15;
        param.setTime(time);
        return userAssetTrendsMapper.select(param);
    }




}
