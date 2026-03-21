package com.ai.serviceuser.async;


import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.BillEnergyMsgDTO;
import com.ai.basecommon.core.dto.user.UserEnergyChangeDTO;
import com.ai.basecommon.core.po.base.*;
import com.ai.basecommon.core.po.user.*;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.common.TransactionUtilX;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.*;
import com.alibaba.fastjson2.JSONArray;
import com.ai.basecommon.core.dto.ad.AdVerifyParamDTO;
import com.ai.basecommon.core.dto.map.IpDTO;
import com.ai.basecommon.core.dto.msg.BillAmountMsgDTO;
import com.ai.basecommon.core.dto.msg.UserAssetTrendsMsgDTO;
import com.ai.basecommon.core.dto.msg.UserDataMsgDTO;
import com.ai.basecommon.core.dto.user.UserBalanceChangeDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.po.shop.GiftCodePO;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.SmsUtilX;
import com.ai.serviceuser.service.IBaseAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * @Description
 * @Author 
 */
@Component
public class UserAsync {

    @Autowired
    private UserDataMapper userDataMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InviteLevelMapper inviteLevelMapper;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private BillAmountProducer billAmountProducer;

    @Autowired
    private BillEnergyProducer billEnergyProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private WsProducer wsProducer;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private GiveSmallQuotaProducer giveSmallQuotaProducer;

    @Autowired
    private UserDataProducer userDataProducer;

    @Autowired
    private IBaseAdService baseAdService;

    @Autowired
    private InviteAuthRecordMapper inviteAuthRecordMapper;

    @Autowired
    private SysConfGiveGiftCodeMapper sysConfGiveGiftCodeMapper;

    @Autowired
    private GiftCodeMapper giftCodeMapper;

    @Autowired
    private SmsUtilX smsUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private SignRecordMapper signRecordMapper;

    @Autowired
    private UserAlipayMapper userAlipayMapper;

    @Autowired
    private SysConfWithdrawMapper sysConfWithdrawMapper;

    @Autowired
    private UserSmallQuotaMapper userSmallQuotaMapper;

    @Autowired
    private UserBlessingMapper userBlessingMapper;

    @Autowired
    private SysConfBlessingMapper sysConfBlessingMapper;

    @Value("${spring.profiles.active}")
    private String profile;

    @Async
    public void registerAfter(UserPO userPO){
        if(null == userPO){
            return;
        }
        Long userId = userPO.getUserId();
        if(null == userId){
            return;
        }
        Long time = System.currentTimeMillis();


        //送小额提现券
        try{
            this.disposeRegisterQuota(userPO);
        }catch (Exception e){
            LogUtil.log(e.getMessage());
        }


        /*int c = userDataMapper.countByUserId(userId);
        if(c > 0){
            userDataMapper.deleteByUserId(userId);
        }*/

        UserDataPO po = new UserDataPO();
        po.setUserId(userId);
        po.setCreateTime(time);
        po.setUpdateTime(time);
        userDataMapper.insert(po);

        //有上级
        if(null != userPO.getInviteUserId()){
            try{
                this.agentInit(userPO);
            }catch (Exception e){
                LogUtil.log(e.getMessage());
            }
        }

        SysConfigPO configPO = sysConfigMapper.find();
        if(null != configPO){

            if(null != configPO.getRegisterAmount() && configPO.getRegisterAmount().compareTo(BigDecimal.ZERO) > 0){

                //增加余额
                userBalanceMapper.incAmount(userId,configPO.getRegisterAmount());


                //账单
                BillAmountMsgDTO amountMsgDTO = new BillAmountMsgDTO();
                amountMsgDTO.setUserId(userId);
                amountMsgDTO.setOrderId("xxx");
                amountMsgDTO.setTypeEnum(BillAmountTypeEnum.REGISTER_AMOUNT.getCode());
                amountMsgDTO.setAmount(configPO.getRegisterAmount());
                amountMsgDTO.setTime(time);
                billAmountProducer.produce(amountMsgDTO);

                //资产动态
                UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
                assetTrendsMsgDTO.setUserId(userId);
                assetTrendsMsgDTO.setAmount(configPO.getRegisterAmount());
                assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.REGISTER_AMOUNT.getCode());
                assetTrendsMsgDTO.setTime(time);
                assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
                userAssetTrendsProducer.produce(assetTrendsMsgDTO);
            }

        }


