package com.ai.serviceuser.common;

import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.po.base.CaiyuanOrderPO;
import com.ai.basecommon.core.po.base.SysConfApiCaiyuanPO;
import com.ai.basecommon.core.po.base.SysConfCaiyuanPO;
import com.ai.basecommon.enums.PayBusinessTypeEnum;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.mapper.AlipayOrderMapper;
import com.ai.serviceuser.mapper.CaiyuanOrderMapper;
import com.ai.serviceuser.mapper.SysConfApiMapper;
import com.ai.serviceuser.mapper.SysConfCaiyuanMapper;
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
public class CaiyuanUtilX {

    @Autowired
    private SysConfApiMapper sysConfApiMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Value("${pile.admin.host}")
    private String adminHost;

    @Autowired
    private AlipayOrderMapper alipayOrderMapper;

    @Autowired
    private SysConfCaiyuanMapper sysConfCaiyuanMapper;

    @Autowired
    private CaiyuanOrderMapper caiyuanOrderMapper;



    //支付
    public String pay(PayBusinessTypeEnum businessTypeEnum, Long userId
            , String orderId, BigDecimal amount, SysConfCaiyuanPO confCaiyuanPO, SysConfApiCaiyuanPO apiCaiyuanPO) throws Exception{

        //SysConfApiPO apiPO = this.getConf();

        String notifyUrl = this.adminHost + "/api/notify/caiyuan";

        Long time = System.currentTimeMillis();

        String goodsName = "bet365";

        Map<String,String> param = new HashMap<>();
        param.put("mchId",confCaiyuanPO.getMchId());
        param.put("appId",confCaiyuanPO.getAppId());
        param.put("productId",apiCaiyuanPO.getProduceId());
        param.put("mchOrderNo",orderId);
        param.put("amount",String.valueOf(amount.multiply(new BigDecimal("100")).intValue()));
        param.put("currency","cny");
        param.put("notifyUrl",notifyUrl);
        param.put("subject",goodsName);
        param.put("body","商品描述");
        param.put("reqTime", DateUtil.timestampToDate(time,"yyyyMMddHHmmss"));
        param.put("version", "1.0");
        //String sign = this.getAsciiSort(param);
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
        str += "key="+confCaiyuanPO.getSecretKey();
        //LogUtil.log("拼接的字符串 加入key后是：" + str);

        String sign = EncryptUtil.md5(str);
        LogUtil.log("签名是：" + sign);

        param.put("sign",sign);


        String url = "http://139.5.200.170:56700/api/pay/create_order";
        String r = null;
        try{
            r = HttpUtil.sendPost(url,param);
        }catch (Exception e){
            LogUtil.log("财源支付下单失败：" + e.getMessage());
            return null;
        }

        LogUtil.log("财源接口返回：" + r);

        if(StringUtil.isEmpty(r)){
            return null;
        }

        JSONObject jsonObject = null;
        try{
            jsonObject = JSONObject.parseObject(r);
        }catch (Exception e){
            LogUtil.log("财源支付 解析结果失败：" + e.getMessage());
            return null;
        }
        if(null == jsonObject){
            return null;
        }
        if(!jsonObject.getString("retCode").equals("0")){
            LogUtil.log("财源支付接口失败 返回数据是：" + r);
            throw new Exception(jsonObject.getString("retMsg"));
        }
        if(StringUtil.isEmpty(jsonObject.getString("payJumpUrl"))){
            LogUtil.log("财源支付接口失败 没有支付url 返回数据是：" + r);
            return null;
        }

        //LogUtil.log("返回的JSON是：" + JSONObject);
        String payMethod = jsonObject.getString("payMethod");
        String payUrl = jsonObject.getString("payUrl");
        String payJumpUrl = jsonObject.getString("payJumpUrl");

        CaiyuanOrderPO po = new CaiyuanOrderPO();
        po.setUserId(userId);
        po.setOrderId(orderId);
        po.setTotalAmount(amount);
        po.setGoodsName(goodsName);
        po.setMchId(confCaiyuanPO.getMchId());
        po.setAppId(confCaiyuanPO.getAppId());
        po.setProductId(apiCaiyuanPO.getProduceId());
        //po.setPayOrderId("");
        po.setPayMethod(payMethod);
        po.setBusinessType(businessTypeEnum.getCode());
        po.setChannelRemark(apiCaiyuanPO.getRemark());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        caiyuanOrderMapper.insert(po);

        //  private String payOrderId;
        //  private String payMethod;

        return payJumpUrl;
    }





}
