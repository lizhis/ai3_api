package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basead.config.TencentProperties;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.*;
import com.ai.basecommon.enums.UserChannelEnum;
import com.ai.basecommon.utils.*;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Description
 * @Author
 */
@Component
public class AdHandler {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private TencentProperties tencentProperties;

    //转化验证
    public Integer verify(AdVerifyParamDTO paramDTO) throws Exception{
        if(null == paramDTO){
            return 0;
        }
        String id = paramDTO.getCheckId();
        String ip = paramDTO.getIp();
        String deviceId = paramDTO.getDeviceId();

        if(StringUtil.isEmpty(id) && StringUtil.isEmpty(ip)){
            return 0;
        }

        LogUtil.log("注册事件 转化验证：" + paramDTO.toString());


        if(!StringUtil.isEmpty(id)){


            String ucKey = RedisKey.ad_uc_id_ + id;
            if(redisUtilX.hasKey(ucKey)){
                LogUtil.log("匹配上了 是超级汇川：" + id);
                AdUcDTO ucDTO = redisUtilX.getObj(ucKey,AdUcDTO.class);
                boolean r = this.verifyUc(ucDTO);
                if(r){
                    redisUtilX.delete(ucKey);
                    return UserChannelEnum.UC.getCode();
                }
            }


            String oceKey = RedisKey.ad_oceanengine_id_ + id;
            if(redisUtilX.hasKey(oceKey)){
                LogUtil.log("匹配上了 是巨量引擎：" + id);
                AdOceanengineDTO oceanengineDTO = redisUtilX.getObj(oceKey, AdOceanengineDTO.class);
                boolean r = this.verifyOceanengine(oceanengineDTO,deviceId);
                if(r){
                    redisUtilX.delete(oceKey);

                    //String oceanengineRemainKey = RedisKey.ad_oceanengine_remain_id_ + deviceId;
                    //redisUtilX.setObj(oceanengineRemainKey,oceanengineDTO,3600*48);

                    return UserChannelEnum.OCEANENGINE.getCode();
                }
            }

            String kwaiKey = RedisKey.ad_kwai_id_ + id;
            if(redisUtilX.hasKey(kwaiKey)){
                LogUtil.log("匹配上了 是快手广告：" + id);
                AdKwaiDTO kwaiDTO = redisUtilX.getObj(kwaiKey, AdKwaiDTO.class);
                boolean r = this.verifyKwai(kwaiDTO,2);
                if(r){
                    redisUtilX.delete(kwaiKey);
                    return UserChannelEnum.KWAI.getCode();
                }
            }

            String baiduKey = RedisKey.ad_baidu_id_ + id;
            if(redisUtilX.hasKey(baiduKey)){
                LogUtil.log("匹配上了 是百度广告：" + id);
                AdBaiduDTO baiduDTO = redisUtilX.getObj(baiduKey, AdBaiduDTO.class);
                boolean r = this.verifyBaidu(baiduDTO,"register");
                if(r){
                    redisUtilX.delete(baiduKey);
                    redisUtilX.delete(RedisKey.ad_baidu_active_id_ + id);
                    return UserChannelEnum.BAIDU.getCode();
                }
            }


            String idV = EncryptUtil.md5(id).toLowerCase();


            String bianxianmaoKey = RedisKey.ad_bianxianmao_id_ + id;
            if(redisUtilX.hasKey(bianxianmaoKey)){
                LogUtil.log("匹配上了 是变现猫广告：" + id);
                AdBianxianmaoDTO bianxianmaoDTO = redisUtilX.getObj(bianxianmaoKey, AdBianxianmaoDTO.class);
                boolean r = this.verifyBianxianmao(bianxianmaoDTO);
                if(r){
                    redisUtilX.delete(bianxianmaoKey);
                    return UserChannelEnum.BIANXIANMAO.getCode();
                }
            }


            //LogUtil.log("转化验证 ID：" + id);
            //LogUtil.log("转化验证 md5之后："+ idV);
            String tencentKey = RedisKey.ad_tencent_id_ + idV;
            if(redisUtilX.hasKey(tencentKey)){
                LogUtil.log("匹配上了 是腾讯广告："+ idV);
                AdTencentDTO tencentDTO = redisUtilX.getObj(tencentKey, AdTencentDTO.class);
                boolean r = this.verifyTencent(tencentDTO,"REGISTER");
                if(r){
                    redisUtilX.delete(tencentKey);

                    String tencentRemainKey = RedisKey.ad_tencent_remain_id_ + deviceId;
                    redisUtilX.setObj(tencentRemainKey,tencentDTO,3600*48);

                    return UserChannelEnum.TENCENT.getCode();
                }
            }
            String tencent2Key = RedisKey.ad_tencent2_id_ + idV;
            if(redisUtilX.hasKey(tencent2Key)){
                LogUtil.log("匹配上了 是腾讯广告2："+ idV);
                AdTencentDTO tencentDTO = redisUtilX.getObj(tencent2Key, AdTencentDTO.class);
                boolean r = this.verifyTencent2(tencentDTO,"REGISTER");
                if(r){
                    redisUtilX.delete(tencent2Key);

                    String tencent2RemainKey = RedisKey.ad_tencent2_remain_id_ + deviceId;
                    redisUtilX.setObj(tencent2RemainKey,tencentDTO,3600*48);

                    return UserChannelEnum.TENCENT2.getCode();
                }
            }

            String tencent3Key = RedisKey.ad_tencent3_id_ + idV;
            if(redisUtilX.hasKey(tencent3Key)){
                LogUtil.log("匹配上了 是腾讯广告3："+ idV);
                AdTencentDTO tencentDTO = redisUtilX.getObj(tencent3Key, AdTencentDTO.class);
                boolean r = this.verifyTencent3(tencentDTO,"REGISTER");
                if(r){
                    redisUtilX.delete(tencent3Key);

                    String tencent3RemainKey = RedisKey.ad_tencent3_remain_id_ + deviceId;
                    redisUtilX.setObj(tencent3RemainKey,tencentDTO,3600*48);

                    return UserChannelEnum.TENCENT3.getCode();
                }
            }

            String tencent4Key = RedisKey.ad_tencent4_id_ + idV;
            if(redisUtilX.hasKey(tencent4Key)){
                LogUtil.log("匹配上了 是腾讯广告4："+ idV);
                AdTencentDTO tencentDTO = redisUtilX.getObj(tencent4Key, AdTencentDTO.class);
                boolean r = this.verifyTencent4(tencentDTO,"REGISTER");
                if(r){
                    redisUtilX.delete(tencent4Key);

                    String tencent4RemainKey = RedisKey.ad_tencent4_remain_id_ + deviceId;
                    redisUtilX.setObj(tencent4RemainKey,tencentDTO,3600*48);

                    return UserChannelEnum.TENCENT4.getCode();
                }
            }

        }




        if(!StringUtil.isEmpty(ip)){

            String bianxianmaoKey = RedisKey.ad_bianxianmao_id_ + ip;
            if(redisUtilX.hasKey(bianxianmaoKey)){
                LogUtil.log("转化验证 ip验证成功 是变现猫广告：" + ip);
                AdBianxianmaoDTO bianxianmaoDTO = redisUtilX.getObj(bianxianmaoKey, AdBianxianmaoDTO.class);
                boolean r = this.verifyBianxianmao(bianxianmaoDTO);
                if(r){
                    redisUtilX.delete(bianxianmaoKey);
                    return UserChannelEnum.BIANXIANMAO.getCode();
                }
            }


            String kuaishouIpKey = RedisKey.ad_kwai_id_ + ip;
            if(redisUtilX.hasKey(kuaishouIpKey)){
                LogUtil.log("转化验证 ip验证成功 是快手IOS广告：" + ip);
                AdKwaiDTO kwaiDTO = redisUtilX.getObj(kuaishouIpKey, AdKwaiDTO.class);
                boolean r = this.verifyKwai(kwaiDTO,2);
                if(r){
                    redisUtilX.delete(kuaishouIpKey);
                    return UserChannelEnum.KWAI.getCode();
                }
            }


/*            String oceKeyIpKey = RedisKey.ad_oceanengine_id_ + ip;
            if(redisUtilX.hasKey(oceKeyIpKey)){
                LogUtil.log("转化验证 ip验证成功 是巨量引擎：" + id);
                AdOceanengineDTO oceanengineDTO = redisUtilX.getObj(oceKeyIpKey, AdOceanengineDTO.class);
                boolean r = this.verifyOceanengine(oceanengineDTO,1);
                if(r){
                    redisUtilX.delete(oceKeyIpKey);

                    String oceanengineRemainKey = RedisKey.ad_oceanengine_remain_id_ + deviceId;
                    redisUtilX.setObj(oceanengineRemainKey,oceanengineDTO,3600*48);
                    return UserChannelEnum.OCEANENGINE.getCode();
                }
            }*/

            String baiduKey = RedisKey.ad_baidu_id_ + ip;
            if(redisUtilX.hasKey(baiduKey)){
                LogUtil.log("转化验证 ip验证成功 是百度广告：" + ip);
                AdBaiduDTO baiduDTO = redisUtilX.getObj(baiduKey, AdBaiduDTO.class);
                boolean r = this.verifyBaidu(baiduDTO,"register");
                if(r){
                    redisUtilX.delete(baiduKey);
                    redisUtilX.delete(RedisKey.ad_baidu_active_id_ + ip);
                    return UserChannelEnum.BAIDU.getCode();
                }
            }



        }





        return 0;
    }


