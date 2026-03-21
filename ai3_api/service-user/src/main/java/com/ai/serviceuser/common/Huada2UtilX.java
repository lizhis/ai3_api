package com.ai.serviceuser.common;

import com.ai.basecommon.core.po.base.*;
import com.ai.basecommon.core.vo.base.Huada2PayInfoVO;
import com.ai.basecommon.enums.PayBusinessTypeEnum;
import com.ai.basecommon.utils.EncryptUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.ChangqingOrderMapper;
import com.ai.serviceuser.mapper.Huada2OrderMapper;
import com.ai.serviceuser.mapper.SysConfChangqingMapper;
import com.alibaba.fastjson2.JSONObject;
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
public class Huada2UtilX {

    @Value("${pile.admin.host}")
    private String adminHost;

    @Autowired
    private Huada2OrderMapper huada2OrderMapper;



    //支付
    public String pay(PayBusinessTypeEnum businessTypeEnum, Long userId
            , String orderId, BigDecimal amount, SysConfHuada2PO confHuada2PO, SysConfApiHuada2PO apiHuada2PO, String ip,String payUserName) throws Exception{

        String notifyUrl = this.adminHost + "/api/notify/huada2";

        Long time = System.currentTimeMillis();

        String goodsName = "bet365";

        Map<String,String> param = new HashMap<>();
        param.put("mchId",confHuada2PO.getMchId());
        param.put("appId",confHuada2PO.getAppId());
        param.put("productId",apiHuada2PO.getProduceId());
        param.put("mchOrderNo",orderId);
        param.put("currency","cny");
        param.put("amount",String.valueOf(amount.multiply(new BigDecimal("100")).intValue()));
        param.put("clientIp",ip);
        param.put("notifyUrl",notifyUrl);
        param.put("subject",goodsName);
        param.put("body","card");
        param.put("extra","card");
        param.put("param2",payUserName);

        Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedMap.putAll(param);

        String str = "";
        for(String k : sortedMap.keySet()){
            str += k + "=" + sortedMap.get(k) + "&";
        }

        LogUtil.log("拼接的字符串是：" + str);
        str += "key="+confHuada2PO.getSecretKey();
        //LogUtil.log("拼接的字符串 加入key后是：" + str);

        String sign = EncryptUtil.md5(str);
        LogUtil.log("签名是：" + sign);

        param.put("sign",sign);

        Map headers = new HashMap();
        headers.put("Content-Type","application/x-www-form-urlencoded");

        String url = "https://cdn.stpaycn.com/api/pay/create_payorder";
        String r = null;
        try{
            r = HttpUtil.sendPost(url,param,headers);
            //r = HttpUtil.sendPostJson(url,JSONObject.toJSONString(param));
        }catch (Exception e){
            LogUtil.log("华达2支付下单失败：" + e.getMessage());
            return null;
        }

        LogUtil.log("华达2接口返回：" + r);


        if(StringUtil.isEmpty(r)){
            return null;
        }

        JSONObject jsonObject = null;
        try{
            jsonObject = JSONObject.parseObject(r);
        }catch (Exception e){
            LogUtil.log("华达2支付 解析结果失败：" + e.getMessage());
            return null;
        }
        if(null == jsonObject){
            return null;
        }
        if(!jsonObject.getString("retCode").equals("SUCCESS")){
            LogUtil.log("华达2支付接口失败 返回数据是：" + r);
            throw new Exception(jsonObject.getString("retMsg"));
        }
        //{"payOrderId":"QP1743435004070333",
        // "sign":"AFE7DB1083F8B7ED6B91E6BA49947F6F",
        // "payParams":{"payUrl":"https://biz.fwuc0i9.com/checkoutCounter?m=93469243&o=QP1743435004070333&s=6296322487ac1463e54ea03f7f753ca3"},
        // "retCode":"SUCCESS",
        // "retMsg":"下单成功"
        // }

        JSONObject payParamsObj = jsonObject.getJSONObject("payParams");

        String payUrl = payParamsObj.getString("payUrl");
        String payOrderId = jsonObject.getString("payOrderId");


        Huada2OrderPO po = new Huada2OrderPO();
        po.setUserId(userId);
        po.setOrderId(orderId);
        po.setTotalAmount(amount);
        po.setGoodsName(goodsName);
        po.setMchId(confHuada2PO.getMchId());
        po.setProductId(apiHuada2PO.getProduceId());
        po.setTradeNo(payOrderId);
        po.setOriginTradeNo(payOrderId);
        po.setBusinessType(businessTypeEnum.getCode());
        po.setChannelRemark(apiHuada2PO.getRemark());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        huada2OrderMapper.insert(po);

        return payUrl;
    }





}
