package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.param.blessingshop.BlessingShopParam;
import com.ai.basecommon.core.po.user.BlessingShopPO;
import com.ai.basecommon.core.vo.user.BlessingShopVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BlessingShopMapper {

    //查询商品
    List<BlessingShopVO> select(BlessingShopParam param);

    //查询详情
    BlessingShopPO findById(@Param("id") Long id);

    //查询详情
    BlessingShopVO detail(@Param("id") Long id);

}
