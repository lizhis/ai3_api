package com.ai.serviceuser.common;

import com.alibaba.fastjson2.JSONObject;
import com.ai.serviceuser.mapper.SysConfApiMapper;
import com.google.common.collect.Maps;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class BankCardUtilX {


    @Autowired
    private SysConfApiMapper sysConfApiMapper;

    @Autowired
    private RedisUtilX redisUtilX;



    final private static Map<String, String> statusMap = Maps.newHashMap();

    static {
        statusMap.put("02", "身份信息与银行卡信息不符");
        statusMap.put("03", "银行卡未开通认证支付");
        statusMap.put("04", "此卡被没收！");
        statusMap.put("05", "银行卡无效");
        statusMap.put("06", "此卡无对应发卡行");
        statusMap.put("07", "该卡未初始化或睡眠卡");
        statusMap.put("08", "此卡为作弊卡、吞卡");
        statusMap.put("09", "此卡已挂失");
        statusMap.put("10", "此卡已过期");
        statusMap.put("11", "此卡为受限制的卡");
        statusMap.put("12", "密码错误次数超限");
        statusMap.put("13", "发卡行不支持此交易");
        statusMap.put("14", "卡状态不正常");
        statusMap.put("16", "输入的密 码、有效期或 CVN2 有误");
        statusMap.put("202", "银行卡暂不支持该业务");
        statusMap.put("203", "此样本姓名身份证号银行卡号认证次数超限");
        statusMap.put("204", "姓名不标准");
        statusMap.put("205", "身份证号不标准");
        statusMap.put("206", "银行卡不标准");
        statusMap.put("208", "交易失败或银行拒绝交易，请联系发卡行");
        statusMap.put("209", "验证要素格式有误");
        statusMap.put("211", "输入参数不正确");
        statusMap.put("3008", "请重新签约或更换其它银行卡签约");
        statusMap.put("3999", "其他无法验证");
        statusMap.put("9999", "服务异常");
    }


    // https://market.aliyun.com/products/57000002/cmapi028807.html?spm=5176.product-detail.sidebar.1.10275bceN4BFVH&scm=20140722.C_cmapi028807.P_146.MO_732-ST_4769-V_1-ID_cmapi028807-OR_rec#sku=yuncode2280700001

    public boolean verify(String realName, String cardNo, String idCard,String mobile) throws Exception{
        if(StringUtil.isEmpty(realName) || StringUtil.isEmpty(cardNo) || StringUtil.isEmpty(idCard) || StringUtil.isEmpty(mobile)){
            LogUtil.log("银行卡四要素参数为空");
            return false;
        }

        SysConfApiPO confApiPO = this.getConf();
        if(null == confApiPO || StringUtil.isEmpty(confApiPO.getBankCard())){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }

        String host = "https://bcard3and4.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String path = "/bankCheck4New";// 【2】后缀
        String urlSend = host + path + "?idCard=" + idCard + "&name="+ URLEncoder.encode(realName, String.valueOf(StandardCharsets.UTF_8)) + "&accountNo="+ cardNo + "&mobile="+ mobile;;  // 【5】拼接请求链接
        URL url = new URL(urlSend);
        HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
        httpURLCon.setRequestProperty("Authorization", "APPCODE " + confApiPO.getBankCard());// 格式Authorization:APPCODE (中间是英文空格)
        int httpCode = httpURLCon.getResponseCode();
        if (httpCode == 200) {
            String json = this.read(httpURLCon.getInputStream());
            LogUtil.log("银行卡认证接口返回：" + json);
            JSONObject jsonObject = JSONObject.parseObject(json);
            String status = jsonObject.getString("status");
            if("01".equals(status)){
                return true;
            }
            String desc = statusMap.getOrDefault(status, "银行卡认证失败");
            BaseException.error(desc);
        } else {
            Map<String, List<String>> map = httpURLCon.getHeaderFields();
            List<String> mapLi = map.get("X-Ca-Error-Message");
            String errMsg;
            if(null != mapLi){
                String error = mapLi.get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    errMsg = "AppCode错误 ";
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    errMsg = "请求的 Method、Path 或者环境错误";
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    errMsg = "参数错误";
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    errMsg = "服务未被授权（或URL和Path不正确）";
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    errMsg = "套餐包次数用完 ";
                } else {
                    errMsg = "参数名错误 或 其他错误";
                }
            }
            else{
                errMsg = "X-Ca-Error-Message 为空";
            }
            LogUtil.log("银行卡认证接口调用失败：" + errMsg);
        }

        return false;
    }



    /*
     * 读取返回结果
     */
    private String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }


    private SysConfApiPO getConf() throws Exception {
        SysConfApiPO confPO = redisUtilX.getObj(RedisKey.conf_api,SysConfApiPO.class);
        if(null != confPO){
            return confPO;
        }
        confPO = sysConfApiMapper.find();
        if(null == confPO){
            BaseException.error(StatusCodeEnum.CONFIG_ERROR);
        }
        redisUtilX.setObj(RedisKey.conf_api,confPO,600);
        return confPO;
    }



}