        //送福卡

/*
        try{
            this.disposeRegisterBlessing(userPO);
        }catch (Exception e){
            LogUtil.log("注册送福卡失败："+e.getMessage());
        }
*/

    }

    private void disposeRegisterQuota(UserPO userPO){
        if(null == userPO){
            return;
        }
        Long userId = userPO.getUserId();

        SysConfWithdrawPO sysConfWithdrawPO = sysConfWithdrawMapper.find();
        if(null == sysConfWithdrawPO){
            return;
        }
        if(!IsEnum.YES.getCode().equals(sysConfWithdrawPO.getQuotaIsOpen())){
            return;
        }
        if(null == sysConfWithdrawPO.getQuotaUserGiveAmount() || sysConfWithdrawPO.getQuotaUserGiveAmount() < 1){
            return;
        }
        //赠送一张
        Long time = System.currentTimeMillis();
        UserSmallQuotaPO userSmallQuotaPO = new UserSmallQuotaPO();
        userSmallQuotaPO.setUserId(userId);
        userSmallQuotaPO.setAmount(sysConfWithdrawPO.getQuotaUserGiveAmount());
        userSmallQuotaPO.setChannel(UserSmallQuotaChannelEnum.REGISTER.getCode());
        userSmallQuotaPO.setStatus(sysConfWithdrawPO.getQuotaActiveSignDay() == 0 ? StatusEnum.YES.getCode() : StatusEnum.NEW.getCode());
        userSmallQuotaPO.setStartTime(time);
        userSmallQuotaPO.setCreateTime(time);
        userSmallQuotaPO.setUpdateTime(time);
        userSmallQuotaMapper.insert(userSmallQuotaPO);
    }

    //福卡
    private void disposeRegisterBlessing(UserPO userPO){
        if(null == userPO){
            return;
        }

        SysConfBlessingPO sysConfBlessingPO = sysConfBlessingMapper.find();
        if(null == sysConfBlessingPO || IsEnum.NO.getCode().equals(sysConfBlessingPO.getIsOpen())){
            LogUtil.log("福卡活动未开启：" + sysConfBlessingPO);
            return;
        }

        Long userId = userPO.getUserId();
        Integer blessingType = BlessingTypeEnum.CARD1.getCode();
        int s = userBlessingMapper.existCard(userId,blessingType);
        if(s > 0){
            return;
        }
        Integer ymd = DateUtil.todayDate();
        UserBlessingPO userBlessingPO = new UserBlessingPO();
        userBlessingPO.setUserId(userId);
        userBlessingPO.setBlessingType(blessingType);
        userBlessingPO.setYmd(ymd);
        userBlessingPO.setCreateTime(userPO.getCreateTime());
        userBlessingPO.setUpdateTime(userPO.getCreateTime());
        userBlessingMapper.insert(userBlessingPO);
    }

    //福卡
    private void disposeAuthBlessing(Long userId){
        if(null == userId){
            return;
        }

        SysConfBlessingPO sysConfBlessingPO = sysConfBlessingMapper.find();
        if(null == sysConfBlessingPO || IsEnum.NO.getCode().equals(sysConfBlessingPO.getIsOpen())){
            LogUtil.log("福卡活动未开启：" + sysConfBlessingPO);
            return;
        }

        Integer blessingType = BlessingTypeEnum.CARD2.getCode();
        int s = userBlessingMapper.existCard(userId,blessingType);
        if(s > 0){
            return;
        }
        Integer ymd = DateUtil.todayDate();
        Long time = System.currentTimeMillis();
        UserBlessingPO userBlessingPO = new UserBlessingPO();
        userBlessingPO.setUserId(userId);
        userBlessingPO.setBlessingType(blessingType);
        userBlessingPO.setYmd(ymd);
        userBlessingPO.setCreateTime(time);
        userBlessingPO.setUpdateTime(time);
        userBlessingMapper.insert(userBlessingPO);

        //校验福卡是否集齐
        this.checkBlessingFull(userId);
    }

    //福卡
    private void disposeSignBlessing(Long userId){
        if(null == userId){
            return;
        }

        SysConfBlessingPO sysConfBlessingPO = sysConfBlessingMapper.find();
        if(null == sysConfBlessingPO || IsEnum.NO.getCode().equals(sysConfBlessingPO.getIsOpen()) || null == sysConfBlessingPO.getSignStartTime()){
            LogUtil.log("福卡活动未开启：" + sysConfBlessingPO);
            return;
        }

        Integer ymd = DateUtil.todayDate();
        Long time = System.currentTimeMillis();

        Long signStartTime = sysConfBlessingPO.getSignStartTime();
        if(signStartTime > time){
            LogUtil.log("福卡签到时间未开始 配置的签到开始时间是：" + DateUtil.timestampToDate(signStartTime,"yyyy-MM-dd"));
            return;
        }

        int s = userBlessingMapper.existCard(userId,BlessingTypeEnum.CARD3.getCode());
        if(0 == s){
            UserBlessingPO userBlessingPO = new UserBlessingPO();
            userBlessingPO.setUserId(userId);
            userBlessingPO.setBlessingType(BlessingTypeEnum.CARD3.getCode());
            userBlessingPO.setYmd(ymd);
            userBlessingPO.setCreateTime(time);
            userBlessingPO.setUpdateTime(time);
            userBlessingMapper.insert(userBlessingPO);
            return;
        }

        s = userBlessingMapper.existCard(userId,BlessingTypeEnum.CARD4.getCode());
        if(0 == s){

            //查询是否够格
            int d = signRecordMapper.countByStartTime(userId,signStartTime);
            if(d < 3){
                return;
            }

            UserBlessingPO userBlessingPO = new UserBlessingPO();
            userBlessingPO.setUserId(userId);
            userBlessingPO.setBlessingType(BlessingTypeEnum.CARD4.getCode());
            userBlessingPO.setYmd(ymd);
            userBlessingPO.setCreateTime(time);
            userBlessingPO.setUpdateTime(time);
            userBlessingMapper.insert(userBlessingPO);
            return;
        }

        s = userBlessingMapper.existCard(userId,BlessingTypeEnum.CARD5.getCode());
        if(s > 0){
            return;
        }

        //查询是否够格
        int d = signRecordMapper.countByStartTime(userId,signStartTime);
        if(d < 7){
            return;
        }

        UserBlessingPO userBlessingPO = new UserBlessingPO();
        userBlessingPO.setUserId(userId);
        userBlessingPO.setBlessingType(BlessingTypeEnum.CARD5.getCode());
        userBlessingPO.setYmd(ymd);
        userBlessingPO.setCreateTime(time);
        userBlessingPO.setUpdateTime(time);
        userBlessingMapper.insert(userBlessingPO);


        //校验福卡是否集齐
        this.checkBlessingFull(userId);
    }


    private void checkBlessingFull(Long userId){
        BigDecimal blessingAmount = userDataMapper.findBlessingAmount(userId);
        if(null != blessingAmount && blessingAmount.compareTo(BigDecimal.ZERO) > 0){
            LogUtil.log("校验福卡集齐，该用户已获得过福卡奖励，用户ID是：" + userId);
            return;
        }
        int x = userBlessingMapper.countByUserIdPassInvite(userId);
        if(x < 5){
            return;
        }
        //5 6 6.8  8.9  9  10  12.8  13.8  14.8
        BigDecimal[] moneyArr = {
                new BigDecimal("5"),
                new BigDecimal("6"),
                new BigDecimal("6.8"),
                new BigDecimal("8.9"),
                new BigDecimal("9"),
                new BigDecimal("10"),
                new BigDecimal("12.8"),
                new BigDecimal("13.8"),
                new BigDecimal("14.8"),
        };
        Random random = new Random();
        int randomIndex = random.nextInt(moneyArr.length);  // 生成 0 到 moneyArr.length-1 之间的随机数

        // 获取随机元素
        BigDecimal money = moneyArr[randomIndex];


        //5-28元
        //BigDecimal money = new BigDecimal(CommonUtil.getRandom(5,28));

        Long time = System.currentTimeMillis();

        //增加余额
        userBalanceMapper.incAmount(userId,money);

        userDataMapper.updateBlessingAmount(userId,money);

        //账单
        BillAmountMsgDTO amountMsgDTO = new BillAmountMsgDTO();
        amountMsgDTO.setUserId(userId);
        amountMsgDTO.setOrderId("xxx");
        amountMsgDTO.setTypeEnum(BillAmountTypeEnum.BLESSING.getCode());
        amountMsgDTO.setAmount(money);
        amountMsgDTO.setTime(time);
        billAmountProducer.produce(amountMsgDTO);

        //资产动态
        UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO.setUserId(userId);
        assetTrendsMsgDTO.setAmount(money);
        assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.BLESSING.getCode());
        assetTrendsMsgDTO.setTime(time);
        assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
        userAssetTrendsProducer.produce(assetTrendsMsgDTO);


        //推送
        WsSendDTO sendDTO = new WsSendDTO();
        sendDTO.setUserId(userId);
        sendDTO.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(sendDTO);

    }


    //广告回传
    @Async
    public void adCallback(Long userId,String deviceId,String ip) {
        if(!profile.contains("prod")){
            return;
        }
        if(StringUtil.isEmpty(deviceId)){
            return;
        }
        if(null == userId){
            return;
        }

        //设备ID去查设备表  拿到oaid  如果oaid非空  则发往ad服务
        DeviceInfoPO deviceInfoPO = deviceInfoMapper.findLastByDeviceId(deviceId);
        if(null == deviceInfoPO){
            LogUtil.log("没有进站信息，退出：" + deviceId);
            return;
        }

        String id = null;

        int platform = CommonUtil.getDevicePlatform(deviceId);
        if(1 == platform){
            if(!StringUtil.isEmpty(deviceInfoPO.getOaid())){
                id = deviceInfoPO.getOaid();
            }
        }
        else if(2 == platform){
            if(!StringUtil.isEmpty(deviceInfoPO.getIdfa())){
                id = deviceInfoPO.getIdfa();
            }
        }
        else {
            return;
        }
        if(StringUtil.isEmpty(id)){
            LogUtil.log("注册归因失败 ID是空 设备号是：" + deviceId);
            return;
        }

        try{

            AdVerifyParamDTO paramDTO = new AdVerifyParamDTO();
            paramDTO.setDeviceId(deviceId);
            paramDTO.setCheckId(id);
            paramDTO.setIp(ip);

            //转化验证
            Integer channel = baseAdService.verify(paramDTO);

            if(null != channel && channel > 0){

                //更新来源渠道
                userMapper.updateChannel(userId,channel);

                if(UserChannelEnum.OCEANENGINE.getCode().equals(channel) || UserChannelEnum.TENCENT.getCode().equals(channel) || UserChannelEnum.TENCENT2.getCode().equals(channel)){
                    String adRemainKey = "ad_remain_id_" + deviceId;
                    Long startTime =  DateUtil.getTodayStartTime() + 86400000L;
                    Long endTime = startTime + 86400000L;
                    Long time = System.currentTimeMillis();
                    Long t = (endTime - time) / 1000;
                    //LogUtil.log("用户注册更新渠道 巨量引擎 留存验证时间为" + DateUtil.timestampToDate(startTime,null) + "，留存结束时间为" + DateUtil.timestampToDate(endTime,null) + "，缓存的key为"+adRemainKey+"，缓存的有效期为" + t/60 + "分钟");
                    //redisUtilX.set(adRemainKey,startTime.toString(),t.intValue());
                }
            }

        }catch (Exception e){
            LogUtil.log("转化验证失败：" + e.getMessage());
        }

    }


    //广告注册归因
    public void adRegister(Long userId,String deviceId){
        if(StringUtil.isEmpty(deviceId)){
            return;
        }
        if(null == userId){
            return;
        }
        LogUtil.log("广告归因：注册，userId是：" + userId + "，设备号是：" + deviceId);

        Integer userChannel = 0;

        String s = redisUtilX.get(RedisKey.ads_deviceid_channel_ + deviceId);
        if(!StringUtil.isEmpty(s)){
            try{
                userChannel = Integer.parseInt(s);
            }catch (Exception e){}
        }
        if(0 == userChannel){
            LogUtil.log("该设备没有渠道");
            return;
        }

        if(userChannel.equals(UserChannelEnum.OCEANENGINE.getCode())){

            LogUtil.log("该设备的来源渠道是：巨量");

            String callbackUrl = redisUtilX.get(RedisKey.ads_deviceid_callbackurl_oceanengine_ + deviceId);
            if(!StringUtil.isEmpty(callbackUrl)){
                callbackUrl += "&event_type=active_register";
                LogUtil.log("注册上报callbackUrl：" + callbackUrl);
                String re = HttpUtil.sendGet(callbackUrl);
                LogUtil.log("注册上报结果：" + re);
                userMapper.updateChannel(userId,userChannel);
            }

        }


    }


    @Async
    public void authAfter(Long userId,String realName){


        Long time = System.currentTimeMillis();

        UserAlipayPO userAlipayPO = userAlipayMapper.findByUserId(userId);
        if(null != userAlipayPO){
            userAlipayMapper.updateRealName(userId,realName,time);
        }

        SysConfigPO configPO = sysConfigMapper.find();
        if(null != configPO){
            if(null != configPO.getAuthAmount() && configPO.getAuthAmount().compareTo(BigDecimal.ZERO) > 0){

                //增加余额
                userBalanceMapper.incAmount(userId,configPO.getAuthAmount());


                //账单
                BillAmountMsgDTO amountMsgDTO = new BillAmountMsgDTO();
                amountMsgDTO.setUserId(userId);
                amountMsgDTO.setOrderId("xxx");
                amountMsgDTO.setTypeEnum(BillAmountTypeEnum.AUTH_AMOUNT.getCode());
                amountMsgDTO.setAmount(configPO.getAuthAmount());
                amountMsgDTO.setTime(time);
                billAmountProducer.produce(amountMsgDTO);

                //资产动态
                UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
                assetTrendsMsgDTO.setUserId(userId);
                assetTrendsMsgDTO.setAmount(configPO.getAuthAmount());
                assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.AUTH_AMOUNT.getCode());
                assetTrendsMsgDTO.setTime(time);
                assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
                userAssetTrendsProducer.produce(assetTrendsMsgDTO);


            }
        }



        //送福卡
