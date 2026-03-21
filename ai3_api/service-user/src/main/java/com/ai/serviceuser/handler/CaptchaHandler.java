package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.dto.sms.SmsSendDTO;
import com.ai.basecommon.core.param.captcha.YzmForgetParam;
import com.ai.basecommon.core.param.captcha.YzmRegisterParam;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.enums.SmsYzmTypeEnum;
import com.ai.basecommon.enums.UserLogActionEnum;
import com.ai.basecommon.enums.UserLogSourceEnum;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.RegUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.common.*;
import com.ai.serviceuser.mapper.UserMapper;
import com.ai.serviceuser.mapper.DeviceInfoMapper;
import com.ai.serviceuser.producer.UserLogProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author
 */
@Component
public class CaptchaHandler {

    @Autowired
    private SmsUtilX smsUtilX;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private UserLogProducer userLogProducer;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private BlacklistUtilX blacklistUtilX;

    //发送注册短信
    public BaseVO register(YzmRegisterParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getTel()) || StringUtil.isEmpty(param.getDeviceId())){
            return BaseVO.bool(false);
        }
        String tel = param.getTel();
        String dviParam = param.getDeviceId();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();

        Long time = System.currentTimeMillis();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.SMS_REGISTER.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark(tel);


        if(!RegUtil.regTel(tel)){
            LogUtil.log("发注册短信 手机号参数为空 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 手机号格式错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REG_TEL_RULE);
        }

        boolean verify = blacklistUtilX.verify(tel,deviceId,ip);
        if(!verify){
            userLogMsgDTO.setAction(UserLogActionEnum.BLACKLIST.getCode());
            userLogMsgDTO.setRemark("用户注册，手机号：" + tel + "，设备号：" + deviceId + "，IP：" + ip);
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.BLACKLIST);
        }


        if(StringUtil.isEmpty(dviParam)){
            LogUtil.log("发注册短信 设备号参数为空 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("没有设备号");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
        }


        if(profile.startsWith("prod")){

            if(!dviParam.equals(deviceId)){
                LogUtil.log("发注册短信 设备号与header中不一致 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("设备号是伪造的");
                userLogMsgDTO.setRemark("传的设备号是：" + dviParam + "，header头的设备号是：" + deviceId);
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
            }


            String ipKey = RedisKey.ip_freeze_ + ip;
            String deviceIdKey = RedisKey.deviceId_freeze_ + deviceId;

            if(redisUtilX.hasKey(ipKey)){
                LogUtil.log("发注册短信 ip被封禁 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("ip被禁止发短信");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.FREEZE);
            }

            if(redisUtilX.hasKey(deviceIdKey)){
                LogUtil.log("发注册短信 设备被封禁 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("设备号被禁止发短信");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.FREEZE);
            }

            //校验设备号
            Long lastInTime = deviceInfoMapper.findLastTimeByDeviceId(deviceId);
            if(null == lastInTime){
                redisUtilX.set(ipKey,"1",86400);
                redisUtilX.set(deviceIdKey,"1",86400);
                LogUtil.log("发注册短信 设备ip双封禁1天 设备没有进站记录 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("设备号没有进站记录，设备ip双封，禁止发短信24小时");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
            }

        }



        //手机号是否注册过
        int s = userMapper.countByTel(tel);
        if(s > 0){
            LogUtil.log("发注册短信 手机号已注册过 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 手机号已经注册过");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.TEL_EXIST);
        }

        SmsSendDTO smsSendDTO = new SmsSendDTO();
        smsSendDTO.setTel(tel);
        smsSendDTO.setDeviceId(dviParam);
        smsSendDTO.setTypeEnum(SmsYzmTypeEnum.REGISTER);
        boolean r = smsUtilX.sendCode(smsSendDTO);

        if(r){
            userLogMsgDTO.setContent("发送成功");
        }
        else{
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("发送失败");
        }
        userLogProducer.produce(userLogMsgDTO);

        return BaseVO.bool(r);
    }


    //发送忘记密码短信
    public BaseVO forget(YzmForgetParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getTel()) || StringUtil.isEmpty(param.getDeviceId())){
            return BaseVO.bool(false);
        }
        String tel = param.getTel();
        String dviParam = param.getDeviceId();
        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();

        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(dviParam);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.SMS_FORGET.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("手机号是："+tel);

        if(!RegUtil.regTel(tel)){
            LogUtil.log("发找回密码短信 手机号参数为空 ip是：" + ip + "，设备号是：" + dviParam + "，手机号是：" + tel);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 手机号格式错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REG_TEL_RULE);
        }
        if(StringUtil.isEmpty(dviParam)){
            LogUtil.log("发找回密码短信 设备号参数为空 ip是：" + ip + "，设备号是：" + dviParam + "，手机号是：" + tel);
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 没有设备号");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
        }


        String ipKey = RedisKey.ip_freeze_ + ip;
        String deviceIdKey = RedisKey.deviceId_freeze_ + deviceId;

        if(profile.startsWith("prod")) {

            if(!dviParam.equals(deviceId)){
                LogUtil.log("发找回密码短信 设备号与header中不一致 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("设备号是伪造的");
                userLogMsgDTO.setRemark("传的设备号是：" + dviParam + "，header头的设备号是：" + deviceId);
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
            }


            if(redisUtilX.hasKey(ipKey)){
                LogUtil.log("发找回密码短信 ip被封禁 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("ip被禁止发短信");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.FREEZE);
            }

            if(redisUtilX.hasKey(deviceIdKey)){
                LogUtil.log("发找回密码短信 设备被封禁 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("设备号被禁止发短信");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.FREEZE);
            }

            //校验设备号
            Long lastInTime = deviceInfoMapper.findLastTimeByDeviceId(deviceId);
            if(null == lastInTime){
                redisUtilX.set(ipKey,"1",86400);
                redisUtilX.set(deviceIdKey,"1",86400);
                LogUtil.log("发找回密码短信 设备ip双封禁1天 设备没有进站记录 ip是：" + ip + "，设备号是：" + deviceId + "，手机号是：" + tel);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("设备号没有进站记录，设备ip双封，禁止发短信24小时");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
            }

        }



        //手机号是否存在
        int s = userMapper.countByTel(tel);
        if(0 == s){
            LogUtil.log("发找回密码短信 手机号不存在 ip是：" + ip + "，传的设备号是：" + deviceId + "，手机号是：" + tel);


            String accountErrorKey = "user_forget_account_error_" + deviceId;
            Integer alreadyCount = redisUtilX.getObj(accountErrorKey,Integer.class);
            if(null == alreadyCount){
                alreadyCount = 0;
            }
            int maxPassCount = 3;

            alreadyCount ++;
            if(alreadyCount >= maxPassCount){
                //达到3次 冻结设备
                redisUtilX.delete(accountErrorKey);
                redisUtilX.set(ipKey,"1",7200);

                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("手机号未注册，已达到3次，禁止ip发短信24小时");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.LOGIN_ACTION_FREEZE);
            }else{
                //未到五次 提醒次数
                redisUtilX.set(accountErrorKey,alreadyCount.toString(),7200);
                String c = String.valueOf(maxPassCount - alreadyCount);
                String msgs =  StatusCodeEnum.LOGIN_ACCOUNT_ERROR_.getMsg().replace("{}",c);
                int codes =  StatusCodeEnum.LOGIN_ACCOUNT_ERROR_.getCode();

                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 手机号未注册 " + msgs);
                userLogProducer.produce(userLogMsgDTO);

                return new BaseVO(codes,msgs);
            }
        }

        SmsSendDTO smsSendDTO = new SmsSendDTO();
        smsSendDTO.setTel(tel);
        smsSendDTO.setDeviceId(dviParam);
        smsSendDTO.setTypeEnum(SmsYzmTypeEnum.FORGET);
        boolean r = smsUtilX.sendCode(smsSendDTO);

        if(r){
            userLogMsgDTO.setContent("发送成功");
        }
        else{
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("发送失败");
        }
        userLogProducer.produce(userLogMsgDTO);

        return BaseVO.bool(r);
    }







}
