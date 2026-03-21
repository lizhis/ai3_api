package com.ai.servicebase.handler;

import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.ai.basecommon.enums.TaskTypeEnum;
import com.ai.servicebase.async.AiChatAsync;
import com.ai.servicebase.config.db.ReadOnly;
import com.ai.servicebase.mapper.AiChatRecordMapper;
import com.ai.servicebase.mapper.DeviceInfoMapper;
import com.ai.servicebase.mapper.SeasonUserMapper;
import com.ai.servicebase.mapper.UserMapper;
import com.ai.servicebase.producer.UserTaskProducer;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.base.AIChatMessageDTO;
import com.ai.basecommon.core.dto.user.UserInfoDTO;
import com.ai.basecommon.core.param.base.AIChatParam;
import com.ai.basecommon.core.po.base.AiChatRecordPO;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.core.po.base.SysConfigPO;
import com.ai.basecommon.core.po.user.SeasonUserPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.enums.StatusEnum;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.servicebase.commom.ConfigUtilX;
import com.ai.servicebase.commom.IpUtilX;
import com.ai.servicebase.commom.RedisUtilX;
import com.ai.servicebase.commom.UserUtilX;
import com.ai.servicebase.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author
 */
@Component
public class AIChatHandler {

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private IpUtilX ipUtilX;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private ConfigUtilX configUtilX;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SeasonUserMapper seasonUserMapper;

    @Autowired
    private AiChatRecordMapper aiChatRecordMapper;

    @Autowired
    private UserTaskProducer userTaskProducer;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private AiChatAsync aiChatAsync;


    //聊天
    public BaseVO send(AIChatParam param) throws Exception{
        if(null == param || StringUtil.isEmpty(param.getContent())){
            return BaseVO.ok();
        }
        String content = param.getContent().trim();
        if(StringUtil.isEmpty(content)){
            return BaseVO.ok();
        }

        if(content.length() > 250){
            return BaseVO.error(StatusCodeEnum.AI_CHAT_CONTENT_LENGTH);
        }
        ipUtilX.getIp();
        Long userId = userUtilX.getUserIdNotError();
        String dvi = userUtilX.getDvi();
        if(StringUtil.isEmpty(dvi)){
            if("dev".equals(this.profile)){
                dvi = "test";
            }
            else{
                LogUtil.log("AI聊天 请求没有设备号");
                return BaseVO.error(StatusCodeEnum.CAN_NOT_USE);
            }
        }

        Long time = System.currentTimeMillis();


        String devicePassKey = "ai_chat_pass_device_" + dvi;
        if(!redisUtilX.hasKey(devicePassKey)){
            Long lastDeviceTime = deviceInfoMapper.findLastTimeByDeviceId(dvi);
            if(null == lastDeviceTime){
                return BaseVO.error(StatusCodeEnum.CAN_NOT_USE);
            }
            if(time - lastDeviceTime > 86400000L){
                return BaseVO.error(StatusCodeEnum.PLEASE_RESTART_APP);
            }
            redisUtilX.set(devicePassKey,"1",7200);
        }

        Integer aiChatVisitorLimit = 0;
        Integer aiChatUserLimit = 0;
        Integer aiChatVipLimit = 0;
        SysConfigPO configPO = configUtilX.loadConf();
        if(null != configPO){
            aiChatVisitorLimit = configPO.getAiChatVisitorLimit();
            aiChatUserLimit = configPO.getAiChatUserLimit();
            aiChatVipLimit = configPO.getAiChatVipLimit();
        }

        Integer ymd = DateUtil.todayDate();


        String k = "";
        if(null != userId){
            k = userId.toString();
            //查使用次数
            int currentCount = aiChatRecordMapper.countUserYmd(userId,ymd);

            if(currentCount > 5){
                String authKey = "user_auth_ok_" + userId;
                if(!redisUtilX.hasKey(authKey)){
                    Integer authStatus = userMapper.findAuthStatue(userId);
                    if(null == authStatus || 1 != authStatus){
                        return BaseVO.error(StatusCodeEnum.AUTH_PLEASE);
                    }
                    redisUtilX.set(authKey,"1",86400);
                }
            }

            if(this.isSeasonVIP(userId)){
                //是会员
                if(aiChatVipLimit > 0 && currentCount >= aiChatVipLimit){
                    return BaseVO.error(StatusCodeEnum.AI_CHAT_VIP_LIMIT);
                }
            }
            else{
                //普通用户
                if(aiChatUserLimit > 0 && currentCount >= aiChatUserLimit){
                    return BaseVO.error(StatusCodeEnum.AI_CHAT_USER_LIMIT);
                }
            }
        }
        else{
            k = dvi;
            //查使用次数
            int currentCount = aiChatRecordMapper.countDeviceYmd(dvi,ymd);
            if(aiChatVisitorLimit > 0 && currentCount >= aiChatVisitorLimit){
                return BaseVO.error(StatusCodeEnum.AI_CHAT_VISITOR_LIMIT);
            }
        }
        aiChatAsync.chat(dvi,userId,content);
        return BaseVO.ok();
    }


