package com.ai.servicebase.commom;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.utils.StringUtil;
import com.ai.servicebase.mapper.BlacklistDeviceMapper;
import com.ai.servicebase.mapper.BlacklistIpMapper;
import com.ai.servicebase.mapper.BlacklistTelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlacklistUtilX {

    @Autowired
    private RedisUtilX redisUtilX;

    @Autowired
    private BlacklistTelMapper blacklistTelMapper;

    @Autowired
    private BlacklistDeviceMapper blacklistDeviceMapper;

    @Autowired
    private BlacklistIpMapper blacklistIpMapper;


    public boolean verify(String tel, String deviceId, String ip){

        boolean telBlacklist = false;
        boolean deviceBlacklist = false;
        boolean ipBlacklist = false;

        // 检查所有黑名单状态
        if (tel != null && !tel.isEmpty()) {
            telBlacklist = redisUtilX.setMemberIsExist(RedisKey.blacklist_tel_set, tel);
        }

        if (deviceId != null && !deviceId.isEmpty()) {
            deviceBlacklist = redisUtilX.setMemberIsExist(RedisKey.blacklist_device_set, deviceId);
        }

        if (ip != null && !ip.isEmpty()) {
            ipBlacklist = redisUtilX.setMemberIsExist(RedisKey.blacklist_ip_set, ip);
        }

        // 判断是否有任何一个命中黑名单
        boolean anyBlacklisted = telBlacklist || deviceBlacklist || ipBlacklist;

        if (anyBlacklisted) {

            // 手机号不在黑名单但需要加入
            if (!telBlacklist && tel != null && !tel.isEmpty()) {
                redisUtilX.setSet(RedisKey.blacklist_tel_set, tel);
                insertTelToDatabase(tel);
            }

            // 设备ID不在黑名单但需要加入
            if (!deviceBlacklist && deviceId != null && !deviceId.isEmpty()) {
                redisUtilX.setSet(RedisKey.blacklist_device_set, deviceId);
                insertDeviceToDatabase(deviceId);
            }

            // IP不在黑名单但需要加入
            if (!ipBlacklist && ip != null && !ip.isEmpty()) {
                redisUtilX.setSet(RedisKey.blacklist_ip_set, ip);
                insertIpToDatabase(ip);
            }

            return false; // 命中黑名单，验证失败
        }

        return true; // 未命中任何黑名单，验证通过
    }


    private void insertTelToDatabase(String tel) {
        if(StringUtil.isEmpty(tel)){
            return;
        }
        int c = blacklistTelMapper.existByTel(tel);
        if(c == 0){
            blacklistTelMapper.insert(tel);
        }
    }

    private void insertDeviceToDatabase(String deviceId) {
        if(StringUtil.isEmpty(deviceId)){
            return;
        }
        int c = blacklistDeviceMapper.existByDeviceId(deviceId);
        if(c == 0){
            blacklistDeviceMapper.insert(deviceId);
        }
    }

    private void insertIpToDatabase(String ip) {
        if(StringUtil.isEmpty(ip)){
            return;
        }
        int c = blacklistIpMapper.existByIp(ip);
        if(c == 0){
            blacklistIpMapper.insert(ip);
        }
    }
}