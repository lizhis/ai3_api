package com.ai.servicebase.handler;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.base.GuideParam;
import com.ai.basecommon.core.vo.base.GuideCateVO;
import com.ai.basecommon.core.vo.base.GuideVO;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.GuideCateMapper;
import com.ai.servicebase.mapper.GuideMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GuideHandler {

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private GuideCateMapper guideCateMapper;


    @ReadOnly
    public List<GuideCateVO> selectCateList() throws Exception{
        return guideCateMapper.select();
    }

    @ReadOnly
    public List<GuideVO> select(GuideParam param) throws Exception{
        return guideMapper.select(param);
    }

    @ReadOnly
    public GuideVO findVOById(IdParam param) throws Exception{
        if(null == param || null == param.getId()){
            return null;
        }
        return guideMapper.findVOById(param.getId());
    }




}
