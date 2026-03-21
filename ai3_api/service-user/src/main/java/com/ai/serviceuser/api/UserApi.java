package com.ai.serviceuser.api;

import com.ai.basecommon.core.dto.user.CheckPasswordPayDTO;
import com.ai.basecommon.core.dto.user.UserAuthDTO;
import com.ai.basecommon.core.po.base.SysVipPO;
import com.ai.basecommon.core.po.user.UserPO;
import com.ai.serviceuser.handler.SysVipHandler;
import com.ai.serviceuser.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/userApi",produces = "application/json;charset=utf-8")
public class UserApi {

    @Autowired
    private UserHandler userHandler;

    @Autowired
    private SysVipHandler sysVipHandler;


    //查询用户实名信息
    @RequestMapping("/findAuthInfo")
    public UserAuthDTO findAuthInfo(@RequestBody Long userId) throws Exception{
        if(null == userId){
            return null;
        }
        return userHandler.findAuthInfo(userId);
    }


    //查询用户实名信息
    @RequestMapping("/findByUserId")
    public UserPO findByUserId(@RequestBody Long userId) throws Exception{
        if(null == userId){
            return null;
        }
        return userHandler.findByUserId(userId);
    }

    //查询用户是否完成新手任务
    @RequestMapping("/isNewbieStatusOK")
    public boolean isNewbieStatusOK(@RequestBody Long userId) throws Exception{
        if(null == userId){
            return false;
        }
        return userHandler.isNewbieStatusOK(userId);
    }

    //查询等级信息
    @RequestMapping("/findVipLevel")
    public SysVipPO findByLevel(@RequestBody Integer level) throws Exception{
        if(null == level){
            return null;
        }
        return sysVipHandler.findByLevel(level);
    }

    //校验支付密码
    @RequestMapping("/checkPasswordPay")
    public boolean checkPasswordPay(@RequestBody CheckPasswordPayDTO dto) throws Exception{
        return userHandler.checkPasswordPay(dto);
    }





}
