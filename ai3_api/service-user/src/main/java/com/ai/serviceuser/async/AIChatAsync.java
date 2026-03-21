package com.ai.serviceuser.async;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.base.AIChatMessageDTO;
import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.ai.basecommon.core.dto.ws.AiChatChatMsgDTO;
import com.ai.basecommon.core.po.base.AiChatRecordPO;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.enums.TaskTypeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.HttpUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.serviceuser.common.ConfigUtilX;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.mapper.AiChatRecordMapper;
import com.ai.serviceuser.producer.AiChatProducer;
import com.ai.serviceuser.producer.UserTaskProducer;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class AIChatAsync {


    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private AiChatRecordMapper aiChatRecordMapper;

    @Autowired
    private ConfigUtilX configUtilX;

    @Autowired
    private UserTaskProducer userTaskProducer;


    @Async
    public void chat(SseEmitter sseEmitter,String deviceId, Long userId, String msgId, String content){

        String k = "";
        if(null != userId){
            k = userId.toString();
        }
        else {
            k = deviceId;
        }

        SysConfApiPO apiPO = configUtilX.loadConfApi();
        String apiKey = apiPO.getAiChatKey();
        String apiModel = apiPO.getAiChatModel();
        if(StringUtil.isEmpty(apiKey) || StringUtil.isEmpty(apiModel)){
            LogUtil.log("AI聊天 没有配置第三方接口");
            sseEmitter.complete();
            return;
        }


        String historyKey = RedisKey.ai_chat_msg_list_ + k;


        //JSONArray msgList = new JSONArray();

        List<AIChatMessageDTO> list = redisUtilX.getObjList(historyKey, AIChatMessageDTO.class);
        if(null == list || list.isEmpty()){
            list = new ArrayList<>();
            List<AiChatRecordPO> recordPOS = new ArrayList<>();
            if(null == userId){
                recordPOS = aiChatRecordMapper.selectByDeviceId(deviceId);
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

                redisUtilX.setObj(historyKey,list,86400);
            }
        }


        final List<ChatMessage> streamMessages = new ArrayList<>();

        if(!list.isEmpty()){
            for(AIChatMessageDTO dto : list){
                if(StringUtil.isEmpty(dto.getContent()) || "null".equals(dto.getContent())){
                    continue;
                }
                ChatMessageRole role = ChatMessageRole.SYSTEM;
                if("user".equals(dto.getRole())){
                    role = ChatMessageRole.USER;
                }
                else if("assistant".equals(dto.getRole())){
                    role = ChatMessageRole.ASSISTANT;
                }
                ChatMessage streamSystemMessage = ChatMessage.builder().role(role).content(dto.getContent()).build();
                streamMessages.add(streamSystemMessage);
            }
        }

        Integer ymd = DateUtil.todayDate();

        Long time = System.currentTimeMillis();

        //JSONObject p = new JSONObject();


        ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(content).build();
        streamMessages.add(streamUserMessage);

        //ChatMessage streamUserMessage2 = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你叫小猪，你是阿里云平台开发的人工智能").build();
        //streamMessages.add(streamUserMessage2);

        //String pp = p.toString();
        //LogUtil.log("最后参数是：" + pp);
        //LogUtil.log("问题是：" + content);

        AiChatRecordPO recordPO = new AiChatRecordPO();
        recordPO.setUserId(userId);
        recordPO.setDeviceId(deviceId);
        recordPO.setContent(content);
        recordPO.setYmd(ymd);
        recordPO.setCreateTime(time);
        recordPO.setUpdateTime(time);
        aiChatRecordMapper.insertGetId(recordPO);



        ArkService service = ArkService.builder().apiKey(apiKey).build();


        BotChatCompletionRequest chatCompletionRequest = BotChatCompletionRequest.builder()
                .model(apiModel)
                .messages(streamMessages)
                .build();


        //BotChatCompletionResult chatCompletionResult =  service.createBotChatCompletion(chatCompletionRequest);
        StringBuilder result = new StringBuilder();

        service.streamBotChatCompletion(chatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(
                        choice -> {
                            if (choice.getChoices().size() > 0) {
                                String str = choice.getChoices().get(0).getMessage().getContent().toString();
                                result.append(str);
                                //String str = convertToUtf8String(obj);
                                //String str = choice.getChoices().get(0).getMessage().getContent().toString();
                                //LogUtil.log("收到消息：" + str);
                                //LogUtil.log("收到消息：" + str + "，是不是utf8编码：" + isUtf8(str));
                                //sseEmitter.send("event:message\ndata:{"+str+"}\n\n");
                                //sseEmitter.send(str);
                                sseEmitter.send(SseEmitter.event().name("message").data(strToBase64Binary(str)));
                            }
                        }
                );

        // shutdown service
        service.shutdownExecutor();
        sseEmitter.complete();
        //LogUtil.log("连接关闭");

        Long replyTime = System.currentTimeMillis();

        recordPO.setReply(result.toString());
        recordPO.setReplyTime(replyTime);
        recordPO.setCreateTime(time);
        recordPO.setUpdateTime(replyTime);
        aiChatRecordMapper.updateReply(recordPO);
        if(result.isEmpty()){
            return;
        }
        AIChatMessageDTO messageUser = new AIChatMessageDTO();
        messageUser.setRole("user");
        messageUser.setContent(content);
        messageUser.setTime(time);
        list.add(messageUser);

        AIChatMessageDTO messageSystem = new AIChatMessageDTO();
        messageSystem.setRole("assistant");
        messageSystem.setContent(result.toString());
        messageSystem.setTime(replyTime);
        list.add(messageSystem);

        redisUtilX.setObj(historyKey,list,86400);

        LogUtil.log("AI聊天接口 用时 " + ((System.currentTimeMillis() - time) / 1000) + "秒");

        if(null != userId){
            UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
            userTaskMsgDTO.setUserId(userId);
            userTaskMsgDTO.setTaskType(TaskTypeEnum.AI_CHAT.getCode());
            userTaskProducer.produce(userTaskMsgDTO);
        }

    }

    public static String strToBase64Binary(String str){
        if(StringUtil.isEmpty(str)){
            return "";
        }
        byte[] binaryData = str.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(binaryData);
    }


}
