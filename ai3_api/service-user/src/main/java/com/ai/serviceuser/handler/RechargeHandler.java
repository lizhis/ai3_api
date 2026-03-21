package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.msg.RechargeMsgDTO;
import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.param.user.*;
import com.ai.basecommon.core.po.base.*;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.*;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.core.po.user.RechargePO;
import com.ai.basecommon.core.vo.user.RechargeVO;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.OrderIdUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.*;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.RechargeProducer;
import com.ai.serviceuser.producer.UserLogProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class RechargeHandler {

    @Autowired
    private SysConfBankMapper sysConfBankMapper;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private RechargeMapper rechargeMapper;

    @Autowired
    private RechargeProducer rechargeProducer;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private AlipayUtilX alipayUtilX;

    @Autowired
    private SysConfRechargeAlipayMapper sysConfRechargeAlipayMapper;

    @Autowired
    private SysConfApiAlipayMapper sysConfApiAlipayMapper;

    @Autowired
    private SysConfApiAlipayScanMapper sysConfApiAlipayScanMapper;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private SysConfCaiyuanMapper sysConfCaiyuanMapper;

    @Autowired
    private SysConfApiCaiyuanMapper sysConfApiCaiyuanMapper;

    @Autowired
    private CaiyuanUtilX caiyuanUtilX;

    @Autowired
    private SysConfApiChangqingMapper sysConfApiChangqingMapper;

    @Autowired
    private SysConfChangqingMapper sysConfChangqingMapper;

    @Autowired
    private ChangqingUtilX changqingUtilX;


    @Autowired
    private SysConfApiZhihuiMapper sysConfApiZhihuiMapper;

    @Autowired
    private SysConfZhihuiMapper sysConfZhihuiMapper;

    @Autowired
    private ZhihuiUtilX zhihuiUtilX;



    @Autowired
    private SysConfApiHuadaMapper sysConfApiHuadaMapper;

    @Autowired
    private SysConfHuadaMapper sysConfHuadaMapper;

    @Autowired
    private HuadaUtilX huadaUtilX;


    @Autowired
    private SysConfApiHuada2Mapper sysConfApiHuada2Mapper;

    @Autowired
    private SysConfHuada2Mapper sysConfHuada2Mapper;

    @Autowired
    private Huada2UtilX huada2UtilX;


    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;


    @Autowired
    private SysConfApiQilinMapper sysConfApiQilinMapper;

    @Autowired
    private SysConfQilinMapper sysConfQilinMapper;

    @Autowired
    private PayQilinUtilX payQilinUtilX;


    private Integer getUserLevel(Long userId) {
        if(null == userId){
            return null;
        }
        String key = "user_level_" + userId;
        if(redisUtilX.hasKey(key)){
            return Integer.parseInt(redisUtilX.get(key));
        }
        Integer level = userMapper.findLevelByUserId(userId);
        redisUtilX.set(key,level.toString(),300);
        return level;
    }



    //获取支付方式
    @ReadOnly
    public List<PayWayVO> payWay() throws Exception{

        Long userId = userUtilX.getUserId();
        Integer userLevel = getUserLevel(userId);
        if(null == userLevel){
            return new ArrayList<>();
        }

        List<PayWayVO> list = new ArrayList<>();

        SysConfBankPO bankPO = sysConfBankMapper.find();
        if(null != bankPO){
            PayWayVO wayVO = new PayWayVO();
            wayVO.setWay(PayWayEnum.BANK.getCode());
            wayVO.setObj(bankPO);
            list.add(wayVO);
        }

        String alipayKey = RedisKey.conf_recharge_alipay;
        SysConfRechargeAlipayPO alipayPO = redisUtilX.getObj(alipayKey,SysConfRechargeAlipayPO.class);
        if(null == alipayPO){
            alipayPO = sysConfRechargeAlipayMapper.find();
        }
        if(null != alipayPO){
            if(IsEnum.YES.getCode().equals(alipayPO.getIsOpen())){
                List<PayAlipayVO> alipayVOList = sysConfApiAlipayMapper.selectVOList();
                if(null != alipayVOList && !alipayVOList.isEmpty()){
                    alipayVOList = alipayVOList.stream().filter(item -> userLevel >= item.getShowLevel()).toList();
                    if(!alipayVOList.isEmpty()){
                        if(1 == alipayPO.getOpenChannel()){
                            Random random = new Random();
                            PayAlipayVO alipayVO = alipayVOList.get(random.nextInt(alipayVOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.ALIPAY.getCode());
                            wayVO.setAmountMax(alipayPO.getAmountMax());
                            wayVO.setObj(alipayVO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayAlipayVO alipayVO : alipayVOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.ALIPAY.getCode());
                                wayVO.setAmountMax(alipayPO.getAmountMax());
                                wayVO.setObj(alipayVO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }

            if(IsEnum.YES.getCode().equals(alipayPO.getScanIsOpen())){
                List<PayAlipayVO> alipayVOList = sysConfApiAlipayScanMapper.selectVOList();
                if(null != alipayVOList && !alipayVOList.isEmpty()){
                    alipayVOList = alipayVOList.stream().filter(item -> userLevel >= item.getShowLevel()).toList();
                    if(!alipayVOList.isEmpty()){
                        if(1 == alipayPO.getScanOpenChannel()){
                            Random random = new Random();
                            PayAlipayVO alipayVO = alipayVOList.get(random.nextInt(alipayVOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.ALIPAY_SCAN.getCode());
                            wayVO.setAmountMax(alipayPO.getScanAmountMax());
                            wayVO.setObj(alipayVO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayAlipayVO alipayVO : alipayVOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.ALIPAY_SCAN.getCode());
                                wayVO.setAmountMax(alipayPO.getScanAmountMax());
                                wayVO.setObj(alipayVO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }

            if(IsEnum.YES.getCode().equals(alipayPO.getTransferOpen()) && !StringUtil.isEmpty(alipayPO.getTransferUrl())){
                PayWayVO wayVO = new PayWayVO();
                wayVO.setWay(PayWayEnum.ALIPAY_TRANSFER.getCode());
                wayVO.setObj(alipayPO);
                list.add(wayVO);
            }
        }

        //财源
        String caiyuanKey = RedisKey.conf_caiyuan;
        SysConfCaiyuanPO caiyuanPO = redisUtilX.getObj(caiyuanKey,SysConfCaiyuanPO.class);
        if(null == caiyuanPO){
            caiyuanPO = sysConfCaiyuanMapper.find();
        }
        if(null != caiyuanPO){
            redisUtilX.setObj(caiyuanKey,caiyuanPO,600);


            if(IsEnum.YES.getCode().equals(caiyuanPO.getIsOpen())){
                boolean isShowCaiyuan = true;
                if(null != caiyuanPO.getShowLevel() && caiyuanPO.getShowLevel() > 0){
                    if(userLevel < caiyuanPO.getShowLevel()){
                        isShowCaiyuan = false;
                    }
                }
                if(isShowCaiyuan){
                    List<PayCaiyuanVO> caiyuanVOList = sysConfApiCaiyuanMapper.selectVOList();
                    if(null != caiyuanVOList && !caiyuanVOList.isEmpty()){
                        if(1 == caiyuanPO.getOpenChannel()){
                            Random random = new Random();
                            PayCaiyuanVO caiyuanVO = caiyuanVOList.get(random.nextInt(caiyuanVOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.CAIYUAN.getCode());
                            wayVO.setAmountMax(caiyuanVO.getAmountMax());
                            wayVO.setObj(caiyuanVO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayCaiyuanVO payCaiyuanVO : caiyuanVOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.CAIYUAN.getCode());
                                wayVO.setAmountMax(payCaiyuanVO.getAmountMax());
                                wayVO.setObj(payCaiyuanVO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }


        }



        //长卿

/*
        String changqingKey = RedisKey.conf_changqing;
        SysConfChangqingPO changqingPO = redisUtilX.getObj(changqingKey,SysConfChangqingPO.class);
        if(null == changqingPO){
            changqingPO = sysConfChangqingMapper.find();
        }
        if(null != changqingPO){
            redisUtilX.setObj(changqingKey,changqingPO,600);


            if(IsEnum.YES.getCode().equals(changqingPO.getIsOpen())){
                boolean isShowChangqing = true;
                if(null != changqingPO.getShowLevel() && changqingPO.getShowLevel() > 0){
                    if(userLevel < changqingPO.getShowLevel()){
                        isShowChangqing = false;
                    }
                }
                if(isShowChangqing){
                    List<PayChangqingVO> changqingVOList = sysConfApiChangqingMapper.selectVOList();
                    if(null != changqingVOList && !changqingVOList.isEmpty()){
                        if(1 == changqingPO.getOpenChannel()){
                            Random random = new Random();
                            PayChangqingVO changqingVO = changqingVOList.get(random.nextInt(changqingVOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.CHANGQING.getCode());
                            wayVO.setAmountMax(changqingVO.getAmountMax());
                            wayVO.setObj(changqingVO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayChangqingVO payChangqingVO : changqingVOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.CHANGQING.getCode());
                                wayVO.setAmountMax(payChangqingVO.getAmountMax());
                                wayVO.setObj(payChangqingVO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }


        }
*/


        //智汇

/*
        String zhihuiKey = RedisKey.conf_zhihui;
        SysConfZhihuiPO zhihuiPO = redisUtilX.getObj(zhihuiKey,SysConfZhihuiPO.class);
        if(null == zhihuiPO){
            zhihuiPO = sysConfZhihuiMapper.find();
        }
        if(null != zhihuiPO){
            redisUtilX.setObj(zhihuiKey,zhihuiPO,600);


            if(IsEnum.YES.getCode().equals(zhihuiPO.getIsOpen())){
                boolean isShowZhihui = true;
                if(null != zhihuiPO.getShowLevel() && zhihuiPO.getShowLevel() > 0){
                    if(userLevel < zhihuiPO.getShowLevel()){
                        isShowZhihui = false;
                    }
                }
                if(isShowZhihui){
                    List<PayZhihuiVO> zhihuiVOList = sysConfApiZhihuiMapper.selectVOList();
                    if(null != zhihuiVOList && !zhihuiVOList.isEmpty()){
                        if(1 == zhihuiPO.getOpenChannel()){
                            Random random = new Random();
                            PayZhihuiVO zhihuiVO = zhihuiVOList.get(random.nextInt(zhihuiVOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.ZHIHUI.getCode());
                            wayVO.setAmountMax(zhihuiVO.getAmountMax());
                            wayVO.setObj(zhihuiVO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayZhihuiVO payZhihuiVO : zhihuiVOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.ZHIHUI.getCode());
                                wayVO.setAmountMax(payZhihuiVO.getAmountMax());
                                wayVO.setObj(payZhihuiVO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }


        }
*/



        //华达

/*
        String huadaKey = RedisKey.conf_huada;
        SysConfHuadaPO huadaPO = redisUtilX.getObj(huadaKey,SysConfHuadaPO.class);
        if(null == huadaPO){
            huadaPO = sysConfHuadaMapper.find();
        }
        if(null != huadaPO){
            redisUtilX.setObj(huadaKey,huadaPO,600);

            if(IsEnum.YES.getCode().equals(huadaPO.getIsOpen())){
                boolean isShowHuada = true;
                if(null != huadaPO.getShowLevel() && huadaPO.getShowLevel() > 0){
                    if(userLevel < huadaPO.getShowLevel()){
                        isShowHuada = false;
                    }
                }
                if(isShowHuada){
                    List<PayHuadaVO> huadaVOList = sysConfApiHuadaMapper.selectVOList();
                    if(null != huadaVOList && !huadaVOList.isEmpty()){
                        if(1 == huadaPO.getOpenChannel()){
                            Random random = new Random();
                            PayHuadaVO huadaVO = huadaVOList.get(random.nextInt(huadaVOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.HUADA.getCode());
                            wayVO.setAmountMax(huadaVO.getAmountMax());
                            wayVO.setObj(huadaVO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayHuadaVO payHuadaVO : huadaVOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.HUADA.getCode());
                                wayVO.setAmountMax(payHuadaVO.getAmountMax());
                                wayVO.setObj(payHuadaVO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }


        }
*/



        //华达2
        String huada2Key = RedisKey.conf_huada2;
        SysConfHuada2PO huada2PO = redisUtilX.getObj(huada2Key,SysConfHuada2PO.class);
        if(null == huada2PO){
            huada2PO = sysConfHuada2Mapper.find();
        }
        if(null != huada2PO){
            redisUtilX.setObj(huada2Key,huada2PO,600);

            if(IsEnum.YES.getCode().equals(huada2PO.getIsOpen())){
                boolean isShowHuada2 = true;
                if(null != huada2PO.getShowLevel() && huada2PO.getShowLevel() > 0){
                    if(userLevel < huada2PO.getShowLevel()){
                        isShowHuada2 = false;
                    }
                }
                if(isShowHuada2){
                    List<PayHuada2VO> huada2VOList = sysConfApiHuada2Mapper.selectVOList();
                    if(null != huada2VOList && !huada2VOList.isEmpty()){
                        if(1 == huada2PO.getOpenChannel()){
                            Random random = new Random();
                            PayHuada2VO huada2VO = huada2VOList.get(random.nextInt(huada2VOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.HUADA2.getCode());
                            wayVO.setAmountMax(huada2VO.getAmountMax());
                            wayVO.setObj(huada2VO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayHuada2VO payHuada2VO : huada2VOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.HUADA2.getCode());
                                wayVO.setAmountMax(payHuada2VO.getAmountMax());
                                wayVO.setObj(payHuada2VO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }


        }


        //麒麟
        String qilinKey = RedisKey.conf_qilin;
        SysConfQilinPO qilinPO = redisUtilX.getObj(qilinKey,SysConfQilinPO.class);
        if(null == qilinPO){
            qilinPO = sysConfQilinMapper.find();
        }
        if(null != qilinPO){
            redisUtilX.setObj(qilinKey,qilinPO,600);

            if(IsEnum.YES.getCode().equals(qilinPO.getIsOpen())){
                boolean isShowQilin = true;
                if(null != qilinPO.getShowLevel() && qilinPO.getShowLevel() > 0){
                    if(userLevel < qilinPO.getShowLevel()){
                        isShowQilin = false;
                    }
                }
                if(isShowQilin){
                    List<PayQilinVO> qilinVOList = sysConfApiQilinMapper.selectVOList();
                    if(null != qilinVOList && !qilinVOList.isEmpty()){
                        if(1 == qilinPO.getOpenChannel()){
                            Random random = new Random();
                            PayQilinVO qilinVO = qilinVOList.get(random.nextInt(qilinVOList.size()));
                            PayWayVO wayVO = new PayWayVO();
                            wayVO.setWay(PayWayEnum.QILIN.getCode());
                            wayVO.setAmountMax(qilinVO.getAmountMax());
                            wayVO.setObj(qilinVO);
                            list.add(wayVO);
                        }
                        else{
                            for (PayQilinVO payQilinVO : qilinVOList) {
                                PayWayVO wayVO = new PayWayVO();
                                wayVO.setWay(PayWayEnum.QILIN.getCode());
                                wayVO.setAmountMax(payQilinVO.getAmountMax());
                                wayVO.setObj(payQilinVO);
                                list.add(wayVO);
                            }
                        }
                    }
                }
            }


        }


        return list;
    }


    //银行卡充值
    public BaseVO bankPay(RechargeBankPayParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || StringUtil.isEmpty(param.getPayName()) || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        String payName = param.getPayName();
        BigDecimal amount = param.getAmount();
        String remark = param.getRemark();

        amount = amount.setScale(2, RoundingMode.DOWN);



        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_BANK.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        String rem = "付款人：" + payName + "，充值金额：" + amount + "元";
        if(StringUtil.isEmpty(remark)){
            rem += "，备注：" + remark;
        }
        userLogMsgDTO.setRemark(rem);

        String k = "user_recharge_bankpay_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 2秒钟内重复提交");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);


        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        RechargePO po = new RechargePO();
        po.setRechargeId(OrderIdUtil.getRechargeId(userId));
        po.setPayName(payName);
        po.setAmount(amount);
        po.setUserId(userId);
        po.setRemark(remark);
        po.setType(RechargeTypeEnum.BANK.getCode());
        po.setYmd(DateUtil.todayDate());
        po.setStatus(RechargeStatusEnum.WAIT.getCode());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        rechargeMapper.insert(po);

        RechargeMsgDTO msgDTO = new RechargeMsgDTO();
        msgDTO.setRechargeId(po.getRechargeId());
        msgDTO.setUserId(userId);
        msgDTO.setAmount(amount);
        rechargeProducer.produce(msgDTO);

        userLogMsgDTO.setContent("提交成功");
        userLogProducer.produce(userLogMsgDTO);
        return BaseVO.bool(true);
    }


    @ReadOnly
    private SysConfigPO loadConf() {
        String key = RedisKey.sys_config;
        SysConfigPO configPO = redisUtilX.getObj(key,SysConfigPO.class);
        if(null == configPO){
            configPO = sysConfigMapper.find();
            redisUtilX.setObj(key,configPO,600);
        }
        return configPO;
    }


    //查询记录
    @ReadOnly
    public List<RechargeVO> select(RechargeRecordParam param) throws Exception{
        if(null == param){
            param = new RechargeRecordParam();
        }
        param.setUserId(userUtilX.getUserId());
        return rechargeMapper.select(param);
    }


    //支付宝支付
    public BaseVO alipay(RechargeAlipayParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_ALIPAY.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("支付宝通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_alipay_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            LogUtil.log("用户频繁调支付宝接口 用户ID是：" + userId);
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 2秒钟内重复调用支付宝接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        SysConfApiAlipayPO sysConfApiAlipayPO = null;
        if(null != id && id > 0L){
            sysConfApiAlipayPO = sysConfApiAlipayMapper.findById(id);
        }
        if(null == sysConfApiAlipayPO){
            List<SysConfApiAlipayPO> sysConfApiAlipayPOS = sysConfApiAlipayMapper.select();
            if(null == sysConfApiAlipayPOS || sysConfApiAlipayPOS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("支付宝通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiAlipayPO = sysConfApiAlipayPOS.get(new Random().nextInt(sysConfApiAlipayPOS.size()));
        }

        String key = RedisKey.conf_recharge_alipay;
        SysConfRechargeAlipayPO po = redisUtilX.getObj(key,SysConfRechargeAlipayPO.class);
        if(null == po){
            po = sysConfRechargeAlipayMapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("支付宝充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("支付宝充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        Integer dayMaxNum = sysConfApiAlipayPO.getDayMaxNum();
        Integer pullMaxNum = sysConfApiAlipayPO.getPullMaxNum();

        Integer ymd = DateUtil.todayDate();

        String pullKey = "alipay_pull_time_user_"+userId+"_"+sysConfApiAlipayPO.getId()+"_"+ymd;

        Integer currentTimes = 0;
        if(null != pullMaxNum && pullMaxNum > 0){
            if(redisUtilX.hasKey(pullKey)){
                try{
                    String str = redisUtilX.get(pullKey);
                    if(!StringUtil.isEmpty(str)){
                        currentTimes = Integer.parseInt(str);
                    }
                }catch (Exception e){
                    LogUtil.log("支付宝充值 当天调起次数解析出错："+e.getMessage());
                    currentTimes = 0;
                }
            }
            if(currentTimes >= pullMaxNum){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("拉起支付宝已达"+pullMaxNum+"次，拒绝请求，收款支付宝的appid是"+sysConfApiAlipayPO.getAppid());
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error("已达付款次数上限，请尝试更换充值方式，或于明日再次支付");
            }
        }


        if(null != dayMaxNum && dayMaxNum > 0){
            int c = rechargeMapper.countAlipayByChannelToday(sysConfApiAlipayPO.getAppid(),ymd);
            if(c >= dayMaxNum){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("今日收款次数已满"+dayMaxNum+"次，暂停收款，收款支付宝的appid是"+sysConfApiAlipayPO.getAppid());
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error("通道繁忙，请更换通道");
            }
        }


        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        String orderId = OrderIdUtil.getRechargeId(userId);
        String body = null;
        try{
            body = alipayUtilX.pay(AlipayBusinessTypeEnum.RECHARGE,po.getGoodsName(),userId,orderId,amount,sysConfApiAlipayPO);
        }catch (Exception e){
            LogUtil.log("调用支付宝接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("调用支付宝接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        userLogMsgDTO.setContent("调用支付宝接口成功");
        userLogProducer.produce(userLogMsgDTO);

        currentTimes++;
        redisUtilX.set(pullKey,currentTimes.toString(),86400);

        return BaseVO.ok(body);
    }


    //支付宝 当面付
    public BaseVO alipayScan(RechargeAlipayScanParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_ALIPAY_SCAN.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("当面付通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_alipay_scan_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 2秒钟内重复调用当面付接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        String key = RedisKey.conf_recharge_alipay;
        SysConfRechargeAlipayPO po = redisUtilX.getObj(key,SysConfRechargeAlipayPO.class);
        if(null == po){
            po = sysConfRechargeAlipayMapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("支付宝充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getScanIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("当面付充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        SysConfApiAlipayScanPO sysConfApiAlipayScanPO = null;
        if(null != id && id > 0L){
            sysConfApiAlipayScanPO = sysConfApiAlipayScanMapper.findById(id);
        }
        if(null == sysConfApiAlipayScanPO){
            List<SysConfApiAlipayScanPO> sysConfApiAlipayScanPOS = sysConfApiAlipayScanMapper.select();
            if(null == sysConfApiAlipayScanPOS || sysConfApiAlipayScanPOS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("当面付通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiAlipayScanPO = sysConfApiAlipayScanPOS.get(new Random().nextInt(sysConfApiAlipayScanPOS.size()));
        }


        Integer dayMaxNum = sysConfApiAlipayScanPO.getDayMaxNum();
        Integer pullMaxNum = sysConfApiAlipayScanPO.getPullMaxNum();
        Integer ymd = DateUtil.todayDate();

        String pullKey = "alipay_scan_pull_time_user_"+userId+"_"+sysConfApiAlipayScanPO.getId()+"_"+ymd;

        Integer currentTimes = 0;
        if(null != pullMaxNum && pullMaxNum > 0){
            if(redisUtilX.hasKey(pullKey)){
                try{
                    String str = redisUtilX.get(pullKey);
                    if(!StringUtil.isEmpty(str)){
                        currentTimes = Integer.parseInt(str);
                    }
                }catch (Exception e){
                    LogUtil.log("支付宝充值 当天调起次数解析出错："+e.getMessage());
                    currentTimes = 0;
                }
            }
            if(currentTimes >= pullMaxNum){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("拉起当面付已达"+pullMaxNum+"次，拒绝请求，收款支付宝的appid是"+sysConfApiAlipayScanPO.getAppid());
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error("已达付款次数上限，请尝试更换充值方式，或于明日再次支付");
            }
        }

        if(null != dayMaxNum && dayMaxNum > 0){
            int c = rechargeMapper.countAlipayScanByChannelToday(sysConfApiAlipayScanPO.getAppid(),ymd);
            if(c >= dayMaxNum){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("今日收款次数已满"+dayMaxNum+"次，暂停收款，收款当面付的appid是"+sysConfApiAlipayScanPO.getAppid());
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.error("通道繁忙，请更换通道");
            }
        }

        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        String orderId = OrderIdUtil.getRechargeId(userId);
        String body = null;
        try{
            body = alipayUtilX.payScan(AlipayBusinessTypeEnum.RECHARGE,po.getScanGoodsName(),userId,orderId,amount,sysConfApiAlipayScanPO);
        }catch (Exception e){
            LogUtil.log("调用支付宝当面付接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("调用支付宝接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        //LogUtil.log("当面付支付接口返回的body是：" + body);
        userLogMsgDTO.setContent("调用当面付接口成功");
        userLogProducer.produce(userLogMsgDTO);

        currentTimes++;

        redisUtilX.set(pullKey,currentTimes.toString(),86400);

        return BaseVO.ok(body);
    }


    //财源支付
    public BaseVO caiyuan(RechargeCaiyuanParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_CAIYUAN.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("财源支付通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_caiyuan_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 2秒钟内重复调用财源支付接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        String key = RedisKey.conf_caiyuan;
        SysConfCaiyuanPO po = redisUtilX.getObj(key,SysConfCaiyuanPO.class);
        if(null == po){
            po = sysConfCaiyuanMapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("财源支付充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("财源支付充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        SysConfApiCaiyuanPO sysConfApiCaiyuanPO = null;
        if(null != id && id > 0L){
            sysConfApiCaiyuanPO = sysConfApiCaiyuanMapper.findById(id);
        }
        if(null == sysConfApiCaiyuanPO){
            List<SysConfApiCaiyuanPO> sysConfApiCaiyuanPOS = sysConfApiCaiyuanMapper.select();
            if(null == sysConfApiCaiyuanPOS || sysConfApiCaiyuanPOS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("财源支付通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiCaiyuanPO = sysConfApiCaiyuanPOS.get(new Random().nextInt(sysConfApiCaiyuanPOS.size()));
        }


        BigDecimal amountMax = sysConfApiCaiyuanPO.getAmountMax();

        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        if(amount.compareTo(amountMax) > 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过高 本渠道最高充值金额是：" + amountMax + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MAX);
        }

        String orderId = OrderIdUtil.getRechargeId(userId);

        String body = null;
        try{
            body = caiyuanUtilX.pay(PayBusinessTypeEnum.RECHARGE,userId,orderId,amount,po,sysConfApiCaiyuanPO);
        }catch (Exception e){
            LogUtil.log("调用财源支付接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("调用财源支付接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        userLogMsgDTO.setContent("调用财源支付接口成功");
        userLogProducer.produce(userLogMsgDTO);
        return BaseVO.ok(body);
    }

    //长青支付
    public BaseVO changqing(RechargeChangqingParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_CHANGQING.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("长卿支付通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_changqing_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 2秒钟内重复调用财源支付接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        String key = RedisKey.conf_changqing;
        SysConfChangqingPO po = redisUtilX.getObj(key,SysConfChangqingPO.class);
        if(null == po){
            po = sysConfChangqingMapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("长卿支付充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("长卿支付充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        SysConfApiChangqingPO sysConfApiChangqingPO = null;
        if(null != id && id > 0L){
            sysConfApiChangqingPO = sysConfApiChangqingMapper.findById(id);
        }
        if(null == sysConfApiChangqingPO){
            List<SysConfApiChangqingPO> sysConfApiChangqingPOS = sysConfApiChangqingMapper.select();
            if(null == sysConfApiChangqingPOS || sysConfApiChangqingPOS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("长卿支付通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiChangqingPO = sysConfApiChangqingPOS.get(new Random().nextInt(sysConfApiChangqingPOS.size()));
        }


        BigDecimal amountMin = sysConfApiChangqingPO.getAmountMin();
        BigDecimal amountMax = sysConfApiChangqingPO.getAmountMax();

        Integer ymd = DateUtil.todayDate();

        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        if(amountMin.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMin) < 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过低 本渠道最高充值金额是：" + amountMin + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
        }

        if(amountMax.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMax) > 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过高 本渠道最高充值金额是：" + amountMax + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MAX);
        }

        String orderId = OrderIdUtil.getRechargeId(userId);

        //LogUtil.log("生成订单号：" + orderId);

        String body = null;
        try{
            body = changqingUtilX.pay(PayBusinessTypeEnum.RECHARGE,userId,orderId,amount,po,sysConfApiChangqingPO,ip);
        }catch (Exception e){
            LogUtil.log("调用长卿支付接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("调用长卿支付接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        LogUtil.log("长卿支付接口返回的body是：" + body);
        userLogMsgDTO.setContent("调用长卿支付接口成功");
        userLogProducer.produce(userLogMsgDTO);


        return BaseVO.ok(body);
    }

    //智汇支付
    public BaseVO zhihui(RechargeZhihuiParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_ZHIHUI.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("智汇支付通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_zhihui_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 2秒钟内重复调用智汇支付接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        String key = RedisKey.conf_zhihui;
        SysConfZhihuiPO po = redisUtilX.getObj(key,SysConfZhihuiPO.class);
        if(null == po){
            po = sysConfZhihuiMapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("智汇支付充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("智汇支付充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        SysConfApiZhihuiPO sysConfApiZhihuiPO = null;
        if(null != id && id > 0L){
            sysConfApiZhihuiPO = sysConfApiZhihuiMapper.findById(id);
        }
        if(null == sysConfApiZhihuiPO){
            List<SysConfApiZhihuiPO> sysConfApiZhihuiPOS = sysConfApiZhihuiMapper.select();
            if(null == sysConfApiZhihuiPOS || sysConfApiZhihuiPOS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("智汇支付通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiZhihuiPO = sysConfApiZhihuiPOS.get(new Random().nextInt(sysConfApiZhihuiPOS.size()));
        }


        BigDecimal amountMin = sysConfApiZhihuiPO.getAmountMin();
        BigDecimal amountMax = sysConfApiZhihuiPO.getAmountMax();

        Integer ymd = DateUtil.todayDate();

        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        if(amountMin.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMin) < 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过低 本渠道最高充值金额是：" + amountMin + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
        }

        if(amountMax.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMax) > 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过高 本渠道最高充值金额是：" + amountMax + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MAX);
        }

        String orderId = OrderIdUtil.getRechargeId(userId);

        //LogUtil.log("生成订单号：" + orderId);

        String body = null;
        try{
            body = zhihuiUtilX.pay(PayBusinessTypeEnum.RECHARGE,userId,orderId,amount,po,sysConfApiZhihuiPO,ip);
        }catch (Exception e){
            LogUtil.log("调用智汇支付接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("调用智汇支付接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        LogUtil.log("智汇支付接口返回的body是：" + body);
        userLogMsgDTO.setContent("调用智汇支付接口成功");
        userLogProducer.produce(userLogMsgDTO);


        return BaseVO.ok(body);
    }

    //智汇支付
    public BaseVO huada(RechargeHuadaParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_HUADA.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("华达支付通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_huada_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 2秒钟内重复调用华达支付接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        String key = RedisKey.conf_huada;
        SysConfHuadaPO po = redisUtilX.getObj(key,SysConfHuadaPO.class);
        if(null == po){
            po = sysConfHuadaMapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("华达支付充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("华达支付充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        SysConfApiHuadaPO sysConfApiHuadaPO = null;
        if(null != id && id > 0L){
            sysConfApiHuadaPO = sysConfApiHuadaMapper.findById(id);
        }
        if(null == sysConfApiHuadaPO){
            List<SysConfApiHuadaPO> sysConfApiHuadaPOS = sysConfApiHuadaMapper.select();
            if(null == sysConfApiHuadaPOS || sysConfApiHuadaPOS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("华达支付通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiHuadaPO = sysConfApiHuadaPOS.get(new Random().nextInt(sysConfApiHuadaPOS.size()));
        }


        BigDecimal amountMin = sysConfApiHuadaPO.getAmountMin();
        BigDecimal amountMax = sysConfApiHuadaPO.getAmountMax();

        Integer ymd = DateUtil.todayDate();

        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        if(amountMin.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMin) < 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过低 本渠道最高充值金额是：" + amountMin + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
        }

        if(amountMax.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMax) > 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过高 本渠道最高充值金额是：" + amountMax + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MAX);
        }

        String orderId = OrderIdUtil.getRechargeId(userId);

        //LogUtil.log("生成订单号：" + orderId);

        String body = null;
        try{
            body = huadaUtilX.pay(PayBusinessTypeEnum.RECHARGE,userId,orderId,amount,po,sysConfApiHuadaPO,ip);
        }catch (Exception e){
            LogUtil.log("调用华达支付接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("调用华达支付接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        LogUtil.log("华达支付接口返回的body是：" + body);
        userLogMsgDTO.setContent("调用华达支付接口成功");
        userLogProducer.produce(userLogMsgDTO);


        return BaseVO.ok(body);
    }

    //华达2支付
    public BaseVO huada2(RechargeHuada2Param param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1 || StringUtil.isEmpty(param.getPayUserName())){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_HUADA2.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("华达2支付通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_huada2_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 2秒钟内重复调用华达2支付接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        String key = RedisKey.conf_huada2;
        SysConfHuada2PO po = redisUtilX.getObj(key,SysConfHuada2PO.class);
        if(null == po){
            po = sysConfHuada2Mapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("华达2支付充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("华达2支付充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        SysConfApiHuada2PO sysConfApiHuada2PO = null;
        if(null != id && id > 0L){
            sysConfApiHuada2PO = sysConfApiHuada2Mapper.findById(id);
        }
        if(null == sysConfApiHuada2PO){
            List<SysConfApiHuada2PO> sysConfApiHuada2POS = sysConfApiHuada2Mapper.select();
            if(null == sysConfApiHuada2POS || sysConfApiHuada2POS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("华达2支付通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiHuada2PO = sysConfApiHuada2POS.get(new Random().nextInt(sysConfApiHuada2POS.size()));
        }


        BigDecimal amountMin = sysConfApiHuada2PO.getAmountMin();
        BigDecimal amountMax = sysConfApiHuada2PO.getAmountMax();

        Integer ymd = DateUtil.todayDate();

        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        if(amountMin.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMin) < 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过低 本渠道最高充值金额是：" + amountMin + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MAX);
        }

        if(amountMax.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMax) > 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过高 本渠道最高充值金额是：" + amountMax + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
        }

        String orderId = OrderIdUtil.getRechargeId(userId);

        //LogUtil.log("生成订单号：" + orderId);

        String body = null;
        try{
            body = huada2UtilX.pay(PayBusinessTypeEnum.RECHARGE,userId,orderId,amount,po,sysConfApiHuada2PO,ip,param.getPayUserName());
        }catch (Exception e){
            LogUtil.log("调用华达2支付接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("调用华达2支付接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        LogUtil.log("华达2支付接口返回的body是：" + body);
        userLogMsgDTO.setContent("调用华达2支付接口成功");
        userLogProducer.produce(userLogMsgDTO);


        return BaseVO.ok(body);
    }

    //麒麟支付
    public BaseVO qilin(RechargeQilinParam param) throws Exception{
        String payChannel = "麒麟支付";
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getAmount() || param.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        Long id = param.getId();
        BigDecimal amount = param.getAmount();
        amount = amount.setScale(2, RoundingMode.DOWN);

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.RECHARGE_HUADA.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark(payChannel+"通道id是："+id+"，充值金额是：" + amount + "元");

        String k = "user_recharge_qilin_" + userId;
        String v = redisUtilX.get(k);
        if(!StringUtil.isEmpty(v)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent(payChannel + " 失败 2秒钟内重复调用支付接口");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_TOO_MANY);
        }
        redisUtilX.set(k,"1",2);

        String key = RedisKey.conf_qilin;
        SysConfQilinPO po = redisUtilX.getObj(key,SysConfQilinPO.class);
        if(null == po){
            po = sysConfQilinMapper.find();
            if(null == po){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent(payChannel + " 充值配置为空");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
            }
        }

        if(!IsEnum.YES.getCode().equals(po.getIsOpen())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent(payChannel + " 充值渠道已关闭");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_NOT);
        }

        SysConfApiQilinPO sysConfApiQilinPO = null;
        if(null != id && id > 0L){
            sysConfApiQilinPO = sysConfApiQilinMapper.findById(id);
        }
        if(null == sysConfApiQilinPO){
            List<SysConfApiQilinPO> sysConfApiQilinPOS = sysConfApiQilinMapper.select();
            if(null == sysConfApiQilinPOS || sysConfApiQilinPOS.isEmpty()){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent(payChannel + "通道没有配置");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_CHANNEL_ERROR);
            }
            sysConfApiQilinPO = sysConfApiQilinPOS.get(new Random().nextInt(sysConfApiQilinPOS.size()));
        }


        BigDecimal amountMin = sysConfApiQilinPO.getAmountMin();
        BigDecimal amountMax = sysConfApiQilinPO.getAmountMax();

        Integer ymd = DateUtil.todayDate();

        SysConfigPO configPO = sysConfigMapper.find();
        if(null == configPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("查不到系统充值配置 sys_config");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        if(null != configPO.getRechargeMin()){
            if(amount.compareTo(configPO.getRechargeMin()) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("充值金额过低 系统最低充值金额是：" + configPO.getRechargeMin() + "元，本条件前端校验过的，有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
            }
        }

        if(amountMin.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMin) < 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过低 本渠道最高充值金额是：" + amountMin + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MIN);
        }

        if(amountMax.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(amountMax) > 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("充值金额过高 本渠道最高充值金额是：" + amountMax + "元，本条件前端校验过的，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.RECHARGE_AMOUNT_MAX);
        }

        String orderId = OrderIdUtil.getRechargeId(userId);

        //LogUtil.log("生成订单号：" + orderId);

        String body = null;
        try{
            body = payQilinUtilX.pay(PayBusinessTypeEnum.RECHARGE,userId,orderId,amount,po,sysConfApiQilinPO,ip);
        }catch (Exception e){
            LogUtil.log(payChannel + " 调用支付接口失败："+e.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent(payChannel + " 调用支付接口失败："+e.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error("通道异常，请联系客服处理");
        }
        LogUtil.log(payChannel + " 支付接口返回的body是：" + body);
        userLogMsgDTO.setContent(payChannel + " 调用支付接口成功");
        userLogProducer.produce(userLogMsgDTO);


        return BaseVO.ok(body);
    }



}