    //激活
    public void active(String id) throws Exception{
        if(StringUtil.isEmpty(id)){
            return;
        }

/*        String activeOceaKey = RedisKey.ad_oceanengine_active_id_ + id;
        if(redisUtilX.hasKey(activeOceaKey)){
            LogUtil.log("巨量引擎广告 激活 ID：" + id);
            AdOceanengineDTO adOceanengineDTO = redisUtilX.getObj(activeOceaKey, AdOceanengineDTO.class);
            boolean r = this.verifyOceanengine(adOceanengineDTO,0);
            if(r){
                redisUtilX.delete(activeOceaKey);
            }
        }*/

        String activeBaiduKey = RedisKey.ad_baidu_active_id_ + id;
        if(redisUtilX.hasKey(activeBaiduKey)){
            //LogUtil.log("百度广告 激活 ID：" + id);
            AdBaiduDTO baiduDTO = redisUtilX.getObj(activeBaiduKey, AdBaiduDTO.class);
            boolean r = this.verifyBaidu(baiduDTO,"activate");
            if(r){
                redisUtilX.delete(activeBaiduKey);
            }
        }


        String activeTencentKey = RedisKey.ad_tencent_active_id_ + id;
        if(redisUtilX.hasKey(activeTencentKey)){
            LogUtil.log("腾讯广告 激活 ID：" + id);
            AdTencentDTO adTencentDTO = redisUtilX.getObj(activeTencentKey, AdTencentDTO.class);
            boolean r = this.verifyTencent(adTencentDTO,"ACTIVATE_APP");
            if(r){
                redisUtilX.delete(activeTencentKey);
            }
        }

        String activeTencent2Key = RedisKey.ad_tencent2_active_id_ + id;
        if(redisUtilX.hasKey(activeTencent2Key)){
            LogUtil.log("腾讯广告2 激活 ID：" + id);
            AdTencentDTO adTencentDTO = redisUtilX.getObj(activeTencent2Key, AdTencentDTO.class);
            boolean r = this.verifyTencent2(adTencentDTO,"ACTIVATE_APP");
            if(r){
                redisUtilX.delete(activeTencent2Key);
            }
        }

        String activeTencent3Key = RedisKey.ad_tencent3_active_id_ + id;
        if(redisUtilX.hasKey(activeTencent3Key)){
            LogUtil.log("腾讯广告3 激活 ID：" + id);
            AdTencentDTO adTencentDTO = redisUtilX.getObj(activeTencent3Key, AdTencentDTO.class);
            boolean r = this.verifyTencent3(adTencentDTO,"ACTIVATE_APP");
            if(r){
                redisUtilX.delete(activeTencent3Key);
            }
        }

        String activeTencent4Key = RedisKey.ad_tencent4_active_id_ + id;
        if(redisUtilX.hasKey(activeTencent4Key)){
            LogUtil.log("腾讯广告4 激活 ID：" + id);
            AdTencentDTO adTencentDTO = redisUtilX.getObj(activeTencent4Key, AdTencentDTO.class);
            boolean r = this.verifyTencent4(adTencentDTO,"ACTIVATE_APP");
            if(r){
                redisUtilX.delete(activeTencent4Key);
            }
        }

        String activeKwaiKey = RedisKey.ad_kwai_active_id_ + id;
        if(redisUtilX.hasKey(activeKwaiKey)){
            LogUtil.log("快手广告 激活 ID：" + id);
            AdKwaiDTO adKwaiDTO = redisUtilX.getObj(activeKwaiKey, AdKwaiDTO.class);
            boolean r = this.verifyKwai(adKwaiDTO,1);
            if(r){
                redisUtilX.delete(activeKwaiKey);
            }
        }


    }


