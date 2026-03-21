package com.ai.servicebusiness.async;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.msg.BillAmountMsgDTO;
import com.ai.basecommon.core.dto.msg.BillEnergyMsgDTO;
import com.ai.basecommon.core.dto.msg.InviteRebateMsgDTO;
import com.ai.basecommon.core.dto.msg.UserAssetTrendsMsgDTO;
import com.ai.basecommon.core.po.base.SysConfWithdrawPO;
import com.ai.basecommon.core.po.pro.ProOrderPO;
import com.ai.basecommon.core.po.user.UserSmallQuotaPO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.utils.LogUtil;
import com.ai.servicebusiness.commom.RedisUtilX;
import com.ai.servicebusiness.mapper.SysConfWithdrawMapper;
import com.ai.servicebusiness.mapper.UserBalanceMapper;
import com.ai.servicebusiness.mapper.UserSmallQuotaMapper;
import com.ai.servicebusiness.producer.*;
import com.ai.servicebusiness.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description
 * @Author
 */
@Component
public class CarPutAsync {

    @Autowired
    private IUserService userService;

    @Autowired
    private GiveSmallQuotaProducer giveSmallQuotaProducer;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private BillAmountProducer billAmountProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private InviteRebateProducer inviteRebateProducer;

    @Autowired
    private BillEnergyProducer billEnergyProducer;

    @Autowired
    private SysConfWithdrawMapper sysConfWithdrawMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private UserSmallQuotaMapper userSmallQuotaMapper;


    @Async
    public synchronized void putAfter(ProOrderPO proOrderPO) throws Exception{
        if(null == proOrderPO || null == proOrderPO.getUserId()){
            return;
        }

        //处理租赁赠送
        try{
            this.disposeGiveAmount(proOrderPO);
        }catch (Exception e){
            LogUtil.log(e.getMessage());
        }

        //处理提现券赠送
        try{
            this.disposeGiveQuota(proOrderPO);
        }catch (Exception e){
            LogUtil.log(e.getMessage());
        }

        InviteRebateMsgDTO inviteRebateMsgDTO = new InviteRebateMsgDTO();
        inviteRebateMsgDTO.setUserId(proOrderPO.getUserId());
        inviteRebateMsgDTO.setPayAmount(proOrderPO.getPayAmount());
        inviteRebateMsgDTO.setOrderId(proOrderPO.getOrderId());
        inviteRebateProducer.produce(inviteRebateMsgDTO);

        //giveSmallQuotaProducer.produce(proOrderPO.getUserId());
    }


    //租赁赠送
    private void disposeGiveAmount(ProOrderPO proOrderPO) {

        if(null == proOrderPO.getGiveAmount() || proOrderPO.getGiveAmount().compareTo(BigDecimal.ZERO) < 1){
            return;
        }
        if(2 == proOrderPO.getGiveGoodsType()){
            return;
        }
        Long userId = proOrderPO.getUserId();

        BigDecimal giveAmount = proOrderPO.getGiveAmount();


        if(3 == proOrderPO.getGiveGoodsType()){

            boolean r = userBalanceMapper.incEnergy(userId,giveAmount.intValue());
            if(!r){
                LogUtil.log("租赁项目赠送云币 修改余额失败，订单号是："+ proOrderPO.getOrderId()+"，赠送云币数量是："+giveAmount);
                return;
            }

            //资金账单
            BillEnergyMsgDTO energyMsgDTO = new BillEnergyMsgDTO();
            energyMsgDTO.setOrderId(proOrderPO.getOrderId());
            energyMsgDTO.setUserId(userId);
            energyMsgDTO.setNum(giveAmount.intValue());
            energyMsgDTO.setTypeEnum(BillEnergyTypeEnum.LEASE_AMOUNT.getCode());
            energyMsgDTO.setTime(proOrderPO.getCreateTime());
            billEnergyProducer.produce(energyMsgDTO);

            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(userId);
            assetTrendsMsgDTO.setAmount(giveAmount);
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.LEASE_AMOUNT.getCode());
            assetTrendsMsgDTO.setTime(proOrderPO.getCreateTime());
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);

        }
        else{
            boolean r = userBalanceMapper.incAmount(userId,giveAmount);
            if(!r){
                LogUtil.log("租赁项目赠送现金 修改余额失败，订单号是："+ proOrderPO.getOrderId()+"，赠送金额是："+giveAmount);
                return;
            }

            //资金账单
            BillAmountMsgDTO amountMsgDTO = new BillAmountMsgDTO();
            amountMsgDTO.setOrderId(proOrderPO.getOrderId());
            amountMsgDTO.setUserId(userId);
            amountMsgDTO.setAmount(giveAmount);
            amountMsgDTO.setTypeEnum(BillAmountTypeEnum.LEASE_AMOUNT.getCode());
            amountMsgDTO.setTime(proOrderPO.getCreateTime());
            billAmountProducer.produce(amountMsgDTO);

            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(userId);
            assetTrendsMsgDTO.setAmount(giveAmount);
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.LEASE_AMOUNT.getCode());
            assetTrendsMsgDTO.setTime(proOrderPO.getCreateTime());
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);

        }


    }


    //提现券赠送
    private void disposeGiveQuota(ProOrderPO proOrderPO) {
        if(null == proOrderPO){
            return;
        }
        if(null == proOrderPO.getGiveQuotaAmount() || proOrderPO.getGiveQuotaAmount() < 1){
            return;
        }
        int quotaActiveSignDay = loadQuotaActiveSignDay();

        UserSmallQuotaPO userSmallQuotaPO = new UserSmallQuotaPO();
        userSmallQuotaPO.setUserId(proOrderPO.getUserId());
        userSmallQuotaPO.setAmount(proOrderPO.getGiveQuotaAmount());
        userSmallQuotaPO.setChannel(UserSmallQuotaChannelEnum.PROJECT.getCode());
        userSmallQuotaPO.setStatus(0 == quotaActiveSignDay ? StatusEnum.YES.getCode() : StatusEnum.NEW.getCode());
        userSmallQuotaPO.setOrderId(proOrderPO.getOrderId());
        userSmallQuotaPO.setCreateTime(proOrderPO.getCreateTime());
        userSmallQuotaPO.setUpdateTime(proOrderPO.getUpdateTime());
        userSmallQuotaMapper.insert(userSmallQuotaPO);
    }

    private int loadQuotaActiveSignDay(){
        String key = RedisKey.conf_withdraw;
        SysConfWithdrawPO confWithdrawPO = null;
        if(redisUtilX.hasKey(key)){
            confWithdrawPO = redisUtilX.getObj(key, SysConfWithdrawPO.class);
        }
        if(null != confWithdrawPO){
            return confWithdrawPO.getQuotaActiveSignDay();
        }
        confWithdrawPO = sysConfWithdrawMapper.find();
        if(null == confWithdrawPO){
            return 0;
        }
        redisUtilX.setObj(key, confWithdrawPO,600);
        return confWithdrawPO.getQuotaActiveSignDay();
    }



}
