package com.ai.servicebusiness.handler;

import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.dto.user.UserAuthDTO;
import com.ai.basecommon.core.param.shop.GiftReceiveParam;
import com.ai.basecommon.core.po.shop.*;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.po.user.UserBankcardPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.shop.ShopVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.OrderIdUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.servicebusiness.commom.IpUtilX;
import com.ai.servicebusiness.commom.RedisUtilX;
import com.ai.servicebusiness.commom.TransactionUtilX;
import com.ai.servicebusiness.commom.UserUtilX;
import com.ai.servicebusiness.config.db.ReadOnly;
import com.ai.servicebusiness.mapper.*;
import com.ai.servicebusiness.producer.UserLogProducer;
import com.ai.servicebusiness.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description
 * @Author
 */
@Component
public class GiftHandler {


    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private GiftMapper giftMapper;

    @Autowired
    private GiftCodeMapper giftCodeMapper;

    @Autowired
    private GiftCodeRecordMapper giftCodeRecordMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private IUserService userService;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private UserBankcardMapper userBankcardMapper;


    //查询免费礼品
    @ReadOnly
    public List<ShopVO> selectGift() throws Exception{
        return shopMapper.selectGift();
    }


    //校验福利码
    public BaseVO checkCode(GiftReceiveParam param) throws Exception{
        BaseVO vo = null;
        if(null == param || null == param.getShopId()){
            vo = new BaseVO(StatusCodeEnum.PARAM_ERROR);
            return vo;
        }
        Long shopId = param.getShopId();
        Long userId = userUtilX.getUserId();
        String code = param.getCode().trim();

        if(StringUtil.isEmpty(code)){
            vo = new BaseVO(StatusCodeEnum.GIFT_CODE_EMPTY);
            return vo;
        }


        UserAuthDTO userAuthDTO = userService.findAuthInfo(userId);
        if(null == userAuthDTO){
            vo = new BaseVO(StatusCodeEnum.NO_AUTH);
            return vo;
        }

        if(!AuthStatusEnum.YES.getCode().equals(userAuthDTO.getAuthStatus())){
            vo = new BaseVO(StatusCodeEnum.PLEASE_AUTH);
            return vo;
        }

        //查礼品
        GiftPO giftPO = giftMapper.findByShopId(shopId);
        if(null == giftPO || !StatusEnum.YES.getCode().equals(giftPO.getStatus())){
            vo = new BaseVO(StatusCodeEnum.GIFT_NO_EXIST);
            return vo;
        }

        //查商品
        ShopPO shopPO = shopMapper.findById(param.getShopId());
        if(null == shopPO){
            vo = new BaseVO(StatusCodeEnum.SHOP_NO_EXIST);
            return vo;
        }

        if(!StatusEnum.YES.getCode().equals(shopPO.getStatus())){
            vo = new BaseVO(StatusCodeEnum.SHOP_STATUS_ERROR);
            return vo;
        }


        //查询福利码是否有效
        GiftCodePO giftCodePO = giftCodeMapper.findByCode(code);
        if(null == giftCodePO || !StatusEnum.YES.getCode().equals(giftCodePO.getStatus())){
            vo = new BaseVO(StatusCodeEnum.GIFT_CODE_ERROR);
            return vo;
        }

        if(null != giftCodePO.getUserId() && !giftCodePO.getUserId().equals(0L)){
            if(!userId.equals(giftCodePO.getUserId())){
                //专属福利码
                vo = new BaseVO(StatusCodeEnum.GIFT_CODE_CANNOT);
                return vo;
            }
        }

        //福利码是否还可用  今天数量满没有  总的数量满没有
        int totalNum = giftCodeRecordMapper.countTotalNum(code);
        if(giftCodePO.getMaxNum() > 0){
            if(giftCodePO.getMaxNum() <= totalNum){
                vo = new BaseVO(StatusCodeEnum.GIFT_CODE_MAX_NUM_FULL);
                return vo;
            }
        }

        Integer ymd = DateUtil.todayDate();
        if(giftCodePO.getDayNum() > 0){
            int dayNum = giftCodeRecordMapper.countDayNum(code,ymd);
            if(giftCodePO.getDayNum() <= dayNum){
                vo = new BaseVO(StatusCodeEnum.GIFT_CODE_DAY_NUM_FULL);
                return vo;
            }
        }

        //该用户是否使用过这个福利码
        String giftCodeUseCacheKey = "gift_code_use_cache_" + code + "_" + userId;
        if(redisUtilX.hasKey(giftCodeUseCacheKey)){
            vo = new BaseVO(StatusCodeEnum.GIFT_CODE_ALREADY_USE);
            return vo;
        }

        int isUse = giftCodeRecordMapper.countUse(userId,code);
        if(isUse > 0){
            vo = new BaseVO(StatusCodeEnum.GIFT_CODE_ALREADY_USE);
            return vo;
        }
        return BaseVO.ok(shopPO);
    }



