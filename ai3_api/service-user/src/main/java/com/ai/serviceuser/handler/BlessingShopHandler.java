package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.dto.msg.*;
import com.ai.basecommon.core.param.OrderIdParam;
import com.ai.basecommon.core.param.blessingshop.BlessingShopBuyParam;
import com.ai.basecommon.core.param.blessingshop.BlessingShopParam;
import com.ai.basecommon.core.po.user.BlessingShopOrderPO;
import com.ai.basecommon.core.po.user.BlessingShopPO;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.BlessingShopCateVO;
import com.ai.basecommon.core.vo.user.BlessingShopOrderDetailVO;
import com.ai.basecommon.core.vo.user.BlessingShopOrderVO;
import com.ai.basecommon.core.vo.user.BlessingShopVO;
import com.ai.basecommon.enums.*;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.common.IpUtilX;
import com.ai.serviceuser.common.Kuaidi100UtilX;
import com.ai.serviceuser.common.TransactionUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.UserLogProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BlessingShopHandler {

    @Autowired
    private BlessingShopMapper blessingShopMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private TransactionUtilX transactionUtilX;

    @Autowired
    private Kuaidi100UtilX kuaidi100UtilX;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private UserBlessingMapper userBlessingMapper;

    @Autowired
    private UserAddrMapper userAddrMapper;

    @Autowired
    private BlessingShopOrderMapper blessingShopOrderMapper;

    @Autowired
    private BlessingShopCateMapper blessingShopCateMapper;

    //查询类目
    @ReadOnly
    public List<BlessingShopCateVO> selectCateList() throws Exception{
        return blessingShopCateMapper.select();
    }


    //查询商品
    @ReadOnly
    public List<BlessingShopVO> select(BlessingShopParam param) throws Exception{
        return blessingShopMapper.select(param);
    }

    //商品详情
    @ReadOnly
    public BlessingShopVO detail(Long id) throws Exception{
        if(null == id){
            return null;
        }
        return blessingShopMapper.detail(id);
    }

    //商品购买
    public BaseVO buy(BlessingShopBuyParam param) throws Exception{

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
        userLogMsgDTO.setUserId(userId);
        userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
        userLogMsgDTO.setAction(UserLogActionEnum.BLESSING_SHOP_BUY.getCode());
        userLogMsgDTO.setLevel(1);
        userLogMsgDTO.setIp(ip);
        userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
        userLogMsgDTO.setCreateTime(time);
        userLogMsgDTO.setUpdateTime(time);
        userLogMsgDTO.setRemark("福卡商品id是："+param.getShopId());


        //查商品
        BlessingShopPO shopPO = blessingShopMapper.findById(param.getShopId());
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

        //查我的卡
        List<Long> cardIdList = userBlessingMapper.selectIdsInviteCard(userId);
        if(null == cardIdList){
            cardIdList = new ArrayList<>();
        }
        int myAmount = cardIdList.size();
        int sumAmount = shopPO.getPrice() * num;

        if(myAmount < sumAmount){
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("用户福卡不足 商品所需邀请卡数量是：" + sumAmount + "，用户拥有的数量是：" + myAmount);
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.BLESSING_SHOP_PRICE_NOT_ENOUGH);
        }

        BlessingShopOrderPO po = new BlessingShopOrderPO();
        po.setOrderId(OrderIdUtil.getShopOrderId(userId));
        po.setBlessingShopId(shopPO.getId());
        po.setUserId(userId);
        po.setNum(num);
        po.setPrice(shopPO.getPrice());
        po.setSum(sumAmount);
        po.setShopName(shopPO.getName());
        po.setShopImage(shopPO.getImage());
        po.setShopContent(shopPO.getContent());
        po.setShopCateId(shopPO.getCateId());
        po.setShopPrice(shopPO.getPrice());

        if(null == param.getAddrId() || param.getAddrId() < 1){
            BaseException.error(StatusCodeEnum.SHOP_ADDR_PLEASE);
        }

        //查收货地址
        UserAddrPO addrPO = userAddrMapper.findById(param.getAddrId());
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

        po.setCreateTime(time);
        po.setUpdateTime(time);

        cardIdList.subList(sumAmount, cardIdList.size()).clear();
        List<Long> inviteCardList = cardIdList;

        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{

            blessingShopOrderMapper.insertGetId(po);

            boolean r = userBlessingMapper.decInviteCard(inviteCardList);
            if(!r){
                throw new RuntimeException("商品下单扣减福卡失败");
            }
        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            userLogMsgDTO.setLevel(3);
            userLogMsgDTO.setContent("数据库操作失败：" + transactionResultDTO.getMessage());
            userLogProducer.produce(userLogMsgDTO);
            return BaseVO.error();
        }

        userLogMsgDTO.setContent("福卡商城兑换成功");
        userLogProducer.produce(userLogMsgDTO);

        return BaseVO.bool(true);
    }


    //我的订单
    @ReadOnly
    public List<BlessingShopOrderVO> myOrder() throws Exception{
        Long userId = userUtilX.getUserId();
        return blessingShopOrderMapper.myOrder(userId);
    }


    //确认收货
    public boolean orderConfirm(OrderIdParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getOrderId())){
            BaseException.error(StatusCodeEnum.PARAM_ERROR);
        }

        Long userId = userUtilX.getUserId();
        BlessingShopOrderPO po = blessingShopOrderMapper.findByOrderId(param.getOrderId());

        if(null == po || !po.getUserId().equals(userId)){
            BaseException.error(StatusCodeEnum.SHOP_ORDER_NO_EXIST);
        }

        if(!ShopOrderStatusEnum.WAIT_FINISH.getCode().equals(po.getStatus())){
            BaseException.error(StatusCodeEnum.SHOP_ORDER_STATUS_ERROR);
        }

        return blessingShopOrderMapper.confirm(po.getOrderId(),System.currentTimeMillis());
    }


    //查看订单详情
    @ReadOnly
    public BlessingShopOrderDetailVO orderDetail(OrderIdParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getOrderId())){
            return null;
        }
        BlessingShopOrderPO po = blessingShopOrderMapper.findByOrderId(param.getOrderId());
        if(null == po){
            return null;
        }
        BlessingShopOrderDetailVO vo = DozerUtil.map(po,BlessingShopOrderDetailVO.class);
        vo.setDeliveryDataItems(kuaidi100UtilX.queryTrack(po.getDeliveryCode(),po.getDeliveryNumber()));
        return vo;
    }



}
