package com.ai.servicebusiness.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.*;
import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.ai.basecommon.core.dto.user.CheckPasswordPayDTO;
import com.ai.basecommon.core.dto.user.UserInfoDTO;
import com.ai.basecommon.core.param.OrderIdParam;
import com.ai.basecommon.core.param.pro.ProOrderAddParam;
import com.ai.basecommon.core.param.pro.MyProListParam;
import com.ai.basecommon.core.po.base.SysConfContractPO;
import com.ai.basecommon.core.po.pro.ProOrderPO;
import com.ai.basecommon.core.po.pro.ProPO;
import com.ai.basecommon.core.po.pro.ProSkuPO;
import com.ai.basecommon.core.po.shop.ShopOrderPO;
import com.ai.basecommon.core.po.shop.ShopPO;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.po.user.UserBalancePO;
import com.ai.basecommon.core.po.user.UserPO;
import com.ai.basecommon.core.po.user.UserPassPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.pro.ProContractVO;
import com.ai.basecommon.core.vo.pro.MyProVO;
import com.ai.basecommon.core.vo.pro.MyProDetailVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.*;
import com.ai.servicebusiness.async.CarPutAsync;
import com.ai.servicebusiness.async.WsPushAsync;
import com.ai.servicebusiness.commom.IpUtilX;
import com.ai.servicebusiness.commom.RedisUtilX;
import com.ai.servicebusiness.commom.TransactionUtilX;
import com.ai.servicebusiness.commom.UserUtilX;
import com.ai.servicebusiness.config.db.ReadOnly;
import com.ai.servicebusiness.mapper.*;
import com.ai.servicebusiness.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProOrderHandler {

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private SysConfContractMapper sysConfContractMapper;

    @Autowired
    private ProMapper proMapper;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private ProOrderMapper proOrderMapper;

    @Autowired
    private ProOrderTakeMapper proOrderTakeMapper;

    @Autowired
    private BillAmountProducer billAmountProducer;

    @Autowired
    private BillIntegralProducer billIntegralProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private UserDataProducer userDataProducer;

    @Autowired
    private CarPutAsync carPutAsync;

    @Autowired
    private UserTaskProducer userTaskProducer;

    @Autowired
    private UserAddrMapper userAddrMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private UserPassMapper userPassMapper;

    @Autowired
    private ProSkuMapper proSkuMapper;

    @Autowired
    private WsPushAsync wsPushAsync;

    //我的订单
    @ReadOnly
    public List<MyProVO> myList(MyProListParam param) throws Exception{
        if(null == param){
            param = new MyProListParam();
        }
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return new ArrayList<>();
        }
        param.setUserId(userId);
        if(null == param.getStatus() || 0 == param.getStatus()){
            param.setStatus(1);
        }
        return proOrderMapper.myList(param);
    }


    //我的项目详情
    @ReadOnly
    public MyProDetailVO myDetail(OrderIdParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getOrderId())){
            return null;
        }
        Long userId = userUtilX.getUserId();
        MyProDetailVO vo = proOrderMapper.myDetail(userId, param.getOrderId());
        if(null == vo){
            return null;
        }
        vo.setTakeList(proOrderTakeMapper.myTakeList(param.getOrderId()));
        return vo;
    }


    //合同
    @ReadOnly
    public ProContractVO contract(OrderIdParam param) throws Exception{
        if(StringUtil.isEmpty(param.getOrderId())){
            return null;
        }
        ProOrderPO po = proOrderMapper.findByOrderId(param.getOrderId());
        if(null == po){
            return null;
        }
        ProContractVO vo = new ProContractVO();
        vo.setId(po.getId());
        vo.setOrderId(po.getOrderId());
        vo.setRealName(po.getRealName());
        vo.setTitle(po.getTitle());
        vo.setStartTime(po.getStartTime());
        vo.setEndTime(po.getEndTime());
        vo.setCreateTime(po.getCreateTime());
        vo.setCateType(po.getCateType());
        vo.setPayAmount(po.getPayAmount());
        vo.setIncomeFloor(po.getIncomeFloor());
        vo.setCheckType(po.getCheckType());
        vo.setIsAutoNext(po.getIsAutoNext());

        UserPO userPO = userUtilX.getCacheUserPO(po.getUserId());
        if(null == userPO){
            return null;
        }
        String idCard = userPO.getIdcard();

        vo.setIdCard(idCard);


        String redisK = RedisKey.conf_contract;
        SysConfContractPO confContractPO = redisUtilX.getObj(redisK,SysConfContractPO.class);
        if(null == confContractPO){
            confContractPO = sysConfContractMapper.find();
            redisUtilX.setObj(redisK,confContractPO,600);
        }

        if(null == confContractPO){
            vo.setBbName("");
        }
        else{
            vo.setBbName(confContractPO.getBbName());
            vo.setBbImage(confContractPO.getBbImage());
        }

        return vo;
    }



    //投放
    public BaseVO put(ProOrderAddParam param) throws Exception{
        Long userId = userUtilX.getUserId();
        if(null == param || null == param.getId() || null == param.getSkuId() || StringUtil.isEmpty(param.getPayPwd())){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }
        if(null == param.getNum() || param.getNum() < 1){
            param.setNum(1);
        }
        if(param.getNum() > 20){
            return new BaseVO(StatusCodeEnum.PRO_LIMIT_20_MAX_NUM);
        }

        Long id = param.getId();
        Integer num = param.getNum();
        Long skuId = param.getSkuId();


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.LEASE.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("项目id是："+id+"，skuId是："+skuId+"，投放数量是：" + num + "份");


        if(id < 1){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 影视id参数有问题 不是正常的前端传值");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }

        if(null == num || num < 1){
            num = 1;
        }


        //查询用户信息
        UserPO userPO = userUtilX.getCacheUserPO(userId);
        if(null == userPO){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 用户不存在");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.USER_NO_EXIST);
        }

        if(!StatusEnum.YES.getCode().equals(userPO.getStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 用户是冻结状态");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.USER_FREEZE);
        }

        if(!AuthStatusEnum.YES.getCode().equals(userPO.getAuthStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 用户没有实名认证");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.AUTH_PLEASE);
        }

        String realName = userPO.getRealName();

        if(!StatusEnum.YES.getCode().equals(userPO.getNewbieStatus())){
            LogUtil.log("该用户没有完成新手任务 无法投放：" + userId);
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 该用户没有完成新手任务 无法投放");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.REQUEST_ERROR);
        }

        ProPO proPO = proMapper.findById(id);
        if(null == proPO || !StatusEnum.YES.getCode().equals(proPO.getStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 项目不存在或者已下架");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PRO_STATUS_ERROR);
        }
        userLogMsgDTO.setRemark(userLogMsgDTO.getRemark() + "，项目标题是：" + proPO.getTitle());

        if(proPO.getCompNum() < 1){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 项目已售罄");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PRO_SELL_OUT);
        }

        if(proPO.getCompNum() < num){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 可购买数量不足");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PRO_SELL_NOT_ENOUGH);
        }

        //等级限制
        if(null != proPO.getLevel() && proPO.getLevel() > 0){
            if(proPO.getLevel() > userPO.getLevel()){
                BaseException.error(StatusCodeEnum.PRO_LIMIT_LEVEL_, proPO.getLevel().toString());
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 当前项目限制VIP"+ proPO.getLevel()+"以上用户可投放");
                userLogProducer.produce(userLogMsgDTO);
                return BaseVO.bool(false);
            }
        }

        //限购逻辑
        if(null != proPO.getBuyMaxNum() && proPO.getBuyMaxNum() > 0){
            int n = proOrderMapper.countByUserIdAndProId(userId, proPO.getId());
            int max = 0;
            if(proPO.getBuyMaxNum() > n){
                max = proPO.getBuyMaxNum() - n;
            }
            if(num > max){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 超过项目限投数量 当前用户最多只能投放"+max+"份");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.PRO_BUY_MAX_NUM);
            }
        }

        String passwordPay = param.getPayPwd();
        CheckPasswordPayDTO payDTO = new CheckPasswordPayDTO();
        payDTO.setUserId(userId);
        payDTO.setPasswordPay(passwordPay);
        boolean isCheck = checkPasswordPay(payDTO);
        if(!isCheck){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 支付密码错误 传的支付密码是：" + passwordPay);
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PASSWORD_PAY_ERROR);
        }

        Integer ymd = DateUtil.todayDate();

        ProSkuPO proSkuPO = proSkuMapper.findById(skuId);
        if(null == proSkuPO){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 skuId无法查出规格信息");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PRO_SKU_ERROR);
        }
        if(!proPO.getId().equals(proSkuPO.getProId())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 篡改数据行为 skuId不属于本项目");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PRO_SKU_ERROR);
        }
        if(null == proSkuPO.getPrice() || proSkuPO.getPrice().compareTo(BigDecimal.ZERO) < 0 || null == proSkuPO.getPutDays() || proSkuPO.getPutDays() < 0 || null == proSkuPO.getTakeAmountMin() || null == proSkuPO.getTakeAmountMax() || null == proSkuPO.getIncomeFloor() || proSkuPO.getIncomeFloor().compareTo(BigDecimal.ZERO) < 1){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 数据错乱 sku信息不完整");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PRO_SKU_ERROR);
        }

        //支付价
        BigDecimal sum = proSkuPO.getPrice().multiply(new BigDecimal(num)).setScale(2,RoundingMode.DOWN);


        //校验余额
        UserBalancePO userBalancePO = userBalanceMapper.findByUserId(userId);
        if(null == userBalancePO || null == userBalancePO.getAmount() || userBalancePO.getAmount().compareTo(sum) < 0){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 余额不足 用户当前余额是：" + userBalancePO.getAmount() + "，项目总价是：" + sum + "，有绕过前端嫌疑");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.BALANCE_NOT_ENOUGH);
        }


        ProOrderPO proOrderPO = new ProOrderPO();

        //是否赠送实物
        ShopOrderPO shopOrderPO = null;
        if(2 == proPO.getGiveGoodsType()){
            UserAddrPO userAddrPO = userAddrMapper.findDefaultAddr(userId);
            if(null == userAddrPO){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("失败 用户没有收获地址 有绕过前端嫌疑");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.SHOP_ADDR_ERROR);
            }

            proPO.setGiveAmount(BigDecimal.ZERO);


            ShopPO shopPO = shopMapper.findById(proPO.getGiveShop());
            if(null != shopPO){

                shopOrderPO = new ShopOrderPO();
                shopOrderPO.setShopId(shopPO.getId());
                shopOrderPO.setUserId(userId);
                shopOrderPO.setNum(1);
                shopOrderPO.setPrice(shopPO.getPrice());
                shopOrderPO.setAmount(shopPO.getAmount());
                shopOrderPO.setSumEnergy(0);
                shopOrderPO.setSumAmount(BigDecimal.ZERO);
                shopOrderPO.setShopName(shopPO.getName());
                shopOrderPO.setShopImage(shopPO.getImage());
                shopOrderPO.setShopContent(shopPO.getContent());
                shopOrderPO.setShopCateId(shopPO.getCateId());
                shopOrderPO.setShopPrice(shopPO.getPrice());
                shopOrderPO.setShopIsVirtual(shopPO.getIsVirtual());

                shopOrderPO.setSource(ShopSourceEnum.PUT.getCode());
                shopOrderPO.setIsGift(IsEnum.NO.getCode());
                shopOrderPO.setGiftCode(null);

                shopOrderPO.setAddrReceiver(userAddrPO.getReceiver());
                shopOrderPO.setAddrMobile(userAddrPO.getMobile());
                shopOrderPO.setAddrProvince(userAddrPO.getProvince());
                shopOrderPO.setAddrCity(userAddrPO.getCity());
                shopOrderPO.setAddrDistrict(userAddrPO.getDistrict());
                shopOrderPO.setAddrDetail(userAddrPO.getDetail());
                shopOrderPO.setAddrCode(userAddrPO.getCode());
                shopOrderPO.setStatus(ShopOrderStatusEnum.WAIT_DELIVERY.getCode());

                shopOrderPO.setCreateTime(time);
                shopOrderPO.setUpdateTime(time);


            }

            proOrderPO.setGiveShop(proPO.getGiveShop());
            proOrderPO.setGiveShopDesc(proPO.getGiveShopDesc());
        }



        //开始时间
        Long startTime = DateUtil.getTomorrowStartTime();

        //结束时间
        Long endTime = 0 == proSkuPO.getPutDays() ? 0 : (startTime + (3600L * 1000 * 24 * proSkuPO.getPutDays()) - 1);

        proOrderPO.setUserId(userId);
        proOrderPO.setRealName(realName);
        proOrderPO.setProId(proPO.getId());
        proOrderPO.setTitle(proPO.getTitle());
        proOrderPO.setImage(proPO.getImage());
        proOrderPO.setTakeAmountMin(proSkuPO.getTakeAmountMin());
        proOrderPO.setTakeAmountMax(proSkuPO.getTakeAmountMax());
        proOrderPO.setIncomeFloor(proSkuPO.getIncomeFloor());
        proOrderPO.setExtraNumMin(proPO.getExtraNumMin());
        proOrderPO.setExtraNumMax(proPO.getExtraNumMax());
        proOrderPO.setFeeRate(proPO.getFeeRate());
        proOrderPO.setPutDays(proSkuPO.getPutDays());
        proOrderPO.setRemark(proPO.getRemark());
        proOrderPO.setContent(proPO.getContent());
        proOrderPO.setCateType(proPO.getCateType());
        proOrderPO.setPrice(proSkuPO.getPrice());
        proOrderPO.setPayAmount(proSkuPO.getPrice());
        proOrderPO.setGiveGoodsType(proPO.getGiveGoodsType());
        proOrderPO.setGiveAmount(proPO.getGiveAmount());
        proOrderPO.setGiveDesc(proPO.getGiveDesc());
        proOrderPO.setGiveQuotaAmount(proPO.getGiveQuotaAmount());
        proOrderPO.setGiveQuotaIsActive(proPO.getGiveQuotaIsActive());
        proOrderPO.setCheckType(proPO.getCheckType());
        proOrderPO.setIsAutoNext(proPO.getIsAutoNext());
        proOrderPO.setYmd(ymd);
        proOrderPO.setStartTime(startTime);
        proOrderPO.setEndTime(endTime);
        proOrderPO.setCreateTime(time);
        proOrderPO.setUpdateTime(time);


        List<String> orderIdList = new ArrayList<>();


        BigDecimal sumFinal = sum;
        Integer integralFinal = sum.setScale(0, RoundingMode.DOWN).intValue();

        int numFinal = num;
        ShopOrderPO shopOrderPOFinal = shopOrderPO;

        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{


            for(int i=0;i<numFinal;i++){
                String orderId = OrderIdUtil.getProjectOrderId(userId);
                proOrderPO.setOrderId(orderId);
                proOrderMapper.insertGetId(proOrderPO);
                if(null != shopOrderPOFinal){
                    shopOrderPOFinal.setOrderId(OrderIdUtil.getShopOrderId(userId));
                    shopOrderPOFinal.setLinkOrderId(orderId);
                    shopOrderMapper.insertGetId(shopOrderPOFinal);
                }
                orderIdList.add(orderId);
            }

            userBalanceMapper.decAmount(userId,sumFinal);

            userBalanceMapper.incIntegral(userId,integralFinal);

            proMapper.compNumDec(proPO.getId(),numFinal);

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("数据库操作失败：" + transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }


        String orderIds = orderIdList.isEmpty() ? proOrderPO.getOrderId() :  orderIdList.stream().collect(Collectors.joining(","));

        //资金账单
        BillAmountMsgDTO amountMsgDTO = new BillAmountMsgDTO();
        amountMsgDTO.setOrderId(orderIds);
        amountMsgDTO.setUserId(userId);
        amountMsgDTO.setAmount(sum);
        amountMsgDTO.setTypeEnum(BillAmountTypeEnum.CAR_PUT.getCode());
        amountMsgDTO.setTime(time);
        billAmountProducer.produce(amountMsgDTO);

        UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO.setUserId(userId);
        assetTrendsMsgDTO.setAmount(sum);
        assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.CAR_PUT_SUBTRACT.getCode());
        assetTrendsMsgDTO.setTime(time);
        assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
        assetTrendsMsgDTO.setRemark(1 == num ? proPO.getTitle() : proPO.getTitle() + "X" + num);
        userAssetTrendsProducer.produce(assetTrendsMsgDTO);


        //积分账单
        BillIntegralMsgDTO integralMsgDTO = new BillIntegralMsgDTO();
        integralMsgDTO.setOrderId(orderIds);
        integralMsgDTO.setUserId(userId);
        integralMsgDTO.setNum(sum.setScale(0,RoundingMode.UP).intValue());
        integralMsgDTO.setTypeEnum(BillIntegralTypeEnum.CAR_PUT.getCode());
        integralMsgDTO.setTime(time);
        billIntegralProducer.produce(integralMsgDTO);

        UserAssetTrendsMsgDTO assetTrendsMsgDTO2 = new UserAssetTrendsMsgDTO();
        assetTrendsMsgDTO2.setUserId(userId);
        assetTrendsMsgDTO2.setAmount(sum);
        assetTrendsMsgDTO2.setTypeEnum(UserAssetTrendsTypeEnum.CAR_PUT_ADD.getCode());
        assetTrendsMsgDTO2.setTime(time);
        assetTrendsMsgDTO2.setAssetType(AssetTypeEnum.INTEGRAL.getCode());
        assetTrendsMsgDTO2.setRemark(proPO.getTitle());
        userAssetTrendsProducer.produce(assetTrendsMsgDTO2);


        UserDataMsgDTO msgDTO = new UserDataMsgDTO();
        msgDTO.setUserId(userId);
        msgDTO.setLeaseAmount(sum);
        userDataProducer.produce(msgDTO);


        if(num > 1){
            for(int i=0;i<num;i++){
                proOrderPO.setOrderId(orderIdList.get(i));
                carPutAsync.putAfter(proOrderPO);
            }
        }
        else{
            carPutAsync.putAfter(proOrderPO);
        }

        UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
        userTaskMsgDTO.setUserId(userId);
        userTaskMsgDTO.setTaskType(TaskTypeEnum.LEASE.getCode());
        userTaskProducer.produce(userTaskMsgDTO);

        userLogMsgDTO.setContent("项目投放成功");
        userLogProducer.produce(userLogMsgDTO);

        wsPushAsync.pushBalance(userId,2000);

        return BaseVO.bool(true);
    }




    @ReadOnly
    private boolean checkPasswordPay(CheckPasswordPayDTO dto) throws Exception{
        if(null == dto || null == dto.getUserId() || StringUtil.isEmpty(dto.getPasswordPay())){
            return false;
        }
        Long userId = dto.getUserId();
        String passwordPay = dto.getPasswordPay().trim();

        UserPassPO passPO = userPassMapper.findByUserId(userId);
        if(null == passPO || StringUtil.isEmpty(passPO.getPasswordPay())){
            return false;
        }
        if(!EncryptUtil.aesDecrypt(passPO.getPasswordPay()).equals(passwordPay)){
            return false;
        }
        return true;
    }






}
