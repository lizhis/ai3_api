package com.ai.servicebase.commom;


import com.ai.basecommon.core.po.base.ClientRejectPO;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.servicebase.mapper.ClientRejectMapper;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.map.IpAddressDTO;
import com.ai.basecommon.core.dto.map.IpDTO;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.servicebase.mapper.SysConfApiMapper;
import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author
 */
@Component
public class IpUtilX {


    @Autowired
    private SysConfApiMapper sysConfApiMapper;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private ClientRejectMapper clientRejectMapper;



    public IpDTO getIpInfo() throws Exception{
        IpDTO ipDTO = new IpDTO();
        ipDTO.setIp(getIp());

        IpAddressDTO ipAddressDTO = getAddress();
        ipDTO.setAddress(ipAddressDTO.getAddress());
        ipDTO.setAddressDetail(ipAddressDTO.getAddressDetail());
        return ipDTO;
    }


    public String getIp() throws Exception{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = getIp(request);
        if(StringUtil.isEmpty(ip)){
            return null;
        }
        this.checkIp(ip);
        return ip;
    }

    private void checkIp(String ip) throws Exception{
        String key = RedisKey.client_reject_list;
        List<ClientRejectPO> list = null;
        try{
            list = redisUtilX.getObjList(key,ClientRejectPO.class);
        }catch (Exception e){
            LogUtil.log(e.getMessage());
        }
        if(null == list || list.isEmpty()){
            list = clientRejectMapper.selectAll();
            if(null != list && !list.isEmpty()){
                redisUtilX.setObj(key,list,600);
            }
        }
        if(null == list || list.isEmpty()){
            return;
        }
        ClientRejectPO po = list.stream().filter(v-> !StringUtil.isEmpty(v.getIp()) && v.getIp().equals(ip)).findFirst().orElse(null);
        if(null != po){
            BaseException.error(StatusCodeEnum.REJECT);
        }
    }


    //获取所在地
    public IpAddressDTO getAddress() throws Exception{

        IpAddressDTO ipAddressDTO = queryDetail();
        if(null == ipAddressDTO.getCountry()){
            return ipAddressDTO;
        }

        if("中国".equals(ipAddressDTO.getCountry())){
            if("北京".equals(ipAddressDTO.getProvince()) || "天津".equals(ipAddressDTO.getProvince()) || "上海".equals(ipAddressDTO.getProvince()) || "重庆".equals(ipAddressDTO.getProvince())){
                ipAddressDTO.setAddress(ipAddressDTO.getProvince());
            }
            else if("香港".equals(ipAddressDTO.getProvince())){
                ipAddressDTO.setAddress(ipAddressDTO.getProvince());
            }
            else{
                ipAddressDTO.setAddress(ipAddressDTO.getProvince() + ipAddressDTO.getCity());
            }
        }
        else{
            String addr = ipAddressDTO.getCountry();
            if(!StringUtil.isEmpty(ipAddressDTO.getProvince())){
                addr += " " + ipAddressDTO.getProvince();
            }
            if(!StringUtil.isEmpty(ipAddressDTO.getCity())){
                addr += " " + ipAddressDTO.getCity();
            }
            ipAddressDTO.setAddress(addr);
        }
        return ipAddressDTO;
    }


    private String getIp(HttpServletRequest request) {
        if(null == request){
            return null;
        }
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");

        //LogUtil.log("Xip是：" + Xip);
        //LogUtil.log("XFor是：" + XFor);

        if (!Strings.isNullOrEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (!Strings.isNullOrEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //LogUtil.log("XFor 1是：" + XFor);
            return XFor;
        }
        if (Strings.nullToEmpty(XFor).trim().isEmpty() || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
            //LogUtil.log("XFor 2是：" + XFor);
        }
        if (Strings.nullToEmpty(XFor).trim().isEmpty() || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
            //LogUtil.log("XFor 3是：" + XFor);
        }
        if (Strings.nullToEmpty(XFor).trim().isEmpty() || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
            //LogUtil.log("XFor 4是：" + XFor);
        }
        if (Strings.nullToEmpty(XFor).trim().isEmpty() || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
            //LogUtil.log("XFor 5是：" + XFor);
        }
        if (Strings.nullToEmpty(XFor).trim().isEmpty() || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
            //LogUtil.log("XFor 6是：" + XFor);
        }
        if("0:0:0:0:0:0:0:1".equals(XFor)){
            XFor = "127.0.0.1";
        }
        return XFor;
    }


    //获取详细
    private IpAddressDTO queryDetail() throws Exception{

        IpAddressDTO ipAddressDTO = new IpAddressDTO();
        String ip = getIp();
        ipAddressDTO.setIp(ip);
        if(ip.equals("127.0.0.1") || ip.equals("localhost") || ip.equals("0:0:0:0:0:0:0:1") || ip.startsWith("192.168")){
            ipAddressDTO.setAddress("本地");
            ipAddressDTO.setAddressDetail("本地");
            return ipAddressDTO;
        }


        SysConfApiPO confApiPO = this.getConf();
        if(null == confApiPO || StringUtil.isEmpty(confApiPO.getIp138())){
            return ipAddressDTO;
        }

        String url = "http://api.ip138.com/ip";
        HashMap<String,String> map = new HashMap<>();
        map.put("token",confApiPO.getIp138());
        map.put("ip",ip);


        try{

            String result = HttpUtil.sendGet(url,map);
            if(null == result){
                LogUtil.log("IP138查询的结果是null，查询的IP是："+ip);
                return ipAddressDTO;
            }

            JSONObject jsonObject = JSONObject.parseObject(result);
            //LogUtil.log("IP返回：" + result);
            String ret = jsonObject.getString("ret");
            if(!"ok".equals(ret)){
                LogUtil.log("ip接口返回错误：" + result);
                return null;
            }

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            //LogUtil.log("国家：" + jsonArray.getString(0));
            //LogUtil.log("省份：" + jsonArray.getString(1));
            //LogUtil.log("城市：" + jsonArray.getString(2));
            //LogUtil.log("区域：" + jsonArray.getString(3));
            //LogUtil.log("运营商：" + jsonArray.getString(4));

            String country = jsonArray.getString(0);
            String province = jsonArray.getString(1);
            String city = jsonArray.getString(2);
            String county = jsonArray.getString(3);
            String isp = jsonArray.getString(4);

            ipAddressDTO.setCountry(country);
            ipAddressDTO.setProvince(province);

            String detail = "";
            if(!StringUtil.isEmpty(country) && !country.equals("中国")){
                detail = country + " ";
            }
            detail += province;
            if(!StringUtil.isEmpty(city)){
                ipAddressDTO.setCity(city);
                detail += " " + city;
            }
            if(!StringUtil.isEmpty(county)){
                ipAddressDTO.setCounty(county);
                detail += " " + county;
            }
            if(!StringUtil.isEmpty(isp)){
                ipAddressDTO.setIsp(isp);
                detail += " " + isp;
            }
            ipAddressDTO.setAddressDetail(detail);

        }catch (Exception e){
            LogUtil.log(e.getMessage());
            return null;
        }
        return ipAddressDTO;
    }




    private SysConfApiPO getConf(){
        SysConfApiPO confPO = redisUtilX.getObj(RedisKey.conf_api,SysConfApiPO.class);
        if(null != confPO){
            return confPO;
        }
        confPO = sysConfApiMapper.find();
        if(null == confPO){
            return null;
        }
        redisUtilX.setObj(RedisKey.conf_api,confPO,600);
        return confPO;
    }




}
