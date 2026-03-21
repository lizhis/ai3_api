package com.ai.serviceuser.common;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.core.vo.user.BlessingShopOrderDetailVO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.SysConfApiMapper;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kuaidi100.sdk.api.QueryTrack;
import com.kuaidi100.sdk.core.IBaseClient;
import com.kuaidi100.sdk.pojo.HttpResult;
import com.kuaidi100.sdk.request.QueryTrackParam;
import com.kuaidi100.sdk.request.QueryTrackReq;
import com.kuaidi100.sdk.utils.SignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class Kuaidi100UtilX {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private SysConfApiMapper sysConfApiMapper;


    public List<BlessingShopOrderDetailVO.DeliveryDataItem> queryTrack(String deliveryCode, String deliveryNumber) throws Exception {

        if(StringUtil.isEmpty(deliveryCode) || StringUtil.isEmpty(deliveryNumber)){
            return null;
        }


        SysConfApiPO confApiPO = this.getConf();
        if(null == confApiPO || StringUtil.isEmpty(confApiPO.getKuaidi100Key()) || StringUtil.isEmpty(confApiPO.getKuaidi100Customer())){
            LogUtil.log("快递100配置错误");
            return null;
        }

        String key = confApiPO.getKuaidi100Key();
        String secret = confApiPO.getKuaidi100Customer();

        QueryTrackReq queryTrackReq = new QueryTrackReq();
        QueryTrackParam queryTrackParam = new QueryTrackParam();
        queryTrackParam.setCom(deliveryCode);
        queryTrackParam.setNum(deliveryNumber);
        String param = JSON.toJSONString(queryTrackParam);

        queryTrackReq.setParam(param);
        queryTrackReq.setCustomer(secret);
        queryTrackReq.setSign(SignUtils.querySign(param ,key,secret));

        IBaseClient baseClient = new QueryTrack();
        HttpResult httpResult = baseClient.execute(queryTrackReq);
        if(200 != httpResult.getStatus()){
            return null;
        }
        String body = httpResult.getBody();

        JSONObject jsonObject = JSONObject.parseObject(body);
        String state = jsonObject.getString("state");
        if(null == state){
            return null;
        }
        List<BlessingShopOrderDetailVO.DeliveryDataItem> item = jsonObject.getList("data", BlessingShopOrderDetailVO.DeliveryDataItem.class);
        return item;
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
