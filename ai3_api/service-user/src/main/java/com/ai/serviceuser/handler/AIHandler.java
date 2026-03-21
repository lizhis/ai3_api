package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.base.AIChatMessageDTO;
import com.ai.basecommon.core.param.base.AIChatParam;
import com.ai.basecommon.core.po.base.AiChatRecordPO;
import com.ai.basecommon.core.po.base.SysConfigPO;
import com.ai.basecommon.core.po.user.SeasonUserPO;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.enums.StatusEnum;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.async.AIChatAsync;
import com.ai.serviceuser.common.ConfigUtilX;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.AiChatRecordMapper;
import com.ai.serviceuser.mapper.DeviceInfoMapper;
import com.ai.serviceuser.mapper.SeasonUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AIHandler {

    @Autowired
    private UserUtilX userUtilX;

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private ConfigUtilX configUtilX;

    @Autowired
    private AiChatRecordMapper aiChatRecordMapper;

    @Autowired
    private SeasonUserMapper seasonUserMapper;

    @Autowired
    private AIChatAsync aiChatAsync;


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
                    list.add(messageDTO1);

                    if(null != recordPO.getReplyTime() && !StringUtil.isEmpty(recordPO.getReply())){
                        AIChatMessageDTO messageDTO2 = new AIChatMessageDTO();
                        messageDTO2.setContent(recordPO.getReply());
                        messageDTO2.setTime(recordPO.getReplyTime());
                        messageDTO2.setRole("assistant");
                        list.add(messageDTO2);
                    }

                }

                redisUtilX.setObj(chatKey,list,86400);
            }
        }

        return list;
    }



    //聊天
    public SseEmitter chat(AIChatParam param) throws Exception{
        SseEmitter sseEmitter = new SseEmitter(300000L);
        AtomicBoolean completed = new AtomicBoolean(false);
        ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (completed.get()) {
                heartbeatExecutor.shutdown();
                return;
            }
            try {
                //LogUtil.log("发送心跳");
                sseEmitter.send(SseEmitter.event().name("ping").data(""));
            } catch (IOException e) {
                LogUtil.log("发送心跳失败: " + e.getMessage());
                completed.set(true);
                sseEmitter.complete();
            }
        }, 0, 10, TimeUnit.SECONDS);

        sseEmitter.onCompletion(() -> {
            LogUtil.log("SSE 连接已完成");
            completed.set(true);
            heartbeatExecutor.shutdown();
        });
        sseEmitter.onTimeout(() -> {
            LogUtil.log("SSE 连接超时");
            completed.set(true);
            heartbeatExecutor.shutdown();
        });
        sseEmitter.onError(e -> {
            LogUtil.log("SSE 连接出错: " + e.getMessage());
            completed.set(true);
            heartbeatExecutor.shutdown();
        });


        if(null == param || StringUtil.isEmpty(param.getContent()) || StringUtil.isEmpty(param.getMsgId())){
            LogUtil.log("发起聊天失败 没有参数");
            sseEmitter.send(SseEmitter.event().name("error").data(""));
            sseEmitter.complete();
            return sseEmitter;
        }
        String content = param.getContent().trim();
        if(StringUtil.isEmpty(content)){
            LogUtil.log("发起聊天失败 空的content");
            sseEmitter.send(SseEmitter.event().name("error").data(""));
            sseEmitter.complete();
            return sseEmitter;
        }
        String msgId = param.getMsgId().trim();
        if(StringUtil.isEmpty(msgId)){
            LogUtil.log("发起聊天失败 空的msgId");
            sseEmitter.send(SseEmitter.event().name("error").data(""));
            sseEmitter.complete();
            return sseEmitter;
        }

        if(content.length() > 512){
            LogUtil.log("发起聊天失败 字数太长 大于了512");
            sseEmitter.send(SseEmitter.event().name("error").data(strToBase64Binary(StatusCodeEnum.AI_CHAT_CONTENT_LENGTH.getMsg())));
            sseEmitter.complete();
            return sseEmitter;
        }

        Long userId = userUtilX.getUserIdNotError();
        String dvi = userUtilX.getDvi();
        if(StringUtil.isEmpty(dvi)){
            if("dev".equals(this.profile)){
                dvi = "test";
            }
            else{
                LogUtil.log("发起聊天失败 请求没有设备号");
                sseEmitter.send(SseEmitter.event().name("error").data(strToBase64Binary(StatusCodeEnum.CAN_NOT_USE.getMsg())));
                sseEmitter.complete();
                return sseEmitter;
            }
        }

        Long time = System.currentTimeMillis();


        String devicePassKey = "ai_chat_pass_device_" + dvi;
        if(!redisUtilX.hasKey(devicePassKey)){
            Long lastDeviceTime = deviceInfoMapper.findLastTimeByDeviceId(dvi);
            if(null == lastDeviceTime){
                LogUtil.log("发起聊天失败 没有进站记录");
                sseEmitter.send(SseEmitter.event().name("error").data(strToBase64Binary(StatusCodeEnum.CAN_NOT_USE.getMsg())));
                sseEmitter.complete();
                return sseEmitter;
            }
            if(time - lastDeviceTime > 86400000L){
                LogUtil.log("发起聊天失败 最近的进站记录在1天之前");
                sseEmitter.send(SseEmitter.event().name("error").data(strToBase64Binary(StatusCodeEnum.PLEASE_RESTART_APP.getMsg())));
                sseEmitter.complete();
                return sseEmitter;
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

            if(this.isSeasonVIP(userId)){
                //是会员
                if(aiChatVipLimit > 0 && currentCount >= aiChatVipLimit){
                    LogUtil.log("发起聊天失败 VIP用户聊天次数已满 用户ID是："+userId);
                    sseEmitter.send(SseEmitter.event().name("error").data(strToBase64Binary(StatusCodeEnum.AI_CHAT_VIP_LIMIT.getMsg())));
                    sseEmitter.complete();
                    return sseEmitter;
                }
            }
            else{
                //普通用户
                if(aiChatUserLimit > 0 && currentCount >= aiChatUserLimit){
                    LogUtil.log("发起聊天失败 普通用户聊天次数已满 用户ID是："+userId);
                    sseEmitter.send(SseEmitter.event().name("error").data(strToBase64Binary(StatusCodeEnum.AI_CHAT_USER_LIMIT.getMsg())));
                    sseEmitter.complete();
                    return sseEmitter;
                }
            }
        }
        else{
            k = dvi;
            //查使用次数
            int currentCount = aiChatRecordMapper.countDeviceYmd(dvi,ymd);
            if(aiChatVisitorLimit > 0 && currentCount >= aiChatVisitorLimit){
                LogUtil.log("发起聊天失败 游客聊天次数已满 设备号是："+dvi);
                sseEmitter.send(SseEmitter.event().name("error").data(strToBase64Binary(StatusCodeEnum.AI_CHAT_VISITOR_LIMIT.getMsg())));
                sseEmitter.complete();
                return sseEmitter;
            }
        }
        aiChatAsync.chat(sseEmitter,dvi,userId,msgId,content);
        return sseEmitter;
    }



    public static String strToBase64Binary(String str){
        if(StringUtil.isEmpty(str)){
            return "";
        }
        byte[] binaryData = str.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(binaryData);
    }



    @ReadOnly
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
