package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.param.user.RechargeRecordParam;
import com.ai.basecommon.core.po.user.RechargePO;
import com.ai.basecommon.core.vo.user.RechargeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RechargeMapper {


    List<RechargeVO> select(RechargeRecordParam param);

    int findAlipayNumToday(@Param("userId") Long userId,@Param("ymd") Integer ymd);

    int countAlipayByChannelToday(@Param("channelNo") String channelNo,@Param("ymd") Integer ymd);

    int countAlipayScanByChannelToday(@Param("channelNo") String channelNo,@Param("ymd") Integer ymd);

    int insert(RechargePO po);


}