    //商品领取
    public BaseVO receive(GiftReceiveParam param) throws Exception{

        if(null == param || null == param.getShopId()){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }
        Long shopId = param.getShopId();
        Long userId = userUtilX.getUserId();
        String code = param.getCode().trim();

        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.GIFT_RECEIVE.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("商品id是："+shopId + "，福利码是：" + code);

        if(StringUtil.isEmpty(code)){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 福利码是空的 严查！");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.GIFT_CODE_EMPTY);
        }


        UserAuthDTO userAuthDTO = userService.findAuthInfo(userId);
        if(null == userAuthDTO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 用户没有实名数据");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.NO_AUTH);
        }

        if(!AuthStatusEnum.YES.getCode().equals(userAuthDTO.getAuthStatus())){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("失败 用户没有实名");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.PLEASE_AUTH);
        }

        //查银行卡号
        UserBankcardPO bankcardPO = userBankcardMapper.findByUserId(userId);
        if(null == bankcardPO || StringUtil.isEmpty(bankcardPO.getCardNo())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("领取免费礼品 未绑定银行卡");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.WITHDRAW_PLEASE_BANK_CARD);
        }

        //查礼品
        GiftPO giftPO = giftMapper.findByShopId(shopId);
        if(null == giftPO || !StatusEnum.YES.getCode().equals(giftPO.getStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 礼品不存在或已下架了");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.GIFT_NO_EXIST);
        }

        //查商品
        ShopPO shopPO = shopMapper.findById(param.getShopId());
        if(null == shopPO){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 这个商品不存在");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SHOP_NO_EXIST);
        }

        if(!StatusEnum.YES.getCode().equals(shopPO.getStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 商品已经下架了");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SHOP_STATUS_ERROR);
        }


        //查询福利码是否有效
        GiftCodePO giftCodePO = giftCodeMapper.findByCode(code);
        if(null == giftCodePO || !StatusEnum.YES.getCode().equals(giftCodePO.getStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 福利码不存在或者已使用");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.GIFT_CODE_ERROR);
        }

        if(null != giftCodePO.getUserId() && !giftCodePO.getUserId().equals(0L)){
            if(!userId.equals(giftCodePO.getUserId())){
                //专属福利码
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 这是专属福利码");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.GIFT_CODE_CANNOT);
            }
        }

        //福利码是否还可用  今天数量满没有  总的数量满没有
        int totalNum = giftCodeRecordMapper.countTotalNum(code);
        if(giftCodePO.getMaxNum() > 0){
            if(giftCodePO.getMaxNum() <= totalNum){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 福利码总使用次数已满 本福利码最大使用次数"+giftCodePO.getMaxNum()+"次，已使用"+totalNum+"次");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.GIFT_CODE_MAX_NUM_FULL);
            }
        }

        Integer ymd = DateUtil.todayDate();
        if(giftCodePO.getDayNum() > 0){
            int dayNum = giftCodeRecordMapper.countDayNum(code,ymd);
            if(giftCodePO.getDayNum() <= dayNum){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 福利码今天使用次数已满 本福利码每天最大使用次数"+giftCodePO.getDayNum()+"次，今天已使用"+dayNum+"次");
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.GIFT_CODE_DAY_NUM_FULL);
            }
        }

        //该用户是否使用过这个福利码
        String giftCodeUseCacheKey = "gift_code_use_cache_" + code + "_" + userId;
        if(redisUtilX.hasKey(giftCodeUseCacheKey)){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 当前用户已经用过这个福利码了");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.GIFT_CODE_ALREADY_USE);
        }

        int isUse = giftCodeRecordMapper.countUse(userId,code);
        if(isUse > 0){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("失败 当前用户已经用过这个福利码了");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.GIFT_CODE_ALREADY_USE);
        }


        ShopOrderPO po = new ShopOrderPO();
        po.setOrderId(OrderIdUtil.getShopOrderId(userId));
        po.setShopId(shopPO.getId());
        po.setUserId(userId);
        po.setNum(1);
        po.setPrice(shopPO.getPrice());
        po.setAmount(shopPO.getAmount());
        po.setSumEnergy(0);
        po.setSumAmount(BigDecimal.ZERO);
        po.setShopName(shopPO.getName());
        po.setShopImage(shopPO.getImage());
        po.setShopContent(shopPO.getContent());
        po.setShopCateId(shopPO.getCateId());
        po.setShopPrice(shopPO.getPrice());
        po.setShopIsVirtual(shopPO.getIsVirtual());

        po.setIsGift(IsEnum.YES.getCode());
        po.setGiftCode(code);

        //如果不是虚拟物品
        if(!IsEnum.YES.getCode().equals(shopPO.getIsVirtual())){

            if(null == param.getAddrId() || param.getAddrId() < 1){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 收获地址有问题 传的地址id是：" + param.getAddrId());
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.SHOP_ADDR_PLEASE);
            }

            //查收货地址
            UserAddrPO addrPO = userService.findAddrById(param.getAddrId());
            if(null == addrPO || !userId.equals(addrPO.getUserId())){
                userLogMsgDTO.setLevel(2);
                userLogMsgDTO.setContent("失败 收获地址有问题 传的地址id是：" + param.getAddrId());
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.SHOP_ADDR_ERROR);
            }

            po.setAddrReceiver(addrPO.getReceiver());
            po.setAddrMobile(addrPO.getMobile());
            po.setAddrProvince(addrPO.getProvince());
            po.setAddrCity(addrPO.getCity());
            po.setAddrDistrict(addrPO.getDistrict());
            po.setAddrDetail(addrPO.getDetail());
            po.setAddrCode(addrPO.getCode());
            po.setStatus(ShopOrderStatusEnum.WAIT_DELIVERY.getCode());
        }
        else{
            po.setStatus(ShopOrderStatusEnum.FINISH.getCode());
        }

        po.setCreateTime(time);
        po.setUpdateTime(time);


        GiftCodeRecordPO recordPO = new GiftCodeRecordPO();
        recordPO.setCode(code);
        recordPO.setOrderId(po.getOrderId());
        recordPO.setShopName(po.getShopName());
        recordPO.setUserId(userId);
        recordPO.setRealName(userAuthDTO.getRealName());
        recordPO.setYmd(ymd);
        recordPO.setCreateTime(time);
        recordPO.setUpdateTime(time);


        totalNum++;


        int totalNumFinal = totalNum;

        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{

            shopOrderMapper.insertGetId(po);

            //tw_gift_code_record
            int s = giftCodeRecordMapper.insert(recordPO);
            if(s < 1){
                throw new RuntimeException("tw_gift_code_record 记录新增失败：" + recordPO);
            }
            //tw_gift_code
            giftCodeMapper.updateUseNum(giftCodePO.getId(),totalNumFinal);

            //tw_gift
            giftMapper.incReceiveNum(giftPO.getId());

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("操作数据库失败：" + transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }


        redisUtilX.set(giftCodeUseCacheKey,"1",86400*3);

        userLogMsgDTO.setContent("领取成功");
        userLogProducer.produce(userLogMsgDTO);

        return BaseVO.ok();
    }





}
