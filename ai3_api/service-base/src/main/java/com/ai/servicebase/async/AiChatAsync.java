package com.ai.servicebase.async;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.base.AIChatMessageDTO;
import com.ai.basecommon.core.dto.sms.UserTaskMsgDTO;
import com.ai.basecommon.core.dto.ws.AiChatChatMsgDTO;
import com.ai.basecommon.core.po.base.AiChatRecordPO;
import com.ai.basecommon.core.po.base.SysConfApiPO;
import com.ai.basecommon.enums.TaskTypeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basecommon.utils.StringUtil;
import com.ai.servicebase.commom.ConfigUtilX;
import com.ai.servicebase.commom.RedisUtilX;
import com.ai.servicebase.mapper.AiChatRecordMapper;
import com.ai.servicebase.producer.AiChatProducer;
import com.ai.servicebase.producer.UserTaskProducer;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AiChatAsync {


    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private AiChatRecordMapper aiChatRecordMapper;

    @Autowired
    private ConfigUtilX configUtilX;

    @Autowired
    private UserTaskProducer userTaskProducer;

    @Autowired
    private AiChatProducer aiChatProducer;

    @Async
    public void chat(String deviceId,Long userId,String content){

        String k = "";
        if(null != userId){
            k = userId.toString();
        }
        else {
            k = deviceId;
        }

        String historyKey = RedisKey.ai_chat_msg_list_ + k;

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

        SysConfApiPO apiPO = configUtilX.loadConfApi();
        String apiKey = apiPO.getAiChatKey();
        String apiModel = apiPO.getAiChatModel();
        if(StringUtil.isEmpty(apiKey) || StringUtil.isEmpty(apiModel)){
            LogUtil.log("AI聊天 没有配置第三方接口");
            return;
        }

        Integer ymd = DateUtil.todayDate();

        Long time = System.currentTimeMillis();


        AiChatRecordPO recordPO = new AiChatRecordPO();
        recordPO.setUserId(userId);
        recordPO.setDeviceId(deviceId);
        recordPO.setContent(content);
        recordPO.setYmd(ymd);
        recordPO.setCreateTime(time);
        recordPO.setUpdateTime(time);
        aiChatRecordMapper.insertGetId(recordPO);

        //ArkService service = ArkService.builder().apiKey("16363e36-ab78-4f9a-ae6e-51bf78320a54").build();
        ArkService service = ArkService.builder().apiKey(apiKey).build();

/*
        final List<ChatMessage> messages = Arrays.asList(
                ChatMessage.builder().role(ChatMessageRole.USER).content("花椰菜是什么？").build(),
                ChatMessage.builder().role(ChatMessageRole.ASSISTANT).content("花椰菜又称菜花、花菜，是一种常见的蔬菜。").build(),
                ChatMessage.builder().role(ChatMessageRole.USER).content("刚刚我问的是什么菜？").build()
        );

        LogUtil.log("消息记录：" + messages.toString());
*/


        ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(content).build();
        streamMessages.add(streamUserMessage);

        BotChatCompletionRequest chatCompletionRequest = BotChatCompletionRequest.builder()
                .model(apiModel)
                .messages(streamMessages)
                .build();

        BotChatCompletionResult chatCompletionResult =  service.createBotChatCompletion(chatCompletionRequest);
        StringBuilder res = new StringBuilder();
        chatCompletionResult.getChoices().forEach(
                choice -> {
                    Object obj = choice.getMessage().getContent();
                    if(null != obj){
                        String str = obj.toString();
                        res.append(str);
                        //LogUtil.log(str);
                    }
                }
        );
        //service.shutdownExecutor();
        // the references example
/*        chatCompletionResult.getReferences().forEach(
                ref -> System.out.println(ref.getUrl())
        );*/



/*

        String r = HttpUtil.sendPostJson(url,p.toString(),headers);
        if(StringUtil.isEmpty(r)){
            recordPO.setUpdateTime(System.currentTimeMillis());
            aiChatRecordMapper.updateNotReply(recordPO);
            return;
        }
*/

        String result = res.toString();
        String resultRole = "";
        String apiMsgId = "";

/*        try{
            JSONObject resultObj = JSONObject.parseObject(r);
            JSONArray resultChoices = resultObj.getJSONArray("choices");
            apiMsgId = resultObj.getString("id");
            if(resultChoices.size() > 0){
                JSONObject ss = (JSONObject)resultChoices.get(0);
                JSONObject resultMsg = ss.getJSONObject("message");
                result = resultMsg.getString("content");
                resultRole = resultMsg.getString("role");
            }

        }catch (Exception e){
            LogUtil.log("ai聊天接口解析失败：" + e.getMessage() + "，解析的内容是：" + r);
            return;
        }*/


        Long replyTime = System.currentTimeMillis();

        recordPO.setReply(result);
        recordPO.setReplyTime(replyTime);
        recordPO.setApiMsgId(apiMsgId);
        recordPO.setCreateTime(time);
        recordPO.setUpdateTime(replyTime);
        aiChatRecordMapper.updateReply(recordPO);

        //LogUtil.log("AI的回答是：" + result);
        if(StringUtil.isEmpty(result)){
            return;
        }

        AIChatMessageDTO messageUser = new AIChatMessageDTO();
        messageUser.setRole("user");
        messageUser.setContent(content);
        messageUser.setTime(time);
        list.add(messageUser);

        AIChatMessageDTO messageSystem = new AIChatMessageDTO();
        messageSystem.setRole(resultRole);
        messageSystem.setContent(result);
        messageSystem.setTime(replyTime);
        list.add(messageSystem);

        redisUtilX.setObj(historyKey,list,86400);

        LogUtil.log("AI聊天接口 用时 " + ((System.currentTimeMillis() - time) / 1000) + "秒");


        AiChatChatMsgDTO chatMsg = new AiChatChatMsgDTO();
        chatMsg.setDeviceId(deviceId);
        chatMsg.setContent(result);
        aiChatProducer.produce(chatMsg);

        //String taskKey = "user_ai_chat_" + userId + "_" + ymd;
        if(null != userId){
            UserTaskMsgDTO userTaskMsgDTO = new UserTaskMsgDTO();
            userTaskMsgDTO.setUserId(userId);
            userTaskMsgDTO.setTaskType(TaskTypeEnum.AI_CHAT.getCode());
            userTaskProducer.produce(userTaskMsgDTO);
        }

    }


}
