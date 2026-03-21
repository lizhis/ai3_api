package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.param.user.UserAssetTrendsParam;
import com.ai.basecommon.core.vo.user.UserAssetTrendsVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserAssetTrendsMapper {


    List<UserAssetTrendsVO> select(UserAssetTrendsParam param);



}
