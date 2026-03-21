package com.ai.basecommon.core.vo.base;

import com.ai.basecommon.core.po.base.ActivityPO;
import com.ai.basecommon.core.po.base.BannerPO;
import com.ai.basecommon.core.po.base.NewbieChannelPO;
import com.ai.basecommon.core.vo.shop.ShopVO;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author
 */
@Data
public class HomeVO {

    private List<BannerVO> bannerList;

    private List<ActivityVO> activityList;

    //private List<NewsVO> newsList;

    private List<NewbieChannelVO> newbieChannelList;

    private SysConfigVO sysConfigVO;

    private AppVersionVO appVersionVO;

}
