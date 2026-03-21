package com.ai.servicebusiness.commom;



import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtilX {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取过期时间
     * @param name
     * @return
     */
    public long getExpire(String name){
        return redisTemplate.getExpire(name);
    }


    /**
     * 获取过期时间
     * @param name
     * @return
     */
    public String getExpireStr(String name){
        long second =  redisTemplate.getExpire(name);
        if(-1 == second){
            return null;
        }
        return this.secondToTimeStr(second);
    }


    /**
     * 秒转时间
     * @param second
     * @return
     */
    private String secondToTimeStr(long second){
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;                //剩余秒数

        if(days > 0){
            if(hours > 0){
                return days + "d" + " " + hours + "h";
            }else{
                return days + "d";
            }
        }
        else if(hours > 0){
            if(minutes > 0){
                return hours + "h" + " " + minutes + "min";
            }else{
                return hours + "h";
            }
        }
        else if(minutes > 0){
            if(second > 0){
                return minutes + "min" + " " + second + "s";
            }else{
                return minutes + "min";
            }
        }
        else{
            return second + "s";
        }
    }


    public void set(String name,String value)
    {
        redisTemplate.opsForValue().set(name,value);
    }

    public void setObj(String name,Object obj)
    {
        redisTemplate.opsForValue().set(name, JSON.toJSONString(obj));
    }

    public void setObj(String name,Object obj,int expire)
    {
        redisTemplate.opsForValue().set(name, JSON.toJSONString(obj),expire,TimeUnit.SECONDS);
    }


    //设置有效期时间 默认秒
    public void set(String name,String value,int expire)
    {
        redisTemplate.opsForValue().set(name,value,expire,TimeUnit.SECONDS);
    }

    //设置有效期时间
    public void set(String name,String value,int expire,TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(name,value,expire,timeUnit);
    }

    public String get(String name)
    {
        return redisTemplate.opsForValue().get(name);
    }


    //取对象
    public JSONObject getObj(String key){
        String str = redisTemplate.opsForValue().get(key);
        if(null == str || "".equals(str)){
            return null;
        }
        return JSON.parseObject(str);
    }

    //取对象
    public <T> T getObj(String key,Class<T> tClass){
        String str = redisTemplate.opsForValue().get(key);
        if(null == str || "".equals(str)){
            return null;
        }
        T obj = JSON.parseObject(str,tClass);
        return obj;
    }


    //取对象
    public <T> List getObjList(String key, Class<T> tClass){
        String str = redisTemplate.opsForValue().get(key);
        if(null == str || "".equals(str)){
            return null;
        }
        List<T> obj = JSON.parseArray(str,tClass);
        return obj;
    }

    //增加步长
    public String setInc(String name,Integer value)
    {
        if(null != value && value > 0){
            redisTemplate.boundValueOps(name).increment(value);//步长增加
        }
        return get(name);
    }

    //减少步长
    public String setDec(String name,Integer value)
    {
        if(null != value && value > 0){
            redisTemplate.boundValueOps(name).decrement(value);//步长减少
        }
        return get(name);
    }



    public void delete(String name)
    {
        redisTemplate.delete(name);
    }


    //查询key是否存在
    public boolean hasKey(String name)
    {
        return redisTemplate.hasKey(name);
    }

    //发布消息
    public void sendMsg(String channel,String message)
    {
        redisTemplate.convertAndSend(channel,message);
    }

    //发布消息
    public void sendMsg(String channel,Object obj)
    {
        redisTemplate.convertAndSend(channel,JSONObject.toJSONString(obj));
    }

    /**
     * set类型设置
     * @param key
     * @param value
     */
    public void setSet(String key,String value){
        redisTemplate.opsForSet().add(key,value);
    }

    /**
     * set类型批量设置
     * @param key
     * @param list
     */
    public void setSetInsertBatch(String key, List<String> list) {
        if (list != null && !list.isEmpty()) {
            String[] valueArray = list.toArray(new String[0]);
            redisTemplate.opsForSet().add(key, valueArray);
        }
    }

    /**
     * set类型获取
     * @param key
     * @return
     */
    public Set<String> getSet(String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * set类型删除一个值
     * @param key
     * @param value
     */
    public void setDelete(String key,String value){
        redisTemplate.opsForSet().remove(key,value);
    }

    /**
     * set类型判断成员是否存在
     * @param key
     * @param value
     * @return
     */
    public boolean setMemberIsExist(String key,Object value){
        if(value instanceof String){
            return redisTemplate.opsForSet().isMember(key,value);
        }
        return redisTemplate.opsForSet().isMember(key,JSONObject.toJSONString(value));
    }


    /**
     * set类型弹出一个值
     * @param key
     * @return
     */
    public String setPopOne(String key){
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * list类型头部插入元素
     * @param key
     * @param value
     */
    public void listLeftPush(String key,String value){
        redisTemplate.opsForList().leftPush(key,value);
    }

    /**
     * 列表类型头部插入对象
     * @param key
     * @param obj
     */
    public void listLeftPushObj(String key,Object obj){
        redisTemplate.opsForList().leftPush(key,JSON.toJSONString(obj));
    }

    /**
     * 列表类型头部弹出元素
     * @param key
     * @return
     */
    public String listLeftPop(String key){
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 列表类型头部弹出对象
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T listLeftPopObj(String key,Class<T> tClass){
        String str = redisTemplate.opsForList().leftPop(key);
        if(null == str || "".equals(str)){
            return null;
        }
        T value = null;
        try{
            value = JSON.parseObject(str,tClass);
        }catch (Exception e){
            LogUtil.log(e.getMessage());
        }
        return value;
    }

    /**
     * 列表类型尾部插入元素
     * @param key
     * @param value
     */
    public void listRightPush(String key,String value){
        redisTemplate.opsForList().rightPush(key,value);
    }

    /**
     * 列表类型尾部插入对象
     * @param key
     * @param obj
     */
    public void listRightPushObj(String key,Object obj){
        redisTemplate.opsForList().rightPush(key,JSON.toJSONString(obj));
    }

    /**
     * 列表类型尾部弹出元素
     * @param key
     * @return
     */
    public String listRightPop(String key){
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 列表类型尾部弹出对象
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T listRightPopObj(String key,Class<T> tClass){
        String str = redisTemplate.opsForList().rightPop(key);
        if(null == str || "".equals(str)){
            return null;
        }
        T obj = null;
        try{
            obj = JSON.parseObject(str,tClass);
        }catch (Exception e){
            System.out.println("Redis解析list结构失败：" + str);
        }
        return obj;
    }

    /**
     * 列表修剪
     * @param key
     * @param startIndex 开始位置
     * @param endIndex 结束位置
     */
    public void listTrim(String key,Integer startIndex,Integer endIndex){
        redisTemplate.opsForList().trim(key,startIndex.longValue(),endIndex.longValue());
    }

    /**
     * 查询列表数据
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> listSelect(String key,Class<T> tClass){
        return this.listSelect(key,0,redisTemplate.opsForList().size(key).intValue(),tClass);
    }

    /**
     * 查询列表范围数据
     * @param key
     * @param startIndex
     * @param endIndex
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> List<T> listSelect(String key,Integer startIndex,Integer endIndex,Class<T> tClass){
        List<String> strList = redisTemplate.opsForList().range(key,startIndex.longValue(),endIndex.longValue());
        if(null == strList || strList.isEmpty()){
            return null;
        }
        List<T> list = new ArrayList<>();
        try{
            for(String str : strList){
                list.add(JSON.parseObject(str,tClass));
            }
        }catch (Exception e){
            System.out.println("Redis解析list结构失败：" + strList.toString());
        }
        return list;
    }



}
