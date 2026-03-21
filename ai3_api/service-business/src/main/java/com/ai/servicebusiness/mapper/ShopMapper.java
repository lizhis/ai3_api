package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.param.shop.ShopParam;
import com.ai.basecommon.core.po.shop.ShopPO;
import com.ai.basecommon.core.vo.shop.ShopDetailVO;
import com.ai.basecommon.core.vo.shop.ShopVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ShopMapper {

    //查询商品
    List<ShopVO> select(ShopParam param);
    List<ShopVO> selectAll();

    //查询推荐商品
    List<ShopVO> selectRecommend();

    //查询免费礼品
    List<ShopVO> selectGift();

    ShopPO findById(@Param("id") Long id);

    //查询详情
    ShopDetailVO findDetailById(@Param("id") Long id);


    ShopVO findVO(@Param("id") Long id);

}
