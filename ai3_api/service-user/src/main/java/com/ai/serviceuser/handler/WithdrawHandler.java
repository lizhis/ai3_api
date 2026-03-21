package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.dto.msg.WithdrawMsgDTO;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.param.user.WithdrawParam;
import com.ai.basecommon.core.param.user.WithdrawQuotaParam;
import com.ai.basecommon.core.po.base.SysConfWithdrawPO;
import com.ai.basecommon.core.po.user.*;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.SysConfWithdrawQuotaChildrenVO;
import com.ai.basecommon.core.vo.base.SysConfWithdrawQuotaVO;
import com.ai.basecommon.core.vo.base.SysConfWithdrawVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.common.IpUtilX;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.TransactionUtilX;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.UserLogProducer;
import com.ai.serviceuser.producer.WithdrawProducer;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.producer.WsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WithdrawHandler {

    @Autowired
    private WithdrawMapper withdrawMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private UserPassMapper userPassMapper;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private UserBankcardMapper userBankcardMapper;

    @Autowired
    private UserAlipayMapper userAlipayMapper;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private WithdrawProducer withdrawProducer;

    @Autowired
    private SysConfWithdrawMapper sysConfWithdrawMapper;

    @Autowired
    private UserSmallQuotaMapper userSmallQuotaMapper;

    @Autowired
    private SignRecordMapper signRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private WsProducer wsProducer;

    @Autowired
    private RedisUtilX redisUtilX;

    //提现配置
    public SysConfWithdrawVO sysConfWithdraw() throws Exception{
        Long userId = userUtilX.getUserId();

        SysConfWithdrawPO sysConfWithdrawPO = loadWithdrawConf();
        if(null == sysConfWithdrawPO){
            BaseException.error(StatusCodeEnum.NO_AUTH);
        }
        SysConfWithdrawVO vo = new SysConfWithdrawVO();
        vo.setWithdrawMin(sysConfWithdrawPO.getWithdrawMin());
        vo.setWithdrawDesc(sysConfWithdrawPO.getWithdrawDesc());
        vo.setQuotaAmount(new BigDecimal(sysConfWithdrawPO.getQuotaUserGiveAmount()));
        vo.setQuotaDesc(sysConfWithdrawPO.getQuotaDesc());

        if(!IsEnum.YES.getCode().equals(sysConfWithdrawPO.getQuotaIsOpen())){
            vo.setQuotaIsOpen(IsEnum.NO.getCode());
            return vo;
        }

        Long time = System.currentTimeMillis();

        //有没有支付宝账号
        UserAlipayPO userAlipayPO = userAlipayMapper.findByUserId(userId);
        if(null == userAlipayPO){
            UserPO userPO = userMapper.findByUserId(userId);
            userAlipayPO = new UserAlipayPO();
            userAlipayPO.setUserId(userId);
            if(AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
                userAlipayPO.setRealName(userPO.getRealName());
            }
            userAlipayPO.setAccount(userPO.getTel());
            userAlipayPO.setCreateTime(time);
            userAlipayPO.setUpdateTime(time);
            userAlipayMapper.insert(userAlipayPO);
        }
        vo.setAlipayAccount(userAlipayPO.getAccount());
        vo.setAlipayRealName(userAlipayPO.getRealName());

        //查询用户有几张可用的  有几张待激活的  最近一张激活的需要再连续签到几天
        int quotaNum = 0;
        int quotaWaitNum = 0;
        int needSignDays = 0;
        List<UserSmallQuotaPO> quotaPOList = userSmallQuotaMapper.selectOKAndWaitList(userId);
        if(null != quotaPOList && !quotaPOList.isEmpty()){
            Map<Integer,List<UserSmallQuotaPO>> map = quotaPOList.stream().collect(Collectors.groupingBy(UserSmallQuotaPO::getStatus));

            if(map.containsKey(StatusEnum.NEW.getCode())){
                List<UserSmallQuotaPO> li = map.get(StatusEnum.NEW.getCode());
                quotaWaitNum = li.size();

                if(null != sysConfWithdrawPO.getQuotaActiveSignDay() && sysConfWithdrawPO.getQuotaActiveSignDay() > 0){

                    UserSmallQuotaPO currentActivePO = li.stream().filter(v -> null != v.getStartTime() && v.getStartTime() > 0L).min(Comparator.comparing(UserSmallQuotaPO::getStartTime)).orElse(null);
                    if(null != currentActivePO){
                        Long startTime = currentActivePO.getStartTime();
                        //从这个时间到现在 连续签到了多少天
                        List<SignRecordPO> recordPOS = signRecordMapper.selectStartList(userId,startTime);
                        if(null != recordPOS && !recordPOS.isEmpty()){
                            int signDays = 0;
                            String d = DateUtil.timestampToDate(recordPOS.get(0).getCreateTime(),"yyyy-MM-dd");
                            Long minTime = DateUtil.dateToTimeStamp(d,"yyyy-MM-dd");
                            Long maxTime = minTime + 86400000L;
                            for(SignRecordPO recordPO : recordPOS){
                                if(recordPO.getCreateTime() >= minTime && recordPO.getCreateTime() < maxTime){
                                    signDays++;
                                    minTime = minTime + 86400000L;
                                    maxTime = maxTime + 86400000L;
                                    //LogUtil.log("循环id"+recordPO.getId()+" 是连续签到 当前连签：" + signDays);
                                }
                                else{
                                    signDays = 1;
                                    String dd = DateUtil.timestampToDate(recordPO.getCreateTime(),"yyyy-MM-dd");
                                    minTime = DateUtil.dateToTimeStamp(dd,"yyyy-MM-dd");
                                    maxTime = minTime + 86400000L;
                                    //LogUtil.log("循环id"+recordPO.getId()+" 不是连续签到 连签置为1");
                                }
                            }
                            needSignDays = sysConfWithdrawPO.getQuotaActiveSignDay() - signDays;
                            if(needSignDays < 1){
                                needSignDays = 1;
                            }
                        }
                        else{
                            needSignDays = sysConfWithdrawPO.getQuotaActiveSignDay();
                        }
                    }
                    else{
/*                        currentActivePO = li.stream().filter(v -> null == v.getStartTime() || v.getStartTime() <= 0L).findFirst().orElse(null);
                        if(null != currentActivePO){
                            userSmallQuotaMapper.updateStartTime(currentActivePO.getId(),time);
                        }*/
                    }
                }
            }
            if(map.containsKey(StatusEnum.YES.getCode())){
                //quotaNum = map.get(StatusEnum.YES.getCode()).size();
                //vo.setQuotaAmount();
                List<UserSmallQuotaPO> li = map.get(StatusEnum.YES.getCode());
                if(null != li && !li.isEmpty()){
                    li.sort(Comparator.comparing(UserSmallQuotaPO::getCreateTime));
                    UserSmallQuotaPO first = li.get(0);
                    vo.setQuotaAmount(new BigDecimal(first.getAmount()));
                    quotaNum = 1;
                }
            }
        }

        if(null != sysConfWithdrawPO.getQuotaActiveSignDay() && sysConfWithdrawPO.getQuotaActiveSignDay() > 0 && quotaWaitNum > 0 && 0 == needSignDays){
            needSignDays = sysConfWithdrawPO.getQuotaActiveSignDay();
        }

        boolean isOpen = 1 == sysConfWithdrawPO.getQuotaIsOpen() && (quotaNum + quotaWaitNum) > 0;
        vo.setQuotaIsOpen(isOpen ? 1 : 0);

        //    private Integer quotaNum;
        //    private Integer quotaWaitNum;
        //    private Integer needSignDays;
        vo.setQuotaNum(quotaNum);
        vo.setQuotaWaitNum(quotaWaitNum);
        vo.setNeedSignDays(needSignDays);
        return vo;
    }


    //提现
    public BaseVO withdraw(WithdrawParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || StringUtil.isEmpty(param.getPayPwd()) || param.getAmount().compareTo(BigDecimal.ZERO) < 0){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }
        Integer isSmallQuota = param.getIsSmallQuota();
        if(!IsEnum.YES.getCode().equals(isSmallQuota)){
            isSmallQuota = 0;
        }

        BigDecimal amount = param.getAmount();

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.WITHDRAW.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("金额："+amount+"，是否小额：" + isSmallQuota + "，资金密码：" + param.getPayPwd());


        SysConfWithdrawPO sysConfWithdrawPO = loadWithdrawConf();
        if(null == sysConfWithdrawPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("系统提现配置为空");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.NO_AUTH);
        }


        //校验支付密码
        UserPassPO passPO = userPassMapper.findByUserId(userId);
        if(null == passPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("用户支付密码没有数据库记录 user_pass");
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }
        if(!EncryptUtil.aesDecrypt(passPO.getPasswordPay()).equals(param.getPayPwd())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("支付密码错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PASSWORD_PAY_ERROR);
        }

        //查余额
        UserBalancePO balancePO = userBalanceMapper.findByUserId(userId);
        if(null == balancePO || amount.compareTo(balancePO.getAmount()) > 0){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 余额不足");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.BALANCE_NOT_ENOUGH);
        }


        UserSmallQuotaPO userSmallQuotaPO = null;


        WithdrawPO po = new WithdrawPO();

        if(IsEnum.YES.getCode().equals(isSmallQuota)){

            if(!IsEnum.YES.getCode().equals(sysConfWithdrawPO.getQuotaIsOpen()) || null == sysConfWithdrawPO.getQuotaAmount() || sysConfWithdrawPO.getQuotaAmount().compareTo(BigDecimal.ZERO) <= 0){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 小额提现配置已关闭");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.WITHDRAW_QUOTA_CLOSE);
            }

            //查小额券
            userSmallQuotaPO = userSmallQuotaMapper.findOK(userId);
            if(null == userSmallQuotaPO){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("用户没有小额券 有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.WITHDRAW_QUOTA_NUM_NO);
            }
            amount = new BigDecimal(userSmallQuotaPO.getAmount());

            //查用户支付宝账号
            UserAlipayPO userAlipayPO = userAlipayMapper.findByUserId(userId);
            if(null == userAlipayPO || StringUtil.isEmpty(userAlipayPO.getAccount()) || StringUtil.isEmpty(userAlipayPO.getRealName())){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("用户没有绑定支付宝账号 有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.WITHDRAW_PLEASE_ALIPAY);
            }
            po.setBankName("");
            po.setReceiver(userAlipayPO.getRealName());
            po.setCardNo(userAlipayPO.getAccount());
            po.setType(WithdrawTypeEnum.ALIPAY.getCode());
            po.setIsSmallQuota(IsEnum.YES.getCode());

            //查银行卡号
            UserBankcardPO bankcardPO = userBankcardMapper.findByUserId(userId);
            if(null == bankcardPO || StringUtil.isEmpty(bankcardPO.getCardNo())){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("小额提现 未绑定银行卡");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.WITHDRAW_PLEASE_BANK_CARD);
            }
        }
        else{

            if(null != sysConfWithdrawPO.getWithdrawMin()){
                if(amount.compareTo(sysConfWithdrawPO.getWithdrawMin()) < 0){
                    userLogMsgDTO.setLevel(3);
                    userLogMsgDTO.setContent("提现金额过低 系统最低提现金额是：" + sysConfWithdrawPO.getWithdrawMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                    userLogProducer.produce(userLogMsgDTO);
                    return new BaseVO(StatusCodeEnum.WITHDRAW_AMOUNT_MIN);
                }
            }

            //查银行卡号
            UserBankcardPO bankcardPO = userBankcardMapper.findByUserId(userId);
            if(null == bankcardPO || StringUtil.isEmpty(bankcardPO.getCardNo())){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("没有绑定银行卡就申请提现，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.WITHDRAW_PLEASE_BANK_CARD);
            }
            po.setBankName(bankcardPO.getBankName());
            po.setReceiver(bankcardPO.getReceiver());
            po.setCardNo(bankcardPO.getCardNo());
            po.setType(WithdrawTypeEnum.BANK.getCode());
            po.setIsSmallQuota(IsEnum.NO.getCode());
        }

        po.setWithdrawId(OrderIdUtil.getWithdrawId(userId));
        po.setUserId(userId);
        po.setAmount(amount);
        po.setStatus(WithdrawStatusEnum.WAIT.getCode());
        po.setCreateTime(time);
        po.setUpdateTime(time);


        BigDecimal amountFinal = amount;
        Integer isSmallQuotaFinal = isSmallQuota;
        Long id = IsEnum.YES.getCode().equals(isSmallQuotaFinal) ? userSmallQuotaPO.getId() : null;

        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{
            withdrawMapper.insert(po);
            userBalanceMapper.decAmount(userId,amountFinal);
            userBalanceMapper.incFreezeAmount(userId,amountFinal);

            if(IsEnum.YES.getCode().equals(isSmallQuotaFinal)){
                userSmallQuotaMapper.updateUse(id,po.getWithdrawId(),time);
            }

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("提现失败："+transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }


        WithdrawMsgDTO msgDTO = new WithdrawMsgDTO();
        msgDTO.setWithdrawId(po.getWithdrawId());
        msgDTO.setUserId(userId);
        msgDTO.setAmount(amount);
        withdrawProducer.produce(msgDTO);

        userLogMsgDTO.setContent("提现成功");
        userLogProducer.produce(userLogMsgDTO);


        WsSendDTO  wsSendDTO2 = new WsSendDTO();
        wsSendDTO2.setUserId(userId);
        wsSendDTO2.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(wsSendDTO2);

        return BaseVO.bool(true);
    }



    //小额提现配置
    public SysConfWithdrawQuotaVO sysConfWithdrawQuota() throws Exception{
        Long userId = userUtilX.getUserId();

        SysConfWithdrawPO sysConfWithdrawPO = loadWithdrawConf();
        if(null == sysConfWithdrawPO){
            BaseException.error(StatusCodeEnum.NO_AUTH);
        }
        SysConfWithdrawQuotaVO vo = new SysConfWithdrawQuotaVO();

        vo.setQuotaIsOpen(sysConfWithdrawPO.getQuotaIsOpen());
        vo.setQuotaDesc(sysConfWithdrawPO.getQuotaDesc());


        if(!IsEnum.YES.getCode().equals(vo.getQuotaIsOpen())){
            return vo;
        }

        Long time = System.currentTimeMillis();

        //有没有支付宝账号
        UserAlipayPO userAlipayPO = userAlipayMapper.findByUserId(userId);
        if(null == userAlipayPO){
            UserPO userPO = userMapper.findByUserId(userId);
            userAlipayPO = new UserAlipayPO();
            userAlipayPO.setUserId(userId);
            if(AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
                userAlipayPO.setRealName(userPO.getRealName());
            }
            userAlipayPO.setAccount(userPO.getTel());
            userAlipayPO.setCreateTime(time);
            userAlipayPO.setUpdateTime(time);
            userAlipayMapper.insert(userAlipayPO);
        }
        vo.setAlipayAccount(userAlipayPO.getAccount());
        vo.setAlipayRealName(userAlipayPO.getRealName());

        vo.setIsBoundBankcard(userBankcardMapper.countByUserId(userId));



        List<SysConfWithdrawQuotaChildrenVO> childrenVOS = new ArrayList<>();

        //查询用户有几张可用的  有几张待激活的  最近一张激活的需要再连续签到几天
        int needSignDays = 0;
        List<UserSmallQuotaPO> quotaPOList = userSmallQuotaMapper.selectOKAndWaitList(userId);
        if(null != quotaPOList && !quotaPOList.isEmpty()){

            for(UserSmallQuotaPO quotaPO : quotaPOList){
                SysConfWithdrawQuotaChildrenVO childrenVO = new SysConfWithdrawQuotaChildrenVO();
                childrenVO.setId(quotaPO.getId());
                childrenVO.setAmount(quotaPO.getAmount());
                childrenVO.setNeedDays(0);
                childrenVO.setCreateTime(quotaPO.getCreateTime());

                if(null == sysConfWithdrawPO.getQuotaActiveSignDay() || sysConfWithdrawPO.getQuotaActiveSignDay() < 1){
                    childrenVO.setStatus(StatusEnum.YES.getCode());
                }
                else{
                    childrenVO.setStatus(quotaPO.getStatus());
                }
                childrenVOS.add(childrenVO);
            }

            if(null != sysConfWithdrawPO.getQuotaActiveSignDay() || sysConfWithdrawPO.getQuotaActiveSignDay() > 0){
                Map<Integer,List<UserSmallQuotaPO>> map = quotaPOList.stream().collect(Collectors.groupingBy(UserSmallQuotaPO::getStatus));

                if(map.containsKey(StatusEnum.NEW.getCode())){
                    List<UserSmallQuotaPO> li = map.get(StatusEnum.NEW.getCode());
                    li.sort(Comparator.comparing(UserSmallQuotaPO::getCreateTime));

                    if(null != sysConfWithdrawPO.getQuotaActiveSignDay() && sysConfWithdrawPO.getQuotaActiveSignDay() > 0){
                        UserSmallQuotaPO currentActivePO = li.stream().filter(v -> null != v.getStartTime() && v.getStartTime() > 0L).min(Comparator.comparing(UserSmallQuotaPO::getStartTime)).orElse(null);
                        if(null != currentActivePO){
                            Long startTime = currentActivePO.getStartTime();
                            //从这个时间到现在 连续签到了多少天
                            List<SignRecordPO> recordPOS = signRecordMapper.selectStartList(userId,startTime);
                            if(null != recordPOS && !recordPOS.isEmpty()){
                                int signDays = 0;
                                String d = DateUtil.timestampToDate(recordPOS.get(0).getCreateTime(),"yyyy-MM-dd");
                                Long minTime = DateUtil.dateToTimeStamp(d,"yyyy-MM-dd");
                                Long maxTime = minTime + 86400000L;
                                for(SignRecordPO recordPO : recordPOS){
                                    if(recordPO.getCreateTime() >= minTime && recordPO.getCreateTime() < maxTime){
                                        signDays++;
                                        minTime = minTime + 86400000L;
                                        maxTime = maxTime + 86400000L;
                                        //LogUtil.log("循环id"+recordPO.getId()+" 是连续签到 当前连签：" + signDays);
                                    }
                                    else{
                                        signDays = 1;
                                        String dd = DateUtil.timestampToDate(recordPO.getCreateTime(),"yyyy-MM-dd");
                                        minTime = DateUtil.dateToTimeStamp(dd,"yyyy-MM-dd");
                                        maxTime = minTime + 86400000L;
                                        //LogUtil.log("循环id"+recordPO.getId()+" 不是连续签到 连签置为1");
                                    }
                                }
                                needSignDays = sysConfWithdrawPO.getQuotaActiveSignDay() - signDays;
                                if(needSignDays < 1){
                                    needSignDays = 1;
                                }
                            }
                            else{
                                needSignDays = sysConfWithdrawPO.getQuotaActiveSignDay();
                            }
                        }
                        else{
                            currentActivePO = li.get(0);
                            needSignDays = sysConfWithdrawPO.getQuotaActiveSignDay();
                        }
                        for(SysConfWithdrawQuotaChildrenVO childrenVO : childrenVOS){
                            if(childrenVO.getId().equals(currentActivePO.getId())){
                                childrenVO.setNeedDays(needSignDays);
                            }
                        }
                    }
                }
            }

        }
        childrenVOS.sort(Comparator.comparing(SysConfWithdrawQuotaChildrenVO::getStatus));
        vo.setList(childrenVOS);
        return vo;
    }


    //提现
    public BaseVO withdrawQuota(WithdrawQuotaParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getQuotaId() || StringUtil.isEmpty(param.getPayPwd())){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.WITHDRAW.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("小额提现，券ID：" + param.getQuotaId() + "，资金密码：" + param.getPayPwd());


        UserSmallQuotaPO userSmallQuotaPO = userSmallQuotaMapper.findById(param.getQuotaId());
        if(null == userSmallQuotaPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("用户没有小额券 有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.WITHDRAW_QUOTA_NUM_NO);
        }

        if(!Objects.equals(userSmallQuotaPO.getUserId(), userId)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("使用他人小额券 此用户有毒！");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.WITHDRAW_QUOTA_NO);
        }

        BigDecimal amount = new BigDecimal(userSmallQuotaPO.getAmount());

        userLogMsgDTO.setRemark(userLogMsgDTO.getRemark() + "，金额是：" + amount);

        SysConfWithdrawPO sysConfWithdrawPO = loadWithdrawConf();
        if(null == sysConfWithdrawPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("系统提现配置为空");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.NO_AUTH);
        }


        //校验支付密码
        UserPassPO passPO = userPassMapper.findByUserId(userId);
        if(null == passPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("用户支付密码没有数据库记录 user_pass");
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }
        if(!EncryptUtil.aesDecrypt(passPO.getPasswordPay()).equals(param.getPayPwd())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("支付密码错误");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PASSWORD_PAY_ERROR);
        }

        //查余额
        UserBalancePO balancePO = userBalanceMapper.findByUserId(userId);
        if(null == balancePO || amount.compareTo(balancePO.getAmount()) > 0){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 余额不足");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.BALANCE_NOT_ENOUGH);
        }


        WithdrawPO po = new WithdrawPO();


        if(!IsEnum.YES.getCode().equals(sysConfWithdrawPO.getQuotaIsOpen())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 小额提现配置已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.WITHDRAW_QUOTA_CLOSE);
        }


        //查用户支付宝账号
        UserAlipayPO userAlipayPO = userAlipayMapper.findByUserId(userId);
        if(null == userAlipayPO || StringUtil.isEmpty(userAlipayPO.getAccount()) || StringUtil.isEmpty(userAlipayPO.getRealName())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("用户没有绑定支付宝账号 有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.WITHDRAW_PLEASE_ALIPAY);
        }
        po.setBankName("");
        po.setReceiver(userAlipayPO.getRealName());
        po.setCardNo(userAlipayPO.getAccount());
        po.setType(WithdrawTypeEnum.ALIPAY.getCode());
        po.setIsSmallQuota(IsEnum.YES.getCode());

        //查银行卡号
        UserBankcardPO bankcardPO = userBankcardMapper.findByUserId(userId);
        if(null == bankcardPO || StringUtil.isEmpty(bankcardPO.getCardNo())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("小额提现 未绑定银行卡");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.WITHDRAW_PLEASE_BANK_CARD);
        }

        po.setWithdrawId(OrderIdUtil.getWithdrawId(userId));
        po.setUserId(userId);
        po.setAmount(amount);
        po.setStatus(WithdrawStatusEnum.WAIT.getCode());
        po.setCreateTime(time);
        po.setUpdateTime(time);


        BigDecimal amountFinal = amount;

        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{
            withdrawMapper.insert(po);
            userBalanceMapper.decAmount(userId,amountFinal);
            userBalanceMapper.incFreezeAmount(userId,amountFinal);

            userSmallQuotaMapper.updateUse(userSmallQuotaPO.getId(), po.getWithdrawId(),time);

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("提现失败："+transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }


        WithdrawMsgDTO msgDTO = new WithdrawMsgDTO();
        msgDTO.setWithdrawId(po.getWithdrawId());
        msgDTO.setUserId(userId);
        msgDTO.setAmount(amount);
        withdrawProducer.produce(msgDTO);

        userLogMsgDTO.setContent("提现成功");
        userLogProducer.produce(userLogMsgDTO);


        WsSendDTO  wsSendDTO2 = new WsSendDTO();
        wsSendDTO2.setUserId(userId);
        wsSendDTO2.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(wsSendDTO2);

        return BaseVO.bool(true);
    }



    private SysConfWithdrawPO loadWithdrawConf(){
        String key = RedisKey.conf_withdraw;
        SysConfWithdrawPO confWithdrawPO = null;
        if(redisUtilX.hasKey(key)){
            confWithdrawPO = redisUtilX.getObj(key, SysConfWithdrawPO.class);
        }
        if(null != confWithdrawPO){
            return confWithdrawPO;
        }
        confWithdrawPO = sysConfWithdrawMapper.find();
        if(null == confWithdrawPO){
            return null;
        }
        redisUtilX.setObj(key, confWithdrawPO,600);
        return confWithdrawPO;
    }





}
