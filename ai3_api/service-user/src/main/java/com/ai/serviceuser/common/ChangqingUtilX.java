package com.ai.serviceuser.common;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.po.base.ChangqingOrderPO;
import com.ai.basecommon.core.po.base.SysConfApiChangqingPO;
import com.ai.basecommon.core.po.base.SysConfChangqingPO;
import com.ai.basecommon.enums.PayBusinessTypeEnum;
import com.ai.basecommon.utils.EncryptUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.ChangqingOrderMapper;
import com.ai.serviceuser.mapper.SysConfChangqingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description
 * @Author
 */
@Component
public class ChangqingUtilX {

    @Value("${pile.admin.host}")
    private String adminHost;

    @Autowired
    private SysConfChangqingMapper sysConfChangqingMapper;

    @Autowired
    private ChangqingOrderMapper changqingOrderMapper;



    //支付
    public String pay(PayBusinessTypeEnum businessTypeEnum, Long userId
            , String orderId, BigDecimal amount, SysConfChangqingPO confChangqingPO, SysConfApiChangqingPO apiChangqingPO,String ip) throws Exception{

        String notifyUrl = this.adminHost + "/api/notify/changqing";

        Long time = System.currentTimeMillis();

        String goodsName = "bet365";

        Map<String,Object> param = new HashMap<>();
        param.put("mchId",confChangqingPO.getMchId());
        param.put("wayCode",apiChangqingPO.getProduceId());
        param.put("outTradeNo",orderId);
        param.put("amount",String.valueOf(amount.multiply(new BigDecimal("100")).intValue()));
        param.put("notifyUrl",notifyUrl);
        param.put("subject",goodsName);
        param.put("body","商品描述");
        param.put("clientIp",ip);
        param.put("reqTime", time.toString());

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
        LogUtil.log("拼接的字符串是：" + str);
        str += "key="+confChangqingPO.getSecretKey();
        //LogUtil.log("拼接的字符串 加入key后是：" + str);

        String sign = EncryptUtil.md5(str);
        LogUtil.log("签名是：" + sign);

        param.put("sign",sign);

        Map headers = new HashMap();
        headers.put("Content-Type","application/json");

        String url = "https://api.changqingpay.xyz/api/pay/order";
        String r = null;
        try{
            //r = HttpUtil.sendPost(url,headers,param);
            r = HttpUtil.sendPostJson(url,JSONObject.toJSONString(param));
        }catch (Exception e){
            LogUtil.log("长卿支付下单失败：" + e.getMessage());
            return null;
        }

        LogUtil.log("长卿接口返回：" + r);

        if(StringUtil.isEmpty(r)){
            return null;
        }

        JSONObject jsonObject = null;
        try{
            jsonObject = JSONObject.parseObject(r);
        }catch (Exception e){
            LogUtil.log("长卿支付 解析结果失败：" + e.getMessage());
            return null;
        }
        if(null == jsonObject){
            return null;
        }
        if(!jsonObject.getString("code").equals("0")){
            LogUtil.log("长卿支付接口失败 返回数据是：" + r);
            throw new Exception(jsonObject.getString("message"));
        }

        JSONObject dataObj = jsonObject.getJSONObject("data");


        //LogUtil.log("返回的JSON是：" + JSONObject);
        String tradeNo = dataObj.getString("tradeNo");
        String originTradeNo = dataObj.getString("originTradeNo");
        String payUrl = dataObj.getString("payUrl");

        if(StringUtil.isEmpty(payUrl)){
            LogUtil.log("长卿支付接口失败 没有支付url 返回数据是：" + r);
            return null;
        }

        ChangqingOrderPO po = new ChangqingOrderPO();
        po.setUserId(userId);
        po.setOrderId(orderId);
        po.setTotalAmount(amount);
        po.setGoodsName(goodsName);
        po.setMchId(confChangqingPO.getMchId());
        po.setProductId(apiChangqingPO.getProduceId());
        po.setTradeNo(tradeNo);
        po.setOriginTradeNo(originTradeNo);
        po.setBusinessType(businessTypeEnum.getCode());
        po.setChannelRemark(apiChangqingPO.getRemark());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        changqingOrderMapper.insert(po);

        return payUrl;
    }





}
