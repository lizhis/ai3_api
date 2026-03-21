package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.param.base.BlessingCardDetailParam;
import com.ai.basecommon.core.po.base.SysConfBlessingCardPO;
import com.ai.basecommon.core.po.base.SysConfBlessingPO;
import com.ai.basecommon.core.vo.user.UserBlessingVO;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.SysConfBlessingCardMapper;
import com.ai.serviceuser.mapper.SysConfBlessingMapper;
import com.ai.serviceuser.mapper.UserBlessingMapper;
import com.ai.serviceuser.mapper.UserDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlessingHandler {

    @Autowired
    private SysConfBlessingMapper sysConfBlessingMapper;

    @Autowired
    private RedisUtilX redisUtilX;


    @Autowired
    private SysConfBlessingCardMapper sysConfBlessingCardMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private UserDataMapper userDataMapper;

    @Autowired
    private UserBlessingMapper userBlessingMapper;


    //查询集福配置
    @ReadOnly
    public SysConfBlessingPO findConf() throws Exception{
        String key = RedisKey.conf_blessing;
        SysConfBlessingPO confBlessingPO = redisUtilX.getObj(key, SysConfBlessingPO.class);
        if(null != confBlessingPO){
            return confBlessingPO;
        }
        confBlessingPO = sysConfBlessingMapper.find();
        if(null != confBlessingPO){
            redisUtilX.setObj(key, confBlessingPO,600);
        }
        return confBlessingPO;
    }


    //查询福卡详情
    @ReadOnly
    public String findCardDetail(BlessingCardDetailParam param) throws Exception{
        if(null == param || null == param.getType()){
            return null;
        }
        List<SysConfBlessingCardPO> list = this.loadCardList();
        if(null == list || list.isEmpty()){
            return null;
        }
        Integer type = param.getType();

        SysConfBlessingCardPO cardPO = list.stream().filter(v->v.getType().equals(type)).findFirst().orElse(null);
        if(null == cardPO){
            return null;
        }
        return cardPO.getDetail();
    }



    //加载卡片列表
    private List<SysConfBlessingCardPO> loadCardList() throws Exception{
        String key = RedisKey.conf_blessing_card;
        List<SysConfBlessingCardPO> list = redisUtilX.getObjList(key, SysConfBlessingCardPO.class);
        if(null != list && !list.isEmpty()){
            return list;
        }
        list = sysConfBlessingCardMapper.select();
        if(null != list && !list.isEmpty()){
            redisUtilX.setObj(key,list,600);
        }
        else{
            list = null;
        }
        return list;
    }



    //我的福卡
    @ReadOnly
    public UserBlessingVO myBlessing() throws Exception{

        UserBlessingVO vo = new UserBlessingVO();
        vo.setAmount(new BigDecimal("0"));
        vo.setCardTypes(new ArrayList<Integer>());

        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return vo;
        }

        List<Integer> typeList = userBlessingMapper.selectTypeByUserIdPassInvite(userId);
        if(null == typeList || typeList.isEmpty()){
            return vo;
        }

        typeList = typeList.stream().distinct().toList();
        vo.setCardTypes(typeList);

        if(typeList.size() >= 5){
            BigDecimal amount = userDataMapper.findBlessingAmount(userId);
            if(null != amount && amount.compareTo(BigDecimal.ZERO) > 0){
                vo.setAmount(amount);
            }
        }

        vo.setInviteCardNum(userBlessingMapper.countByUserIdInviteCard(userId));
        return vo;
    }

    //查询我的邀请卡数量
    @ReadOnly
    public Integer myInviteBlessing() throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        Integer num = 0;
        if(null == userId){
            return num;
        }
        num = userBlessingMapper.countByUserIdInviteCard(userId);
        return num;
    }


}
