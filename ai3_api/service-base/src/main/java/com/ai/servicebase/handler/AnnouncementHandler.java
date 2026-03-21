package com.ai.servicebase.handler;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.vo.base.AnnounVO;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.AnnounMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnnouncementHandler {

    @Autowired
    private AnnounMapper announMapper;

    @ReadOnly
    public List<AnnounVO> select(PageIn param) throws Exception{
        return announMapper.select(param);
    }

    @ReadOnly
    public AnnounVO findById(IdParam param) throws Exception{
        if(null == param || null == param.getId()){
            return null;
        }
        return announMapper.findById(param.getId());
    }




}