/*        try{
            this.disposeAuthBlessing(userId);
        }catch (Exception e){
            LogUtil.log("实名送福卡失败："+e.getMessage());
        }*/


        //上级
        InviteLevelPO inviteLevelPO1 = inviteLevelMapper.findParent1(userId);
        if(null != inviteLevelPO1){
            InviteAuthRecordPO inviteAuthRecordPO = new InviteAuthRecordPO();
            inviteAuthRecordPO.setUserId(inviteLevelPO1.getUserId());
            inviteAuthRecordPO.setChildUserId(userId);
            inviteAuthRecordPO.setCreateTime(time);
            inviteAuthRecordPO.setUpdateTime(time);
            inviteAuthRecordMapper.insert(inviteAuthRecordPO);

            //查询邀请赠送福利码的配置 tw_sys_conf_give_gift_code
            SysConfGiveGiftCodePO sysConfGiveGiftCodePO = sysConfGiveGiftCodeMapper.find();
            if(null != sysConfGiveGiftCodePO && IsEnum.YES.getCode().equals(sysConfGiveGiftCodePO.getIsOpenInvite()) && !StringUtil.isEmpty(sysConfGiveGiftCodePO.getInviteTpl())){

                if(null == sysConfGiveGiftCodePO.getInviteNum() || sysConfGiveGiftCodePO.getInviteNum() < 1){
                    sysConfGiveGiftCodePO.setInviteNum(1);
                }

                GiftCodePO giftCodePO = giftCodeMapper.findLastForInvite(inviteLevelPO1.getUserId());
                Long startTime = 0L;
                if(null != giftCodePO){
                    startTime = giftCodePO.getCreateTime();
                }
                int c = inviteAuthRecordMapper.countByTime(inviteLevelPO1.getUserId(), startTime);
                if(c >= sysConfGiveGiftCodePO.getInviteNum()){

                    giftCodePO = new GiftCodePO();
                    giftCodePO.setCode(this.generateGiftCode());
                    giftCodePO.setUserId(inviteLevelPO1.getUserId());
                    giftCodePO.setDayNum(1);
                    giftCodePO.setMaxNum(1);
                    //giftCodePO.setRemark("邀请赠送");
                    giftCodePO.setSource(GiftCodeSourceEnum.INVITE.getCode());
                    giftCodePO.setCreateTime(time);
                    giftCodePO.setUpdateTime(time);
                    int s = giftCodeMapper.insert(giftCodePO);
                    if(s > 0){
                        String tel = userMapper.findTelByUserId(inviteLevelPO1.getUserId());
                        if(!StringUtil.isEmpty(tel)){
                            try{
                                smsUtilX.sendGiftCode(tel,giftCodePO.getCode());
                            }catch (Exception e){
                                LogUtil.log("发送福利码短信失败："+e.getMessage());
                            }
                        }
                    }

                }

            }


            //送5c
            int energy = 5;

            //增加余额 上级
            userBalanceMapper.incEnergy(inviteLevelPO1.getUserId(),energy);

            //账单
            BillEnergyMsgDTO energyMsgDTO = new BillEnergyMsgDTO();
            energyMsgDTO.setUserId(inviteLevelPO1.getUserId());
            energyMsgDTO.setOrderId("xxx");
            energyMsgDTO.setTypeEnum(BillEnergyTypeEnum.INVITE.getCode());
            energyMsgDTO.setNum(energy);
            energyMsgDTO.setTime(time);
            billEnergyProducer.produce(energyMsgDTO);

            //资产动态
            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(inviteLevelPO1.getUserId());
            assetTrendsMsgDTO.setAmount(new BigDecimal(energy));
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.INVITE.getCode());
            assetTrendsMsgDTO.setTime(time);
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);

            //推送
            WsSendDTO sendDTO = new WsSendDTO();
            sendDTO.setUserId(inviteLevelPO1.getUserId());
            sendDTO.setCode(WsCodeEnum.USER_BALANCE.getCode());
            wsProducer.produce(sendDTO);



            //增加余额  下级
            userBalanceMapper.incEnergy(userId,energy);

            //账单
            BillEnergyMsgDTO energyMsgDTO2 = new BillEnergyMsgDTO();
            energyMsgDTO2.setUserId(userId);
            energyMsgDTO2.setOrderId("xxx");
            energyMsgDTO2.setTypeEnum(BillEnergyTypeEnum.INVITE.getCode());
            energyMsgDTO2.setNum(energy);
            energyMsgDTO2.setTime(time);
            billEnergyProducer.produce(energyMsgDTO2);

            //资产动态
            UserAssetTrendsMsgDTO assetTrendsMsgDTO2 = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO2.setUserId(userId);
            assetTrendsMsgDTO2.setAmount(new BigDecimal(energy));
            assetTrendsMsgDTO2.setTypeEnum(UserAssetTrendsTypeEnum.INVITE.getCode());
            assetTrendsMsgDTO2.setTime(time);
            assetTrendsMsgDTO2.setAssetType(AssetTypeEnum.ENERGY.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO2);




        }


        //推送
        WsSendDTO sendDTO = new WsSendDTO();
        sendDTO.setUserId(userId);
        sendDTO.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(sendDTO);


    }


    @Async
    public void signAfter(SignRecordPO po){

        Long userId = po.getUserId();

        try{
            this.disposeSignQuota(po);
        }catch (Exception e){
            LogUtil.log("签到处理小额提现失败："+e.getMessage());
        }

        try{
            this.disposeGiftCode(userId);
        }catch (Exception e){
            LogUtil.log("签到处理福利码失败："+e.getMessage());
        }

        //送福卡
/*
        try{
            this.disposeSignBlessing(userId);
        }catch (Exception e){
            LogUtil.log("签到处理福卡失败："+e.getMessage());
        }
*/


    }


    private void disposeSignQuota(SignRecordPO po){

        Long userId = po.getUserId();
        Long time = po.getCreateTime();

        SysConfWithdrawPO sysConfWithdrawPO = sysConfWithdrawMapper.find();
        if(null == sysConfWithdrawPO || !IsEnum.YES.getCode().equals(sysConfWithdrawPO.getQuotaIsOpen()) || null == sysConfWithdrawPO.getQuotaActiveSignDay() || sysConfWithdrawPO.getQuotaActiveSignDay() < 1){
            return;
        }

        List<UserSmallQuotaPO> quotaPOList = userSmallQuotaMapper.selectWaitList(userId);
        if(null == quotaPOList || quotaPOList.isEmpty()){
            return;
        }

        List<UserSmallQuotaPO> quotaStartList = quotaPOList.stream().filter(v->null != v.getStartTime() && v.getStartTime() > 0L).toList();
        if(!quotaStartList.isEmpty()){
            if(quotaStartList.size() > 1){
                for(int i=1;i<quotaStartList.size();i++){
                    UserSmallQuotaPO p = quotaStartList.get(i);
                    userSmallQuotaMapper.updateStartTime(p.getId(),null);
                }
            }
            UserSmallQuotaPO quotaPO = quotaStartList.get(0);

            Long startTime = quotaPO.getStartTime();
            //从这个时间到现在 连续签到了多少天
            List<SignRecordPO> recordPOS = signRecordMapper.selectStartList(userId,startTime);
            if(null != recordPOS && !recordPOS.isEmpty()){
                int signDays = 0;
                String d = DateUtil.timestampToDate(recordPOS.get(0).getCreateTime(),"yyyy-MM-dd");
                Long minTime = DateUtil.dateToTimeStamp(d,"yyyy-MM-dd");
                Long maxTime = minTime + 86400000L;
                for(SignRecordPO recordPO : recordPOS){
                    if(recordPO.getCreateTime() >= minTime && recordPO.getCreateTime() < maxTime){
                        signDays++;
                        minTime = minTime + 86400000L;
                        maxTime = maxTime + 86400000L;
                        //LogUtil.log("循环id"+recordPO.getId()+" 是连续签到 当前连签：" + signDays);
                    }
                    else{
                        signDays = 1;
                        String dd = DateUtil.timestampToDate(recordPO.getCreateTime(),"yyyy-MM-dd");
                        minTime = DateUtil.dateToTimeStamp(dd,"yyyy-MM-dd");
                        maxTime = minTime + 86400000L;
                        //LogUtil.log("循环id"+recordPO.getId()+" 不是连续签到 连签置为1");
                    }
                }
                if(signDays >= sysConfWithdrawPO.getQuotaActiveSignDay()){
                    //激活
                    userSmallQuotaMapper.updateOK(quotaPO.getId(),time);
                }
            }
        }
        else{
            List<UserSmallQuotaPO> quotaWaitList = new ArrayList<>(quotaPOList.stream().filter(v -> null == v.getStartTime() || v.getStartTime() <= 0L).toList());
            if(!quotaWaitList.isEmpty()){
                quotaWaitList.sort(Comparator.comparing(UserSmallQuotaPO::getId));
                UserSmallQuotaPO quotaPO = quotaWaitList.get(0);
                if(1 == sysConfWithdrawPO.getQuotaActiveSignDay()){
                    userSmallQuotaMapper.updateOK(quotaPO.getId(),time);
                }
                else{
                    userSmallQuotaMapper.updateStartTime(quotaPO.getId(),time);
                }
            }
        }
    }

    private void disposeGiftCode(Long userId){

        //查询是否实名
        UserPO userPO = userMapper.findByUserId(userId);
        if(!AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
            return;
        }

        SysConfGiveGiftCodePO sysConfGiveGiftCodePO = sysConfGiveGiftCodeMapper.find();
        if(null == sysConfGiveGiftCodePO){
            return;
        }

        if(!IsEnum.YES.getCode().equals(sysConfGiveGiftCodePO.getIsOpenSign())){
            return;
        }

        if(null != sysConfGiveGiftCodePO.getSignStartTime()){
            if(userPO.getCreateTime() < sysConfGiveGiftCodePO.getSignStartTime()){
                return;
            }
        }

        Integer userChannel = userPO.getChannel();

        String signChannels = sysConfGiveGiftCodePO.getSignChannels();
        if(!StringUtil.isEmpty(signChannels)){

            List<Integer> channelList = new ArrayList<>();
            try{
                channelList = JSONArray.parseArray(signChannels,Integer.class);
            }catch (Exception e){
                LogUtil.log("签到送福利码 解析渠道配置json失败：" + signChannels);
            }
            if(!channelList.isEmpty()){
                if(!channelList.contains(userChannel)){
                    return;
                }
            }
        }

        //判断是否领取过
        int c = giftCodeMapper.countByUserSource(userId, GiftCodeSourceEnum.SIGN.getCode());
        if(c > 0){
            return;
        }

        Integer signNum = signRecordMapper.countByUserId(userId);
        if(signNum < sysConfGiveGiftCodePO.getSignDays()){
            //LogUtil.log("签到送福利码 用户签到天数不够：" + userId);
            return;
        }

        Long time = System.currentTimeMillis();

        GiftCodePO giftCodePO = new GiftCodePO();
        giftCodePO.setCode(this.generateGiftCode());
        giftCodePO.setUserId(userId);
        giftCodePO.setDayNum(1);
        giftCodePO.setMaxNum(1);
        giftCodePO.setSource(GiftCodeSourceEnum.SIGN.getCode());
        giftCodePO.setCreateTime(time);
        giftCodePO.setUpdateTime(time);
        int s = giftCodeMapper.insert(giftCodePO);
        if(s > 0){
            String tel = userMapper.findTelByUserId(userId);
            if(!StringUtil.isEmpty(tel)){
                try{
                    smsUtilX.sendGiftCode(tel,giftCodePO.getCode());
                }catch (Exception e){
                    LogUtil.log("发送福利码短信失败："+e.getMessage());
                }
            }
        }

    }



    private String generateGiftCode(){
        String code = CommonUtil.getRandom(6);
        int c = giftCodeMapper.countByCode(code);
        if(c > 0){
            return generateGiftCode();
        }
        else{
            return code;
        }
    }


    //代理初始化
    public void agentInit(UserPO userPO) throws Exception{
        if(null == userPO || null == userPO.getUserId() || null == userPO.getInviteUserId()){
            return;
        }

        InviteLevelPO parent = inviteLevelMapper.findParent1(userPO.getUserId());
        if(null != parent){
            inviteLevelMapper.deleteByChildUserId(userPO.getUserId());
        }

        Long time = System.currentTimeMillis();



        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{


            InviteLevelPO po1 = new InviteLevelPO();
            po1.setUserId(userPO.getInviteUserId());
            po1.setChildUserId(userPO.getUserId());
            po1.setLevel(1);
            po1.setCreateTime(time);
            po1.setUpdateTime(time);
            inviteLevelMapper.insert(po1);

            this.incChildren1Num(userPO.getInviteUserId());


            InviteLevelPO parent2 = inviteLevelMapper.findParent1(userPO.getInviteUserId());
            if(null == parent2){
                return;
            }

            InviteLevelPO po2 = new InviteLevelPO();
            po2.setUserId(parent2.getUserId());
            po2.setChildUserId(userPO.getUserId());
            po2.setLevel(2);
            po2.setCreateTime(time);
            po2.setUpdateTime(time);
            inviteLevelMapper.insert(po2);

            this.incChildren2Num(parent2.getUserId());

            InviteLevelPO parent3 = inviteLevelMapper.findParent1(parent2.getUserId());
            if(null == parent3){
                return;
            }

            InviteLevelPO po3 = new InviteLevelPO();
            po3.setUserId(parent3.getUserId());
            po3.setChildUserId(userPO.getUserId());
            po3.setLevel(3);
            po3.setCreateTime(time);
            po3.setUpdateTime(time);
            inviteLevelMapper.insert(po3);

            this.incChildren3Num(parent3.getUserId());

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
        }

    }


    private void incChildren1Num(Long userId){
        this.checkData(userId);
        userDataMapper.incChildren1Num(userId);
    }

    private void incChildren2Num(Long userId) {
        this.checkData(userId);
        userDataMapper.incChildren2Num(userId);
    }

    private void incChildren3Num(Long userId) {
        this.checkData(userId);
        userDataMapper.incChildren3Num(userId);
    }

    private void checkData(Long userId) {
        UserDataPO dataPO = userDataMapper.findByUserId(userId);
        if(null == dataPO){
            Long time = System.currentTimeMillis();
            dataPO = new UserDataPO();
            dataPO.setUserId(userId);
            dataPO.setCreateTime(time);
            dataPO.setUpdateTime(time);
            userDataMapper.insert(dataPO);
        }
    }




    @Async
    public void loginAfter(Long userId, IpDTO ipDTO){

        if(null == ipDTO){
            return;
        }

        Long time = System.currentTimeMillis();

        userMapper.updateLastLoginInfo(userId, ipDTO.getIp(), ipDTO.getAddress(), time);


    }


}
