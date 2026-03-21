package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.vo.user.BlessingShopCateVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BlessingShopCateMapper {

    List<BlessingShopCateVO> select();

}
