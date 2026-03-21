package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.BillAmountMsgDTO;
import com.ai.basecommon.core.dto.msg.BillEnergyMsgDTO;
import com.ai.basecommon.core.dto.msg.UserAssetTrendsMsgDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.param.shop.ShopBuyParam;
import com.ai.basecommon.core.param.user.SeasonBuyParam;
import com.ai.basecommon.core.po.shop.ShopOrderPO;
import com.ai.basecommon.core.po.shop.ShopPO;
import com.ai.basecommon.core.po.user.*;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.core.vo.user.SysConfSeasonVO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.OrderIdUtil;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.TransactionUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.BillAmountProducer;
import com.ai.serviceuser.producer.BillEnergyProducer;
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
public class SeasonHandler {

    @Autowired
    private SysConfSeasonMapper sysConfSeasonMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private SeasonRecordMapper seasonRecordMapper;

    @Autowired
    private SeasonUserMapper seasonUserMapper;

    @Autowired
    private BillAmountProducer billAmountProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private WsProducer wsProducer;

    @Autowired
    private UserAddrMapper userAddrMapper;

    @Autowired
    private BillEnergyProducer billEnergyProducer;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private SeasonGiftRecordMapper seasonGiftRecordMapper;

    @Autowired
    private UserMapper userMapper;


    //季卡配置
    @ReadOnly
    public SysConfSeasonVO conf() throws Exception{
        SysConfSeasonPO po = sysConfSeasonMapper.find();
        if(null == po){
            return null;
        }
        SysConfSeasonVO vo = new SysConfSeasonVO();
        vo.setSeasonPrice(po.getSeasonPrice());
        vo.setYearPrice(po.getYearPrice());

        if(null != po.getGift1() && po.getGift1().compareTo(0L) > 0){
            ShopPO shopPO1 = shopMapper.findById(po.getGift1());
            if(null != shopPO1 && StatusEnum.YES.getCode().equals(shopPO1.getStatus()) && IsEnum.NO.getCode().equals(shopPO1.getIsVirtual())){
                vo.setGift1(po.getGift1());
                vo.setGift1Name(shopPO1.getName());
                vo.setGift1Image(shopPO1.getImage());
            }
        }

        if(null != po.getGift2() && po.getGift2().compareTo(0L) > 0){
            ShopPO shopPO2 = shopMapper.findById(po.getGift2());
            if(null != shopPO2 && StatusEnum.YES.getCode().equals(shopPO2.getStatus()) && IsEnum.NO.getCode().equals(shopPO2.getIsVirtual())){
                vo.setGift2(po.getGift2());
                vo.setGift2Name(shopPO2.getName());
                vo.setGift2Image(shopPO2.getImage());
            }
        }

        if(null != po.getGift3() && po.getGift3().compareTo(0L) > 0){
            ShopPO shopPO3 = shopMapper.findById(po.getGift3());
            if(null != shopPO3 && StatusEnum.YES.getCode().equals(shopPO3.getStatus()) && IsEnum.NO.getCode().equals(shopPO3.getIsVirtual())){
                vo.setGift3(po.getGift3());
                vo.setGift3Name(shopPO3.getName());
                vo.setGift3Image(shopPO3.getImage());
            }
        }

        return vo;
    }


    //季卡开通
    public boolean buy(SeasonBuyParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getSeasonType()){
            return false;
        }
        if(!SeasonTypeEnum.SEASON.getCode().equals(param.getSeasonType()) && !SeasonTypeEnum.YEAR.getCode().equals(param.getSeasonType())){
            return false;
        }

        String lockKey = "lock_season_buy_userid_" + userId;
        if(redisUtilX.hasKey(lockKey)){
            BaseException.error(StatusCodeEnum.REQUEST_LIMIT);
        }
        redisUtilX.set(lockKey,"1",2);


        UserPO userPO = userMapper.findByUserId(userId);
        if(null == userPO || !AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
            BaseException.error(StatusCodeEnum.AUTH_PLEASE);
        }

        //查询季卡配置
        SysConfSeasonPO confSeasonPO = sysConfSeasonMapper.find();
        if(null == confSeasonPO){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }

