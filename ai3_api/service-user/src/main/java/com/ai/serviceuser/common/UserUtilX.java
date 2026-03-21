package com.ai.serviceuser.common;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.ClientRejectPO;
import com.ai.basecommon.core.po.user.UserPO;
import com.ai.basecommon.enums.StatusEnum;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.ClientRejectMapper;
import com.ai.serviceuser.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * @Description
 * @Author
 */
@Component
public class UserUtilX {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private ClientRejectMapper clientRejectMapper;

    @Autowired
    private UserMapper userMapper;


    //获取当前用户ID
    public Long getUserId() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object obj = request.getAttribute("X-UserId");
        Long userId = null;
        if(null != obj && !"".equals(obj.toString())){
            userId = Long.valueOf(obj.toString());
        }
        if(null == userId || userId < 1){
            BaseException.error(StatusCodeEnum.AUTH_ERROR);
        }
        String k = RedisKey.user_freeze_ + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            BaseException.error(StatusCodeEnum.USER_FREEZE);
        }
        return userId;
    }


    //获取当前用户ID
    public Long getUserIdNotError() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object obj = request.getAttribute("X-UserId");
        Long userId = null;
        if(null != obj && !"".equals(obj.toString())){
            userId = Long.valueOf(obj.toString());
        }
        if(null != userId){
            String k = RedisKey.user_freeze_ + userId;
            String v = redisUtilX.get(k);
            if(!StringUtil.isEmpty(v)){
                userId = null;
            }
        }
        return userId;
    }


    //清缓存
    public void cleanUserCache(Long userId) {
        String userInfoKey = RedisKey.user_po_cache_ + userId;
        redisUtilX.delete(userInfoKey);
    }

    //获取用户信息缓存
    public UserPO getCacheUserPO(Long userId) {
        UserPO userPO = null;
        String userPOKey = RedisKey.user_po_cache_ + userId;
        if(!redisUtilX.hasKey(userPOKey)){
            userPO = userMapper.findByUserId(userId);
        }
        else{
            userPO = redisUtilX.getObj(userPOKey,UserPO.class);
        }
        if(null != userPO){
            redisUtilX.setObj(userPOKey,userPO,600);
        }
        if(!StatusEnum.YES.getCode().equals(userPO.getStatus())){
            return null;
        }
        return userPO;
    }


    //获取当前请求的dvi
    public String getDvi() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object obj = request.getAttribute("X-Dvi");
        String dvi = null;
        if(null != obj && !"".equals(obj.toString())){
            dvi = (String) obj;
        }
        this.checkDvi(dvi);
        return dvi;
    }

    //获取当前请求的Oaid
    public String getOaid() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object obj = request.getAttribute("X-Oaid");
        String oaid = null;
        if(null != obj && !"".equals(obj.toString())){
            oaid = (String) obj;
        }
        return oaid;
    }

    public void checkDvi(String dvi) throws Exception{
        if(StringUtil.isEmpty(dvi)){
            return;
        }
        String key = RedisKey.client_reject_list;
        List<ClientRejectPO> list = null;
        try{
            list = redisUtilX.getObjList(key,ClientRejectPO.class);
        }catch (Exception e){
            LogUtil.log(e.getMessage());
        }
        if(null == list || list.isEmpty()){
            list = clientRejectMapper.selectAll();
            if(null != list && !list.isEmpty()){
                redisUtilX.setObj(key,list,600);
            }
        }
        if(null == list || list.isEmpty()){
            return;
        }
        ClientRejectPO po = list.stream().filter(v-> !StringUtil.isEmpty(v.getDeviceId()) && v.getDeviceId().equals(dvi)).findFirst().orElse(null);
        if(null != po){
            BaseException.error(StatusCodeEnum.REJECT);
        }
    }

    //获取当前请求的appVersion
    public String getAppVersion() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object obj = request.getAttribute("X-Version");
        String appVersion = null;
        if(null != obj && !"".equals(obj.toString())){
            appVersion = (String) obj;
        }
        return appVersion;
    }


}
