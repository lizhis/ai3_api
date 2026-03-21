package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.map.IpDTO;
import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.param.base.DeviceInfoAddParam;
import com.ai.basecommon.core.param.base.PageLogAddParam;
import com.ai.basecommon.core.po.base.DeviceInfoPO;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.core.po.base.SysConfigPO;
import com.ai.basecommon.core.po.user.DeviceSecretPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.DviResultVO;
import com.ai.basecommon.core.vo.base.SysConfigVO;
import com.ai.basecommon.core.vo.user.InitSecretVO;
import com.ai.basecommon.enums.UserLogActionEnum;
import com.ai.basecommon.enums.UserLogSourceEnum;
import com.ai.basecommon.utils.*;
import com.ai.serviceuser.common.IpUtilX;
import com.ai.serviceuser.common.MapUtilX;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.*;
import com.ai.serviceuser.producer.UserLogProducer;
import com.ai.serviceuser.service.IBaseAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class BaseHandler {

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private MapUtilX mapUtilX;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private IBaseAdService baseAdService;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private SysConfApiMapper sysConfApiMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SysConfNewbieMapper sysConfNewbieMapper;

    @Autowired
    private UserLogProducer userLogProducer;

    @Autowired
    private DeviceSecretMapper deviceSecretMapper;


    //白名单
    private final List<String> appIds = List.of("");


    private final HashMap<String,String> routerMap = new HashMap<String, String>(){{
        put("/", "APP");
        put("/link_h5", "h5页面");
        put("/city_select", "城市选择");
        put("/customer_service", "云币客服");
        put("/announcement", "公告");
        put("/announcement_detail", "公告-详情");
        put("/aboutus", "关于我们");
        put("/aboutus_detail", "关于我们-详情");
        put("/guide", "新手指南");
        put("/life_pay", "生活缴费");
        put("/news_detail", "新闻-详情");
        put("/suggest", "投诉建议");
        put("/register", "注册页面");
        put("/login", "登录页面");
        put("/forget", "找回密码页面");
        put("/setting", "设置");
        put("/msg", "私信");
        put("/msg_detail", "私信-详情");
        put("/set_userinfo", "编辑个人信息");
        put("/user_addr", "收货地址");
        put("/user_addr_add", "收货地址-添加");
        put("/user_addr_edit", "收货地址-编辑");
        put("/vip", "会员VIP");
        put("/account_safe", "账号安全");
        put("/edit_password", "修改密码");
        put("/edit_pay_password", "修改支付密码");
        put("/auth", "实名认证");
        put("/daily_sign_in", "签到");
        put("/car_auth", "车主认证");
        put("/user_asset_trends", "收支明细");
        put("/user_agreement", "用户协议");
        put("/privacy_agreement", "隐私政策");
        put("/app_permissions", "应用权限");
        put("/car_agreement", "车主隐私申明");
        put("/season_agreement", "季卡PLUS协议");
        put("/account_cancel", "账号注销");
        put("/season_card", "季卡会员");
        put("/season_card_gift", "季卡会员礼品");
        put("/my_invite", "邀请好友页面");
        put("/invite_scan", "邀请二维码页面");
        put("/my_order", "我的兑换");
        put("/my_order_detail", "我的兑换-详情");
        put("/my_car", "我的影视");
        put("/my_car_detail", "我的影视-详情");
        put("/my_car_contract", "我的影视合同");
        put("/recharge", "充值页面");
        put("/recharge_bank", "银行汇款页面");
        put("/withdraw", "提现页面");
        put("/bind_card", "绑定银行卡");
        put("/project_info", "影视-详情");
        put("/shop_detail", "商品-详情");
        put("/shop_order_confirm", "商品-确认订单");
        put("/gift", "0元领好礼");
        put("/blessing", "集福");
        put("/blessing_detail", "福卡详情");
        put("/blessing_ranking_week", "集福周榜");
        put("/blessing_ranking_total", "集福总榜");
        put("/clock_in", "步步生金");
        put("/blessing_shop", "福卡商城");
        put("/blessing_shop_detail", "福卡商品-详情");
        put("/blessing_shop_order_confirm", "福卡商品-确认订单");
        put("/blessing_shop_order", "我的福卡商品兑换");
        put("/blessing_shop_order_detail", "我的福卡商品兑换-详情");
    }};


    @ReadOnly
    public SysConfigVO sysConfig() throws Exception{
        SysConfigPO configPO = this.loadConf();
        SysConfigVO vo = new SysConfigVO();
        if(null != configPO){
            vo.setCustomerServiceUrl(configPO.getCustomerServiceUrl());
            vo.setAnnouncement(configPO.getAnnouncement());
            vo.setRechargeMin(configPO.getRechargeMin());
        }
        return vo;
    }

    @ReadOnly
    public String findInviteQr() throws Exception{
        SysConfigPO configPO = this.loadConf();
        return configPO.getInviteQr();
    }


    @ReadOnly
    public SysConfigPO loadConf() {
        String key = RedisKey.sys_config;
        SysConfigPO configPO = redisUtilX.getObj(key,SysConfigPO.class);
        if(null == configPO){
            configPO = sysConfigMapper.find();
            redisUtilX.setObj(key,configPO,600);
        }
        return configPO;
    }

    //设备信息入库
    public DviResultVO deviceInfo(DeviceInfoAddParam param) throws Exception{

        DviResultVO vo = new DviResultVO();

        if(null == param){
            return vo;
        }
        if(StringUtil.isEmpty(param.getDeviceId())){
            return vo;
        }

        int type = CommonUtil.getDevicePlatform(param.getDeviceId());
        String platform = 2 == type ? "ios" : "android";
        //LogUtil.log("进来的设备信息是：" + param);


        Long time = System.currentTimeMillis();
        Integer ymd = DateUtil.todayDate();

        DeviceInfoPO po = new DeviceInfoPO();

        po.setDeviceId(param.getDeviceId());
        po.setPlatform(platform);
        po.setBrand(param.getBrand());
        po.setModel(param.getModel());
        po.setVersion(param.getVersion());
        po.setBrowserName(param.getBrowserName());
        po.setComputerName(param.getComputerName());
        po.setHostName(param.getHostName());
        po.setOaid(param.getOaid());
        po.setAppVersion(userUtilX.getAppVersion());


        if(!"dev".equals(this.profile) && appIds.contains(param.getDeviceId())){
            IpDTO ipDTO = new IpDTO();
            ipDTO.setIp("120.204.63.35");
            ipDTO.setAddress("上海");
            ipDTO.setAddressDetail("上海 上海 浦东 电信");
        }
        else{

            IpDTO ipDTO = ipUtilX.getIpInfo();
            po.setIp(ipDTO.getIp());
            po.setIpAddr(ipDTO.getAddressDetail());
/*
            if(!StringUtil.isEmpty(param.getLat()) && !StringUtil.isEmpty(param.getLng())){
                LocationDTO locationDTO = new LocationDTO();
                locationDTO.setLat(new BigDecimal(param.getLat()));
                locationDTO.setLng(new BigDecimal(param.getLng()));

                po.setLng(locationDTO.getLng());
                po.setLat(locationDTO.getLat());

                try{
                    MapAddressDTO mapAddressDTO = mapUtilX.locationToAddress(locationDTO);
                    if(null != mapAddressDTO){
                        po.setCityCode(mapAddressDTO.getCityCode());
                        po.setAdcode(mapAddressDTO.getAdcode());
                        po.setProvince(mapAddressDTO.getProvince());
                        po.setCity(mapAddressDTO.getCity());
                        po.setDistrict(mapAddressDTO.getDistrict());
                        po.setStreet(mapAddressDTO.getStreet());
                        po.setStreetNumber(mapAddressDTO.getStreetNumber());

                        vo.setProvince(po.getProvince());
                        vo.setCity(po.getCity());
                        vo.setCityCode(po.getCityCode());
                        vo.setDistrict(po.getDistrict());
                        vo.setAdcode(po.getAdcode());
                    }
                }catch (Exception e){
                    LogUtil.log("经纬度转地址出错");
                    LogUtil.log(e.getMessage());
                }
            }
            */

        }

        po.setYmd(ymd);
        po.setCreateTime(time);
        po.setUpdateTime(time);

        String activeId = "";
        if("android".equals(platform)){
            po.setOaid(param.getOaid());
            activeId = po.getOaid();
        }
        if("ios".equals(platform)){
            po.setIdfa(param.getIdfa());
            activeId = po.getIp();
        }

        Long userId = userUtilX.getUserIdNotError();
        po.setUserId(userId);

        try{
            deviceInfoMapper.insert(po);

            if(null != userId){
                userMapper.updateLastEnterTime(userId,time);
            }
        }catch (Exception e){
            //e.printStackTrace();
            LogUtil.log(e.getMessage());
        }

        //redis缓存起来
        //redisUtilX.setObj(po.getDeviceId(),po,3600*24);


        //广告激活
/*
        if(!StringUtil.isEmpty(activeId)){
            String k = "ad_active_id_" + activeId;
            if(redisUtilX.hasKey(k)){
                try{
                    baseAdService.active(activeId);
                    redisUtilX.delete(k);
                }catch (Exception e){
                    LogUtil.log(e.getMessage());
                }
            }
            else{
                String m = EncryptUtil.md5(activeId).toLowerCase();
                //LogUtil.log("校验激活hashID：" + m);
                k = "ad_active_id_" + m;
                if(redisUtilX.hasKey(k)){
                    try{
                        baseAdService.active(m);
                        redisUtilX.delete(k);
                    }catch (Exception e){
                        LogUtil.log(e.getMessage());
                    }
                }
            }
        }

        String adRemainKey = "ad_remain_id_" + param.getDeviceId();
        if(redisUtilX.hasKey(adRemainKey)){
            //LogUtil.log("该设备有留存标记需验证：" + adRemainKey);
            try{
                String s = redisUtilX.get(adRemainKey);
                Long t = Long.parseLong(s);
                if(time > t){
                    //LogUtil.log("已在注册次日 发往ad服务 进行留存验证：" + param.getDeviceId());
                    baseAdService.remain(param.getDeviceId());
                }
            }catch (Exception e){
                LogUtil.log(e.getMessage());
            }

        }
*/




        return vo;
    }

    @ReadOnly
    public SysConfApiPO findSysConfApi() throws Exception{
        return sysConfApiMapper.find();
    }


    //页面日志
    public void pageLog(PageLogAddParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getRoute()) || null == param.getAction()){
            return;
        }

        //LogUtil.log("页面日志请求：" + param);

        String route = param.getRoute();
        Integer action = param.getAction();
        String remark = param.getRemark();

        UserLogActionEnum actionEnum = UserLogActionEnum.getByCode(action);
        if(null == actionEnum){
            return;
        }

        String routeStr = this.routerMap.get(route);
        if(StringUtil.isEmpty(routeStr)){
            return;
        }

        String deviceId = userUtilX.getDvi();
        if(StringUtil.isEmpty(deviceId)){
            return;
        }
        Long userId = userUtilX.getUserIdNotError();
        String ip = ipUtilX.getIp();

        Long time = System.currentTimeMillis();

        UserLogMsgDTO dto = new UserLogMsgDTO();
        dto.setDeviceId(deviceId);
        dto.setUserId(userId);
        dto.setSource(UserLogSourceEnum.PAGE.getCode());
        dto.setAction(action);
        dto.setLevel(1);
        dto.setContent(routeStr);
        dto.setRemark(remark);
        dto.setIp(ip);
        dto.setCreateTime(time);
        dto.setUpdateTime(time);
        if(action.equals(UserLogActionEnum.PHONE_DARK.getCode())){
            String darkMark = "app_dark_" + deviceId;
            redisUtilX.setObj(darkMark,dto,20);
        }
        else{
            userLogProducer.produce(dto);
        }
    }


    //页面日志
    public void pageLog2() throws Exception{

        String deviceId = userUtilX.getDvi();
        Long userId = userUtilX.getUserIdNotError();
        String ip = ipUtilX.getIp();
        if(StringUtil.isEmpty(deviceId)){
            return;
        }
        //LogUtil.log("退出登录：" + userId);

        Long time = System.currentTimeMillis();

        UserLogMsgDTO dto = new UserLogMsgDTO();
        dto.setDeviceId(deviceId);
        dto.setUserId(userId);
        dto.setSource(UserLogSourceEnum.ACTION.getCode());
        dto.setAction(UserLogActionEnum.LOGIN_OUT.getCode());
        dto.setLevel(1);
        dto.setContent(null);
        dto.setRemark(null);
        dto.setIp(ip);
        dto.setCreateTime(time);
        dto.setUpdateTime(time);
        userLogProducer.produce(dto);
    }



    //初始化密钥
    public BaseVO initSecret() throws Exception{
        String dvi = userUtilX.getDvi();
        if(StringUtil.isEmpty(dvi)){
            LogUtil.log("设备号不存在");
            return BaseVO.error();
        }
        int c = deviceSecretMapper.countByDeviceId(dvi);
        if(c > 0){
            boolean r = deviceSecretMapper.deleteByDeviceId(dvi);
        }
        String secret = CommonUtil.getStringRandom(32);
        Long time = System.currentTimeMillis();

        DeviceSecretPO po = new DeviceSecretPO();
        po.setDeviceId(dvi);
        po.setSecret(secret);
        po.setYmd(DateUtil.todayDate());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        deviceSecretMapper.insert(po);
        return BaseVO.ok(secret);
    }

    //初始化密钥
    public BaseVO initSecret2() throws Exception{
        String dvi = userUtilX.getDvi();
        if(StringUtil.isEmpty(dvi)){
            LogUtil.log("初始化密钥 设备号不存在");
            return BaseVO.error();
        }
        int c = deviceSecretMapper.countByDeviceId(dvi);
        if(c > 0){
            deviceSecretMapper.deleteByDeviceId(dvi);
        }
        String secret = CommonUtil.getStringRandom(32);
        Long time = System.currentTimeMillis();

        DeviceSecretPO po = new DeviceSecretPO();
        po.setDeviceId(dvi);
        po.setSecret(secret);
        po.setYmd(DateUtil.todayDate());
        po.setCreateTime(time);
        po.setUpdateTime(time);
        deviceSecretMapper.insert(po);

        Integer platform = CommonUtil.getDevicePlatform(dvi);
        String activeId;
        if(2 == platform){
            activeId = ipUtilX.getIp();
        }
        else{
            activeId = userUtilX.getOaid();
        }
        Integer channel = 0;

        if(!StringUtil.isEmpty(activeId)){
            String checkId = "ad_active_id_" + activeId;
            if(redisUtilX.hasKey(checkId)){
                try{
                    channel = Integer.parseInt(redisUtilX.get(checkId));
                    redisUtilX.delete(checkId);
                }catch (Exception e){}
            }
            else{
                if(1 == platform){
                    String hashCheckId = "ad_active_id_" + EncryptUtil.md5(activeId).toLowerCase();
                    if(redisUtilX.hasKey(hashCheckId)){
                        try{
                            channel = Integer.parseInt(redisUtilX.get(hashCheckId));
                            redisUtilX.delete(hashCheckId);
                        }catch (Exception e){}
                    }
                }
            }

            LogUtil.log("初始化密钥 activeId是："+activeId+"，确定渠道channel是："+channel+"， 设备号是："+dvi + "，platform是："+platform);
        }
        else{
            LogUtil.log("初始化密钥 activeId不存在 不走广告激活 设备号是："+dvi + "，platform是："+platform);
        }

        InitSecretVO initSecretVO = new InitSecretVO();
        initSecretVO.setChannel(channel);
        initSecretVO.setSecret(secret);
        return BaseVO.ok(initSecretVO);
    }



}
