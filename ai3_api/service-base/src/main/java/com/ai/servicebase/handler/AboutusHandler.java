package com.ai.servicebase.handler;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.vo.base.AboutusVO;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.AboutusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AboutusHandler {

    @Autowired
    private AboutusMapper aboutusMapper;


    @ReadOnly
    public List<AboutusVO> select(PageIn param) throws Exception{
        return aboutusMapper.select(param);
    }

    @ReadOnly
    public AboutusVO findById(IdParam param) throws Exception{
        if(null == param || null == param.getId()){
            return null;
        }
        return aboutusMapper.findById(param.getId());
    }




}
