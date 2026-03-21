package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.ai.basecommon.core.dto.user.CheckPasswordPayDTO;
import com.ai.basecommon.core.dto.user.UserAuthDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.param.user.*;
import com.ai.basecommon.core.vo.user.UserBankcardVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.core.po.base.SysConfNewbiePO;
import com.ai.basecommon.core.po.user.UserAlipayPO;
import com.ai.basecommon.core.po.user.UserBankcardPO;
import com.ai.basecommon.core.po.user.UserPO;
import com.ai.basecommon.core.po.user.UserPassPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.async.UserAsync;
import com.ai.serviceuser.common.*;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.UserLogProducer;
import com.ai.serviceuser.producer.UserTaskProducer;
import com.ai.serviceuser.producer.WsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UserHandler {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private UserPassMapper userPassMapper;

    @Autowired
    private IdCardUtilX idCardUtilX;

    @Autowired
    private UploadUtilX uploadUtilX;

    @Autowired
    private UserBankcardMapper userBankcardMapper;

    @Autowired
    private UserAlipayMapper userAlipayMapper;

    @Autowired
    private BankCardUtilX bankCardUtilX;

    @Autowired
    private WsProducer wsProducer;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private SysConfNewbieMapper sysConfNewbieMapper;

    @Autowired
    private UserAsync userAsync;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private UserTaskProducer userTaskProducer;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;


    //修改密码
    public boolean editPassword(EditPasswordParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getOldpassword()) || StringUtil.isEmpty(param.getPassword())){
            return false;
        }

        Long userId = userUtilX.getUserId();

        String oldPassword = param.getOldpassword();
        String password = param.getPassword();

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.EDIT_PASSWORD.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("新密码：" + password);

        if(!RegUtil.regPassword(password)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("密码不符合规则");
            userLogProducer.produce(userLogMsgDTO);
            BaseException.error(StatusCodeEnum.PASS_RULE_ERROR);
        }

        UserPassPO po = userPassMapper.findByUserId(userId);

        if(null == po){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("用户不存在");
            userLogMsgDTO.setRemark("旧密码：" + oldPassword + "新密码：" + password + "，操作userId是：" + userId);
            userLogProducer.produce(userLogMsgDTO);
            BaseException.error(StatusCodeEnum.LOGIN_ACCOUNT_NO_EXIST);
        }

        if(!EncryptUtil.aesDecrypt(po.getPassword()).equals(oldPassword)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("旧密码错误");
            userLogMsgDTO.setRemark("旧密码：" + oldPassword + "新密码：" + password);
            userLogProducer.produce(userLogMsgDTO);
            BaseException.error(StatusCodeEnum.EDIT_DATA_OLD_PASSWORD_ERR);
        }


        boolean r = userPassMapper.updatePass(userId,EncryptUtil.aesEncrypt(password));
        userLogMsgDTO.setLevel(r ? 1 : 2);
        userLogProducer.produce(userLogMsgDTO);
        return r;
    }


    //修改支付密码
    public boolean editPayPassword(EditPayPasswordParam param) throws Exception{

        if(null == param || StringUtil.isEmpty(param.getPassword())){
            return false;
        }

        Long userId = userUtilX.getUserId();

        String oldPassword = param.getOldpassword();
        String password = param.getPassword();

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.EDIT_PAY_PASSWORD.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("新密码：" + password);

        if(!RegUtil.regPassword(password)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("密码不符合规则");
            userLogProducer.produce(userLogMsgDTO);
            BaseException.error(StatusCodeEnum.PASS_RULE_ERROR);
        }

        UserPassPO po = userPassMapper.findByUserId(userId);

        boolean s = false;
        if(StringUtil.isEmpty(po.getPasswordPay())){
            if(!StringUtil.isEmpty(oldPassword)){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("修改失败，并未设置支付密码，却传了旧密码参数");
                userLogMsgDTO.setRemark("参数旧密码：" + oldPassword + "参数新密码：" + password);
                userLogProducer.produce(userLogMsgDTO);
                BaseException.error(StatusCodeEnum.EDIT_DATA_OLD_PASSWORD_ERR);
            }
            s = true;
        }
        else{
            if(StringUtil.isEmpty(oldPassword) || !EncryptUtil.aesDecrypt(po.getPasswordPay()).equals(oldPassword)){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("旧密码错误");
                userLogMsgDTO.setRemark("参数旧密码：" + oldPassword + "参数新密码：" + password);
                userLogProducer.produce(userLogMsgDTO);
                BaseException.error(StatusCodeEnum.EDIT_DATA_OLD_PASSWORD_ERR);
            }
        }

        boolean r = userPassMapper.updatePassPay(userId,EncryptUtil.aesEncrypt(password));

        if(r && s){
            WsSendDTO wsSendDTO2 = new WsSendDTO();
            wsSendDTO2.setUserId(po.getUserId());
            wsSendDTO2.setCode(WsCodeEnum.USER_INFO.getCode());
            wsProducer.produce(wsSendDTO2);

            userLogMsgDTO.setLevel(1);
            userLogMsgDTO.setContent("支付密码设置成功");
            userLogProducer.produce(userLogMsgDTO);
        }
        else if(r){
            userLogMsgDTO.setLevel(1);
            userLogMsgDTO.setContent("支付密码修改成功");
            userLogProducer.produce(userLogMsgDTO);
        }
        else {
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("支付密码修改失败");
            userLogProducer.produce(userLogMsgDTO);
        }
        return r;
    }



    //实名认证
    public BaseVO auth(UserAuthParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getRealName()) || StringUtil.isEmpty(param.getIdcard())){
            return BaseVO.bool(false);
        }

        Long userId = userUtilX.getUserId();

        String realName = param.getRealName();
        String idcard = param.getIdcard();


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.AUTH.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("姓名：" + realName + "，身份证：" + idcard);


        String k = "user_auth_num_" + userId;
        String v = redisUtilX.get(k);

        int c = 0;
        if(!StringUtil.isEmpty(v)){
            try{
                c = Integer.parseInt(v);
            }catch (Exception e){
                LogUtil.log(e.getMessage());
            }
            if(c > 2){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("认证已超3次，请联系客服");
                userLogProducer.produce(userLogMsgDTO);
                BaseException.error("认证已超3次，请联系客服");
            }
        }
        c++;


        UserPO userPO = userMapper.findByUserId(userId);
        if(null == userPO){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("查不到用户信息");
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.bool(false);
        }
        if(!UserStatusEnum.NORMAL.getCode().equals(userPO.getStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("用户冻结状态");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.USER_FREEZE);
        }

        if(AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("用户已经是认证成功的状态了");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.AUTH_STATUS_YES);
        }

        int s = userMapper.countByIdcard(idcard);
        if(s > 0){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("该身份证已绑定过了");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.AUTH_EXIST);
        }

        redisUtilX.set(k, Integer.toString(c),86400);

        boolean r = false;
        if("dev".equals(this.profile)){
            r = true;
        }
        else{
            r = idCardUtilX.verify(realName,idcard,userPO.getTel());
        }

        if(!r){
            LogUtil.log("用户实名认证，认证不通过！ 用户ID：" +userId+ "，姓名：" + realName + "，身份证：" + idcard);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("认证不通过");
            userLogProducer.produce(userLogMsgDTO);
            //LogUtil.log("用户实名认证，认证不通过！ 用户ID：" +userId+ "，姓名：" + realName + "，身份证：" + idcard);
            return new BaseVO(StatusCodeEnum.AUTH_FAIL);
        }

        boolean f = userMapper.auth(userId,realName,idcard);

        LogUtil.log("用户实名认证，认证成功！用户ID：" +userId+ "，姓名：" + realName + "，身份证：" + idcard + "，第三方验证结果：" + r + "，入库结果：" + f);
        if(r){
            UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
            userTaskMsgDTO.setUserId(userId);
            userTaskMsgDTO.setTaskType(TaskTypeEnum.AUTH.getCode());
            userTaskProducer.produce(userTaskMsgDTO);

            userLogMsgDTO.setContent("认证成功");

        }
        else{
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("认证成功 入库失败");
        }

        userLogProducer.produce(userLogMsgDTO);

        userAsync.authAfter(userId,realName);
        userUtilX.cleanUserCache(userId);
        return BaseVO.bool(true);
    }


    //更新头像
    public String editPortrait(MultipartFile file) throws Exception{
        Long userId = userUtilX.getUserId();
        String fileName = uploadUtilX.file(file);
        if(StringUtil.isEmpty(fileName)){
            BaseException.error("头像上传失败");
        }
        boolean s = userMapper.updatePortrait(userId,fileName);
        if(!s){
            BaseException.error("头像更新失败");
        }
        userUtilX.cleanUserCache(userId);
        return fileName;
    }



    //查询用户实名信息
    public UserAuthDTO findAuthInfo(Long userId) throws Exception{
        if(null == userId || userId < 1){
            return null;
        }
        return userMapper.findAuthInfo(userId);
    }

    //查用户信息
    public UserPO findByUserId(Long userId) throws Exception{
        if(null == userId || userId < 1){
            return null;
        }
        return userMapper.findByUserId(userId);
    }

    //是否完成新手任务
    public boolean isNewbieStatusOK(Long userId) throws Exception{
        SysConfNewbiePO newbiePO = sysConfNewbieMapper.find();
        if(null != newbiePO && IsEnum.NO.getCode().equals(newbiePO.getOpenShell())){
            return true;
        }
        if(null == userId || userId < 1){
            return false;
        }
        int s = userMapper.findNewbieStatusByUserId(userId);
        return StatusEnum.YES.getCode().equals(s);
    }


    //获取我的银行卡
    public UserBankcardVO myBankCard() throws Exception{
        Long userId = userUtilX.getUserId();
        UserBankcardPO po = userBankcardMapper.findByUserId(userId);
        if(null == po){
            return null;
        }
        UserBankcardVO vo = new UserBankcardVO();
        vo.setReceiver(po.getReceiver());
        vo.setBankName(po.getBankName());
        vo.setCardNo(po.getCardNo());
        vo.setOpenName(po.getOpenName());
        return vo;
    }

    //绑定银行卡
    public boolean bindCard(BindCardParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || StringUtil.isEmpty(param.getBankName()) || StringUtil.isEmpty(param.getCardNo())){
            BaseException.error(StatusCodeEnum.PARAM_ERROR);
        }
        String k = "user_bind_card_num_" + userId;
        String v = redisUtilX.get(k);


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.BIND_CARD.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        String remark = "银行：" + param.getBankName();
        if(!StringUtil.isEmpty(param.getOpenName())){
            remark += "，开户行：" + param.getOpenName();
        }
        remark += "，卡号：" + param.getCardNo();
        userLogMsgDTO.setRemark(remark);


        if(this.profile.startsWith("prod")){
            int c = 0;
            if(!StringUtil.isEmpty(v)){
                try{
                    c = Integer.parseInt(v);
                }catch (Exception e){
                    LogUtil.log(e.getMessage());
                }
                if(c > 2){
                    userLogMsgDTO.setLevel(2);
                    userLogMsgDTO.setContent("认证已超3次，请联系客服");
                    userLogProducer.produce(userLogMsgDTO);
                    BaseException.error("认证已超3次，请联系客服");
                }
            }
            c++;

            redisUtilX.set(k, Integer.toString(c),86400);
        }

        UserPO userPO = userMapper.findByUserId(userId);
        if(null == userPO || !AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent(StatusCodeEnum.AUTH_PLEASE.getMsg());
            userLogProducer.produce(userLogMsgDTO);
            BaseException.error(StatusCodeEnum.AUTH_PLEASE);
        }
        remark += "，姓名：" + userPO.getRealName();
        userLogMsgDTO.setRemark(remark);

        int s = userBankcardMapper.countByUserId(userId);
        if(s > 0){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent(StatusCodeEnum.BANK_CARD_EXIST.getMsg());
            userLogProducer.produce(userLogMsgDTO);
            BaseException.error(StatusCodeEnum.BANK_CARD_EXIST);
        }

        if(this.profile.startsWith("prod")){
            boolean res = bankCardUtilX.verify(userPO.getRealName(), param.getCardNo(), userPO.getIdcard(),userPO.getTel());
            if(!res){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("银行卡4要素认证失败");
                userLogMsgDTO.setRemark("姓名：" + userPO.getRealName() + "，" + userLogMsgDTO.getRemark());
                userLogProducer.produce(userLogMsgDTO);
                BaseException.error("银行卡4要素认证失败");
            }
        }


        UserBankcardPO po = new UserBankcardPO();
        po.setUserId(userId);
        po.setReceiver(userPO.getRealName());
        po.setMobile(userPO.getTel());
        po.setBankName(param.getBankName());
        po.setOpenName(param.getOpenName());
        po.setCardNo(param.getCardNo());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        userBankcardMapper.insert(po);

        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setContent("银行卡绑定成功");
        userLogMsgDTO.setRemark("姓名：" + userPO.getRealName() + "，" + userLogMsgDTO.getRemark());
        userLogProducer.produce(userLogMsgDTO);

        return true;
    }



    //校验支付密码
    public boolean checkPasswordPay(CheckPasswordPayDTO dto) throws Exception{
        if(null == dto || null == dto.getUserId() || StringUtil.isEmpty(dto.getPasswordPay())){
            return false;
        }
        Long userId = dto.getUserId();
        String passwordPay = dto.getPasswordPay().trim();

        UserPassPO passPO = userPassMapper.findByUserId(userId);
        if(null == passPO){
            return false;
        }
        if(!EncryptUtil.aesDecrypt(passPO.getPasswordPay()).equals(passwordPay)){
            return false;
        }
        return true;
    }



    //获取我的支付宝账号
    public UserAlipayPO myAlipay() throws Exception{
        Long userId = userUtilX.getUserId();
        return userAlipayMapper.findByUserId(userId);
    }

    //绑定支付宝
    public boolean bindAlipay(BindAlipayParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || StringUtil.isEmpty(param.getAccount())){
            BaseException.error(StatusCodeEnum.PARAM_ERROR);
        }

        String account = param.getAccount();
        if(!RegUtil.regTel(account) && !RegUtil.regEmail(account)){
            BaseException.error(StatusCodeEnum.BANK_ALIPAY_ERROR);
        }

        UserPO userPO = userMapper.findByUserId(userId);
        if(null == userPO || !AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
            BaseException.error(StatusCodeEnum.AUTH_PLEASE);
        }

        int s = userAlipayMapper.countByUserId(userId);
        if(s > 0){
            BaseException.error(StatusCodeEnum.BANK_ALIPAY_EXIST);
        }

        Long time = System.currentTimeMillis();
        UserAlipayPO po = new UserAlipayPO();
        po.setUserId(userId);
        po.setRealName(userPO.getRealName());
        po.setAccount(param.getAccount());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        userAlipayMapper.insert(po);

        return true;
    }





}
