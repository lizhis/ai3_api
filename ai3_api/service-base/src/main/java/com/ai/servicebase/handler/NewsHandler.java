package com.ai.servicebase.handler;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.PageIn;
import com.ai.basecommon.core.po.base.article.NewsPO;
import com.ai.basecommon.core.vo.base.NewsDetailVO;
import com.ai.basecommon.core.vo.base.NewsVO;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsHandler {

    @Autowired
    private NewsMapper newsMapper;

    @ReadOnly
    public List<NewsVO> select(PageIn param) throws Exception{
        return newsMapper.select(param);
    }

    @ReadOnly
    public NewsDetailVO findById(IdParam param) throws Exception{
        if(null == param || null == param.getId()){
            return null;
        }
        return newsMapper.findById(param.getId());
    }




}
