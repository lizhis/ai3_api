package com.ai.serviceuser.common;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.map.IpDTO;
import com.ai.basecommon.core.dto.sms.SmsResponseDTO;
import com.ai.basecommon.core.dto.sms.SmsSendDTO;
import com.ai.basecommon.core.dto.sms.VerifyCodeDTO;
import com.ai.basecommon.core.po.base.SysConfGiveGiftCodePO;
import com.ai.basecommon.core.po.base.SysConfSmsPO;
import com.ai.basecommon.core.po.user.SmsRecordPO;
import com.ai.basecommon.enums.StatusEnum;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.mapper.SmsRecordMapper;
import com.ai.serviceuser.mapper.SysConfGiveGiftCodeMapper;
import com.ai.serviceuser.mapper.SysConfSmsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description
 * @Author
 */
@Component
public class SmsUtilX {

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private SmsRecordMapper smsRecordMapper;

    @Autowired
    private SysConfSmsMapper sysConfSmsMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private SysConfGiveGiftCodeMapper sysConfGiveGiftCodeMapper;


    //发送短信
    public boolean sendCodeOld(SmsSendDTO sendDTO) throws Exception {
        if(null == sendDTO){
            return false;
        }
        if(StringUtil.isEmpty(sendDTO.getTel()) || StringUtil.isEmpty(sendDTO.getDeviceId()) || null == sendDTO.getTypeEnum()){
            LogUtil.log("发送短信参数错误：" + sendDTO);
            return false;
        }

        SysConfSmsPO confSmsPO = this.getConf();

        Integer ymd = DateUtil.todayDate();

        int c = smsRecordMapper.countByDeviceIdToday(sendDTO.getDeviceId(), ymd);
        if(c > 20){
            LogUtil.log("拒绝发送短信 此设备号今日发送量已超过20条：" + sendDTO);
            return false;
        }

        String tel = sendDTO.getTel();

        Integer code = this.generateCode();

        String tpl = confSmsPO.getTpl();
        //String tpl = "【e寻桩】您的验证码为{code}，在10分钟内有效。";

        String httpUrl = "http://api.smsbao.com/sms";


        String content = tpl.replace("{time}","10");
        content = content.replace("{code}",code.toString());


        SmsResponseDTO response = new SmsResponseDTO();


        String testUsername = confSmsPO.getUsername(); //在短信宝注册的用户名
        String testPassword = confSmsPO.getPassword(); //在短信宝注册的密码
        String testPhone = tel;
        String testContent = content;

        StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(testUsername).append("&");
        httpArg.append("p=").append(md5(testPassword)).append("&");
        httpArg.append("m=").append(testPhone).append("&");
        httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));

        String result = request(httpUrl, httpArg.toString());

        if("0".equals(result)){
            response.setSendStatus(StatusEnum.YES.getCode());
        }
        else{
            response.setSendStatus(StatusEnum.NO.getCode());
            if(null == result){
                response.setErrorMsg("短信宝接口 返回空");
            }
            else if("30".equals(result)){
                response.setErrorMsg("短信宝接口 错误密码");
            }
            else if("40".equals(result)){
                response.setErrorMsg("短信宝接口 账号不存在");
            }
            else if("41".equals(result)){
                response.setErrorMsg("短信宝接口 余额不足");
            }
            else if("43".equals(result)){
                response.setErrorMsg("短信宝接口 IP地址限制");
            }
            else if("50".equals(result)){
                response.setErrorMsg("短信宝接口 内容含有敏感词");
            }
            else if("51".equals(result)){
                response.setErrorMsg("短信宝接口 手机号码不正确");
            }
            else{
                response.setErrorMsg("短信宝接口返回未知状态");
            }
        }

        //LogUtil.log("发送短信结果：" + result);

        Long time = System.currentTimeMillis();

        //IP
        IpDTO ipDTO = ipUtilX.getIpInfo();

        SmsRecordPO smsRecordPO = new SmsRecordPO();
        smsRecordPO.setTel(tel);
        smsRecordPO.setCode(code);
        smsRecordPO.setYzmType(sendDTO.getTypeEnum().getCode());
        smsRecordPO.setContent(content);
        smsRecordPO.setErrorMsg(response.getErrorMsg());
        smsRecordPO.setDeviceId(sendDTO.getDeviceId());
        smsRecordPO.setIp(ipDTO.getIp());
        smsRecordPO.setIpAddr(ipDTO.getAddressDetail());
        smsRecordPO.setSendStatus(response.getSendStatus());
        if(StatusEnum.YES.getCode().equals(response.getSendStatus())){
            smsRecordPO.setStatus(StatusEnum.YES.getCode());
            smsRecordPO.setExpireTime(System.currentTimeMillis() + (600 * 1000));
        }else{
            smsRecordPO.setStatus(StatusEnum.NO.getCode());
            smsRecordPO.setExpireTime(0L);
        }
        smsRecordPO.setYmd(ymd);
        smsRecordPO.setCreateTime(time);
        smsRecordPO.setUpdateTime(time);
        smsRecordMapper.insert(smsRecordPO);
        return response.getSendStatus().equals(StatusEnum.YES.getCode());
    }



    //发送短信
    public boolean sendCode(SmsSendDTO sendDTO) throws Exception {
        if(null == sendDTO){
            return false;
        }
        if(StringUtil.isEmpty(sendDTO.getTel()) || StringUtil.isEmpty(sendDTO.getDeviceId()) || null == sendDTO.getTypeEnum()){
            LogUtil.log("发送短信参数错误：" + sendDTO);
            return false;
        }

        SysConfSmsPO confSmsPO = this.getConf();
        if(StringUtil.isEmpty(confSmsPO.getAliyunSignName()) || StringUtil.isEmpty(confSmsPO.getAliyunAccessKey()) || StringUtil.isEmpty(confSmsPO.getAliyunAccessSecret()) || StringUtil.isEmpty(confSmsPO.getAliyunTplTemplateCode()) || StringUtil.isEmpty(confSmsPO.getAliyunTplParam()) || StringUtil.isEmpty(confSmsPO.getAliyunTplContent())){
            LogUtil.log("阿里云短信配置不全：" + confSmsPO);
            return false;
        }

        Integer ymd = DateUtil.todayDate();

        int c = smsRecordMapper.countByDeviceIdToday(sendDTO.getDeviceId(), ymd);
        if(c > 20){
            LogUtil.log("拒绝发送短信 此设备号今日发送量已超过20条：" + sendDTO);
            return false;
        }

        String tel = sendDTO.getTel();

        Integer code = this.generateCode();


        String param = "{\""+confSmsPO.getAliyunTplParam()+"\":\""+code+"\"}";


        SmsResponseDTO response = new SmsResponseDTO();

        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //String content = tpl.replace("{time}","10");
        String content = confSmsPO.getAliyunTplContent().replace("${"+confSmsPO.getAliyunTplParam()+"}",code.toString());

        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", confSmsPO.getAliyunAccessKey(), confSmsPO.getAliyunAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-shanghai");
        request.putQueryParameter("PhoneNumbers", tel);
        request.putQueryParameter("SignName", confSmsPO.getAliyunSignName());
        request.putQueryParameter("TemplateCode", confSmsPO.getAliyunTplTemplateCode());
        request.putQueryParameter("TemplateParam", param);
        try {
            CommonResponse data = client.getCommonResponse(request);
            //LogUtil.log("短信发送成功：" + data.getData());
            response.setSendStatus(StatusEnum.YES.getCode());
        } catch (Exception e) {
            //response.setErrorMsg(e.getMessage());
            response.setSendStatus(StatusEnum.NO.getCode());
            LogUtil.log("短信发送失败：" + e.getMessage());
        }

        Long time = System.currentTimeMillis();

        //IP
        IpDTO ipDTO = ipUtilX.getIpInfo();

        SmsRecordPO smsRecordPO = new SmsRecordPO();
        smsRecordPO.setTel(tel);
        smsRecordPO.setCode(code);
        smsRecordPO.setYzmType(sendDTO.getTypeEnum().getCode());
        smsRecordPO.setContent(content);
        smsRecordPO.setErrorMsg(response.getErrorMsg());
        smsRecordPO.setDeviceId(sendDTO.getDeviceId());
        smsRecordPO.setIp(ipDTO.getIp());
        smsRecordPO.setIpAddr(ipDTO.getAddressDetail());
        smsRecordPO.setSendStatus(response.getSendStatus());
        if(StatusEnum.YES.getCode().equals(response.getSendStatus())){
            smsRecordPO.setStatus(StatusEnum.YES.getCode());
            smsRecordPO.setExpireTime(System.currentTimeMillis() + (600 * 1000));
        }else{
            smsRecordPO.setStatus(StatusEnum.NO.getCode());
            smsRecordPO.setExpireTime(0L);
        }
        smsRecordPO.setYmd(ymd);
        smsRecordPO.setCreateTime(time);
        smsRecordPO.setUpdateTime(time);
        smsRecordMapper.insert(smsRecordPO);
        return response.getSendStatus().equals(StatusEnum.YES.getCode());
    }





    //验证码校验
    public boolean verifyCode(VerifyCodeDTO verifyCodeDTO) throws Exception{
        if(null == verifyCodeDTO || StringUtil.isEmpty(verifyCodeDTO.getTel()) || null == verifyCodeDTO.getCode() || null == verifyCodeDTO.getTypeEnum()){
            LogUtil.log("验证码校验参数错误");
            return false;
        }

        String tel = verifyCodeDTO.getTel();
        Integer code = verifyCodeDTO.getCode();
        Integer yzmType = verifyCodeDTO.getTypeEnum().getCode();

        SmsRecordPO recordPO = smsRecordMapper.findEndByTelAndYzmType(tel,yzmType);
        if(null == recordPO){
            BaseException.error(StatusCodeEnum.CAPTCHA_ERROR);
        }

        if(!code.equals(recordPO.getCode())){
            BaseException.error(StatusCodeEnum.CAPTCHA_ERROR);
        }

        Long time = System.currentTimeMillis();
        if(time.compareTo(recordPO.getExpireTime()) > 0){
            BaseException.error(StatusCodeEnum.CAPTCHA_EXPIRE);
        }

        smsRecordMapper.updateYzmToUse(recordPO.getId(),time);
        return true;
    }


    //发送福利码通知短信
    public boolean sendGiftCodeSmsBao(String tel,String code) throws Exception {
        if(StringUtil.isEmpty(tel)){
            LogUtil.log("发送短信参数错误：" + tel);
            return false;
        }

        SysConfSmsPO confSmsPO = this.getConf();

        SysConfGiveGiftCodePO sysConfGiveGiftCodePO = sysConfGiveGiftCodeMapper.find();
        if(null == sysConfGiveGiftCodePO){
            LogUtil.log("福利码短信配置错误，发送短信失败");
            return false;
        }
        if(StringUtil.isEmpty(sysConfGiveGiftCodePO.getInviteTpl())){
            LogUtil.log("福利码短信配置错误，模板为空，发送短信失败");
            return false;
        }

        String tpl = sysConfGiveGiftCodePO.getInviteTpl();

        String httpUrl = "http://api.smsbao.com/sms";


        String content = tpl.replace("{time}","10");
        content = content.replace("{code}",code.toString());


        SmsResponseDTO response = new SmsResponseDTO();


        String testUsername = confSmsPO.getUsername(); //在短信宝注册的用户名
        String testPassword = confSmsPO.getPassword(); //在短信宝注册的密码
        String testPhone = tel;
        String testContent = content;

        StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(testUsername).append("&");
        httpArg.append("p=").append(md5(testPassword)).append("&");
        httpArg.append("m=").append(testPhone).append("&");
        httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));

        String result = request(httpUrl, httpArg.toString());

        if("0".equals(result)){
            response.setSendStatus(StatusEnum.YES.getCode());
        }
        else{
            response.setSendStatus(StatusEnum.NO.getCode());
            if(null == result){
                response.setErrorMsg("短信宝接口 返回空");
            }
            else if("30".equals(result)){
                response.setErrorMsg("短信宝接口 错误密码");
            }
            else if("40".equals(result)){
                response.setErrorMsg("短信宝接口 账号不存在");
            }
            else if("41".equals(result)){
                response.setErrorMsg("短信宝接口 余额不足");
            }
            else if("43".equals(result)){
                response.setErrorMsg("短信宝接口 IP地址限制");
            }
            else if("50".equals(result)){
                response.setErrorMsg("短信宝接口 内容含有敏感词");
            }
            else if("51".equals(result)){
                response.setErrorMsg("短信宝接口 手机号码不正确");
            }
            else{
                response.setErrorMsg("短信宝接口返回未知状态");
            }
        }

        LogUtil.log("发送福利码短信结果：" + response);

        return response.getSendStatus().equals(StatusEnum.YES.getCode());
    }

    //发送福利码通知短信
    public boolean sendGiftCode(String tel,String code) throws Exception {
        if(StringUtil.isEmpty(tel)){
            LogUtil.log("发送短信参数错误：" + tel);
            return false;
        }

        SysConfSmsPO confSmsPO = this.getConf();
        if(null == confSmsPO || StringUtil.isEmpty(confSmsPO.getGiftTplTemplateCode()) || StringUtil.isEmpty(confSmsPO.getGiftTplParam()) || StringUtil.isEmpty(confSmsPO.getGiftTplContent())){
            LogUtil.log("福利码短信配置错误，发送短信失败：" + confSmsPO);
            return false;
        }

/*        SysConfGiveGiftCodePO sysConfGiveGiftCodePO = sysConfGiveGiftCodeMapper.find();
        if(null == sysConfGiveGiftCodePO){
            LogUtil.log("福利码短信配置错误，发送短信失败");
            return false;
        }
        if(StringUtil.isEmpty(sysConfGiveGiftCodePO.getInviteTpl())){
            LogUtil.log("福利码短信配置错误，模板为空，发送短信失败");
            return false;
        }*/

        String param = "{\""+confSmsPO.getGiftTplParam()+"\":\""+code+"\"}";

        SmsResponseDTO response = new SmsResponseDTO();

        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //String content = tpl.replace("{time}","10");
        String content = confSmsPO.getGiftTplContent().replace("{"+confSmsPO.getGiftTplParam()+"}",code.toString());

        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", confSmsPO.getAliyunAccessKey(), confSmsPO.getAliyunAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-shanghai");
        request.putQueryParameter("PhoneNumbers", tel);
        request.putQueryParameter("SignName", confSmsPO.getAliyunSignName());
        request.putQueryParameter("TemplateCode", confSmsPO.getGiftTplTemplateCode());
        request.putQueryParameter("TemplateParam", param);
        try {
            CommonResponse data = client.getCommonResponse(request);
            //LogUtil.log("短信发送成功：" + data.getData());
            response.setSendStatus(StatusEnum.YES.getCode());
        } catch (Exception e) {
            //response.setErrorMsg(e.getMessage());
            response.setSendStatus(StatusEnum.NO.getCode());
            LogUtil.log("短信发送失败：" + e.getMessage());
        }

        //LogUtil.log("发送福利码短信结果：" + response);

        return StatusEnum.YES.getCode().equals(response.getSendStatus());
    }


    private SysConfSmsPO getConf() throws Exception {
        SysConfSmsPO confPO = redisUtilX.getObj(RedisKey.conf_sms,SysConfSmsPO.class);
        if(null != confPO){
            return confPO;
        }
        confPO = sysConfSmsMapper.find();
        if(null == confPO){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }
        if(StringUtil.isEmpty(confPO.getUsername()) || StringUtil.isEmpty(confPO.getPassword()) || StringUtil.isEmpty(confPO.getTpl())){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }
        redisUtilX.setObj(RedisKey.conf_sms,confPO,600);
        return confPO;
    }



    //生成验证码
    protected Integer generateCode(){
        return (int)((Math.random()*9+1)*100000);
    }

    private static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String md5(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    private static String encodeUrlString(String str, String charset) {
        String strret = null;
        if (str == null)
            return str;
        try {
            strret = java.net.URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }



}
