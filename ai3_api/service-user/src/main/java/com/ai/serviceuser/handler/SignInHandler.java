package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.*;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.SignRecordVO;
import com.ai.basecommon.core.vo.user.SysConfSignChildVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.ai.basecommon.core.dto.user.UserBalanceChangeDTO;
import com.ai.basecommon.core.dto.user.UserEnergyChangeDTO;
import com.ai.basecommon.core.dto.user.UserIntegralChangeDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.po.base.SysConfSignPO;
import com.ai.basecommon.core.po.user.SignRecordPO;
import com.ai.basecommon.core.vo.user.SysConfSignVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.DozerUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.serviceuser.async.UserAsync;
import com.ai.serviceuser.common.IpUtilX;
import com.ai.serviceuser.common.LockUtilX;
import com.ai.serviceuser.common.TransactionUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.*;
import com.ai.serviceuser.common.UserUtilX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SignInHandler {

    @Autowired
    private SysConfSignMapper sysConfSignMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private SignRecordMapper signRecordMapper;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private UserDataProducer userDataProducer;

    @Autowired
    private BillAmountProducer billAmountProducer;

    @Autowired
    private BillIntegralProducer billIntegralProducer;

    @Autowired
    private BillEnergyProducer billEnergyProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private CollectDayProducer collectDayProducer;

    @Autowired
    private WsProducer wsProducer;

    @Autowired
    private UserDataMapper userDataMapper;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private UserAsync userAsync;

    @Autowired
    private UserTaskProducer userTaskProducer;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private LockUtilX lockUtilX;


    //获取签到配置
    @ReadOnly
    public SysConfSignVO selectConf() throws Exception{

        SysConfSignVO vo = new SysConfSignVO();

        List<SysConfSignPO> pos = sysConfSignMapper.select();
        if(null == pos || pos.isEmpty()){
            return vo;
        }
        List<SysConfSignChildVO> childVOS = DozerUtil.maps(pos, SysConfSignChildVO.class);

        Map<Integer,List<SysConfSignChildVO>> map = childVOS.stream().collect(Collectors.groupingBy(SysConfSignChildVO::getType));

        vo.setAlong(map.get(1));
        vo.setTotal(map.get(2));

        Long userId = userUtilX.getUserIdNotError();
        if(null != userId){
            Integer today = DateUtil.todayDate();
            String todayStr = today.toString();
            Integer ym = Integer.valueOf(todayStr.substring(0,6));
            List<SignRecordVO> recordVOS = signRecordMapper.selectByYm(userId,ym);
            vo.setRecord(recordVOS);
        }

        return vo;
    }


    //获取本月签到记录
    public List<SignRecordVO> selectRecord() throws Exception{
        Long userId = userUtilX.getUserId();
        Integer today = DateUtil.todayDate();
        String todayStr = today.toString();
        Integer ym = Integer.valueOf(todayStr.substring(0,6));
        return signRecordMapper.selectByYm(userId,ym);
    }


    //签到
    public BaseVO signIn() throws Exception{
        Long userId = userUtilX.getUserId();

        Integer today = DateUtil.todayDate();
        String todayStr = today.toString();
        String years = todayStr.substring(0,4);
        String months = todayStr.substring(4,6);
        String days = todayStr.substring(6,8);
        Integer ym = Integer.valueOf(todayStr.substring(0,6));

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.SIGN_IN.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(today);
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark(null);


        Object lock = lockUtilX.getLock(userId);

        synchronized (lock) {

            //查询今天有没有签到过
            int c = signRecordMapper.countBy(userId,today);
            if(c > 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("失败 今天已经签到过了");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.SIGN_ALREADY);
            }

            int signDays = 0;
            int totalSignDays = 0;

            //昨天有没有签到
            Integer yesterday = DateUtil.yesterdayIntDate();
            SignRecordPO yesterdayRecord = signRecordMapper.findBy(userId,yesterday);
            if(null != yesterdayRecord){
                signDays = yesterdayRecord.getSignDays();
                totalSignDays = yesterdayRecord.getSignTotalDays();
            }
            else{
                if(!"01".equals(days)){
                    totalSignDays = signRecordMapper.countByYm(userId,ym);
                }
            }
            if("01".equals(days)){
                totalSignDays = 0;
            }

            //7天轮训
            if(signDays >= 7){
                signDays = 0;
            }

            signDays++;

            //BigDecimal signDaysAmount = BigDecimal.ZERO;
            //连续签到奖励
            SysConfSignPO confSignPO = sysConfSignMapper.findByDaysAndType(signDays,1);


            //int cc = signRecordMapper.countByYm(userId,ym);
            totalSignDays++;
            //累计签到奖励
            SysConfSignPO confSignTotalPO = sysConfSignMapper.findByDaysAndType(totalSignDays,2);

            SignRecordPO po = new SignRecordPO();
            po.setUserId(userId);
            po.setYears(Integer.valueOf(years));
            po.setMonths(Integer.valueOf(months));
            po.setDays(Integer.valueOf(days));
            po.setYm(ym);
            po.setYmd(today);
            po.setSignDays(signDays);
            po.setSignTotalDays(totalSignDays);
            po.setCreateTime(time);
            po.setUpdateTime(time);

            int gold = 10;

            TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{


                if(null != confSignPO && confSignPO.getAmount().compareTo(BigDecimal.ZERO) > 0){
                    if(!SignGiveTypeEnum.CASH.getCode().equals(confSignPO.getGiveType()) && SignGiveTypeEnum.INTEGRAL.getCode().equals(confSignPO.getGiveType()) && SignGiveTypeEnum.ENERGY.getCode().equals(confSignPO.getGiveType())){
                        throw new RuntimeException("连续签到奖励配置错误：" + confSignPO);
                    }
                    if(SignGiveTypeEnum.CASH.getCode().equals(confSignPO.getGiveType())){
                        userBalanceMapper.incAmount(userId,confSignPO.getAmount());
                    }
                    else if(SignGiveTypeEnum.INTEGRAL.getCode().equals(confSignPO.getGiveType())){
                        userBalanceMapper.incIntegral(userId,confSignPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    }
                    else if(SignGiveTypeEnum.ENERGY.getCode().equals(confSignPO.getGiveType())){
                        userBalanceMapper.incEnergy(userId,confSignPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    }
                    else{
                        LogUtil.log("连续签到加钱失败 这个奖励配置不对：" + confSignPO);
                    }
                }


                if(null != confSignTotalPO && confSignTotalPO.getAmount().compareTo(BigDecimal.ZERO) > 0){
                    if(!SignGiveTypeEnum.CASH.getCode().equals(confSignTotalPO.getGiveType()) && SignGiveTypeEnum.INTEGRAL.getCode().equals(confSignTotalPO.getGiveType()) && SignGiveTypeEnum.ENERGY.getCode().equals(confSignTotalPO.getGiveType())){
                        throw new RuntimeException("累计签到奖励配置错误：" + confSignTotalPO);
                    }
                    if(SignGiveTypeEnum.CASH.getCode().equals(confSignTotalPO.getGiveType())){
                        userBalanceMapper.incAmount(userId,confSignTotalPO.getAmount());
                    }
                    else if(SignGiveTypeEnum.INTEGRAL.getCode().equals(confSignTotalPO.getGiveType())){
                        userBalanceMapper.incIntegral(userId,confSignTotalPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    }
                    else if(SignGiveTypeEnum.ENERGY.getCode().equals(confSignTotalPO.getGiveType())){
                        userBalanceMapper.incEnergy(userId,confSignTotalPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    }
                    else{
                        LogUtil.log("累计签到加钱失败 这个奖励配置不对：" + confSignPO);
                    }
                }

                //入库
                signRecordMapper.insertGetId(po);

                //云豆
                userBalanceMapper.incGold(userId,gold);

            });

            if (!transactionResultDTO.isSuccess()) {
                LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("签到失败：" + transactionResultDTO.getMessage());
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.ERROR);
            }


            //连续签到账单
            if(null != confSignPO && confSignPO.getAmount().compareTo(BigDecimal.ZERO) > 0){

                UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
                assetTrendsMsgDTO.setUserId(userId);
                assetTrendsMsgDTO.setAmount(confSignPO.getAmount());
                assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.SIGN_IN_ALONG.getCode());
                assetTrendsMsgDTO.setTime(time);

                if(SignGiveTypeEnum.CASH.getCode().equals(confSignPO.getGiveType())){
                    BillAmountMsgDTO msgDTO = new BillAmountMsgDTO();
                    msgDTO.setUserId(userId);
                    msgDTO.setOrderId(po.getId().toString());
                    msgDTO.setAmount(confSignPO.getAmount());
                    msgDTO.setTypeEnum(BillAmountTypeEnum.SIGN_IN_ALONG.getCode());
                    msgDTO.setTime(time);
                    msgDTO.setRemark("订单号是签到记录的ID");
                    billAmountProducer.produce(msgDTO);
                    assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
                }
                else if(SignGiveTypeEnum.INTEGRAL.getCode().equals(confSignPO.getGiveType())){
                    BillIntegralMsgDTO msgDTO = new BillIntegralMsgDTO();
                    msgDTO.setUserId(userId);
                    msgDTO.setOrderId(po.getId().toString());
                    msgDTO.setNum(confSignPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    msgDTO.setTypeEnum(BillAmountTypeEnum.SIGN_IN_ALONG.getCode());
                    msgDTO.setTime(time);
                    msgDTO.setRemark("订单号是签到记录的ID");
                    billIntegralProducer.produce(msgDTO);
                    assetTrendsMsgDTO.setAssetType(AssetTypeEnum.INTEGRAL.getCode());
                }
                else if(SignGiveTypeEnum.ENERGY.getCode().equals(confSignPO.getGiveType())){
                    BillEnergyMsgDTO msgDTO = new BillEnergyMsgDTO();
                    msgDTO.setUserId(userId);
                    msgDTO.setOrderId(po.getId().toString());
                    msgDTO.setNum(confSignPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    msgDTO.setTypeEnum(BillAmountTypeEnum.SIGN_IN_ALONG.getCode());
                    msgDTO.setTime(time);
                    msgDTO.setRemark("订单号是签到记录的ID");
                    billEnergyProducer.produce(msgDTO);
                    assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
                }

                userAssetTrendsProducer.produce(assetTrendsMsgDTO);
            }

            //累计签到账单
            if(null != confSignTotalPO && confSignTotalPO.getAmount().compareTo(BigDecimal.ZERO) > 0){


                UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
                assetTrendsMsgDTO.setUserId(userId);
                assetTrendsMsgDTO.setAmount(confSignTotalPO.getAmount());
                assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.SIGN_IN_TOTAL.getCode());
                assetTrendsMsgDTO.setTime(time);


                if(SignGiveTypeEnum.CASH.getCode().equals(confSignTotalPO.getGiveType())){
                    BillAmountMsgDTO msgDTO = new BillAmountMsgDTO();
                    msgDTO.setUserId(userId);
                    msgDTO.setOrderId(po.getId().toString());
                    msgDTO.setAmount(confSignTotalPO.getAmount());
                    msgDTO.setTypeEnum(BillAmountTypeEnum.SIGN_IN_TOTAL.getCode());
                    msgDTO.setTime(time);
                    msgDTO.setRemark("订单号是签到记录的ID");
                    billAmountProducer.produce(msgDTO);
                    assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
                }
                else if(SignGiveTypeEnum.INTEGRAL.getCode().equals(confSignTotalPO.getGiveType())){
                    BillIntegralMsgDTO msgDTO = new BillIntegralMsgDTO();
                    msgDTO.setUserId(userId);
                    msgDTO.setOrderId(po.getId().toString());
                    msgDTO.setNum(confSignTotalPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    msgDTO.setTypeEnum(BillAmountTypeEnum.SIGN_IN_TOTAL.getCode());
                    msgDTO.setTime(time);
                    msgDTO.setRemark("订单号是签到记录的ID");
                    billIntegralProducer.produce(msgDTO);
                    assetTrendsMsgDTO.setAssetType(AssetTypeEnum.INTEGRAL.getCode());
                }
                else if(SignGiveTypeEnum.ENERGY.getCode().equals(confSignTotalPO.getGiveType())){
                    BillEnergyMsgDTO msgDTO = new BillEnergyMsgDTO();
                    msgDTO.setUserId(userId);
                    msgDTO.setOrderId(po.getId().toString());
                    msgDTO.setNum(confSignTotalPO.getAmount().setScale(0, RoundingMode.UP).intValue());
                    msgDTO.setTypeEnum(BillEnergyTypeEnum.SIGN_IN_TOTAL.getCode());
                    msgDTO.setTime(time);
                    msgDTO.setRemark("订单号是签到记录的ID");
                    billEnergyProducer.produce(msgDTO);
                    assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
                }
                userAssetTrendsProducer.produce(assetTrendsMsgDTO);
            }


            userLogMsgDTO.setContent("签到成功");
            userLogProducer.produce(userLogMsgDTO);


            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(userId);
            assetTrendsMsgDTO.setAmount(new BigDecimal(gold));
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.SIGN_IN_EXTRA.getCode());
            assetTrendsMsgDTO.setTime(time);
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.GOLD.getCode());
            //assetTrendsMsgDTO.setRemark();
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);


            UserDataMsgDTO msgDTO = new UserDataMsgDTO();
            msgDTO.setUserId(userId);
            msgDTO.setSign(true);
            userDataProducer.produce(msgDTO);

            CollectDayMsgDTO collectDayMsgDTO = new CollectDayMsgDTO();
            collectDayMsgDTO.setSign(true);
            collectDayProducer.produce(collectDayMsgDTO);

            //BigDecimal total = todayAmount.add(signDaysAmount);
            //return total.stripTrailingZeros().toPlainString();

            WsSendDTO sendDTO = new WsSendDTO();
            sendDTO.setUserId(userId);
            sendDTO.setCode(WsCodeEnum.USER_BALANCE.getCode());
            wsProducer.produce(sendDTO);


            UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
            userTaskMsgDTO.setUserId(userId);
            userTaskMsgDTO.setTaskType(TaskTypeEnum.SIGN_IN.getCode());
            userTaskProducer.produce(userTaskMsgDTO);


            userAsync.signAfter(po);


        }


        return BaseVO.bool(true);
    }


    //查询签到总次数
    public Integer totalSign() throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return 999;
        }
        //如果不是电1 直接出
        if(!"prod".equals(this.profile) && !"dev".equals(this.profile)){
            return 999;
        }
        int totalSign = userDataMapper.findSignNumByUserId(userId);
        return totalSign;
    }


}
