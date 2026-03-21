package com.ai.serviceuser.common;

import com.ai.basecommon.core.po.base.*;
import com.ai.basecommon.enums.PayBusinessTypeEnum;
import com.ai.basecommon.utils.EncryptUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.HuadaOrderMapper;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class HuadaUtilX {

    @Value("${pile.admin.host}")
    private String adminHost;

    @Autowired
    private HuadaOrderMapper huadaOrderMapper;


    private final String channelName = "华达支付";

    //支付
    public String pay(PayBusinessTypeEnum businessTypeEnum, Long userId
            , String orderId, BigDecimal amount, SysConfHuadaPO confHuadaPO, SysConfApiHuadaPO apiHuadaPO, String ip) throws Exception{

        String notifyUrl = this.adminHost + "/api/notify/huada";

        Long time = System.currentTimeMillis();

        String goodsName = "bet365";

        Map<String,Object> param = new HashMap<>();
        param.put("Timestamp", time);
        param.put("AccessKey",confHuadaPO.getMchId());
        param.put("PayChannelId",apiHuadaPO.getProduceId());
        param.put("OrderNo",orderId);
        param.put("Amount",amount.toPlainString());
        param.put("CallbackUrl",notifyUrl);

        //param.put("ClientIp",ip);

        //param.put("subject",goodsName);
        //param.put("body","商品描述");



        Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedMap.putAll(param);

        String str = "";
        for(String k : sortedMap.keySet()){
            str += k + "=" + sortedMap.get(k) + "&";
        }
        //str = str.substring(0, str.length() - 1);
        LogUtil.log(channelName + "拼接的字符串是：" + str);
        str += "SecretKey="+confHuadaPO.getSecretKey();
        LogUtil.log(channelName + "拼接的字符串 加入key后是：" + str);

        String sign = EncryptUtil.md5(str).toLowerCase();
        LogUtil.log(channelName + "签名是：" + sign);

        param.put("Sign",sign);

        Map headers = new HashMap();
        headers.put("Content-Type","application/json");

        String url = "https://merchant.huadazhifu.xyz/api/PayV2/submit";
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
        if(!jsonObject.getString("Code").equals("0")){
            LogUtil.log(channelName + "接口失败 返回数据是：" + r);
            throw new Exception(jsonObject.getString("Message"));
        }

        String payUrl = null;

        try{

            JSONObject dataObj = jsonObject.getJSONObject("Data");

            LogUtil.log(channelName + "返回的JSON是：" + dataObj);
            String tradeNo = dataObj.getString("MerchantOrderNo");
            String originTradeNo = dataObj.getString("OrderNo");

            JSONObject infoObj = dataObj.getJSONObject("PayeeInfo");
            payUrl = infoObj.getString("CashUrl");

            if(StringUtil.isEmpty(payUrl)){
                LogUtil.log(channelName + "接口失败 没有支付url 返回数据是：" + r);
                return null;
            }

            HuadaOrderPO po = new HuadaOrderPO();
            po.setUserId(userId);
            po.setOrderId(orderId);
            po.setTotalAmount(amount);
            po.setGoodsName(goodsName);
            po.setMchId(confHuadaPO.getMchId());
            po.setProductId(apiHuadaPO.getProduceId());
            po.setTradeNo(tradeNo);
            po.setOriginTradeNo(originTradeNo);
            po.setBusinessType(businessTypeEnum.getCode());
            po.setChannelRemark(apiHuadaPO.getRemark());
            po.setCreateTime(time);
            po.setUpdateTime(time);
            huadaOrderMapper.insert(po);

        }catch (Exception e){
            LogUtil.log(channelName + "接口失败 解析返回对象入库出错：" + e.getMessage());
            return null;
        }

        return payUrl;
    }





}