    //留存
    public void remain(String deviceId) throws Exception{

        //LogUtil.log("留存验证：" + deviceId);

/*        String oceanengineRemainKey = RedisKey.ad_oceanengine_remain_id_ + deviceId;
        if(redisUtilX.hasKey(oceanengineRemainKey)){
            LogUtil.log("巨量引擎 留存验证 设备ID：" + deviceId);
            AdOceanengineDTO adOceanengineDTO = redisUtilX.getObj(oceanengineRemainKey, AdOceanengineDTO.class);
            boolean r = this.verifyOceanengine(adOceanengineDTO,6);
            if(r){
                redisUtilX.delete(oceanengineRemainKey);
            }
        }*/

        String tencentRemainKey = RedisKey.ad_tencent_remain_id_ + deviceId;
        if(redisUtilX.hasKey(tencentRemainKey)){
            LogUtil.log("腾讯广告 留存验证 设备ID：" + deviceId);
            AdTencentDTO adTencentDTO = redisUtilX.getObj(tencentRemainKey, AdTencentDTO.class);
            boolean r = this.verifyTencent(adTencentDTO,"ONE_DAY_LEAVE");
            if(r){
                redisUtilX.delete(tencentRemainKey);
            }
        }
        String tencent2RemainKey = RedisKey.ad_tencent2_remain_id_ + deviceId;
        if(redisUtilX.hasKey(tencent2RemainKey)){
            LogUtil.log("腾讯广告2 留存验证 设备ID：" + deviceId);
            AdTencentDTO adTencentDTO = redisUtilX.getObj(tencent2RemainKey, AdTencentDTO.class);
            boolean r = this.verifyTencent2(adTencentDTO,"ONE_DAY_LEAVE");
            if(r){
                redisUtilX.delete(tencent2RemainKey);
            }
        }

    }


