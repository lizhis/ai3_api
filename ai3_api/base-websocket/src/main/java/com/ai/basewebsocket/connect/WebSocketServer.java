package com.ai.basewebsocket.connect;


import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.dto.msg.ServiceChatUserStatusMsgDTO;
import com.ai.basecommon.core.dto.msg.UserLogMsgDTO;
import com.ai.basecommon.enums.ServiceChatUserStatusEnum;
import com.ai.basecommon.enums.UserLogActionEnum;
import com.ai.basecommon.enums.UserLogSourceEnum;
import com.ai.basecommon.utils.StringUtil;
import com.ai.basewebsocket.async.CommonAsync;
import com.ai.basewebsocket.common.RedisUtilX;
import com.ai.basewebsocket.common.UserUtilX;
import com.ai.basewebsocket.producer.ServiceChatUserStatusProducer;
import com.ai.basewebsocket.producer.UserLogProducer;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.dto.msg.UserDataMsgDTO;
import com.ai.basecommon.core.dto.ws.WebSocketDTO;
import com.ai.basecommon.enums.WsCodeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.JwtUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.basewebsocket.mapper.DeviceInfoMapper;
import com.ai.basewebsocket.mapper.UserMapper;
import com.ai.basewebsocket.producer.UserDataProducer;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @Description
 * @Author
 * 
 */
@Component
@ServerEndpoint("/{X-Dvi}")
public class WebSocketServer {


    //用户频道

    //Map<userId,List<sessionId>>
    private static Map<Long, List<String>> channelUsers = new ConcurrentHashMap<>();

    //Map<sessionId,userId>
    private static Map<String, Long> channelUsersSessionIdToUserId = new ConcurrentHashMap<>();

    //Map<sessionId,deviceId>
    private static Map<String, String> channelSessionIdToDeviceId = new ConcurrentHashMap<>();

    //客户端存储  Map<sessionId,Session>
    private static Map<String, Session> clients = new ConcurrentHashMap<>();


    private static UserMapper userMapper;