        BigDecimal amount = null;
        int days = 0;
        if(SeasonTypeEnum.SEASON.getCode().equals(param.getSeasonType())){
            amount = confSeasonPO.getSeasonPrice();
            days = 90;
        }
        if(SeasonTypeEnum.YEAR.getCode().equals(param.getSeasonType())){
            amount = confSeasonPO.getYearPrice();
            days = 365;
        }
        if(null == amount || amount.compareTo(BigDecimal.ZERO) < 1){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }

        //检测余额
        BigDecimal balance = userBalanceMapper.findAmountByUserId(userId);
        if(null == balance || balance.compareTo(amount) < 0){
            BaseException.error(StatusCodeEnum.BALANCE_NOT_ENOUGH);
        }

        Long time = System.currentTimeMillis();

        SeasonRecordPO recordPO = new SeasonRecordPO();
        recordPO.setUserId(userId);
        recordPO.setSeasonType(param.getSeasonType());
        recordPO.setDays(days);
        recordPO.setAmount(amount);
        recordPO.setCreateTime(time);
        recordPO.setUpdateTime(time);


        int daysFinal = days;

        BigDecimal amountFinal = amount;

        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{


            //扣钱
            userBalanceMapper.decAmount(userId,amountFinal);

            //购买记录
            seasonRecordMapper.insertGetId(recordPO);

            //季卡用户
            SeasonUserPO seasonUserPO = seasonUserMapper.findByUserId(userId);
            if(null == seasonUserPO){
                //新购
                seasonUserPO = new SeasonUserPO();
                seasonUserPO.setUserId(userId);
                seasonUserPO.setStatus(1);
                seasonUserPO.setExpireTime(time + 3600L*1000*24*daysFinal);
                seasonUserPO.setCreateTime(time);
                seasonUserPO.setUpdateTime(time);
                seasonUserMapper.insert(seasonUserPO);
            }
            else{
                //续费
                if(seasonUserPO.getExpireTime().compareTo(time) < 1){
                    //早就到期了
                    seasonUserPO.setExpireTime(time + 3600L*1000*24*daysFinal);
                }
                else{
                    //还未到期
                    seasonUserPO.setExpireTime(seasonUserPO.getExpireTime() + 3600L*1000*24*daysFinal);
                }
                seasonUserMapper.updateRenew(seasonUserPO.getId(),seasonUserPO.getExpireTime(),time);
            }

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            return false;
        }


        //资金账单
        BillAmountMsgDTO amountMsgDTO = new BillAmountMsgDTO();
        amountMsgDTO.setOrderId(recordPO.getId().toString());
        amountMsgDTO.setUserId(userId);
        amountMsgDTO.setAmount(amount);
        amountMsgDTO.setTypeEnum(BillAmountTypeEnum.SEASON_CARD.getCode());
        amountMsgDTO.setTime(time);
        amountMsgDTO.setRemark("订单号是开通记录的ID");
        billAmountProducer.produce(amountMsgDTO);

        UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO.setUserId(userId);
        assetTrendsMsgDTO.setAmount(amount);
        assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.SEASON_CARD.getCode());
        assetTrendsMsgDTO.setTime(time);
        assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
        userAssetTrendsProducer.produce(assetTrendsMsgDTO);

        WsSendDTO wsSendDTO = new WsSendDTO();
        wsSendDTO.setUserId(userId);
        wsSendDTO.setCode(WsCodeEnum.USER_INFO.getCode());
        wsProducer.produce(wsSendDTO);

        return true;
    }


    //是否领取礼品
    @ReadOnly
    public boolean isGift() throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return false;
        }
        Integer ym = DateUtil.ym();
        int c = seasonGiftRecordMapper.countByUserIdAndYm(userId,ym);
        return c > 0;
    }


    //礼品领取
    public boolean gift(ShopBuyParam param) throws Exception{

        Long userId = userUtilX.getUserId();

        String lockK = "lock_gift_" + userId;
        if(redisUtilX.hasKey(lockK)){
            BaseException.error(StatusCodeEnum.REQUEST_LIMIT);
        }
        redisUtilX.set(lockK,"1",2);

        if(null == param || null == param.getShopId()){
            BaseException.error(StatusCodeEnum.PARAM_ERROR);
        }

        //查商品
        ShopPO shopPO = shopMapper.findById(param.getShopId());
        if(null == shopPO){
            BaseException.error(StatusCodeEnum.SHOP_NO_EXIST);
        }

        if(!StatusEnum.YES.getCode().equals(shopPO.getStatus())){
            BaseException.error(StatusCodeEnum.SHOP_STATUS_ERROR);
        }

        //查询是否季卡会员
        SeasonUserPO seasonUserPO = seasonUserMapper.findByUserId(userId);
        if(null == seasonUserPO || !StatusEnum.YES.getCode().equals(seasonUserPO.getStatus())){
            BaseException.error(StatusCodeEnum.SEASON_NO);
        }

        Integer ym = DateUtil.ym();
        Integer ymd = DateUtil.todayDate();

        //查询本月是否领取过礼品
        int c = seasonGiftRecordMapper.countByUserIdAndYm(userId,ym);
        if(c > 0){
            BaseException.error(StatusCodeEnum.SEASON_GIFT_MONTH_EXIST);
        }


        Long time = System.currentTimeMillis();

        ShopOrderPO po = new ShopOrderPO();
        po.setOrderId(OrderIdUtil.getShopOrderId(userId));
        po.setShopId(shopPO.getId());
        po.setUserId(userId);
        po.setNum(1);
        po.setPrice(0);
        po.setAmount(BigDecimal.ZERO);
        po.setSumEnergy(0);
        po.setSumAmount(BigDecimal.ZERO);
        po.setShopName(shopPO.getName());
        po.setShopImage(shopPO.getImage());
        po.setShopContent(shopPO.getContent());
        po.setShopCateId(shopPO.getCateId());
        po.setShopPrice(shopPO.getPrice());
        po.setShopIsVirtual(shopPO.getIsVirtual());

        //如果不是虚拟物品
        if(!IsEnum.YES.getCode().equals(shopPO.getIsVirtual())){

            if(null == param.getAddrId() || param.getAddrId() < 1){
                BaseException.error(StatusCodeEnum.SHOP_ADDR_PLEASE);
            }

            //查收货地址
            UserAddrPO addrPO = userAddrMapper.findById(param.getAddrId());
            if(null == addrPO || !userId.equals(addrPO.getUserId())){
                BaseException.error(StatusCodeEnum.SHOP_ADDR_ERROR);
            }

            po.setAddrReceiver(addrPO.getReceiver());
            po.setAddrMobile(addrPO.getMobile());
            po.setAddrProvince(addrPO.getProvince());
            po.setAddrCity(addrPO.getCity());
            po.setAddrDistrict(addrPO.getDistrict());
            po.setAddrDetail(addrPO.getDetail());
            po.setAddrCode(addrPO.getCode());
            po.setStatus(ShopOrderStatusEnum.WAIT_DELIVERY.getCode());
        }
        else{
            po.setStatus(ShopOrderStatusEnum.FINISH.getCode());
        }

        po.setCreateTime(time);
        po.setUpdateTime(time);

        SeasonGiftRecordPO giftRecordPO = new SeasonGiftRecordPO();
        giftRecordPO.setUserId(userId);
        giftRecordPO.setShopId(shopPO.getId());
        giftRecordPO.setOrderId(po.getOrderId());
        giftRecordPO.setYm(ym);
        giftRecordPO.setYmd(ymd);
        giftRecordPO.setCreateTime(time);
        giftRecordPO.setUpdateTime(time);



        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{

            shopOrderMapper.insertGetId(po);

            seasonGiftRecordMapper.insert(giftRecordPO);

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            return false;
        }

        BillEnergyMsgDTO energyMsgDTO = new BillEnergyMsgDTO();
        energyMsgDTO.setUserId(userId);
        energyMsgDTO.setOrderId(po.getId().toString());
        energyMsgDTO.setNum(0);
        energyMsgDTO.setTypeEnum(BillEnergyTypeEnum.BUY.getCode());
        energyMsgDTO.setRemark("会员礼品");
        energyMsgDTO.setTime(time);
        billEnergyProducer.produce(energyMsgDTO);

        UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO.setUserId(userId);
        assetTrendsMsgDTO.setAmount(BigDecimal.ZERO);
        assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.BUY.getCode());
        assetTrendsMsgDTO.setTime(time);
        assetTrendsMsgDTO.setRemark("会员礼品");
        assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
        userAssetTrendsProducer.produce(assetTrendsMsgDTO);


        return true;
    }



}
