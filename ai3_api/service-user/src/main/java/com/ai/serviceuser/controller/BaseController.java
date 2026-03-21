package com.ai.serviceuser.controller;


import com.ai.basecommon.core.param.base.DeviceInfoAddParam;
import com.ai.basecommon.core.param.base.PageLogAddParam;
import com.ai.basecommon.core.po.base.SysConfigPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.base.DviResultVO;
import com.ai.basecommon.core.vo.base.SysConfigVO;
import com.ai.basecommon.core.vo.base.SysPopVO;
import com.ai.basecommon.core.vo.user.UserInitDataVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.serviceuser.common.SignatureUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.handler.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "综合")
@RequestMapping("/base")
public class BaseController {


    @Autowired
    private BaseHandler baseHandler;

    @Autowired
    private SuggestHandler suggestHandler;

    @Autowired
    private EntranceHandler entranceHandler;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private BalanceHandler balanceHandler;

    @Autowired
    private StepGoldHandler stepGoldHandler;

    @Autowired
    private SignatureUtilX signatureUtilX;

    @Operation(summary = "获取平台配置",description = "")
    @GetMapping("/sysConfig")
    public BaseVO sysConfig() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        SysConfigVO result = baseHandler.sysConfig();
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询邀请二维码",description = "")
    @GetMapping("/findInviteQr")
    public BaseVO findInviteQr() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        String result = baseHandler.findInviteQr();
        return BaseVO.ok(result);
    }

    @Operation(summary = "设备信息",description = "")
    @PostMapping("/dvi")
    public BaseVO dvi(@RequestBody DeviceInfoAddParam param) throws Exception{
        DviResultVO result = baseHandler.deviceInfo(param);
        return BaseVO.ok(result);
    }


    @Operation(summary = "页面日志",description = "")
    @PostMapping("/page")
    public void pageLog(@RequestBody PageLogAddParam param) throws Exception{
        baseHandler.pageLog(param);
    }

    @Operation(summary = "页面日志2",description = "")
    @PostMapping("/loginOut")
    public BaseVO loginOut() throws Exception{
        baseHandler.pageLog2();
        return BaseVO.ok();
    }


    @Operation(summary = "投诉建议",description = "")
    @PostMapping("/suggest")
    public BaseVO suggest(@RequestHeader("X-Token") @Parameter(name = "X-Token", description = "token值",in = ParameterIn.HEADER,required = false) String token, @RequestParam(name = "files",required = false) MultipartFile[] files,@RequestParam("content") String content) throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        boolean result = suggestHandler.add(content,files);
        return BaseVO.bool(result);
    }

    @Operation(summary = "投诉建议最新回复",description = "")
    @GetMapping("/suggestLastReply")
    public BaseVO lastReply() throws Exception{
        String result = suggestHandler.lastReply();
        return BaseVO.ok(result);
    }


    @Operation(summary = "用户初始化数据",description = "")
    @GetMapping("/userInitData")
    public BaseVO userInitData() throws Exception{
        StatusCodeEnum statusCodeEnum = signatureUtilX.checkSignature();
        if(!StatusCodeEnum.SUCCESS.equals(statusCodeEnum)){
            return BaseVO.error(statusCodeEnum);
        }
        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            return BaseVO.error(StatusCodeEnum.AUTH_ERROR);
        }

        UserInitDataVO vo = new UserInitDataVO();
        vo.setUserInfoVO(entranceHandler.userInfo());
        vo.setUserBalanceVO(balanceHandler.info());
        vo.setMyClockVO(stepGoldHandler.myClock());

        SysConfigPO configPO = baseHandler.loadConf();
        SysPopVO sysPopVO = new SysPopVO();
        sysPopVO.setPopIsOpen(0);
        if(null != configPO){
            if(1 == vo.getUserInfoVO().getNewbieStatus()){
                sysPopVO.setPopIsOpen(configPO.getPopIsOpen());
                sysPopVO.setPopImage(configPO.getPopImage());
                sysPopVO.setPopLinkType(configPO.getPopLinkType());
                sysPopVO.setPopLinkTarget(configPO.getPopLinkTarget());
            }
        }
        vo.setSysPopVO(sysPopVO);
        return BaseVO.ok(vo);
    }


    @Operation(summary = "初始化密钥",description = "")
    @PostMapping("/initSecret")
    public BaseVO initSecret(@RequestHeader("X-Dvi") @Parameter(name = "X-Dvi", description = "设备号",in = ParameterIn.HEADER,required = false) String dvi) throws Exception{
        BaseVO result = baseHandler.initSecret();
        return result;
    }

    @Operation(summary = "初始化密钥2",description = "")
    @PostMapping("/initSecret2")
    public BaseVO initSecret2(@RequestHeader("X-Dvi") @Parameter(name = "X-Dvi", description = "设备号",in = ParameterIn.HEADER,required = false) String dvi) throws Exception{
        BaseVO result = baseHandler.initSecret2();
        return result;
    }





}
