package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.msg.*;
import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.ai.basecommon.core.param.step.ClockParam;
import com.ai.basecommon.core.param.step.StepReportParam;
import com.ai.basecommon.core.po.shop.UserStepGoldPO;
import com.ai.basecommon.core.po.user.UserDataDayPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.step.MyClockVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.serviceuser.common.IpUtilX;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.UserBalanceMapper;
import com.ai.serviceuser.mapper.UserDataDayMapper;
import com.ai.serviceuser.mapper.UserStepGoldMapper;
import com.ai.serviceuser.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class StepGoldHandler {

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private UserDataProducer userDataProducer;

    @Autowired
    private BillEnergyProducer billEnergyProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private CollectDayProducer collectDayProducer;

    @Autowired
    private UserStepGoldMapper userStepGoldMapper;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private UserDataDayProducer userDataDayProducer;

    @Autowired
    private UserDataDayMapper userDataDayMapper;

    @Autowired
    private UserTaskProducer userTaskProducer;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;


    @ReadOnly
    public MyClockVO myClock() throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        Integer ymd = DateUtil.todayDate();

        MyClockVO vo = new MyClockVO();
        vo.setClockNum(0);
        vo.setWaterNum(0);
        vo.setStepNum(0);
        vo.setClockMorning(0);
        vo.setClockEvening(0);
        if(null == userId){
            return vo;
        }

        UserDataDayPO userDataDayPO = userDataDayMapper.find(userId,ymd);
        if(null != userDataDayPO){
            vo.setStepNum(userDataDayPO.getStep());
        }

        List<UserStepGoldPO> list = userStepGoldMapper.selectList(userId,ymd);
        if(null == list || list.isEmpty()){
            return vo;
        }

        List<UserStepGoldPO> clockList = list.stream().filter(v->v.getType().equals(StepGoldTypeEnum.CLOCK.getCode())).toList();
        int clockNum = 0;
        int clockMorning = 0;
        int clockEvening = 0;
        if(null != clockList && !clockList.isEmpty()){

            String date = DateUtil.timestampToDate(System.currentTimeMillis(),"yyyy-MM-dd");
            Long timeMorningA = DateUtil.dateToMillisecond(date + " 06:00:00","yyyy-MM-dd HH:mm:ss");
            Long timeMorningB = DateUtil.dateToMillisecond(date + " 10:00:00","yyyy-MM-dd HH:mm:ss");

            Long timeEveningA = DateUtil.dateToMillisecond(date + " 20:00:00","yyyy-MM-dd HH:mm:ss");
            Long timeEveningB = DateUtil.dateToMillisecond(date + " 23:59:59","yyyy-MM-dd HH:mm:ss");

            for(UserStepGoldPO goldPO : clockList){
                clockNum ++ ;
                Long time = goldPO.getCreateTime();
                if(time >= timeMorningA && time <= timeMorningB){
                    clockMorning = 1;
                }
                if(time >= timeEveningA && time <= timeEveningB){
                    clockEvening = 1;
                }
            }
        }

        vo.setClockNum(clockNum);
        vo.setClockMorning(clockMorning);
        vo.setClockEvening(clockEvening);

        long waterNum = list.stream().filter(v->v.getType().equals(StepGoldTypeEnum.WATER.getCode())).count();
        vo.setWaterNum((int) waterNum);
        return vo;
    }

    //打卡
    public BaseVO clock(ClockParam param) throws Exception{

        Long userId = userUtilX.getUserId();

        if(null == param || null == param.getType() || param.getType() < 1){
            return BaseVO.ok();
        }

        Integer type = param.getType();
        StepGoldTypeEnum typeEnum = StepGoldTypeEnum.getByCode(type);

        if(null == typeEnum){
            LogUtil.log("打卡请求 类型错误： 用户ID是"+userId+"，行为类型是"+type);
            return BaseVO.error();
        }

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.STEP_GOLD.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("用户行为：" + typeEnum.getValue());


        //LogUtil.log("打卡请求： 用户ID是"+userId+"，行为类型是"+typeEnum.getValue());


/*

        UserAuthDTO userAuthDTO = userService.findAuthInfo(userId);
        if(null == userAuthDTO){
            BaseException.error(StatusCodeEnum.NO_AUTH);
        }

        if(!AuthStatusEnum.YES.getCode().equals(userAuthDTO.getAuthStatus())){
            BaseException.error(StatusCodeEnum.PLEASE_AUTH);
        }
*/



        Integer ymd = DateUtil.todayDate();
        String date = DateUtil.timestampToDate(time,"yyyy-MM-dd");


        //早上打卡：6:00-10:00
        //晚上打卡：20-24
        //喝水时间：9-20点 间隔1小时一次
        //步数：每满1000步赠送1云币

        Long id = null;

        //如果是打卡  判断现在是几点  查询区间内是否有打过卡
        if(typeEnum == StepGoldTypeEnum.CLOCK){

            Long timeA = null;
            Long timeB = null;

            Long timeMorningA = DateUtil.dateToMillisecond(date + " 06:00:00","yyyy-MM-dd HH:mm:ss");
            Long timeMorningB = DateUtil.dateToMillisecond(date + " 10:00:00","yyyy-MM-dd HH:mm:ss");
            //LogUtil.log("早卡A：" + timeMorningA);
            //LogUtil.log("早卡B：" + timeMorningB);

            Long timeEveningA = DateUtil.dateToMillisecond(date + " 20:00:00","yyyy-MM-dd HH:mm:ss");
            Long timeEveningB = DateUtil.dateToMillisecond(date + " 23:59:59","yyyy-MM-dd HH:mm:ss");
            //LogUtil.log("晚卡A：" + timeEveningA);
            //LogUtil.log("晚卡B：" + timeEveningB);

            int timeType = 0;
            if(time > timeMorningA && time < timeMorningB){
                //LogUtil.log("现在正是早上打卡：" + time);
                timeA = timeMorningA;
                timeB = timeMorningB;
                timeType = 1;
            }
            else if(time > timeEveningA && time < timeEveningB){
                //LogUtil.log("现在正是晚上打卡：" + time);
                timeA = timeEveningA;
                timeB = timeEveningB;
                timeType = 2;
            }
            else{
                //LogUtil.log("现在不在打卡时间：" + time);
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("现在不在打卡时间");
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error(StatusCodeEnum.STEP_GOLD_CLOCK_TIME_ERROR);
            }

            int c = userStepGoldMapper.existClock(userId,timeA,timeB);
            if(c > 0){
                //LogUtil.log("你已经打过卡了");
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("已经打过卡了");
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error(StatusCodeEnum.STEP_GOLD_CLOCK_EXIST);
            }

            UserStepGoldPO po = new UserStepGoldPO();
            po.setUserId(userId);
            po.setType(type);
            po.setEnergy(4);
            po.setTimeType(timeType);
            po.setYmd(ymd);
            po.setCreateTime(time);
            po.setUpdateTime(time);

            try{
                userStepGoldMapper.insertGetId(po);
                userBalanceMapper.incEnergy(userId,po.getEnergy());
            }catch (Exception e){
                LogUtil.log(e.getMessage());
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("数据库处理失败：" + e.getMessage());
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error();
            }

            id = po.getId();


            BillEnergyMsgDTO energyMsgDTO = new BillEnergyMsgDTO();
            energyMsgDTO.setUserId(userId);
            energyMsgDTO.setOrderId(po.getId().toString());
            energyMsgDTO.setNum(po.getEnergy());
            energyMsgDTO.setTypeEnum(BillEnergyTypeEnum.STEP_GOLD_CLOCK.getCode());
            energyMsgDTO.setTime(time);
            energyMsgDTO.setRemark("订单号是记录id");
            billEnergyProducer.produce(energyMsgDTO);


            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(userId);
            assetTrendsMsgDTO.setAmount(BigDecimal.valueOf(po.getEnergy()));
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.STEP_GOLD_CLOCK.getCode());
            assetTrendsMsgDTO.setTime(time);
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);

            UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
            userTaskMsgDTO.setUserId(userId);
            userTaskMsgDTO.setTaskType(TaskTypeEnum.STEP_GOLD_CLOCK.getCode());
            userTaskMsgDTO.setId(id);
            userTaskProducer.produce(userTaskMsgDTO);

        }
        else if(typeEnum == StepGoldTypeEnum.WATER){

            //如果是喝水  判断是否在喝水期间 上一次喝水距离此刻是否超过1小时
            //喝水时间从早上9点到晚上20点之间，每次喝水之间的间隔必须大于1小时，一天最多喝8次

            Long timeA = DateUtil.dateToMillisecond(date + " 09:00:00","yyyy-MM-dd HH:mm:ss");
            Long timeB = DateUtil.dateToMillisecond(date + " 20:00:00","yyyy-MM-dd HH:mm:ss");
            if(time < timeA || time > timeB){
                //LogUtil.log("现在不在喝水时间");
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("现在不在喝水时间");
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error(StatusCodeEnum.STEP_GOLD_WATER_TIME_ERROR);
            }
            //查询今天喝水次数
            List<UserStepGoldPO> list = userStepGoldMapper.selectWaterList(userId,ymd);
            if(null != list && !list.isEmpty()){
                if(list.size() >= 8){
                    //LogUtil.log("今天已经喝水8次了");
                    userLogMsgDTO.setLevel(2);
                    userLogMsgDTO.setContent("今天已经喝水8次了");
                    userLogProducer.produce(userLogMsgDTO);
                    return BaseVO.error(StatusCodeEnum.STEP_GOLD_WATER_FULL);
                }
                UserStepGoldPO lastPO = list.get(list.size()-1);
                if(time - lastPO.getCreateTime() < 3600000L){
                    String lastDate = DateUtil.timestampToDate(lastPO.getCreateTime(),null);
                    LogUtil.log("距上次喝水不足1小时 上次时间：" + lastDate + "，本次时间：" + DateUtil.timestampToDate(time,null));
                    userLogMsgDTO.setLevel(2);
                    userLogMsgDTO.setContent("距离上次喝水不足1小时 上次喝水时间是：" + lastDate);
                    userLogProducer.produce(userLogMsgDTO);
                    return BaseVO.error(StatusCodeEnum.STEP_GOLD_WATER_SPACE_ERROR);
                }
            }

            UserStepGoldPO po = new UserStepGoldPO();
            po.setUserId(userId);
            po.setType(type);
            po.setEnergy(2);
            po.setTimeType(0);
            po.setYmd(ymd);
            po.setCreateTime(time);
            po.setUpdateTime(time);

            try{
                userStepGoldMapper.insertGetId(po);
                userBalanceMapper.incEnergy(userId,po.getEnergy());
            }catch (Exception e){
                LogUtil.log(e.getMessage());
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("数据库处理失败：" + e.getMessage());
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error();
            }

            id = po.getId();


            BillEnergyMsgDTO energyMsgDTO = new BillEnergyMsgDTO();
            energyMsgDTO.setUserId(userId);
            energyMsgDTO.setOrderId(po.getId().toString());
            energyMsgDTO.setNum(po.getEnergy());
            energyMsgDTO.setTypeEnum(BillEnergyTypeEnum.STEP_GOLD_WATER.getCode());
            energyMsgDTO.setTime(time);
            energyMsgDTO.setRemark("订单号是记录id");
            billEnergyProducer.produce(energyMsgDTO);


            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(userId);
            assetTrendsMsgDTO.setAmount(BigDecimal.valueOf(po.getEnergy()));
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.STEP_GOLD_WATER.getCode());
            assetTrendsMsgDTO.setTime(time);
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);


            UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
            userTaskMsgDTO.setUserId(userId);
            userTaskMsgDTO.setTaskType(TaskTypeEnum.STEP_GOLD_WATER.getCode());
            userTaskMsgDTO.setId(id);
            userTaskProducer.produce(userTaskMsgDTO);


        }
        else{
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("传参错误 严查该用户");
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }

        UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
        userTaskMsgDTO.setUserId(userId);
        userTaskMsgDTO.setTaskType(TaskTypeEnum.STEP_GOLD.getCode());
        userTaskMsgDTO.setId(id);
        userTaskProducer.produce(userTaskMsgDTO);




        UserDataMsgDTO msgDTO = new UserDataMsgDTO();
        msgDTO.setUserId(userId);
        msgDTO.setStepGold(true);
        userDataProducer.produce(msgDTO);

        String k = "user_step_gold_" + userId + "_" + ymd;
        if(!redisUtilX.hasKey(k)){
            CollectDayMsgDTO collectDayMsgDTO = new CollectDayMsgDTO();
            collectDayMsgDTO.setStepGold(true);
            collectDayProducer.produce(collectDayMsgDTO);
            redisUtilX.set(k,"1",86400);
        }

        userLogMsgDTO.setContent("成功");
        userLogProducer.produce(userLogMsgDTO);
        return BaseVO.ok();
    }


    //步数上报
    public BaseVO report(StepReportParam param) throws Exception{
        if(null == param || null == param.getNum() || param.getNum() < 1){
            return BaseVO.error();
        }
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return BaseVO.error();
        }
        Integer ymd = DateUtil.todayDate();
        Integer num = param.getNum();

        UserDataDayMsgDTO msgDTO = new UserDataDayMsgDTO();
        msgDTO.setUserId(userId);
        msgDTO.setYmd(ymd);
        msgDTO.setStep(num);
        userDataDayProducer.produce(msgDTO);
        return BaseVO.ok();
    }



}
