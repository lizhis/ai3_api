package com.ai.servicebase.controller;

import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.core.param.PlatformTypeParam;
import com.ai.basecommon.core.po.base.SysConfigPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.AppVersionVO;
import com.ai.basecommon.core.vo.base.HomeVO;
import com.ai.basecommon.core.vo.base.SysConfigVO;
import com.ai.basecommon.enums.UserLogActionEnum;
import com.ai.basecommon.enums.UserLogSourceEnum;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.servicebase.commom.BlacklistUtilX;
import com.ai.servicebase.commom.IpUtilX;
import com.ai.servicebase.commom.SignatureUtilX;
import com.ai.servicebase.commom.UserUtilX;
import com.ai.servicebase.handler.*;
import com.ai.servicebase.producer.UserLogProducer;
import com.ai.servicebase.service.IEnergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "综合服务")
@RequestMapping("/base")
public class BaseController {

    @Autowired
    private BaseHandler baseHandler;

    @Autowired
    private BannerHandler bannerHandler;

    @Autowired
    private ActivityHandler activityHandler;

    @Autowired
    private NewbieChannelHandler newbieChannelHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Autowired
    private BlacklistUtilX blacklistUtilX;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private UserLogProducer userLogProducer;


    @Operation(summary = "获取最新app版本号",description = "")
    @GetMapping("/appVersion")
    public BaseVO appVersion(@ModelAttribute PlatformTypeParam param) throws Exception{
        AppVersionVO result = baseHandler.appVersion(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "首页数据",description = "")
    @GetMapping("/home")
    public BaseVO home(@ModelAttribute PlatformTypeParam param) throws Exception{

        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }

        String deviceId = userUtilX.getDvi();
        String ip = ipUtilX.getIp();
        boolean verify = blacklistUtilX.verify(null,deviceId,ip);
        if(!verify){
            Long time = System.currentTimeMillis();
            UserLogMsgDTO userLogMsgDTO = new UserLogMsgDTO();
            userLogMsgDTO.setDeviceId(deviceId);
            userLogMsgDTO.setSource(UserLogSourceEnum.ACTION.getCode());
            userLogMsgDTO.setAction(UserLogActionEnum.BLACKLIST.getCode());
            userLogMsgDTO.setLevel(1);
            userLogMsgDTO.setIp(ip);
            userLogMsgDTO.setYmd(Integer.parseInt(DateUtil.timestampToDate(time,"yyyyMMdd")));
            userLogMsgDTO.setCreateTime(time);
            userLogMsgDTO.setUpdateTime(time);
            userLogMsgDTO.setRemark("进入app，设备号：" + deviceId + "，IP：" + ip);
            userLogProducer.produce(userLogMsgDTO);
            return new BaseVO(StatusCodeEnum.BLACKLIST);
        }


        HomeVO vo = new HomeVO();
        vo.setBannerList(bannerHandler.select());
        vo.setActivityList(activityHandler.select());
        vo.setNewbieChannelList(newbieChannelHandler.select());

        SysConfigPO configPO = baseHandler.loadConf();
        SysConfigVO sysConfigVO = new SysConfigVO();
        if(null != configPO){
            sysConfigVO.setCustomerServiceUrl(configPO.getCustomerServiceUrl());
            sysConfigVO.setAnnouncement(configPO.getAnnouncement());
            sysConfigVO.setRechargeMin(configPO.getRechargeMin());
        }
        vo.setSysConfigVO(sysConfigVO);

        AppVersionVO versionVO = baseHandler.appVersion(param);
        vo.setAppVersionVO(versionVO);


        return BaseVO.ok(vo);
    }





}
