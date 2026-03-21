package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.map.IpDTO;
import com.ai.basecommon.core.dto.msg.CollectDayMsgDTO;
import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.dto.sms.VerifyCodeDTO;
import com.ai.basecommon.core.dto.user.UserInfoDTO;
import com.ai.basecommon.core.param.entrance.ForgetParam;
import com.ai.basecommon.core.param.entrance.LoginParam;
import com.ai.basecommon.core.param.entrance.RegisterParam;
import com.ai.basecommon.core.po.base.SysConfNewbiePO;
import com.ai.basecommon.core.po.user.*;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.UserInfoVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.common.*;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.CollectDayProducer;
import com.ai.serviceuser.async.UserAsync;
import com.ai.serviceuser.producer.UserLogProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author
 */
@Component
public class EntranceHandler {

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPassMapper userPassMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private UserAsync userAsync;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private SmsUtilX smsUtilX;

    @Autowired
    private CollectDayProducer collectDayProducer;

    @Autowired
    private SysConfNewbieMapper sysConfNewbieMapper;

    @Autowired
    private SeasonUserMapper seasonUserMapper;


    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private UserLoginMapper userLoginMapper;


    //白名单
    private final List<String> appIds = List.of("");


    //注册
    public BaseVO register(RegisterParam param) throws Exception{
        //LogUtil.log("有人注册：" + param);
        if(null == param || StringUtil.isEmpty(param.getTel()) || StringUtil.isEmpty(param.getPassword()) || null == param.getCode()){
            return BaseVO.bool(false);
        }
        if(null == param.getChannel()){
            param.setChannel(0);
        }

        String tel = param.getTel();
        String password = param.getPassword();
        String inviteTel = param.getInviteTel();
        Integer code = param.getCode();


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.REGISTER.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        String remark = "手机号：" + tel + "，密码：" + password + "，验证码：" + code;
        if(!StringUtil.isEmpty(inviteTel)){
            remark += "，推荐人：" + inviteTel;
        }
        userLogMsgDTO.setRemark(remark);


        if(!RegUtil.regTel(tel)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 手机号格式错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REG_TEL_RULE);
        }

