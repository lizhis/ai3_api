package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.msg.BillAmountMsgDTO;
import com.ai.basecommon.core.dto.msg.UserAssetTrendsMsgDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.param.user.GoldExchangeParam;
import com.ai.basecommon.core.po.user.UserBalancePO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.UserBalanceVO;
import com.ai.basecommon.enums.AssetTypeEnum;
import com.ai.basecommon.enums.BillAmountTypeEnum;
import com.ai.basecommon.enums.UserAssetTrendsTypeEnum;
import com.ai.basecommon.enums.WsCodeEnum;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.mapper.UserBalanceMapper;
import com.ai.serviceuser.producer.BillAmountProducer;
import com.ai.serviceuser.producer.UserAssetTrendsProducer;
import com.ai.serviceuser.producer.WsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Component
public class BalanceHandler {

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private BillAmountProducer billAmountProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private WsProducer wsProducer;


    //余额信息
    public UserBalanceVO info() throws Exception{

        Long userId = userUtilX.getUserId();

        UserBalancePO balancePO = userBalanceMapper.findByUserId(userId);
        if(null == balancePO){
            balancePO = new UserBalancePO();
            balancePO.setUserId(userId);
            Long time = System.currentTimeMillis();
            balancePO.setCreateTime(time);
            balancePO.setUpdateTime(time);
            userBalanceMapper.insert(balancePO);
            balancePO = userBalanceMapper.findByUserId(userId);
        }
        UserBalanceVO balanceVO = new UserBalanceVO();
        balanceVO.setAmount(balancePO.getAmount());
        balanceVO.setFreezeAmount(balancePO.getFreezeAmount());
        balanceVO.setEnergy(balancePO.getEnergy());
        balanceVO.setIntegral(balancePO.getIntegral());
        balanceVO.setGold(balancePO.getGold());
        return balanceVO;
    }


    //云豆提取
    public BaseVO goldExchange(GoldExchangeParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        Integer gold = 0;
        if(null == param || null == param.getGold() || param.getGold() < 1){
            gold = 0;
        }
        else{
            gold = param.getGold();
        }
        //查询用户当前的云豆余额
        Integer goldBalance = userBalanceMapper.findGoldByUserId(userId);
        if(0 == goldBalance){
            return BaseVO.ok();
        }

        if(gold > goldBalance){
            return BaseVO.error(StatusCodeEnum.NUM_NOT_ENOUGH);
        }
        if(0 == gold){
            gold = goldBalance;
        }

        if(gold < 10){
            return BaseVO.error(StatusCodeEnum.NUM_NOT_ENOUGH);
        }

        int num = gold/10;
        BigDecimal toAmount = new BigDecimal(num);
        Integer toGold = num * 10;

        boolean r = userBalanceMapper.goldExchange(userId,toAmount,toGold);
        if(!r){
            LogUtil.log("云豆提取入库失败");
            return BaseVO.error();
        }

        Long time = System.currentTimeMillis();


        UserAssetTrendsMsgDTO assetTrendsMsgDTO2 = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO2.setUserId(userId);
        assetTrendsMsgDTO2.setAmount(new BigDecimal(toGold));
        assetTrendsMsgDTO2.setTypeEnum(UserAssetTrendsTypeEnum.GOLD_EXCHANGE_SUB.getCode());
        assetTrendsMsgDTO2.setTime(time);
        assetTrendsMsgDTO2.setAssetType(AssetTypeEnum.GOLD.getCode());
        userAssetTrendsProducer.produce(assetTrendsMsgDTO2);


        BillAmountMsgDTO msgDTO = new BillAmountMsgDTO();
        msgDTO.setUserId(userId);
        msgDTO.setOrderId("xxx");
        msgDTO.setAmount(toAmount);
        msgDTO.setTypeEnum(BillAmountTypeEnum.GOLD_EXCHANGE.getCode());
        msgDTO.setTime(time);
        billAmountProducer.produce(msgDTO);

        UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO.setUserId(userId);
        assetTrendsMsgDTO.setAmount(toAmount);
        assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.GOLD_EXCHANGE_ADD.getCode());
        assetTrendsMsgDTO.setTime(time);
        assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
        userAssetTrendsProducer.produce(assetTrendsMsgDTO);

        WsSendDTO  wsSendDTO2 = new WsSendDTO();
        wsSendDTO2.setUserId(userId);
        wsSendDTO2.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(wsSendDTO2);

        return BaseVO.ok();
    }



}