    private boolean verifyUc(AdUcDTO ucDTO) throws Exception{
        if(null == ucDTO){
            return false;
        }

        String callback = ucDTO.getCallbackUrl();
        if(StringUtil.isEmpty(callback)){
            return false;
        }
        callback = URLDecoder.decode(callback, StandardCharsets.UTF_8);



        //回调地址：https://huichuan.uc.cn/callback/appapi?click_id=17804147055232650244&convert_id=11506414&event_type=0&dmp_id=dmp_-6355098508615923886&sid=18008963544603741203&uctrackid=czoxODAwODk2MzU0NDYwMzc0MTIwMztjOjEwMTY4NjAyNjtkOmRtcF8tNjM1NTA5ODUwODYxNTkyMzg4NjtwOmhj&act_type=&uid=210908896

        //在回调地址后追加参数
        //type：转化类型 填27
        //event_time：转化时间戳
        //idfa：iOS必填
        //oaid：安卓必填

        Long time = System.currentTimeMillis();

        callback += "&type=27&event_time="+time+"&oaid="+ucDTO.getOaid()+"&idfa="+ucDTO.getIdfa1();

        LogUtil.log("超级汇川 上报地址：" + callback);

        HttpUtil.sendGet(callback);
        return true;
    }


    private boolean verifyOceanengine(AdOceanengineDTO oceanengineDTO,String deviceId) throws Exception{
        if(null == oceanengineDTO){
            return false;
        }

        String callbackParam = oceanengineDTO.getCallbackParam();
        if(StringUtil.isEmpty(callbackParam)){
            return false;
        }

        Long time = System.currentTimeMillis();



        String activeUrl = "https://analytics.oceanengine.com/api/v2/conversion";

        JSONObject json = new JSONObject();
        json.put("event_type", "active_register");
        json.put("timestamp", time);

        JSONObject context = new JSONObject();

        JSONObject ad = new JSONObject();
        ad.put("callback", callbackParam);
        context.put("ad", ad);

        JSONObject device = new JSONObject();

        int platformType = CommonUtil.getDevicePlatform(deviceId);
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

        LogUtil.log("巨量引擎 注册上报url：" + activeUrl + "，参数：" + jsonString);
        String activeRes = HttpUtil.sendPostJson(activeUrl, jsonString);
        LogUtil.log("巨量引擎 注册上报结果：" + activeRes);


        return true;
    }


