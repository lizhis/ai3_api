package com.ai.basead.handler;

import com.ai.basead.commom.RedisUtilX;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.ad.AdUcDTO;
import com.ai.basecommon.core.param.ad.AdUcParam;
import com.ai.basecommon.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UcHandler {

    @Autowired
    private RedisUtilX redisUtilX;


    public void listen(AdUcParam param) throws Exception{
        //LogUtil.log("超级汇川广告 收到监听事件：" + param.toString());


        String id = null;
        if(0 == param.getOsId()){
            //LogUtil.log("超级汇川广告 收到ios监听事件：" + param);
            if(!StringUtil.isEmpty(param.getIdfa1())){
                id = param.getIdfa1();
            }
        }
        if(1 == param.getOsId()){
            if(!StringUtil.isEmpty(param.getOaid())){
                id = param.getOaid();
            }
        }

        if(null == id){
            //LogUtil.log("没有设备ID 不处理");
            return;
        }

        // idfaSum=,
        // idfa=null,
        // caid=null,
        // imeiSum=,
        // imeiSum1=,
        // oaid=e7a72d8676843f8b200ff99701671c0185a5a0cc1d738aff9472667382ea0304,
        // oaidSum=E2EB5B5166C67F479B8AB51424E4EDDE,
        // oaidSum1=D9DE973061139B68938C545EADA7B79D,
        // androididSum=17AB9366D490EE5AE51F08462E552077,
        // androididSum1=4ec93a4ffdfa97d6789629cbf026c7bd,
        // ip=115.227.224.67,
        // uxTs=1692627991812,
        // callbackUrl=https://huichuan.uc.cn/callback/appapi?click_id=17804147055232650244&convert_id=11506414&event_type=0&dmp_id=dmp_-6355098508615923886&sid=18008963544603741203&uctrackid=czoxODAwODk2MzU0NDYwMzc0MTIwMztjOjEwMTY4NjAyNjtkOmRtcF8tNjM1NTA5ODUwODYxNTkyMzg4NjtwOmhj&act_type=&uid=210908896,
        // acid=210908896,
        // gid=121854585,
        // aid=1428825215,
        // cid=101686026,
        // osId=1,
        // model1=V1955A
        // )




        AdUcDTO dto = new AdUcDTO();
        dto.setIdfa1(param.getIdfa1());
        dto.setIdfaSum(param.getIdfaSum());
        dto.setCaid(param.getCaid());
        dto.setImeiSum(param.getImeiSum());
        dto.setImeiSum1(param.getImeiSum1());
        dto.setOaid(param.getOaid());
        dto.setOaidSum(param.getOaidSum());
        dto.setOaidSum1(param.getOaidSum1());
        dto.setAndroididSum(param.getAndroididSum());
        dto.setAndroididSum1(param.getAndroididSum1());
        dto.setIp(param.getIp());
        dto.setUxTs(param.getUxTs());
        dto.setCallbackUrl(param.getCallbackUrl());
        dto.setAcid(param.getAcid());
        dto.setGid(param.getGid());
        dto.setAid(param.getAid());
        dto.setCid(param.getCid());
        dto.setOsId(param.getOsId());
        dto.setModel1(param.getModel1());


        String key = RedisKey.ad_uc_id_ + id;
        redisUtilX.setObj(key,dto,3600*48);

    }






}
