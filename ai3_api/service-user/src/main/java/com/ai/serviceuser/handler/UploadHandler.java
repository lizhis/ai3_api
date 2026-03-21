package com.ai.serviceuser.handler;

import com.ai.basecommon.enums.UploadImageUseTypeEnum;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.common.HeaderUtil;
import com.ai.serviceuser.common.UploadUtilX;
import com.ai.serviceuser.common.UserUtilX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
public class UploadHandler {

    @Autowired
    private UploadUtilX uploadUtilX;

    @Autowired
    private UserUtilX userUtilX;


    public String file(MultipartFile file) throws Exception{
        LogUtil.log("正在上传文件：" + file.getOriginalFilename());

        String usetype = HeaderUtil.getHeader("usetype");
        LogUtil.log("使用类型是：" + usetype);
        if(StringUtil.isEmpty(usetype) || !usetype.equals(UploadImageUseTypeEnum.SERVICE_CHAT.getCode().toString())){
            Long userId = userUtilX.getUserId();
        }

        //LogUtil.log("headers是：" + headers);
//{content-length=19280, remote-host=, userid=, token=, accept=*/*, x-real-ip=, x-request-source=gateway, dvi=89aaa371d65a588b, host=192.168.0.114:8092, content-type=multipart/form-data; boundary=297d0e30-9609-4f95-ae47-2a2195bf4aa6, usetype=4, accept-encoding=gzip, user-agent=okhttp/4.9.3}

        String fileName = uploadUtilX.file(file);
        if(StringUtil.isEmpty(fileName)){
            BaseException.error("上传失败");
        }
        return fileName;
    }



}