    //获取聊天记录
    @ReadOnly
    public List<AIChatMessageDTO> history() throws Exception{

        Long userId = userUtilX.getUserIdNotError();
        String dvi = userUtilX.getDvi();

        if(StringUtil.isEmpty(dvi)){
            if("dev".equals(this.profile)){
                dvi = "test";
            }
            else{
                LogUtil.log("AI获取聊天记录 请求没有设备号");
                return new ArrayList<>();
            }
        }

        String k = "";
        if(null != userId){
            k = userId.toString();
        }
        else{
            k = dvi;
        }

        String chatKey = RedisKey.ai_chat_msg_list_ + k;



        List<AIChatMessageDTO> list = redisUtilX.getObjList(chatKey, AIChatMessageDTO.class);
        if(null == list || list.isEmpty()){
            list = new ArrayList<>();
            List<AiChatRecordPO> recordPOS = new ArrayList<>();
            if(null == userId){
                recordPOS = aiChatRecordMapper.selectByDeviceId(dvi);
            }
            else{
                recordPOS = aiChatRecordMapper.selectByUserId(userId);
            }
            if(null != recordPOS && !recordPOS.isEmpty()){

                for(AiChatRecordPO recordPO : recordPOS){

                    AIChatMessageDTO messageDTO1 = new AIChatMessageDTO();
                    messageDTO1.setContent(recordPO.getContent());
                    messageDTO1.setTime(recordPO.getCreateTime());
                    messageDTO1.setRole("user");

                    AIChatMessageDTO messageDTO2 = new AIChatMessageDTO();
                    messageDTO2.setContent(recordPO.getReply());
                    messageDTO2.setTime(recordPO.getReplyTime());
                    messageDTO2.setRole("assistant");

                    list.add(messageDTO1);
                    list.add(messageDTO2);
                }

                redisUtilX.setObj(chatKey,list,86400);
            }
        }

        return list;
    }

    //清除会话
    public BaseVO cleanHistory() throws Exception{
        Long userId = userUtilX.getUserIdNotError();
        String dvi = userUtilX.getDvi();

        if(StringUtil.isEmpty(dvi)){
            if("dev".equals(this.profile)){
                dvi = "test";
            }
            else{
                return BaseVO.error();
            }
        }

        String k = "";
        if(null != userId){
            k = userId.toString();
            aiChatRecordMapper.cleanByUserId(userId);
        }
        else{
            aiChatRecordMapper.cleanByDeviceId(dvi);
            k = dvi;
        }

        String chatKey = RedisKey.ai_chat_msg_list_ + k;
        redisUtilX.delete(chatKey);

        return BaseVO.ok();
    }


    private boolean isSeasonVIP(Long userId) throws Exception{
        if(null == userId){
            return false;
        }
        String key = RedisKey.user_is_season_cache_ + userId;
        if(redisUtilX.hasKey(key)){
            String val = redisUtilX.get(key);
            return "1".equals(val);
        }
        SeasonUserPO seasonUserPO = seasonUserMapper.findByUserId(userId);
        String val = "0";
        if(null != seasonUserPO && StatusEnum.YES.getCode().equals(seasonUserPO.getStatus())){
            val = "1";
        }
        redisUtilX.set(key,val,7200);
        return "1".equals(val);
    }




}
