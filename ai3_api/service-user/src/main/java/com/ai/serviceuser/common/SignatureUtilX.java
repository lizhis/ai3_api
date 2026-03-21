package com.ai.serviceuser.common;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.EncryptUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.DeviceSecretMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
public class SignatureUtilX {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private DeviceSecretMapper deviceSecretMapper;

    @Value("${spring.profiles.active}")
    private String profile;


    //校验签名
    public StatusCodeEnum checkSignature() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object fromSwagger = request.getAttribute("X-From-Swagger");
        if(!profile.startsWith("prod") && Objects.equals("true",fromSwagger)){
            return StatusCodeEnum.SUCCESS;
        }

        Object signObj = request.getAttribute("X-Signature");
        Object dviObj = request.getAttribute("X-Dvi");
        Object timeObj = request.getAttribute("X-Timestamp");
        String signature = null;
        String dvi = null;
        Long timestamp = null;
        try{
            if(null != signObj && !"".equals(signObj.toString())){
                signature = (String) signObj;
            }
            if(null != dviObj && !"".equals(dviObj.toString())){
                dvi = (String) dviObj;
            }
            if(null != timeObj && !"".equals(timeObj.toString())){
                timestamp = Long.valueOf(timeObj.toString());
            }
        }catch (Exception e){
            LogUtil.log("解析失败：" + e.getMessage());
        }
        if(null == signature || null == dvi || null == timestamp){
            LogUtil.log("签名校验 没带header头：signature-"+signature+",dvi-"+dvi+",timestamp-"+timestamp);
            return StatusCodeEnum.REQUEST_ERROR;
        }
        String secret = loadSecret(dvi);
        if(StringUtil.isEmpty(secret)){
            LogUtil.log("签名校验 服务器里没有该设备的secret："+ dvi);
            return StatusCodeEnum.REQUEST_ERROR;
        }
        String sign = EncryptUtil.md5(dvi+secret+timestamp);
        if(!signature.equals(sign)){
            LogUtil.log("签名校验 签名错误："+ dvi);
            return StatusCodeEnum.SIGN_ERROR;
        }
        Long time = System.currentTimeMillis();
        if(time - timestamp > 180000){
            LogUtil.log("签名校验 时间戳超出3分钟："+ dvi);
            return StatusCodeEnum.REQUEST_ERROR;
        }
        return StatusCodeEnum.SUCCESS;
    }


    private String loadSecret(String dvi){
        if(StringUtil.isEmpty(dvi)){
            return null;
        }
        String secret = null;
        String key = RedisKey.device_id_to_secret_ + dvi;
        if(!redisUtilX.hasKey(key)){
            secret = redisUtilX.get(key);
        }
        if(StringUtil.isEmpty(secret)){
            secret = deviceSecretMapper.findSecretByDeviceId(dvi);
            if(!StringUtil.isEmpty(secret)){
                redisUtilX.set(key, secret,86400);
            }
        }
        return secret;
    }



}
