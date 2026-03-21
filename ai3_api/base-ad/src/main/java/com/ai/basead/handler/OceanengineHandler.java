package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basead.mapstruct.MapMapper;
import com.ai.basead.producer.AdActiveProducer;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.AdOceanengineDTO;
import com.ai.basecommon.core.dto.ad.AdVerifyParamDTO;
import com.ai.basecommon.core.dto.msg.AdActiveMsgDTO;
import com.ai.basecommon.core.param.ad.AdOceanengineParam;
import com.ai.basecommon.enums.UserChannelEnum;
import com.ai.basecommon.utils.CommonUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Component
public class OceanengineHandler {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private AdActiveProducer adActiveProducer;

    @Autowired
    private MapMapper mapMapper;


    public void listen(AdOceanengineParam param) throws Exception{
        LogUtil.log("巨量引擎广告 收到监听事件：" + param.toString());

        String id = null;
        if(!StringUtil.isEmpty(param.getOaid())){
            id = param.getOaid();
        }
        else{
            id = param.getIp();
        }

        if(null == id){
            LogUtil.log("巨量引擎广告 没有设备ID 不处理");
            return;
        }

        AdOceanengineDTO dto = mapMapper.adOceanengineParamToDTO(param);

        id = id.toLowerCase();

        String key = RedisKey.ad_oceanengine_id_ + id;
        redisUtilX.setObj(key,dto,3600*48);

        String activeKey = RedisKey.ad_oceanengine_active_id_ + id;
        redisUtilX.setObj(activeKey,dto,3600*48);

        //adActiveProducer.produce(id);

        AdActiveMsgDTO msgDTO = new AdActiveMsgDTO();
        msgDTO.setActiveId(id);
        msgDTO.setChannel(UserChannelEnum.OCEANENGINE.getCode());
        adActiveProducer.produce(msgDTO);

    }



