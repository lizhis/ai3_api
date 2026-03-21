package com.ai.servicebusiness.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.*;
import com.ai.basecommon.core.dto.ws.WsSendDTO;
import com.ai.basecommon.core.param.OrderIdParam;
import com.ai.basecommon.core.param.shop.MyOrderParam;
import com.ai.basecommon.core.param.shop.ShopBuyParam;
import com.ai.basecommon.core.param.shop.ShopParam;
import com.ai.basecommon.core.po.shop.ShopOrderPO;
import com.ai.basecommon.core.po.shop.ShopPO;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.po.user.UserBalancePO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.shop.*;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.*;
import com.ai.servicebusiness.commom.*;
import com.ai.servicebusiness.config.db.ReadOnly;
import com.ai.servicebusiness.mapper.ShopCateMapper;
import com.ai.servicebusiness.mapper.ShopMapper;
import com.ai.servicebusiness.mapper.ShopOrderMapper;
import com.ai.servicebusiness.mapper.UserBalanceMapper;
import com.ai.servicebusiness.producer.*;
import com.ai.servicebusiness.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShopHandler {

    @Autowired
    private ShopCateMapper shopCateMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private IUserService userService;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private Kuaidi100UtilX kuaidi100UtilX;

    @Autowired
    private BillEnergyProducer billEnergyProducer;

    @Autowired
    private UserAssetTrendsProducer userAssetTrendsProducer;

    @Autowired
    private UserDataProducer userDataProducer;

    @Autowired
    private BillAmountProducer billAmountProducer;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    @Autowired
    private WsProducer wsProducer;

    @Autowired
    private RedisUtilX redisUtilX;


    //查询类目
    @ReadOnly
    public List<ShopCateVO> selectCateList() throws Exception{
        return shopCateMapper.select();
    }

    //查询商品
    @ReadOnly
    public List<ShopVO> select(ShopParam param) throws Exception{
        return shopMapper.select(param);
    }

    @ReadOnly
    public ShopAllVO selectAll() throws Exception{
        ShopAllVO vo = new ShopAllVO();
        String key = RedisKey.shop_all;
        if(redisUtilX.hasKey(key)){
            LogUtil.log("商品有缓存 拿缓存");
            vo = redisUtilX.getObj(key, ShopAllVO.class);
            return vo;
        }
        List<ShopCateVO> cateList = selectCateList();
        List<ShopVO> shopList = shopMapper.selectAll();
        vo.setCateList(null != cateList ? cateList : new ArrayList<>());
        vo.setShopList(null != shopList ? shopList : new ArrayList<>());
        redisUtilX.setObj(key, vo,3600);
        return vo;
    }



    //查询推荐商品
    @ReadOnly
    public List<ShopVO> selectRecommend() throws Exception{
        return shopMapper.selectRecommend();
    }

    //商品详情
    @ReadOnly
    public ShopDetailVO detail(Long id) throws Exception{
        if(null == id){
            return null;
        }
        ShopDetailVO vo = null;
        String key = RedisKey.shop_detail_by_id_ + id;
        if(redisUtilX.hasKey(key)){
            vo = redisUtilX.getObj(key, ShopDetailVO.class);
            return vo;
        }
        vo = shopMapper.findDetailById(id);
        if(null != vo){
            redisUtilX.setObj(key, vo,3600);
        }
        return vo;
    }


    public ShopOrderConfirmVO findShopConfirm(Long id) throws Exception{
        if(null == id){
            return null;
        }
        Long userId = userUtilX.getUserId();
        ShopVO shopVO = shopMapper.findVO(id);
        if(null == shopVO){
            return null;
        }
        ShopOrderConfirmVO vo = new ShopOrderConfirmVO();
        vo.setShop(shopVO);
        LogUtil.log("userId是：" + userId);
        vo.setUserAddr(userService.findDefaultAddr(userId));
        return vo;
    }


    //商品购买
    public BaseVO buy(ShopBuyParam param) throws Exception{

        if(null == param || null == param.getShopId()){
            return new BaseVO(StatusCodeEnum.PARAM_ERROR);
        }
        if(null == param.getNum() || param.getNum() < 1){
            param.setNum(1);
        }
        Integer num = param.getNum();

        Long userId = userUtilX.getUserId();


        Long time = System.currentTimeMillis();
        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
        userLogMsgDTO.setDeviceId(deviceId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.SHOP_BUY.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("商品id是："+param.getShopId());


        //查商品
        ShopPO shopPO = shopMapper.findById(param.getShopId());
        if(null == shopPO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("商品不存在");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SHOP_NO_EXIST);
        }

        if(!StatusEnum.YES.getCode().equals(shopPO.getStatus())){
            userLogMsgDTO.setLevel(2);
            userLogMsgDTO.setContent("商品已下架");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.SHOP_STATUS_ERROR);
        }

        //查余额
        UserBalancePO balancePO = userBalanceMapper.findByUserId(userId);
        if(null == balancePO){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("用户余额数据是空的");
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.NO_AUTH);
        }

        if(null != shopPO.getPrice() && shopPO.getPrice() > 0){
            if(balancePO.getEnergy() < shopPO.getPrice() * num){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("用户云币不足 商品所需云币是：" + shopPO.getPrice() + "，用户拥有的云币是：" + balancePO.getEnergy());
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.SHOP_ENERGY_NOT_ENOUGH);
            }
        }

        if(null != shopPO.getAmount() && shopPO.getAmount().compareTo(BigDecimal.ZERO) > 0){
            if(balancePO.getAmount().compareTo(shopPO.getAmount().multiply(new BigDecimal(num))) < 0){
                userLogMsgDTO.setLevel(3);
                userLogMsgDTO.setContent("用户余额不足 商品所需的金额是：" + shopPO.getAmount() + "，用户的余额是：" + balancePO.getAmount());
                userLogProducer.produce(userLogMsgDTO);
                return new BaseVO(StatusCodeEnum.SHOP_BALANCE_NOT_ENOUGH);
            }
        }

        Integer sumEnergy = (null == shopPO.getPrice() || shopPO.getPrice() < 1) ? 0 : shopPO.getPrice() * num;
        BigDecimal sumAmount = (null == shopPO.getAmount() || shopPO.getAmount().compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ZERO : shopPO.getAmount().multiply(new BigDecimal(num));

        ShopOrderPO po = new ShopOrderPO();
        po.setOrderId(OrderIdUtil.getShopOrderId(userId));
        po.setShopId(shopPO.getId());
        po.setUserId(userId);
        po.setNum(num);
        po.setPrice(shopPO.getPrice());
        po.setAmount(shopPO.getAmount());
        po.setSumEnergy(sumEnergy);
        po.setSumAmount(sumAmount);
        po.setShopName(shopPO.getName());
        po.setShopImage(shopPO.getImage());
        po.setShopContent(shopPO.getContent());
        po.setShopCateId(shopPO.getCateId());
        po.setShopPrice(shopPO.getPrice());
        po.setShopIsVirtual(shopPO.getIsVirtual());
        po.setSource(ShopSourceEnum.SHOP.getCode());
        po.setLinkOrderId(null);

        //如果不是虚拟物品
        if(!IsEnum.YES.getCode().equals(shopPO.getIsVirtual())){

            if(null == param.getAddrId() || param.getAddrId() < 1){
                BaseException.error(StatusCodeEnum.SHOP_ADDR_PLEASE);
            }

            //查收货地址
            UserAddrPO addrPO = userService.findAddrById(param.getAddrId());
            if(null == addrPO || !userId.equals(addrPO.getUserId())){
                BaseException.error(StatusCodeEnum.SHOP_ADDR_ERROR);
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



        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{

            shopOrderMapper.insertGetId(po);

            if(null != shopPO.getPrice() && shopPO.getPrice() > 0){
                boolean r = userBalanceMapper.decEnergy(userId,sumEnergy);
                if(!r){
                    throw new RuntimeException("商品下单扣减云币值失败");
                }
            }

            if(null != shopPO.getAmount() && shopPO.getAmount().compareTo(BigDecimal.ZERO) > 0){
                boolean r = userBalanceMapper.decAmount(userId,sumAmount);
                if(!r){
                    throw new RuntimeException("商品下单扣减余额失败");
                }
            }
        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("数据库操作失败：" + transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }


        if(po.getSumEnergy() > 0){
            BillEnergyMsgDTO energyMsgDTO = new BillEnergyMsgDTO();
            energyMsgDTO.setUserId(userId);
            energyMsgDTO.setOrderId(po.getId().toString());
            energyMsgDTO.setNum(po.getSumEnergy());
            energyMsgDTO.setTypeEnum(BillEnergyTypeEnum.BUY.getCode());
            energyMsgDTO.setTime(time);
            billEnergyProducer.produce(energyMsgDTO);

            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(userId);
            assetTrendsMsgDTO.setAmount(BigDecimal.valueOf(po.getSumEnergy()));
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.BUY.getCode());
            assetTrendsMsgDTO.setTime(time);
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.ENERGY.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);
        }

        if(po.getSumAmount().compareTo(BigDecimal.ZERO) > 0){
            BillAmountMsgDTO amountMsgDTO = new BillAmountMsgDTO();
            amountMsgDTO.setUserId(userId);
            amountMsgDTO.setOrderId(po.getId().toString());
            amountMsgDTO.setAmount(po.getSumAmount());
            amountMsgDTO.setTypeEnum(BillAmountTypeEnum.BUY.getCode());
            amountMsgDTO.setTime(time);
            billAmountProducer.produce(amountMsgDTO);

            UserAssetTrendsMsgDTO assetTrendsMsgDTO = new UserAssetTrendsMsgDTO();
            assetTrendsMsgDTO.setUserId(userId);
            assetTrendsMsgDTO.setAmount(po.getSumAmount());
            assetTrendsMsgDTO.setTypeEnum(UserAssetTrendsTypeEnum.BUY.getCode());
            assetTrendsMsgDTO.setTime(time);
            assetTrendsMsgDTO.setAssetType(AssetTypeEnum.CASH.getCode());
            userAssetTrendsProducer.produce(assetTrendsMsgDTO);
        }


        UserDataMsgDTO msgDTO = new UserDataMsgDTO();
        msgDTO.setUserId(userId);
        msgDTO.setShopEnergy(po.getSumEnergy());
        msgDTO.setShopAmount(po.getSumAmount());
        userDataProducer.produce(msgDTO);

        userLogMsgDTO.setContent("商城兑换成功");
        userLogProducer.produce(userLogMsgDTO);


        WsSendDTO wsSendDTO2 = new WsSendDTO();
        wsSendDTO2.setUserId(userId);
        wsSendDTO2.setCode(WsCodeEnum.USER_BALANCE.getCode());
        wsProducer.produce(wsSendDTO2);

        return BaseVO.bool(true);
    }


    //我的订单
    @ReadOnly
    public List<ShopOrderVO> myOrder(MyOrderParam param) throws Exception{
        if(null == param){
            BaseException.error(StatusCodeEnum.PARAM_ERROR);
        }
        param.setUserId(userUtilX.getUserId());
        return shopOrderMapper.myOrder(param);
    }


    //确认收货
    public boolean orderConfirm(OrderIdParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getOrderId())){
            BaseException.error(StatusCodeEnum.PARAM_ERROR);
        }

        Long userId = userUtilX.getUserId();
        ShopOrderPO po = shopOrderMapper.findByOrderId(param.getOrderId());

        if(null == po || !po.getUserId().equals(userId)){
            BaseException.error(StatusCodeEnum.SHOP_ORDER_NO_EXIST);
        }

        if(!ShopOrderStatusEnum.WAIT_FINISH.getCode().equals(po.getStatus())){
            BaseException.error(StatusCodeEnum.SHOP_ORDER_STATUS_ERROR);
        }

        return shopOrderMapper.confirm(po.getOrderId(),System.currentTimeMillis());
    }


    //查看订单详情
    @ReadOnly
    public ShopOrderDetailVO orderDetail(OrderIdParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getOrderId())){
            return null;
        }
        ShopOrderPO po = shopOrderMapper.findByOrderId(param.getOrderId());
        if(null == po){
            return null;
        }
        ShopOrderDetailVO vo = DozerUtil.map(po,ShopOrderDetailVO.class);
        if(IsEnum.YES.getCode().equals(vo.getShopIsVirtual())){
            return vo;
        }
        vo.setDeliveryDataItems(kuaidi100UtilX.queryTrack(po.getDeliveryCode(),po.getDeliveryNumber()));
        return vo;
    }



}
