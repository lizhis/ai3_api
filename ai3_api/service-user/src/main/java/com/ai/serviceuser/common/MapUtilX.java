package com.ai.serviceuser.common;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.map.LocationDTO;
import com.ai.basecommon.core.dto.map.MapAddressDTO;
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

@Component
public class MapUtilX {


    @Autowired
    private SysConfApiMapper sysConfApiMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    /**
     * 经纬度转地址
     * @return
     * @throws Exception
     */
    public MapAddressDTO locationToAddress(LocationDTO param) throws Exception{

        if(null == param || null == param.getLat() || null == param.getLng()){
            LogUtil.log("经纬度转地址 没有参数");
            return null;
        }

        SysConfApiPO confApiPO = this.getConf();
        if(null == confApiPO || StringUtil.isEmpty(confApiPO.getAmapWeb())){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }

        //格式：location=lat<纬度>,lng<经度>   location= 39.984154,116.307490
        String lo = param.getLng().toString() + "," + param.getLat().toString();

        String url = "https://restapi.amap.com/v3/geocode/regeo";

        HashMap map = new HashMap();
        map.put("key",confApiPO.getAmapWeb());
        map.put("location",lo);//关键词

        String result = HttpUtil.sendGet(url,map);

        if(StringUtil.isEmpty(result)){
            return null;
        }

        MapAddressDTO dto = new MapAddressDTO();

        try{

            JSONObject jsonObject = JSONObject.parseObject(result);
            if(null == jsonObject){
                return null;
            }
            if(jsonObject.getInteger("status") != 1){
                LogUtil.log("经纬度转地址 接口返回失败：" + jsonObject.toString());
                return null;
            }

            JSONObject content = jsonObject.getJSONObject("regeocode");

            JSONObject addressComponent = content.getJSONObject("addressComponent");
            dto.setNation(addressComponent.getString("country"));
            dto.setProvince(addressComponent.getString("province"));
            dto.setCity(addressComponent.getString("city"));
            dto.setDistrict(addressComponent.getString("district"));
            dto.setStreet(addressComponent.getString("township"));

            JSONObject streetObj = addressComponent.getJSONObject("streetNumber");
            dto.setStreetNumber(streetObj.getString("street")+streetObj.getString("number"));


            String c = addressComponent.getString("adcode");
            dto.setAdcode(Integer.valueOf(c));
            dto.setCityCode(Integer.valueOf(c.substring(0,4)+"00"));

        }catch (Exception e){
            LogUtil.log(e.getMessage());
            return null;
        }

        return dto;
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
