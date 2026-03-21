package com.ai.serviceuser.common;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.AlipayOrderPO;
import com.ai.basecommon.core.po.base.SysConfApiAlipayPO;
import com.ai.basecommon.core.po.base.SysConfApiAlipayScanPO;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.enums.AlipayBusinessTypeEnum;
import com.ai.basecommon.enums.PayWayEnum;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.AlipayOrderMapper;
import com.ai.serviceuser.mapper.SysConfApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Component
public class AlipayUtilX {

    @Autowired
    private SysConfApiMapper sysConfApiMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Value("${pile.admin.host}")
    private String adminHost;

    @Autowired
    private AlipayOrderMapper alipayOrderMapper;

    //支付
    public String pay(AlipayBusinessTypeEnum businessTypeEnum, String subject, Long userId
            , String orderId, BigDecimal amount, SysConfApiAlipayPO apiPO) throws Exception{

        //SysConfApiPO apiPO = this.getConf();

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(apiPO.getAppid());
        alipayConfig.setPrivateKey(apiPO.getPrivateKey());
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(apiPO.getPublicKey());
        alipayConfig.setCharset("utf-8");
        alipayConfig.setSignType("RSA2");
        alipayConfig.setEncryptType(apiPO.getEncryptType());
        alipayConfig.setEncryptKey(apiPO.getEncryptKey());

        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

        String url = this.adminHost + "/api/notify/alipay";
        LogUtil.log("通知地址：" + url);
        request.setNotifyUrl(this.adminHost + "/api/notify/alipay");


        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(orderId);
        model.setTotalAmount(amount.toString());
        model.setSubject(subject);
        //model.setPassbackParams(URLEncoder.encode("{‘businessType’:"+businessTypeEnum.getCode()+"}", StandardCharsets.UTF_8));
        model.setPassbackParams(businessTypeEnum.getCode().toString());
        request.setBizModel(model);

        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        if(!response.isSuccess()){
            LogUtil.log("支付宝接口返回失败：" + response);
            return null;
        }

        Long time = System.currentTimeMillis();
        AlipayOrderPO po = new AlipayOrderPO();
        po.setUserId(userId);
        po.setOrderId(orderId);
        po.setTotalAmount(amount);
        po.setGoodsName(subject);
        po.setBusinessType(businessTypeEnum.getCode());
        po.setPayWay(PayWayEnum.ALIPAY.getCode());
        po.setChannelRemark(apiPO.getRemark());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        alipayOrderMapper.insert(po);
        return response.getBody();
    }



    public String payScan(AlipayBusinessTypeEnum businessTypeEnum, String subject, Long userId
            , String orderId, BigDecimal amount, SysConfApiAlipayScanPO apiPO) throws Exception{

        //SysConfApiPO apiPO = this.getConf();

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(apiPO.getAppid());
        alipayConfig.setPrivateKey(apiPO.getPrivateKey());
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(apiPO.getPublicKey());
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");

        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        // 构造请求参数以调用接口
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();


        // 设置商户订单号
        model.setOutTradeNo(orderId);

        // 设置订单总金额
        model.setTotalAmount(amount.toString());

        // 设置订单标题
        model.setSubject(subject);


        // 设置商户门店编号
        //model.setStoreId("NJ_001");

        // 设置商户操作员编号
        //model.setOperatorId("yx_001");

        // 设置产品码
        //model.setProductCode("FACE_TO_FACE_PAYMENT");

        // 设置订单附加信息
        //model.setBody("Iphone6 16G");

        // 设置订单包含的商品列表信息
/*        List<GoodsDetail> goodsDetail = new ArrayList<GoodsDetail>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);*/

        // 设置商户的原始订单号
        //model.setMerchantOrderNo("20161008001");

        // 设置可打折金额
        //model.setDiscountableAmount("80.00");

        // 设置商户传入业务信息
        //BusinessParams businessParams = new BusinessParams();
        //businessParams.setMcCreateTradeIp("127.0.0.1");
        //model.setBusinessParams(businessParams);

        // 设置卖家支付宝用户ID
        //model.setSellerId("2088102146225135");

        // 设置商户机具终端编号
        //model.setTerminalId("NJ_T_001");

        model.setPassbackParams(businessTypeEnum.getCode().toString());

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");


        String url = this.adminHost + "/api/notify/alipayScan";
        //LogUtil.log("当面付通知地址：" + url);
        request.setNotifyUrl(url);


        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        if(!response.isSuccess()){
            LogUtil.log("当面付接口返回失败：" + response);
            return null;
        }

        String qrcode = null;
        try{
            JSONObject jsonObject = JSONObject.parseObject(response.getBody());
            JSONObject content = jsonObject.getJSONObject("alipay_trade_precreate_response");
            qrcode = content.getString("qr_code");
        }catch (Exception e){
            LogUtil.log("当面付解析结果错误：" + response.getBody());
            return null;
        }


        Long time = System.currentTimeMillis();
        AlipayOrderPO po = new AlipayOrderPO();
        po.setUserId(userId);
        po.setOrderId(orderId);
        po.setTotalAmount(amount);
        po.setGoodsName(subject);
        po.setBusinessType(businessTypeEnum.getCode());
        po.setPayWay(PayWayEnum.ALIPAY_SCAN.getCode());
        po.setChannelRemark(apiPO.getRemark());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        alipayOrderMapper.insert(po);
        return qrcode;
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
        if(StringUtil.isEmpty(confPO.getAlipayAppid()) || StringUtil.isEmpty(confPO.getAlipayPayPublicKey()) || StringUtil.isEmpty(confPO.getAlipayAppPrivateKey()) || StringUtil.isEmpty(confPO.getAlipayEncryptType())){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }
        redisUtilX.setObj(RedisKey.conf_api,confPO,600);
        return confPO;
    }



}
