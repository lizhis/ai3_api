package com.ai.serviceuser.common;

import com.ai.basecommon.core.po.base.*;
import com.ai.basecommon.enums.PayBusinessTypeEnum;
import com.ai.basecommon.utils.EncryptUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.QilinOrderMapper;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class PayQilinUtilX {

    @Value("${pile.admin.host}")
    private String adminHost;

    @Autowired
    private QilinOrderMapper qilinOrderMapper;


    private final String channelName = "麒麟支付";

    //支付
    public String pay(PayBusinessTypeEnum businessTypeEnum, Long userId
            , String orderId, BigDecimal amount, SysConfQilinPO confQilinPO, SysConfApiQilinPO apiQilinPO, String ip) throws Exception{

        String notifyUrl = this.adminHost + "/api/notify/qilin";

        Long time = System.currentTimeMillis();

        String goodsName = "bet365";

        Map<String,Object> param = new HashMap<>();

        param.put("mchNo",confQilinPO.getMchId());
        param.put("mchOrderNo",orderId);
        param.put("payCode",apiQilinPO.getProduceId());
        param.put("payAmount",String.valueOf(amount.multiply(new BigDecimal("100")).intValue()));
        param.put("clientIp",ip);
        param.put("returnUrl","https://www.xxxxx.com");
        param.put("notifyUrl",notifyUrl);
        param.put("remark","支付");

        Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedMap.putAll(param);

        String str = "";
        for(String k : sortedMap.keySet()){
            str += k + "=" + sortedMap.get(k) + "&";
        }
        //str = str.substring(0, str.length() - 1);
        LogUtil.log(channelName + "拼接的字符串是：" + str);
        str += "key="+confQilinPO.getSecretKey();
        LogUtil.log(channelName + "拼接的字符串 加入key后是：" + str);

        String sign = EncryptUtil.md5(str).toUpperCase();
        LogUtil.log(channelName + "签名是：" + sign);

        param.put("sign",sign);

        Map headers = new HashMap();
        headers.put("Content-Type","application/json");

        String url = "http://47.83.209.123:5000/pay-api/order/create/doSelf";
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
        if(!jsonObject.getString("code").equals("200")){
            LogUtil.log(channelName + "接口失败 返回数据是：" + r);
            throw new Exception(jsonObject.getString("message"));
        }

        String payUrl = null;

        try{

            JSONObject dataObj = jsonObject.getJSONObject("data");

            LogUtil.log(channelName + "返回的JSON是：" + dataObj);
            String mchOrderNo = dataObj.getString("mchOrderNo");
            String tradeNo = dataObj.getString("tradeNo");
            payUrl = dataObj.getString("payUrl");

            if(StringUtil.isEmpty(payUrl)){
                LogUtil.log(channelName + "接口失败 没有支付url 返回数据是：" + r);
                return null;
            }

            QilinOrderPO po = new QilinOrderPO();
            po.setUserId(userId);
            po.setOrderId(orderId);
            po.setTotalAmount(amount);
            po.setGoodsName(goodsName);
            po.setMchId(confQilinPO.getMchId());
            po.setProductId(apiQilinPO.getProduceId());
            po.setTradeNo(tradeNo);
            po.setOriginTradeNo(tradeNo);
            po.setBusinessType(businessTypeEnum.getCode());
            po.setChannelRemark(apiQilinPO.getRemark());
            po.setCreateTime(time);
            po.setUpdateTime(time);
            qilinOrderMapper.insert(po);

        }catch (Exception e){
            LogUtil.log(channelName + "接口失败 解析返回对象入库出错：" + e.getMessage());
            return null;
        }

        return payUrl;
    }





}
