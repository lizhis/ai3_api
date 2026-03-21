package com.ai.serviceuser.handler;

import com.ai.basecommon.core.po.user.SuggestPO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.common.UploadUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.SuggestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SuggestHandler {

    @Autowired
    private SuggestMapper suggestMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private UploadUtilX uploadUtilX;



    public boolean add(String content, MultipartFile[] files) throws Exception{
        Long userId = userUtilX.getUserId();
        if(StringUtil.isEmpty(content)){
            BaseException.error(StatusCodeEnum.PARAM_ERROR);
        }
        List<String> imgList = new ArrayList<>();
        if(null != files && files.length > 0){
            for (int i = 0; i < files.length; i++) {
                String path = uploadUtilX.file(files[i]);
                if(!StringUtil.isEmpty(path)){
                    imgList.add(path);
                }
            }
        }

        Long time = System.currentTimeMillis();
        SuggestPO po = new SuggestPO();
        po.setUserId(userId);
        po.setContent(content);
        if(!imgList.isEmpty()){
            po.setImages(imgList.stream().collect(Collectors.joining(",")));
        }
        po.setCreateTime(time);
        po.setUpdateTime(time);
        suggestMapper.insert(po);
        return true;
    }

    @ReadOnly
    public String lastReply() throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return null;
        }
        return suggestMapper.lastReply(userId);
    }


}
