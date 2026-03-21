package com.ai.serviceuser.common;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.SysConfApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author
 */
@Component
public class IdCardUtilX {


    @Autowired
    private SysConfApiMapper sysConfApiMapper;

    @Autowired
    private RedisUtilX redisUtilX;



    private final String url = "https://checkphone.market.alicloudapi.com/carrieroperator";


    //身份认证
    public boolean verify(String realName,String idcard,String tel) throws Exception{
        if(StringUtil.isEmpty(realName) || StringUtil.isEmpty(idcard) || StringUtil.isEmpty(tel)){
            return false;
        }

        SysConfApiPO confApiPO = this.getConf();
        if(null == confApiPO || StringUtil.isEmpty(confApiPO.getAuth())){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }

        HashMap<String,String> map = new HashMap<>();
        map.put("name",realName);
        map.put("idCard",idcard);
        map.put("mobile",tel);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization","APPCODE " + confApiPO.getAuth());

        try{

            String res = HttpUtil.sendGet(this.url,map,headers);
            if(StringUtil.isEmpty(res)){
                return false;
            }
            LogUtil.log("身份验证接口返回：" + res);

            JSONObject jsonObject = JSONObject.parseObject(res);
            Integer status = jsonObject.getInteger("status");
            if(status != 1){
                return false;
            }
        }catch (Exception e){
            LogUtil.log("身份验证接口失败：" + e.getMessage());
            return false;
        }

        return true;
    }

    private SysConfApiPO getConf() throws Exception {
        SysConfApiPO confPO = redisUtilX.getObj(RedisKey.conf_api,SysConfApiPO.class);
        if(null != confPO){
            return confPO;
        }
        confPO = sysConfApiMapper.find();
        if(null == confPO){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }
        redisUtilX.setObj(RedisKey.conf_api,confPO,600);
        return confPO;
    }



}
