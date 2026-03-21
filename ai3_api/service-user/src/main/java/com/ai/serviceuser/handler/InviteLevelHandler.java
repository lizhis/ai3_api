package com.ai.serviceuser.handler;

import com.ai.basecommon.core.vo.user.MyChildrenVO;
import com.ai.basecommon.core.vo.user.MyChildrenUserVO;
import com.ai.basecommon.utils.CommonUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class InviteLevelHandler {

    @Autowired
    private InviteLevelMapper inviteLevelMapper;

    @Autowired
    private UserDataMapper userDataMapper;

    @Autowired
    private UserUtilX userUtilX;



    //我的一级下线
    @ReadOnly
    public MyChildrenVO myChildren1() throws Exception{
        Long userId = userUtilX.getUserId();

        MyChildrenVO vo = new MyChildrenVO();

        //一级2%  二级1%  三级0.5%
        BigDecimal rate1 = new BigDecimal("2");
        BigDecimal rate2 = new BigDecimal("1");
        BigDecimal rate3 = new BigDecimal("0.5");

        vo.setRate1(rate1);
        vo.setRate2(rate2);
        vo.setRate3(rate3);

        BigDecimal amount = userDataMapper.findChildren1Sum(userId);
        if(null == amount || amount.compareTo(BigDecimal.ZERO) < 1){
            amount = BigDecimal.ZERO;
        }

        vo.setAmount(amount);

        List<MyChildrenUserVO> childrenUserVOS = inviteLevelMapper.selectChildren1List(userId);
        if(null != childrenUserVOS && !childrenUserVOS.isEmpty()){
            for(MyChildrenUserVO child : childrenUserVOS){
                if(!StringUtil.isEmpty(child.getRealName())){
                    child.setName(CommonUtil.getHideName(child.getRealName()));
                }
                else{
                    child.setName(CommonUtil.getHideTel(child.getTel()));
                }
                child.setTel(null);
                child.setRealName(null);
            }
        }
        else{
            childrenUserVOS = new ArrayList<>();
        }

        vo.setList(childrenUserVOS);

        return vo;
    }





}