        if(!RegUtil.regPassword(password)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 密码格式错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PASS_RULE_ERROR);
        }

        int c = userMapper.countByTel(tel);
        if(c > 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("手机号已注册 与上一步发送验证码填的不是一个手机号");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.TEL_EXIST);
        }



        UserPO po = new UserPO();


        if(!StringUtil.isEmpty(inviteTel)){
            UserPO inviteUser = userMapper.findByTel(inviteTel);
            if(null == inviteUser){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 查不到推荐人手机号");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.INVITE_TEL_ERROR);
            }
            po.setInviteUserId(inviteUser.getUserId());
        }

        //校验验证码
        if(!"dev".equals(this.profile)){
            VerifyCodeDTO verifyCodeDTO = new VerifyCodeDTO();
            verifyCodeDTO.setTel(tel);
            verifyCodeDTO.setCode(code);
            verifyCodeDTO.setTypeEnum(SmsYzmTypeEnum.REGISTER);
            boolean r = smsUtilX.verifyCode(verifyCodeDTO);
            if(!r){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 验证码错误");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.CAPTCHA_ERROR);
            }
        }

        //密码复杂度
        Integer passLevel = CommonUtil.getPassLevel(password);

        //IP
        IpDTO ipDTO = ipUtilX.getIpInfo();

        //生成随机昵称
        String nickname = StringUtil.randomStr(12);

        //tel,nickname,avatar,invite_user_id,pass_level,register_ip,register_addr,create_time,update_time
        po.setTel(tel);
        po.setNickname(nickname);
        po.setAvatar(null);
        po.setPassLevel(passLevel);
        po.setRegisterIp(ipDTO.getIp());
        po.setRegisterAddr(ipDTO.getAddress());
        po.setCreateTime(time);
        po.setUpdateTime(time);


        String passEncrypt = EncryptUtil.aesEncrypt(password);

        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{


            userMapper.insertGetId(po);

            UserPassPO passPO = new UserPassPO();
            passPO.setUserId(po.getUserId());
            passPO.setPassword(passEncrypt);
            userPassMapper.insert(passPO);

            //添加余额数据
            UserBalancePO balancePO = new UserBalancePO();
            balancePO.setUserId(po.getUserId());
            balancePO.setCreateTime(time);
            balancePO.setUpdateTime(time);
            userBalanceMapper.insert(balancePO);

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("入库失败：" + transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.bool(false);
        }

        CollectDayMsgDTO collectDayMsgDTO = new CollectDayMsgDTO();
        collectDayMsgDTO.setRegister(true);
        collectDayProducer.produce(collectDayMsgDTO);

        userAsync.registerAfter(po);

        //LogUtil.log("用户入库对象：" + po);

        if(param.getChannel() > 0){
            userMapper.updateChannel(po.getUserId(),param.getChannel());
        }
        else{
            userAsync.adCallback(po.getUserId(),deviceId,po.getRegisterIp());
        }
        //userAsync.adRegister(po.getUserId(),param.getDeviceId());


        userLogMsgDTO.setUserId(po.getUserId());
        userLogMsgDTO.setContent("注册成功");
        userLogProducer.produce(userLogMsgDTO);

        return BaseVO.bool(true);
    }



    //登录
    public BaseVO login(LoginParam param) throws Exception{

        if(null == param || StringUtil.isEmpty(param.getAccount()) || StringUtil.isEmpty(param.getPassword())){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        String account = param.getAccount();
        String password = param.getPassword();

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();

        IpDTO ipDTO = new IpDTO();
        if(!"dev".equals(this.profile) && appIds.contains(deviceId)){
            ipDTO.setIp("120.204.63.35");
            ipDTO.setAddress("上海");
            ipDTO.setAddressDetail("上海 上海 浦东 电信");
        }else{
            ipDTO = ipUtilX.getIpInfo();
        }
        String ip = ipDTO.getIp();


        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.LOGIN.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("登录账号：" + account + "，登录密码：" + password);

        String ipKey = RedisKey.ip_freeze_ + ip;


        if(profile.startsWith("prod")){

            if(redisUtilX.hasKey(ipKey)){
                LogUtil.log("用户登录错误 ip被封禁 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);

                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("ip被禁止登录");
                userLogProducer.produce(userLogMsgDTO);

                return new BaseVO(StatusCodeEnum.FREEZE);
            }

            String deviceIdKey = RedisKey.deviceId_freeze_ + deviceId;
            if(StringUtil.isEmpty(deviceId)){
                LogUtil.log("用户登录错误 没有设备号 登录ip是：" + ip + "，登录账号是：" + account + "，登录密码是：" + password);
                redisUtilX.set(ipKey,"1",86400);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("没有设备号，禁止ip登录24小时");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
            }
            else{
                if(redisUtilX.hasKey(deviceIdKey)){
                    LogUtil.log("用户登录错误 设备号被封禁 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);
                    userLogMsgDTO.setLevel(3);
                    userLogMsgDTO.setContent("设备号被禁止登录");
                    userLogProducer.produce(userLogMsgDTO);
                    return new BaseVO(StatusCodeEnum.FREEZE);
                }
            }
        }


        UserPO userPO = userMapper.findByTel(account);
        if(null == userPO){
            LogUtil.log("用户登录错误 账号不存在 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);

            String accountErrorKey = RedisKey.user_login_account_error_deviceId_ + deviceId;
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
                userLogMsgDTO.setContent("账号不存在，已达到3次，禁止ip登录2小时");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.LOGIN_ACTION_FREEZE);
            }else{
                //未到3次 提醒次数
                redisUtilX.set(accountErrorKey,alreadyCount.toString(),7200);
                String c = String.valueOf(maxPassCount - alreadyCount);
                String msgs =  StatusCodeEnum.LOGIN_ACCOUNT_ERROR_.getMsg().replace("{}",c);
                int codes =  StatusCodeEnum.LOGIN_ACCOUNT_ERROR_.getCode();

                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("账号不存在 " + msgs);
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(codes,msgs);
            }
        }

        Long userId = userPO.getUserId();

        if(!IsEnum.NO.getCode().equals(userPO.getIsDel())){
            LogUtil.log("用户登录错误 账号已删除 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 账号已经被删除");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.LOGIN_ACCOUNT_NO_EXIST);
        }

        //用户被冻结
        String userIdFreezeKey = RedisKey.user_freeze_ + userPO.getUserId();
        if(redisUtilX.hasKey(userIdFreezeKey)){
            LogUtil.log("用户登录错误 账号已冻结 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 账号已经被冻结");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.LOGIN_ACCOUNT_FREEZE);
        }

        if(!UserStatusEnum.NORMAL.getCode().equals(userPO.getStatus())){
            redisUtilX.set(userIdFreezeKey,"1",30, TimeUnit.DAYS);
            LogUtil.log("用户登录错误 账号已冻结 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 账号已经被冻结");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.LOGIN_ACCOUNT_FREEZE);
        }
        else{
            redisUtilX.delete(userIdFreezeKey);
        }


        //密码错误次数
        String passErrorKey = RedisKey.user_login_pass_error_userId_ + userPO.getUserId();
        Integer alreadyCount = redisUtilX.getObj(passErrorKey,Integer.class);
        if(null == alreadyCount){
            alreadyCount = 0;
        }
        int maxPassCount = 5;


        //校验密码
        UserPassPO passPO = userPassMapper.findByUserId(userPO.getUserId());
        if(null == passPO || StringUtil.isEmpty(passPO.getPassword())){
            LogUtil.log("用户登录错误 没有密码数据 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("没有密码数据 请联系技术排查！");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        if(!EncryptUtil.aesDecrypt(passPO.getPassword()).equals(password)){
            alreadyCount ++;
            if(alreadyCount >= maxPassCount){
                //达到五次 冻结账号
                redisUtilX.delete(passErrorKey);
                redisUtilX.set(userIdFreezeKey,"1",7200);
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("密码错误，已达到5次，禁止该用户登录2小时");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.LOGIN_ACTION_FREEZE);
            }else{
                //未到五次 提醒次数
                redisUtilX.set(passErrorKey,alreadyCount.toString(),7200);
                String c = String.valueOf(maxPassCount - alreadyCount);
                String msgs =  StatusCodeEnum.LOGIN_PASSWORD_ERROR_.getMsg().replace("{}",c);
                int codes =  StatusCodeEnum.LOGIN_PASSWORD_ERROR_.getCode();
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("密码错误 " + msgs);
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(codes,msgs);
            }
        }

        //生成token
        String token = JwtUtil.generateToken(userId);
        if(StringUtil.isEmpty(token)){
            LogUtil.log("用户登录错误 生成token为空 登录ip是：" + ip + "，设备号是：" + deviceId + "，登录账号是：" + account + "，登录密码是：" + password);
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 系统生成令牌失败");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.LOGIN_FAIL);
        }


        List<UserLoginPO> onlinePOs = userLoginMapper.selectByOnlineUserId(userId);
        if(null != onlinePOs && !onlinePOs.isEmpty()){
            for(UserLoginPO onlinePO : onlinePOs){
                if(!onlinePO.getDeviceId().equals(deviceId)){
                    //todo 推送消息  你的账号已在其他设备登录
                }
            }
            userLoginMapper.updateDisableByUserId(userId);
        }

        UserLoginPO userLoginPO = new UserLoginPO();
        userLoginPO.setUserId(userPO.getUserId());
        userLoginPO.setDeviceId(deviceId);
        userLoginPO.setIp(ip);
        userLoginPO.setToken(token);
        userLoginPO.setTokenMd5(EncryptUtil.md5(token));
        userLoginPO.setStatus(1);
        userLoginPO.setCreateTime(time + 86400L * 30L * 1000);
        userLoginPO.setCreateTime(time);
        userLoginPO.setUpdateTime(time);
        userLoginMapper.insert(userLoginPO);

        String k = "token_go_" + userLoginPO.getTokenMd5();
        redisUtilX.set(k,"1",86400);

        userLogMsgDTO.setUserId(userPO.getUserId());
        userLogMsgDTO.setContent("登录成功");
        userLogProducer.produce(userLogMsgDTO);

        userAsync.loginAfter(userPO.getUserId(),ipDTO);
        userUtilX.cleanUserCache(userPO.getUserId());
        return BaseVO.ok(token);
    }


    //找回密码
    public BaseVO forget(ForgetParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getTel()) || StringUtil.isEmpty(param.getPassword()) || null == param.getCode()){
            return BaseVO.bool(false);
        }

        String tel = param.getTel();
        String password = param.getPassword();
        Integer code = param.getCode();

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.FORGET.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("找回账号：" + tel + "，新密码：" + password + "，验证码：" + code);


        if(!RegUtil.regTel(tel)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 手机号格式错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REG_TEL_RULE);
        }

        if(!RegUtil.regPassword(password)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 密码格式错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PASS_RULE_ERROR);
        }

        UserPO userPO = userMapper.findByTel(tel);
        if(null == userPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("手机号不存在 与上一步发送验证码填的不是一个手机号");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.LOGIN_ACCOUNT_NO_EXIST);
        }

        //校验验证码
        VerifyCodeDTO verifyCodeDTO = new VerifyCodeDTO();
        verifyCodeDTO.setTel(tel);
        verifyCodeDTO.setCode(code);
        verifyCodeDTO.setTypeEnum(SmsYzmTypeEnum.FORGET);
        boolean r = smsUtilX.verifyCode(verifyCodeDTO);
        if(!r){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 验证码错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.CAPTCHA_ERROR);
        }


        //密码复杂度
        Integer passLevel = CommonUtil.getPassLevel(password);

        //更新密码
        String pass = EncryptUtil.aesEncrypt(password);



        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{
            userMapper.updatePassLevel(userPO.getUserId(),passLevel);
            userPassMapper.updatePass(userPO.getUserId(),pass);
        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("入库失败：" + transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.bool(false);
        }

        userLogMsgDTO.setContent("找回密码成功 用户ID是：" + userPO.getUserId());
        userLogProducer.produce(userLogMsgDTO);
        return BaseVO.bool(true);
    }


    //拉取用户信息
    @ReadOnly
    public UserInfoVO userInfo() throws Exception{
        Long userId = userUtilX.getUserId();
        UserInfoDTO userInfoDTO = userMapper.userInfo(userId);
        if(null == userInfoDTO){
            return null;
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(userId);
        vo.setNickname(userInfoDTO.getNickname());
        vo.setAvatar(userInfoDTO.getAvatar());
        //vo.setTel(CommonUtil.getHideTel(userInfoDTO.getTel()));
        vo.setTel(userInfoDTO.getTel());
        vo.setPassLevel(userInfoDTO.getPassLevel());
        vo.setRealName(userInfoDTO.getRealName());
        vo.setIdcard(CommonUtil.getHideIdCard(userInfoDTO.getIdcard()));
        vo.setAuthStatus(userInfoDTO.getAuthStatus());
        vo.setIsPasswordPay(!StringUtil.isEmpty(userInfoDTO.getPasswordPay()));
        vo.setLevel(userInfoDTO.getLevel());

        SysConfNewbiePO newbiePO = sysConfNewbieMapper.find();
        if(null != newbiePO && IsEnum.NO.getCode().equals(newbiePO.getOpenShell())){
            vo.setNewbieStatus(StatusEnum.YES.getCode());
        }
        else{
            vo.setNewbieStatus(userInfoDTO.getNewbieStatus());
        }

        vo.setIsSeason(0);
        vo.setSeasonTime(null);
        SeasonUserPO seasonUserPO = seasonUserMapper.findByUserId(userId);
        if(null != seasonUserPO){
            if(1 == seasonUserPO.getStatus()){
                vo.setIsSeason(1);
            }
            vo.setSeasonTime(seasonUserPO.getExpireTime());
        }

        return vo;
    }




}