    private boolean verifyBaidu(AdBaiduDTO baiduDTO,String aType) throws Exception{
        if(null == baiduDTO){
            return false;
        }

        String callback = baiduDTO.getCallbackUrl();
        if(StringUtil.isEmpty(callback)){
            return false;
        }
        callback = URLDecoder.decode(callback, StandardCharsets.UTF_8);


        callback = callback.replace("{{ATYPE}}",aType);
        callback = callback.replace("{{AVALUE}}","0");


        Long time = System.currentTimeMillis();

        callback += "&oaid="+baiduDTO.getOaid() + "&idfa=" + baiduDTO.getIdfa() + "&ip=" + baiduDTO.getIp();

        LogUtil.log("百度广告 上报地址：" + callback);

        String r = HttpUtil.sendGet(callback);

        LogUtil.log("百度广告 上报返回：" + r);

        return true;
    }


    private boolean verifyKwai(AdKwaiDTO kwaiDTO,int eventType) throws Exception{
        if(null == kwaiDTO){
            return false;
        }

        //String callback = "http://ad.partner.gifshow.com/track/activate";


        String callback = kwaiDTO.getCallback();
        if(StringUtil.isEmpty(callback)){
            return false;
        }
        callback = URLDecoder.decode(callback, StandardCharsets.UTF_8);


        Long time = System.currentTimeMillis();

        //event_type 1是激活 2是注册

        callback += "&event_type="+eventType+"&event_time="+time;

        LogUtil.log("快手广告 上报地址：" + callback);

        String res = HttpUtil.sendGet(callback);
        LogUtil.log("快手广告上报返回：" + res);
        return true;
    }


