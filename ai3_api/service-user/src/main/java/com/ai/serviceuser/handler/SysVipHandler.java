package com.ai.serviceuser.handler;

import com.ai.basecommon.core.po.base.SysVipPO;
import com.ai.basecommon.core.vo.user.SysVipVO;
import com.ai.basecommon.utils.DozerUtil;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.SysVipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SysVipHandler {

    @Autowired
    private SysVipMapper sysVipMapper;


    @ReadOnly
    public List<SysVipVO> vipList() throws Exception{
        List<SysVipPO> pos = sysVipMapper.select();
        if(null == pos){
            return null;
        }
        List<SysVipVO> list = DozerUtil.maps(pos, SysVipVO.class);
        return list;
    }

    @ReadOnly
    public SysVipPO findByLevel(Integer level) throws Exception{
        if(null == level || level < 1){
            return null;
        }
        return sysVipMapper.findByLevel(level);
    }


}
