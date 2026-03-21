package com.ai.serviceuser.handler;

import com.ai.basecommon.core.param.IdParam;
import com.ai.basecommon.core.param.user.UserMsgParam;
import com.ai.basecommon.core.po.user.UserMsgPO;
import com.ai.basecommon.core.vo.user.MyNewMsgVO;
import com.ai.basecommon.core.vo.user.UserMsgDetailVO;
import com.ai.basecommon.core.vo.user.UserMsgVO;
import com.ai.basecommon.enums.IsEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.MsgMapper;
import com.ai.serviceuser.mapper.UserMsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMsgHandler {

    @Autowired
    private UserMsgMapper userMsgMapper;

    @Autowired
    private MsgMapper msgMapper;

    @Autowired
    private UserUtilX userUtilX;

    @ReadOnly
    public List<UserMsgVO> select(UserMsgParam param) throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return null;
        }
        param.setUserId(userId);
        return userMsgMapper.select(param);
    }


    public UserMsgDetailVO detail(IdParam param) throws Exception{
        if(null == param || null == param.getId()){
            return null;
        }
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return null;
        }
        UserMsgPO po = userMsgMapper.findById(param.getId());
        if(null == po || !po.getUserId().equals(userId)){
            return null;
        }
        if(IsEnum.NO.getCode().equals(po.getIsRead())){
            //已读
            try{
                userMsgMapper.updateRead(po.getId(),System.currentTimeMillis());
                msgMapper.incReadNum(po.getMsgId());
            }catch (Exception e){
                LogUtil.log(e.getMessage());
            }
        }
        UserMsgDetailVO vo = new UserMsgDetailVO();
        vo.setTitle(po.getTitle());
        vo.setContent(po.getContent());
        vo.setContentType(po.getContentType());
        vo.setCreateTime(po.getCreateTime());
        return vo;
    }


    public MyNewMsgVO newMsg() throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return null;
        }
        int count = userMsgMapper.countUnRead(userId);
/*        if(count < 1){
            return null;
        }*/
        UserMsgVO userMsgVO = userMsgMapper.findMyNewMsg(userId);
        MyNewMsgVO vo = new MyNewMsgVO();
        vo.setCount(count);
        vo.setMsg(userMsgVO);
        if(null != userMsgVO){
            userMsgMapper.updateNotice(userMsgVO.getId());
        }
        return vo;
    }




}