    private boolean verifyTencent(AdTencentDTO tencentDTO,String type) throws Exception{
        if(null == tencentDTO){
            return false;
        }

        String callback = tencentDTO.getCallback();
        if(StringUtil.isEmpty(callback)){
            return false;
        }

        Long time = System.currentTimeMillis()/1000;

        String url = "https://api.e.qq.com/v3.0/user_actions/add?access_token="+tencentProperties.getAccessToken1()+"&timestamp="+time+"&nonce="+CommonUtil.getRandom(18);

        //access_token=<ACCESS_TOKEN>&timestamp=<TIMESTAMP>&nonce=<NONCE


        JSONObject useridParam = new JSONObject();
        useridParam.put("hash_android_id",tencentDTO.getHashAndroidId());
        useridParam.put("hash_oaid",tencentDTO.getHashOaid());

        JSONObject action = new JSONObject();
        action.put("action_time",time.toString());
        action.put("user_id",useridParam);
        action.put("action_type",type); //ACTIVATE_APP REGISTER ONE_DAY_LEAVE


        JSONObject actionParam = new JSONObject();
        //3注册归因 4激活归因
        actionParam.put("claim_type",4);
        if(type.equals("REGISTER")){
            actionParam.put("claim_type",3);
        }
        if(type.equals("ONE_DAY_LEAVE")){
            actionParam.put("length_of_stay",1);
            action.put("action_param",actionParam);
        }

        JSONObject traceParam = new JSONObject();
        traceParam.put("click_id",tencentDTO.getClickId());
        action.put("trace",traceParam);

        JSONArray actionsArr  = new JSONArray();
        actionsArr.add(action);

        JSONObject body = new JSONObject();
        body.put("actions",actionsArr);
        body.put("account_id",tencentProperties.getAccountId1());
        body.put("user_action_set_id",tencentProperties.getDataSourceId1());

        LogUtil.log("腾讯广告 上报地址：" + url + "，参数体："+body.toString());

        String res = HttpUtil.sendPostJson(url,body.toString());

        LogUtil.log("腾讯广告 上报返回：" + res);

        return true;
    }


    private boolean verifyTencent2(AdTencentDTO tencentDTO,String type) throws Exception{
        if(null == tencentDTO){
            return false;
        }

        String callback = tencentDTO.getCallback();
        if(StringUtil.isEmpty(callback)){
            return false;
        }

        Long time = System.currentTimeMillis()/1000;

        String url = "https://api.e.qq.com/v3.0/user_actions/add?access_token="+tencentProperties.getAccessToken2()+"&timestamp="+time+"&nonce="+CommonUtil.getRandom(18);

        //access_token=<ACCESS_TOKEN>&timestamp=<TIMESTAMP>&nonce=<NONCE


        JSONObject useridParam = new JSONObject();
        useridParam.put("hash_android_id",tencentDTO.getHashAndroidId());
        useridParam.put("hash_oaid",tencentDTO.getHashOaid());

        JSONObject action = new JSONObject();
        action.put("action_time",time.toString());
        action.put("user_id",useridParam);
        action.put("action_type",type); //ACTIVATE_APP REGISTER ONE_DAY_LEAVE


        JSONObject actionParam = new JSONObject();
        //3注册归因 4激活归因
        actionParam.put("claim_type",4);
        if(type.equals("REGISTER")){
            actionParam.put("claim_type",3);
        }
        if(type.equals("ONE_DAY_LEAVE")){
            actionParam.put("length_of_stay",1);
            action.put("action_param",actionParam);
        }

        JSONObject traceParam = new JSONObject();
        traceParam.put("click_id",tencentDTO.getClickId());
        action.put("trace",traceParam);

        JSONArray actionsArr  = new JSONArray();
        actionsArr.add(action);

        JSONObject body = new JSONObject();
        body.put("actions",actionsArr);
        body.put("account_id",tencentProperties.getAccountId2());
        body.put("user_action_set_id",tencentProperties.getDataSourceId2());

        LogUtil.log("腾讯广告2 上报地址：" + url + "，参数体："+body.toString());

        String res = HttpUtil.sendPostJson(url,body.toString());

        LogUtil.log("腾讯广告2 上报返回：" + res);

        return true;
    }