    public void active(AdVerifyParamDTO paramDTO) throws Exception{

        LogUtil.log("进入方法 巨量激活归因 参数：" + paramDTO.toString());

        String deviceId = paramDTO.getDeviceId();
        if(StringUtil.isEmpty(deviceId)){
            return;
        }

        String id = paramDTO.getCheckId();
        if(StringUtil.isEmpty(id)){
            return;
        }

        String appPackage = paramDTO.getAppPackage();
        if(StringUtil.isEmpty(appPackage)){
            return;
        }

        String activeKey = RedisKey.ad_oceanengine_active_id_ + id;
        if(!redisUtilX.hasKey(activeKey)){
            LogUtil.log("没有待激活标记");
            return;
        }

        int platformType = CommonUtil.getDevicePlatform(deviceId);
        if(platformType < 1){
            return;
        }

        //LogUtil.log("设备号是：" + deviceId + "，平台是：" + platformType);

        //https://analytics.oceanengine.com/sdk/app/attribution
        String url = "https://analytics.oceanengine.com/sdk/app/attribution";

        HashMap<String,String> param = new HashMap<>();

        if(1 == platformType){
            param.put("platform","android");
            param.put("android_id",deviceId);
        }
        else{
            param.put("platform","ios");
            param.put("idfv",deviceId);
        }
        param.put("package_name",appPackage);
        //param.put("customer_active_time",appPackage);

        //{
        //    "platform": "ios",  // ios或android
        //    "idfv": "xxxx-xxxx-xxxx-xxxx",  // 仅ios需要
        //    "android_id": "xxxxxxxxxxxxxxxx" // 仅android需要
        //    "package_name": "com.test.demo"
        //    "customer_active_time": "1722938240000" // 毫秒时间戳，客户激活归因时间点
        //}
        //LogUtil.log("请求巨量实时归因接口，参数是：" + param.toString());
        String res = HttpUtil.sendPostJson(url, JSON.toJSONString(param));
        //String res = HttpUtil.sendPost(url, param);
        //LogUtil.log("实时归因接口返回：" + res);

        //{"code":100,"message":"无效的请求,请求数据解析失败"}

        //{"code":0,"message":"成功","adv_idfv":"","adv_android_id":"89aaa371d65a588b","idfa":"","caid":"","oaid":"","odid":"","is_dp_open":false,"activation_window":30,"active_time":1753786532,"last_touch_time":0,"project_id":0,"project_name":"","promotion_id":0,"promotion_name":"","aid":0,"aid_name":"","cid":0,"cid_name":"","advertiser_id":0,"req_id":"","track_id":"","callback_param":"","callback_url":"","mid1":0,"mid2":0,"mid3":0,"mid4":0,"mid5":0,"mid6":0,"active_track_url":"","action_track_url":"","csite":0,"union_site":0,"campaign_name":"","campaign_id":0,"convert_source":"others","demand_id":0,"item_id":0}
        JSONObject resObj = JSONObject.parseObject(res);
        if(0 == resObj.getInteger("code") && !StringUtil.isEmpty(resObj.getString("callback_url"))){

            //last_touch_time  最后触点时间

            String callbackUrl = resObj.getString("callback_url");
            String callbackParam = resObj.getString("callback_param");

            Long lastTouchTime = resObj.getLong("last_touch_time");

            String key = RedisKey.ad_oceanengine_id_ + id;
            if(redisUtilX.hasKey(key)){
                AdOceanengineDTO dto = redisUtilX.getObj(key, AdOceanengineDTO.class);
                if(dto.getTime().compareTo(lastTouchTime) > 0){
                    LogUtil.log("监测链接的时间更近");
                    //String v = dto.getCallbackUrl();
                    callbackParam = dto.getCallbackParam();
                    //dto.setCallbackParam(callbackParam);
                    //redisUtilX.setObj(key, dto, 3600*48);
                    //redisUtilX.delete(activeKey);
                }
                else{
                    dto.setCallbackParam(callbackParam);
                    redisUtilX.setObj(key, dto, 3600*48);
                }
            }
            else{
                AdOceanengineDTO dto = new AdOceanengineDTO();
                dto.setCallbackParam(callbackParam);
                dto.setCallbackUrl(callbackUrl);
                redisUtilX.setObj(key, dto, 3600*48);
            }
            redisUtilX.delete(activeKey);


            callbackUrl = URLDecoder.decode(callbackUrl, StandardCharsets.UTF_8);
            //redisUtilX.set(RedisKey.ads_deviceid_channel_ + deviceId, UserChannelEnum.OCEANENGINE.getCode().toString(),86400*7);
            //redisUtilX.set(RedisKey.ads_deviceid_callbackurl_oceanengine_ + deviceId,callbackUrl,86400*7);


            callbackUrl += "&event_type=active";

            if(2 == platformType){
                callbackUrl += "&platform=ios&idfv="+deviceId;
            }
            else{
                callbackUrl += "&platform=android&android_id="+deviceId;
            }

            String activeUrl = "https://analytics.oceanengine.com/api/v2/conversion";

/*
            {
                "event_type": "active",
                "context": {
                    "ad": {
                        "callback": "demo_callback"
                    },
                    "device": {
                        "platform": "android",
                        "android_id": "abaca12312"
                    }
                 },
                "timestamp": 1604888786102
            }
*/

            Long time = System.currentTimeMillis();

            JSONObject json = new JSONObject();
            json.put("event_type", "active");
            json.put("timestamp", time);

            JSONObject context = new JSONObject();

            JSONObject ad = new JSONObject();
            ad.put("callback", callbackParam);
            context.put("ad", ad);

            JSONObject device = new JSONObject();

            if(2 == platformType){
                device.put("platform", "ios");
                device.put("idfv", deviceId);
            }
            else{
                device.put("platform", "android");
                device.put("android_id", deviceId);
            }
            context.put("device", device);

            json.put("context", context);

            String jsonString = json.toJSONString();

            LogUtil.log("巨量引擎 激活上报url：" + activeUrl + "，参数：" + jsonString);
            String activeRes = HttpUtil.sendPostJson(activeUrl, jsonString);
            LogUtil.log("巨量引擎 激活上报结果：" + activeRes);
/*

            LogUtil.log("激活上报callbackUrl：" + callbackUrl);
            String re = HttpUtil.sendGet(callbackUrl);
            LogUtil.log("激活上报结果：" + re);
*/

        }
        else{
            LogUtil.log("实时归因返回错误：" + res);
        }
    }





}
