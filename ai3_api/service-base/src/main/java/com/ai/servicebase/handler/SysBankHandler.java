package com.ai.servicebase.handler;

import com.ai.basecommon.core.vo.base.SysBankVO;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.SysBankMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author
 */
@Component
public class SysBankHandler {

    @Autowired
    private SysBankMapper sysBankMapper;

    @ReadOnly
    public List<SysBankVO> select() throws Exception{
        List<SysBankVO> list = sysBankMapper.select();
        if(null == list || list.isEmpty()){
            return new ArrayList<>();
        }
        return list;
    }

}