    private boolean verifyTencent3(AdTencentDTO tencentDTO,String type) throws Exception{
        if(null == tencentDTO){
            return false;
        }

        String callback = tencentDTO.getCallback();
        if(StringUtil.isEmpty(callback)){
            return false;
        }

        Long time = System.currentTimeMillis()/1000;

        String url = "https://api.e.qq.com/v3.0/user_actions/add?access_token="+tencentProperties.getAccessToken3()+"&timestamp="+time+"&nonce="+CommonUtil.getRandom(18);

        //access_token=<ACCESS_TOKEN>&timestamp=<TIMESTAMP>&nonce=<NONCE


        JSONObject useridParam = new JSONObject();
        useridParam.put("hash_android_id",tencentDTO.getHashAndroidId());
        useridParam.put("hash_oaid",tencentDTO.getHashOaid());

        JSONObject action = new JSONObject();
        action.put("action_time",time.toString());
        action.put("user_id",useridParam);
        action.put("action_type",type); //ACTIVATE_APP REGISTER ONE_DAY_LEAVE


        JSONObject actionParam = new JSONObject();
        //3注册归因 4激活归因
        actionParam.put("claim_type",4);
        if(type.equals("REGISTER")){
            actionParam.put("claim_type",3);
        }
        if(type.equals("ONE_DAY_LEAVE")){
            actionParam.put("length_of_stay",1);
            action.put("action_param",actionParam);
        }

        JSONObject traceParam = new JSONObject();
        traceParam.put("click_id",tencentDTO.getClickId());
        action.put("trace",traceParam);

        JSONArray actionsArr  = new JSONArray();
        actionsArr.add(action);

        JSONObject body = new JSONObject();
        body.put("actions",actionsArr);
        body.put("account_id",tencentProperties.getAccountId3());
        body.put("user_action_set_id",tencentProperties.getDataSourceId3());

        LogUtil.log("腾讯广告3 上报地址：" + url + "，参数体："+body.toString());

        String res = HttpUtil.sendPostJson(url,body.toString());

        LogUtil.log("腾讯广告3 上报返回：" + res);

        return true;
    }

    private boolean verifyTencent4(AdTencentDTO tencentDTO,String type) throws Exception{
        if(null == tencentDTO){
            return false;
        }

        String callback = tencentDTO.getCallback();
        if(StringUtil.isEmpty(callback)){
            return false;
        }

        Long time = System.currentTimeMillis()/1000;

        String url = "https://api.e.qq.com/v3.0/user_actions/add?access_token="+tencentProperties.getAccessToken4()+"&timestamp="+time+"&nonce="+CommonUtil.getRandom(18);

        //access_token=<ACCESS_TOKEN>&timestamp=<TIMESTAMP>&nonce=<NONCE


        JSONObject useridParam = new JSONObject();
        useridParam.put("hash_android_id",tencentDTO.getHashAndroidId());
        useridParam.put("hash_oaid",tencentDTO.getHashOaid());

        JSONObject action = new JSONObject();
        action.put("action_time",time.toString());
        action.put("user_id",useridParam);
        action.put("action_type",type); //ACTIVATE_APP REGISTER ONE_DAY_LEAVE


        JSONObject actionParam = new JSONObject();
        //3注册归因 4激活归因
        actionParam.put("claim_type",4);
        if(type.equals("REGISTER")){
            actionParam.put("claim_type",3);
        }
        if(type.equals("ONE_DAY_LEAVE")){
            actionParam.put("length_of_stay",1);
            action.put("action_param",actionParam);
        }

        JSONObject traceParam = new JSONObject();
        traceParam.put("click_id",tencentDTO.getClickId());
        action.put("trace",traceParam);

        JSONArray actionsArr  = new JSONArray();
        actionsArr.add(action);

        JSONObject body = new JSONObject();
        body.put("actions",actionsArr);
        body.put("account_id",tencentProperties.getAccountId4());
        body.put("user_action_set_id",tencentProperties.getDataSourceId4());

        LogUtil.log("腾讯广告4 上报地址：" + url + "，参数体："+body.toString());

        String res = HttpUtil.sendPostJson(url,body.toString());

        LogUtil.log("腾讯广告4 上报返回：" + res);

        return true;
    }


    private boolean verifyBianxianmao(AdBianxianmaoDTO bianxianmaoDTO) throws Exception{
        if(null == bianxianmaoDTO){
            return false;
        }

        String callback = bianxianmaoDTO.getCallback();
        if(StringUtil.isEmpty(callback)){
            return false;
        }
        callback = URLDecoder.decode(callback, StandardCharsets.UTF_8);

        Long time = System.currentTimeMillis();

        callback += "&conversion_type=2";

        LogUtil.log("变现猫 上报地址：" + callback);

        String res = HttpUtil.sendGet(callback);
        LogUtil.log("变现猫 上报返回值：" + res);
        return true;
    }


}