    @Autowired
    private void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }


    private static DeviceInfoMapper deviceInfoMapper;

    @Autowired
    private void setDeviceInfoMapper(DeviceInfoMapper deviceInfoMapper){
        WebSocketServer.deviceInfoMapper = deviceInfoMapper;
    }

    private static RedisUtilX redisUtilX;

    @Autowired
    private void setRedisUtilX(RedisUtilX redisUtilX){
        WebSocketServer.redisUtilX = redisUtilX;
    }


    private static UserUtilX userUtilX;

    @Autowired
    private void setUserUtilX(UserUtilX userUtilX){
        WebSocketServer.userUtilX = userUtilX;
    }



    private static UserDataProducer userDataProducer;

    @Autowired
    private void setUserDataProducer(UserDataProducer userDataProducer){
        WebSocketServer.userDataProducer = userDataProducer;
    }

    private static ServiceChatUserStatusProducer serviceChatUserStatusProducer;

    @Autowired
    private void setServiceChatUserStatusProducer(ServiceChatUserStatusProducer serviceChatUserStatusProducer){
        WebSocketServer.serviceChatUserStatusProducer = serviceChatUserStatusProducer;
    }



    private static UserLogProducer userLogProducer;

    @Autowired
    private void setUserLogProducer(UserLogProducer userLogProducer){
        WebSocketServer.userLogProducer = userLogProducer;
    }


    private static CommonAsync commonAsync;

    @Autowired
    private void setCommonAsync(CommonAsync commonAsync){
        WebSocketServer.commonAsync = commonAsync;
    }




    //延迟任务调度
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Set<Integer> USER_DEBOUNCE_CODES = Set.of(1,2, 7);
    private static final ConcurrentMap<String, ScheduledFuture<?>> userDebounceTasks = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, WebSocketDTO> userPendingMessages = new ConcurrentHashMap<>();



    @OnOpen
    public void onOpen(Session session,@PathParam("X-Dvi") String dvi) {
        LogUtil.log("有客户端开始连接：" + dvi);
        if(!clients.containsKey(session.getId())){
            clients.put(session.getId(), session);
        }

        if(null != dvi && !"".equals(dvi) && !"".equals(dvi.trim())){
            dvi = dvi.trim();

            boolean result = userUtilX.checkDvi(dvi);
            if(!result){
                WebSocketDTO webSocketDTO = new WebSocketDTO();
                webSocketDTO.setCode(WsCodeEnum.REJECT.getCode());
                this.send(session.getId(),webSocketDTO);
                return;
            }
            if(!channelSessionIdToDeviceId.containsKey(session.getId())){
                channelSessionIdToDeviceId.put(session.getId(), dvi);
            }

            commonAsync.wsNotifyServiceUnRead(dvi,1000);

        }

        LogUtil.log("websocket连接 sessionId是：" + session.getId() + "，设备号是：" + dvi + "，现有连接数：" + clients.size() + "个");
    }


    /**
     * 客户端关闭
     * @param session session
     */
    @OnClose
    public void onClose(Session session) {
        if(clients.containsKey(session.getId())){
            clients.remove(session.getId());
            LogUtil.log("websocket断开：" + session.getId());
        }
        if(channelSessionIdToDeviceId.containsKey(session.getId())){
            String deviceId = channelSessionIdToDeviceId.get(session.getId());
            Long userId = null;
            if(channelUsersSessionIdToUserId.containsKey(session.getId())){
                userId = channelUsersSessionIdToUserId.get(session.getId());
            }
            channelSessionIdToDeviceId.remove(session.getId());

            String deviceOnlineKey = RedisKey.device_is_online_ + deviceId;
            String chatSubscribeKey = RedisKey.customer_service_subscribe_ + deviceId;
            String openChatKey = RedisKey.device_id_to_chat_ + deviceId;
            if(redisUtilX.hasKey(openChatKey)){
                redisUtilX.delete(deviceOnlineKey);
                redisUtilX.delete(chatSubscribeKey);
                Long chatId;
                try{
                    chatId = Long.parseLong(redisUtilX.get(openChatKey));
                    ServiceChatUserStatusMsgDTO msgDTO = new ServiceChatUserStatusMsgDTO();
                    msgDTO.setChatId(chatId);
                    msgDTO.setUserStatus(ServiceChatUserStatusEnum.OFFLINE.getCode());
                    serviceChatUserStatusProducer.produce(msgDTO);
                }catch (Exception e){}

            }



            String darkMark = "app_dark_" + deviceId;
            UserLogMsgDTO dto = null;
            if(redisUtilX.hasKey(darkMark)){
                try{
                    dto = redisUtilX.getObj(darkMark, UserLogMsgDTO.class);
                }catch (Exception e){
                    LogUtil.log("解析标记错误：" + e.getMessage());
                }
                redisUtilX.delete(darkMark);
                if(null != dto){
                    userLogProducer.produce(dto);
                }
            }
            if(null == dto){
                Long time = System.currentTimeMillis();
                dto = new UserLogMsgDTO();
                dto.setDeviceId(deviceId);
                dto.setUserId(userId);
                dto.setSource(UserLogSourceEnum.PAGE.getCode());
                dto.setAction(UserLogActionEnum.PAGE_OUT.getCode());
                dto.setLevel(1);
                dto.setContent("APP");
                dto.setRemark(null);
                dto.setIp(null);
                dto.setCreateTime(time);
                dto.setUpdateTime(time);
                userLogProducer.produce(dto);
            }

        }
        if(channelUsersSessionIdToUserId.containsKey(session.getId())){
            Long userId = channelUsersSessionIdToUserId.get(session.getId());
            channelUsersSessionIdToUserId.remove(session.getId());

            LogUtil.log("断开 这个连接对应的userID是：" + userId);

            this.offLine(userId);

            //订阅的用户频道
            List<String> sessionIdList = channelUsers.get(userId);
            if(null != sessionIdList && !sessionIdList.isEmpty()){
                //删除这个连接
                sessionIdList.remove(session.getId());
                if(sessionIdList.isEmpty()){
                    channelUsers.remove(userId);
                }
                else{
                    //这个用户还有连接在订阅
                    channelUsers.put(userId,sessionIdList);
                }
            }

            LogUtil.log("断开 用户频道已下线：" + userId);

        }
    }

    /**
     * 发生错误
     * @param throwable e
     */
    @OnError
    public void onError(Throwable throwable) {
        LogUtil.log("ws发生错误：" + throwable.getMessage());
        throwable.printStackTrace();
    }


    /**
     * 收到客户端发来消息
     * @param message  消息对象
     */
    @OnMessage
    public void onMessage(String message,Session session) {
        //LogUtil.log("服务端收到客户端：" + session.getId() + "：发来的消息："+message);
        if(StringUtils.isEmpty(message) || "ping".equals(message)){
            //LogUtil.log("用户-ping...");
            if(channelSessionIdToDeviceId.containsKey(session.getId())){
                String deviceId = channelSessionIdToDeviceId.get(session.getId());
                String deviceOnlineKey = RedisKey.device_is_online_ + deviceId;
                if(!redisUtilX.hasKey(deviceOnlineKey)){
                    redisUtilX.set(deviceOnlineKey,"1",120);

                    LogUtil.log("ping... 设置设备在线状态");

                    String chatSubscribeKey = RedisKey.customer_service_subscribe_ + deviceId;
                    String openChatKey = RedisKey.device_id_to_chat_ + deviceId;

                    LogUtil.log("是否有订阅会话：" + redisUtilX.hasKey(chatSubscribeKey));
                    if(!redisUtilX.hasKey(chatSubscribeKey)){
                        LogUtil.log("设备没有订阅会话");

                        //查询是否有会话
                        if(redisUtilX.hasKey(openChatKey)){
                            LogUtil.log("设备身上有客服会话");
                            Long chatId;
                            try{
                                chatId = Long.parseLong(redisUtilX.get(openChatKey));
                                ServiceChatUserStatusMsgDTO msgDTO = new ServiceChatUserStatusMsgDTO();
                                msgDTO.setChatId(chatId);
                                msgDTO.setUserStatus(ServiceChatUserStatusEnum.ONLINE.getCode());
                                serviceChatUserStatusProducer.produce(msgDTO);
                                LogUtil.log("推送用户状态：在线");
                            }catch (Exception e){}
                        }
                        else{
                            redisUtilX.delete(chatSubscribeKey);
                        }
                    }
                    else{
                        if(redisUtilX.hasKey(openChatKey)){
                            long sedonds = redisUtilX.getSecondsExpire(chatSubscribeKey);
                            if(sedonds < 60L){
                                LogUtil.log(chatSubscribeKey + " 续期3分钟");
                                String v = redisUtilX.get(chatSubscribeKey);
                                redisUtilX.set(chatSubscribeKey,v,180);
                            }
                        }
                        else{
                            redisUtilX.delete(chatSubscribeKey);
                        }
                    }
                }
            }
            return;
        }
        // 发送人  接收人 消息内容
        LogUtil.log("收到消息 客户端sessionId是："+session.getId()+"，消息内容是："+message);

        String channel = null;
        String operation = null;
        String token = null;
        Long chatId = null;

        try{
            JSONObject jsonObject = JSONObject.parseObject(message);
            channel = jsonObject.getString("channel");
            operation = jsonObject.getString("operation");
            token = jsonObject.getString("Authorization");
            chatId = jsonObject.getLong("chatId");
        }catch (Exception e){
            LogUtil.log("收到消息 解析json失败：" + message);
            return;
        }

        if(null == channel || "".equals(channel)){
            LogUtil.log("收到消息 channel参数缺失：" + message);
            return;
        }

        if(null == operation || "".equals(operation)){
            LogUtil.log("收到消息 operation参数缺失：" + message);
            return;
        }

        //channel：user
        //operation：subscribe
        //Authorization：token。。。

        //用户频道
        if(channel.equals("user")){

            if(operation.equals("subscribe")){

                LogUtil.log("有连接正在订阅用户频道，sessionId是：" + session.getId());

                //订阅
                if(null == token || "".equals(token)){
                    LogUtil.log("订阅用户频道 没有token，sessionId是：" + session.getId());
                    return;
                }
                Long userId = null;

                try{
                    //userId = baseApiService.findUserIdByToken(token);
                    Long id = JwtUtil.getUserIdFromToken(token);
                    if(null != id && id > 0){
                        userId = id;
                    }
                }catch (Exception e){
                    LogUtil.log(e.getMessage());
                    e.printStackTrace();
                    return;
                }

                if(null == userId){
                    return;
                }

                List<String> sessionIdList = new ArrayList<>();

                //频道是否有这个用户
                if(channelUsers.containsKey(userId)){

                    //取出这个用户的连接
                    sessionIdList = channelUsers.get(userId);

                    //如果当前连接已经存在 就移除
                    if(sessionIdList.contains(session.getId())){
                        sessionIdList.remove(session.getId());
                    }
                }
                //加入连接
                sessionIdList.add(session.getId());

                //频道用户更新
                channelUsers.put(userId, sessionIdList);
                channelUsersSessionIdToUserId.put(session.getId(),userId);

                //上线
                onLine(userId);

                int s = deviceInfoMapper.countByUserIdYmd(userId, DateUtil.todayDate());
                if(s < 1){
                    UserDataMsgDTO userDataMsgDTO = new UserDataMsgDTO();
                    userDataMsgDTO.setUserId(userId);
                    userDataMsgDTO.setOnline(true);
                    userDataProducer.produce(userDataMsgDTO);
                }

                //WebSocketDTO webSocketDTO = new WebSocketDTO();
                //webSocketDTO.setCode(WsCodeEnum.USER_INFO.getCode());

                LogUtil.log("用户频道订阅成功， 用户ID是：" + userId);
                LogUtil.log("用户加入了sessionIdList,当前list是：" + sessionIdList);
                //sendUser(userId, webSocketDTO);
            }

            else if(operation.equals("cancel")){
                //取消订阅
                LogUtil.log("有连接正在取消订阅用户频道...");

                if(!channelUsersSessionIdToUserId.containsKey(session.getId())){
                    return;
                }
                Long userId = channelUsersSessionIdToUserId.get(session.getId());
                this.offLine(userId);
                if(!channelUsers.containsKey(userId)){
                    channelUsersSessionIdToUserId.remove(session.getId());
                    return;
                }
                //取出当前用户的所有连接
                List<String> sessionIdList = channelUsers.get(userId);
                if(null == sessionIdList || sessionIdList.isEmpty()){
                    return;
                }
                if(!sessionIdList.contains(session.getId())){
                    return;
                }
                //移除这个连接
                sessionIdList.remove(session.getId());

                channelUsersSessionIdToUserId.remove(session.getId());

                if(sessionIdList.isEmpty()){
                    //空了  当前用户一个订阅连接都没有了
                    channelUsers.remove(userId);
                }
                else{
                    //还有
                    channelUsers.put(userId,sessionIdList);
                }
                LogUtil.log("用户频道取消订阅成功， 用户ID是：" + userId);
            }

            else{}

        }

        //客服频道
        else if(channel.equals("service")){

            if(operation.equals("subscribe")){

                LogUtil.log("有连接正在订阅客服频道，sessionId是：" + session.getId());

                if(null == chatId || chatId < 1){
                    LogUtil.log("订阅失败 chatId是空的，sessionId是：" + session.getId());
                    return;
                }

                if(!channelSessionIdToDeviceId.containsKey(session.getId())){
                    LogUtil.log("订阅失败 找不到存储的deviceId，sessionId是：" + session.getId());
                    return;
                }
                String deviceId = channelSessionIdToDeviceId.get(session.getId());

                String key = RedisKey.customer_service_subscribe_ + deviceId;
                redisUtilX.set(key,chatId.toString(),3600);

                ServiceChatUserStatusMsgDTO msgDTO = new ServiceChatUserStatusMsgDTO();
                msgDTO.setChatId(chatId);
                msgDTO.setUserStatus(ServiceChatUserStatusEnum.ENTRE.getCode());
                serviceChatUserStatusProducer.produce(msgDTO);
                LogUtil.log("推送状态：已进入");

                LogUtil.log("订阅客服频道成功， 设备号是：" + deviceId + "，订阅的chatId是：" + chatId);
            }

            else if(operation.equals("cancel")){

                //取消订阅
                LogUtil.log("有连接正在取消订阅客服频道...");

                if(!channelSessionIdToDeviceId.containsKey(session.getId())){
                    LogUtil.log("取消失败 找不到存储的deviceId，sessionId是：" + session.getId());
                    return;
                }
                String deviceId = channelSessionIdToDeviceId.get(session.getId());

                String chatSubscribeKey = RedisKey.customer_service_subscribe_ + deviceId;
                redisUtilX.delete(chatSubscribeKey);

                String deviceOnlineKey = RedisKey.device_is_online_ + deviceId;
                String openChatKey = RedisKey.device_id_to_chat_ + deviceId;
                if(redisUtilX.hasKey(openChatKey)){
                    //判断是否在线  如果在线 就推送在线  如果不在线 就推送下线
                    Long chatId2;
                    try{
                        chatId2 = Long.parseLong(redisUtilX.get(openChatKey));
                        ServiceChatUserStatusMsgDTO msgDTO = new ServiceChatUserStatusMsgDTO();
                        msgDTO.setChatId(chatId2);

                        if(redisUtilX.hasKey(deviceOnlineKey)){
                            LogUtil.log("还在线，推送状态：在线");
                            msgDTO.setUserStatus(ServiceChatUserStatusEnum.ONLINE.getCode());
                        }
                        else{
                            LogUtil.log("不在线，推送状态：下线");
                            msgDTO.setUserStatus(ServiceChatUserStatusEnum.OFFLINE.getCode());
                        }
                        serviceChatUserStatusProducer.produce(msgDTO);
                    }catch (Exception e){}
                }


                LogUtil.log("取消订阅客服频道成功， 设备号是：" + deviceId);
            }

            else if(operation.equals("typing")){
                if(null == chatId || chatId < 1){
                    LogUtil.log("订阅正在输入失败 chatId是空的，sessionId是：" + session.getId());
                    return;
                }
                if(!channelSessionIdToDeviceId.containsKey(session.getId())){
                    LogUtil.log("订阅正在输入失败 找不到存储的deviceId，sessionId是：" + session.getId());
                    return;
                }
                String deviceId = channelSessionIdToDeviceId.get(session.getId());

                String subscribeKey = RedisKey.customer_service_subscribe_ + deviceId;
                if(!redisUtilX.hasKey(subscribeKey)){
                    redisUtilX.set(subscribeKey,chatId.toString(),600);
                }

                String typingKey = RedisKey.customer_service_typing_ + deviceId;
                redisUtilX.set(typingKey,chatId.toString(),10);

                ServiceChatUserStatusMsgDTO msgDTO = new ServiceChatUserStatusMsgDTO();
                msgDTO.setChatId(chatId);
                msgDTO.setUserStatus(ServiceChatUserStatusEnum.TYPING.getCode());
                serviceChatUserStatusProducer.produce(msgDTO);
                LogUtil.log("推送状态：正在输入");

            }
            else if(operation.equals("idle")){

                if(!channelSessionIdToDeviceId.containsKey(session.getId())){
                    LogUtil.log("取消订阅 正在输入失败 找不到存储的deviceId，sessionId是：" + session.getId());
                    return;
                }
                String deviceId = channelSessionIdToDeviceId.get(session.getId());

                String typingKey = RedisKey.customer_service_typing_ + deviceId;
                redisUtilX.delete(typingKey);

                String subscribeKey = RedisKey.customer_service_subscribe_ + deviceId;
                String deviceOnlineKey = RedisKey.device_is_online_ + deviceId;
                String deviceToChatKey = RedisKey.device_id_to_chat_ + deviceId;

                if(redisUtilX.hasKey(subscribeKey)){
                    try{
                        chatId = Long.parseLong(redisUtilX.get(subscribeKey));
                    }catch (Exception e){}
                }

                if(null != chatId && redisUtilX.hasKey(deviceToChatKey)){
                    chatId = Long.parseLong(redisUtilX.get(deviceToChatKey));
                }

                ServiceChatUserStatusMsgDTO msgDTO = new ServiceChatUserStatusMsgDTO();
                msgDTO.setChatId(chatId);

                if(redisUtilX.hasKey(subscribeKey)){
                    LogUtil.log("推送状态：已进入");
                    msgDTO.setUserStatus(ServiceChatUserStatusEnum.ENTRE.getCode());
                }
                else if(redisUtilX.hasKey(deviceOnlineKey)){
                    LogUtil.log("推送状态：在线");
                    msgDTO.setUserStatus(ServiceChatUserStatusEnum.ONLINE.getCode());
                }
                else{
                    LogUtil.log("推送状态：已下线");
                    msgDTO.setUserStatus(ServiceChatUserStatusEnum.OFFLINE.getCode());
                }
                serviceChatUserStatusProducer.produce(msgDTO);
            }

            else{}

        }

    }

    //上线
    private void onLine(Long userId){
        if(null == userId || userId < 1){
            return;
        }
        userMapper.updateOnline(userId);
        String userPOKey = RedisKey.user_po_cache_ + userId;
        redisUtilX.delete(userPOKey);
        //判断当前用户是否有会话
    }

    //下线状态
    private void offLine(Long userId){
        if(null == userId || userId < 1){
            return;
        }
        userMapper.updateOffline(userId);
        String userPOKey = RedisKey.user_po_cache_ + userId;
        redisUtilX.delete(userPOKey);
    }



    //发送用户消息
    public static void sendUser(Long userId, WebSocketDTO webSocketDTO) {
        if (userId == null || webSocketDTO == null || webSocketDTO.getCode() == null) {
            return;
        }
        int code = webSocketDTO.getCode();

        // 防抖
        if (USER_DEBOUNCE_CODES.contains(code)) {

            LogUtil.log("防抖过滤 发送给用户消息："+userId+"，内容是：" + webSocketDTO);

            String key = userId + ":" + code;
            userPendingMessages.put(key, webSocketDTO);

            ScheduledFuture<?> prev = userDebounceTasks.get(key);
            if (prev != null && !prev.isDone()) {
                prev.cancel(false);
            }
            ScheduledFuture<?> future = SCHEDULER.schedule(() -> {
                WebSocketDTO lastMsg = userPendingMessages.remove(key);
                userDebounceTasks.remove(key);
                if (lastMsg != null) {
                    doSendUserNow(userId, lastMsg);
                }
            }, 3, TimeUnit.SECONDS);

            userDebounceTasks.put(key, future);
        } else {
            doSendUserNow(userId, webSocketDTO);
        }
    }


    private static void doSendUserNow(Long userId, WebSocketDTO webSocketDTO) {
        if(null == userId || null == webSocketDTO){
            return;
        }
        if(null == webSocketDTO.getCode()){
            LogUtil.log("拒绝发送无效消息："+webSocketDTO.toString());
            return;
        }

        LogUtil.log("真实发送给用户消息："+userId+"，内容是：" + webSocketDTO);
        try {

            List<String> sessionIdList = channelUsers.get(userId);
            if(null == sessionIdList || sessionIdList.isEmpty()){
                return;
            }
            LogUtil.log("sessionIdList是：" + sessionIdList);
            for(String sessionId : sessionIdList){
                if(StringUtil.isEmpty(sessionId)){
                    continue;
                }
                Session session = clients.get(sessionId);
                if (session != null && session.isOpen()) {
                    String str = JSON.toJSONString(webSocketDTO);
                    synchronized(session){
                        try{
                            session.getBasicRemote().sendBinary(ByteBuffer.wrap(str.getBytes()));
                        }catch (Exception e){
                            LogUtil.log(e.getMessage());
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 群发消息
     * @param webSocketDTO 消息内容
     */
    public static void sendAll(WebSocketDTO webSocketDTO) {
        if(null == webSocketDTO || null == webSocketDTO.getCode()){
            LogUtil.log("拒绝发送无效消息："+webSocketDTO.toString());
            return;
        }
        LogUtil.log("发送全体消息：" + webSocketDTO);

        for(String sessionId : clients.keySet()){
            Session session = clients.get(sessionId);
            String str = JSON.toJSONString(webSocketDTO);
            try{
                //session.getAsyncRemote().sendBinary(ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));
                session.getBasicRemote().sendBinary(ByteBuffer.wrap(str.getBytes()));
            }catch (Exception e){
                LogUtil.log(e.getMessage());
            }
        }
    }


    // 发送设备消息
    public static void sendDeviceId(String deviceId, WebSocketDTO webSocketDTO) {
        if(StringUtil.isEmpty(deviceId) || null == webSocketDTO){
            return;
        }
        if(null == webSocketDTO.getCode()){
            LogUtil.log("拒绝发送无效消息："+webSocketDTO.toString());
            return;
        }

        //LogUtil.log("发送给设备消息：" + webSocketDTO);
        if(!channelSessionIdToDeviceId.containsValue(deviceId)){
            LogUtil.log("没有设备记录");
            return;
        }

        String sessionId = null;
        try {
            for(String id : channelSessionIdToDeviceId.keySet()){
                String dvi = channelSessionIdToDeviceId.get(id);
                if(dvi.equals(deviceId)){
                    sessionId = id;
                    break;
                }
            }

            Session session = clients.get(sessionId);
            if (session != null && session.isOpen()) {
                String str = JSON.toJSONString(webSocketDTO);

                    /*try{
                        session.getAsyncRemote().sendBinary(ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));
                    }catch (Exception e){
                        LogUtil.log(e.getMessage());
                    }*/
                synchronized(session){
                    try{
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(str.getBytes()));
                    }catch (Exception e){
                        LogUtil.log("发送设备消息失败："+e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void send(String sessionId, WebSocketDTO webSocketDTO) {
        if(StringUtil.isEmpty(sessionId)){
            return;
        }
        if(null == webSocketDTO.getCode()){
            LogUtil.log("拒绝发送无效消息："+webSocketDTO.toString());
            return;
        }
        try {
            Session session = clients.get(sessionId);
            if (session != null && session.isOpen()) {
                String str = JSON.toJSONString(webSocketDTO);
                synchronized(session){
                    try{
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(str.getBytes()));
                    }catch (Exception e){
                        LogUtil.log(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Map<Long, List<String>> getChannelUsers() {
        return channelUsers;
    }



    @PreDestroy
    public void cleanup() {
        SCHEDULER.shutdown();
        try {
            if (!SCHEDULER.awaitTermination(5, TimeUnit.SECONDS)) {
                SCHEDULER.shutdownNow();
            }
        } catch (InterruptedException e) {
            SCHEDULER.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
