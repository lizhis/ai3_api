package com.ai.servicebusiness.mapper;

import com.ai.basecommon.core.vo.shop.ShopCateVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ShopCateMapper {

    List<ShopCateVO> select();

}
