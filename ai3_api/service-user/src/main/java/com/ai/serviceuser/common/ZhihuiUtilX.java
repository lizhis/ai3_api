package com.ai.serviceuser.common;

import com.ai.basecommon.core.po.base.*;
import com.ai.basecommon.enums.PayBusinessTypeEnum;
import com.ai.basecommon.utils.EncryptUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.ChangqingOrderMapper;
import com.ai.serviceuser.mapper.SysConfChangqingMapper;
import com.ai.serviceuser.mapper.SysConfZhihuiMapper;
import com.ai.serviceuser.mapper.ZhihuiOrderMapper;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ZhihuiUtilX {

    @Value("${pile.admin.host}")
    private String adminHost;

    @Autowired
    private ZhihuiOrderMapper zhihuiOrderMapper;


    private final String channelName = "智汇支付";

    //支付
    public String pay(PayBusinessTypeEnum businessTypeEnum, Long userId
            , String orderId, BigDecimal amount, SysConfZhihuiPO confZhihuiPO, SysConfApiZhihuiPO apiZhihuiPO, String ip) throws Exception{

        String notifyUrl = this.adminHost + "/api/notify/zhihui";

        Long time = System.currentTimeMillis();

        String goodsName = "bet365";

        Map<String,Object> param = new HashMap<>();
        param.put("mch",confZhihuiPO.getMchId());
        param.put("code",apiZhihuiPO.getProduceId());
        param.put("orderid",orderId);
        param.put("price",String.valueOf(amount.multiply(new BigDecimal("100")).intValue()));
        param.put("notify",notifyUrl);
        param.put("reqTime", time);
        param.put("clientIP",ip);

        //param.put("subject",goodsName);
        //param.put("body","商品描述");



        Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedMap.putAll(param);

        // 打印排序后的Map
        //LogUtil.log("排序后：");
        //sortedMap.forEach((key, value) -> LogUtil.log(key + " -> " + value));

        String str = "";
        for(String k : sortedMap.keySet()){
            str += k + "=" + sortedMap.get(k) + "&";
        }
        //str = str.substring(0, str.length() - 1);
        LogUtil.log(channelName + "拼接的字符串是：" + str);
        str += "secret="+confZhihuiPO.getSecretKey();
        LogUtil.log(channelName + "拼接的字符串 加入key后是：" + str);

        String sign = EncryptUtil.md5(str).toUpperCase();
        LogUtil.log(channelName + "签名是：" + sign);

        param.put("sign",sign);

        Map headers = new HashMap();
        headers.put("Content-Type","application/json");

        String url = "https://api.zhihui.ruyizf.xyz/pay";
        String r = null;
        try{
            //r = HttpUtil.sendPost(url,headers,param);
            r = HttpUtil.sendPostJson(url,JSONObject.toJSONString(param));
        }catch (Exception e){
            LogUtil.log(channelName + "下单失败：" + e.getMessage());
            return null;
        }

        LogUtil.log(channelName + "接口返回：" + r);

        if(StringUtil.isEmpty(r)){
            return null;
        }

        JSONObject jsonObject = null;
        try{
            jsonObject = JSONObject.parseObject(r);
        }catch (Exception e){
            LogUtil.log(channelName + " 解析结果失败：" + e.getMessage());
            return null;
        }
        if(null == jsonObject){
            return null;
        }
        if(!jsonObject.getString("code").equals("0")){
            LogUtil.log(channelName + "接口失败 返回数据是：" + r);
            throw new Exception(jsonObject.getString("message"));
        }

        JSONObject dataObj = jsonObject.getJSONObject("data");


        //LogUtil.log("返回的JSON是：" + JSONObject);
        //String tradeNo = dataObj.getString("tradeNo");
        //String originTradeNo = dataObj.getString("originTradeNo");
        String payUrl = dataObj.getString("url");

        if(StringUtil.isEmpty(payUrl)){
            LogUtil.log(channelName + "接口失败 没有支付url 返回数据是：" + r);
            return null;
        }



        ZhihuiOrderPO po = new ZhihuiOrderPO();
        po.setUserId(userId);
        po.setOrderId(orderId);
        po.setTotalAmount(amount);
        po.setGoodsName(goodsName);
        po.setMchId(confZhihuiPO.getMchId());
        po.setProductId(apiZhihuiPO.getProduceId());
        po.setTradeNo(orderId);
        po.setOriginTradeNo(orderId);
        po.setBusinessType(businessTypeEnum.getCode());
        po.setChannelRemark(apiZhihuiPO.getRemark());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        zhihuiOrderMapper.insert(po);

        return payUrl;
    }





}
