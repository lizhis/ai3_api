package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.msg.BillEnergyMsgDTO;
import com.ai.basecommon.core.dto.msg.CollectDayMsgDTO;
import com.ai.basecommon.core.dto.msg.UserAssetTrendsMsgDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.param.forest.ForestCareParam;
import com.ai.basecommon.core.po.forest.*;
import com.ai.basecommon.core.po.user.UserPO;
import com.ai.basecommon.core.vo.forest.ForestCareVO;
import com.ai.basecommon.core.vo.forest.ForestTreeVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.CommonUtil;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.TransactionUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.BillEnergyProducer;
import com.ai.serviceuser.producer.CollectDayProducer;
import com.ai.serviceuser.producer.UserAssetTrendsProducer;
import com.ai.serviceuser.producer.WsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ForestHandler {

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private ForestTreeMapper forestTreeMapper;

    @Autowired
    private ForestConfigMapper forestConfigMapper;

    @Autowired
    private ForestTreeCareMapper forestTreeCareMapper;

    @Autowired
    private ForestTreeLevelMapper forestTreeLevelMapper;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private BillEnergyProducer billEnergyProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private CollectDayProducer collectDayProducer;

    @Autowired
    private WsProducer wsProducer;


    //获取我的树
    public ForestTreeVO myTree() throws Exception{
        Long userId = userUtilX.getUserIdNotError();

        ForestTreeVO vo = new ForestTreeVO();
        if(null == userId){
            vo.setLevel(1);
            vo.setStatus(1);
            vo.setCanWaterNum(0);
            vo.setCanFertilizeNum(0);
            return vo;
        }

        String k = "query_mytree_user_id_" + userId;
        if(!StringUtil.isEmpty(redisUtilX.get(k))){
            BaseException.error(StatusCodeEnum.REQUEST_LIMIT);
        }
        redisUtilX.set(k,"1",3);

        ForestTreePO treePO = forestTreeMapper.findByUserId(userId);
        if(null == treePO){
            Long time = System.currentTimeMillis();
            treePO = new ForestTreePO();
            treePO.setUserId(userId);
            treePO.setLevel(1);
            treePO.setWaterTimes(0);
            treePO.setWaterDays(0);
            treePO.setFertilizeTimes(0);
            treePO.setFertilizeDays(0);
            treePO.setStatus(1);
            treePO.setCreateTime(time);
            treePO.setUpdateTime(time);
            forestTreeMapper.insert(treePO);
        }

        vo.setLevel(treePO.getLevel());
        vo.setStatus(treePO.getStatus());
        ForestConfigPO configPO = forestConfigMapper.find();
        Integer today = DateUtil.todayDate();
        if(null != configPO){
            int waterNum = forestTreeCareMapper.countCareNum(userId, CareTypeEnum.WATER.getCode(),today);
            int fertilizeNum = forestTreeCareMapper.countCareNum(userId,CareTypeEnum.FERTILIZE.getCode(),today);
            int canWaterNum = Math.max(configPO.getDayLimitWater()-waterNum, 0);
            int canFertilizeNum = Math.max(configPO.getDayLimitFertilize()-fertilizeNum, 0);
            vo.setCanWaterNum(canWaterNum);
            vo.setCanFertilizeNum(canFertilizeNum);
        }
        else{
            vo.setCanWaterNum(0);
            vo.setCanFertilizeNum(0);
        }
        redisUtilX.delete(k);
        return vo;
    }


    //养护
    public ForestCareVO care(ForestCareParam param) throws Exception{
        if(null == param || null == param.getType()){
            BaseException.error(StatusCodeEnum.ERROR);
        }

        Integer type = param.getType();
        if(!CareTypeEnum.WATER.getCode().equals(type) && !CareTypeEnum.FERTILIZE.getCode().equals(type)){
            BaseException.error(StatusCodeEnum.ERROR);
        }

        Long userId = userUtilX.getUserId();

        UserPO userPO = userUtilX.getCacheUserPO(userId);
        if(!AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
            BaseException.error(StatusCodeEnum.PLEASE_AUTH);
        }

        ForestTreePO treePO = forestTreeMapper.findByUserId(userId);
        if(null == treePO){
            BaseException.error(StatusCodeEnum.FOREST_TREE_NO_EXIST);
        }

        ForestConfigPO forestConfigPO = forestConfigMapper.find();
        if(null == forestConfigPO){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }

        Integer todayDate = DateUtil.todayDate();

        int c = forestTreeCareMapper.countCareNum(userId,type, todayDate);

        if(CareTypeEnum.WATER.getCode().equals(type)){
            //今日浇水次数
            if(c >= forestConfigPO.getDayLimitWater()){
                BaseException.error(StatusCodeEnum.FOREST_WATER_LIMIT);
            }

        }
        else{
            //今日施肥次数
            if(c >= forestConfigPO.getDayLimitFertilize()){
                BaseException.error(StatusCodeEnum.FOREST_FERTILIZE_LIMIT);
            }

        }

        List<ForestTreeLevelPO> treeLevelList = this.selectAllTreeLevel();
        if(null == treeLevelList || treeLevelList.isEmpty()){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }

        //当前等级
        ForestTreeLevelPO currentLevelPO = treeLevelList.stream().filter(v->v.getLevel().equals(treePO.getLevel())).findFirst().orElse(null);

        //下一等级
        ForestTreeLevelPO nextLevelPO = treeLevelList.stream().filter(v->v.getLevel().equals(treePO.getLevel()+1)).findFirst().orElse(null);


        Long time = System.currentTimeMillis();

        //生成云币
        Integer min = currentLevelPO.getGenEnergyMin();
        Integer max = currentLevelPO.getGenEnergyMax();
        Integer energy = CommonUtil.getRandom(min,max);

        ForestTreeCarePO forestTreeCarePO = new ForestTreeCarePO();
        forestTreeCarePO.setUserId(userId);
        forestTreeCarePO.setTreeLevel(treePO.getLevel());
        forestTreeCarePO.setType(type);
        forestTreeCarePO.setEnergy(energy);
        forestTreeCarePO.setYmd(todayDate);
        forestTreeCarePO.setCreateTime(time);
        forestTreeCarePO.setUpdateTime(time);



        Integer currentLevel = transactionUtilX.executeReturn(()->{


            userBalanceMapper.incEnergy(userId,energy);

            //浇水记录
            forestTreeCareMapper.insertGetId(forestTreeCarePO);


            Integer waterTimes = treePO.getWaterTimes();
            Integer fertilizeTimes = treePO.getFertilizeTimes();

            //今天如果没有浇过水  则浇水天数+1
            if(CareTypeEnum.WATER.getCode().equals(type)){
                waterTimes++;
                forestTreeMapper.waterInc(treePO.getId(),1,c > 0 ? 0 : 1);
            }
            else{
                fertilizeTimes++;
                forestTreeMapper.fertilizeInc(treePO.getId(),1,c > 0 ? 0 : 1);
            }

            if(null != nextLevelPO){
                if(waterTimes >= nextLevelPO.getSillWaterTimes() && fertilizeTimes >= nextLevelPO.getSillFertilizeTimes()){
                    //升级
                    forestTreeMapper.updateLevel(treePO.getId(), nextLevelPO.getLevel());
                    return nextLevelPO.getLevel();
                }
            }
            return null;
        });

        if(null == currentLevel){
            currentLevel = treePO.getLevel();
        }

/*
        UserDataMsgDTO msgDTO = new UserDataMsgDTO();
        msgDTO.setUserId(userId);
        msgDTO.setCare(true);
        userDataProducer.produce(msgDTO);*/

        if(c == 0){
            CollectDayMsgDTO collectDayMsgDTO = new CollectDayMsgDTO();
            collectDayMsgDTO.setCare(true);
            collectDayProducer.produce(collectDayMsgDTO);
        }


        BillEnergyMsgDTO msgDTO = new BillEnergyMsgDTO();
        msgDTO.setUserId(userId);
        msgDTO.setNum(energy);
        msgDTO.setOrderId(forestTreeCarePO.getId().toString());
        msgDTO.setTypeEnum(BillEnergyTypeEnum.FOREST_ENERGY.getCode());
        msgDTO.setTime(time);
        msgDTO.setRemark("ID是养护记录的ID");
        billEnergyProducer.produce(msgDTO);


        UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO.setUserId(userId);
        assetTrendsMsgDTO.setAmount(new BigDecimal(energy));
        assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.FOREST_ENERGY.getCode());
        assetTrendsMsgDTO.setTime(time);
        assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
        userAssetTrendsProducer.produce(assetTrendsMsgDTO);


        WsSendDTO wsSendDTO = new WsSendDTO();
        wsSendDTO.setUserId(userId);
        wsSendDTO.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(wsSendDTO);


        ForestCareVO vo = new ForestCareVO();
        vo.setLevel(currentLevel);
        vo.setEnergy(energy);

        return vo;
    }


    @ReadOnly
    private List<ForestTreeLevelPO> selectAllTreeLevel() throws Exception{
        List<ForestTreeLevelPO> treeLevelList = null;
        String key = RedisKey.forest_tree_level_list;
        if(redisUtilX.hasKey(key)){
            treeLevelList = redisUtilX.getObjList(key, ForestTreeLevelPO.class);
            if(null != treeLevelList && !treeLevelList.isEmpty()){
                return treeLevelList;
            }
        }
        treeLevelList = forestTreeLevelMapper.selectAll();
        if(null != treeLevelList && !treeLevelList.isEmpty()){
            redisUtilX.setObj(key, treeLevelList,600);
        }
        return treeLevelList;
    }




}
