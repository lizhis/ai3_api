package com.ai.serviceuser.mapper;

import com.ai.basecommon.core.dto.user.UserAuthDTO;
import com.ai.basecommon.core.dto.user.UserInfoDTO;
import com.ai.basecommon.core.po.user.UserPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface UserMapper {


    //新增
    int insertGetId(UserPO po);

    //手机号是否存在
    int countByTel(@Param("tel") String tel);

    String findTelByUserId(@Param("userId") Long userId);

    UserPO findByUserId(@Param("userId") Long userId);

    int findNewbieStatusByUserId(@Param("userId") Long userId);

    //获取当前等级
    int findLevelByUserId(@Param("userId") Long userId);

    String findRealNameByUserId(@Param("userId") Long userId);

    UserPO findByTel(@Param("tel") String tel);


    //是否存在身份证
    int countByIdcard(@Param("idcard") String idcard);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserInfoDTO userInfo(@Param("userId") Long userId);


    //查询用户实名信息
    UserAuthDTO findAuthInfo(@Param("userId") Long userId);

    //修改头像
    boolean updatePortrait(@Param("userId") Long userId,@Param("avatar") String avatar);


    //实名认证
    boolean auth(@Param("userId") Long userId,@Param("realName") String realName,@Param("idcard") String idcard);



    //更新用户等级
    boolean updateLevel(@Param("userId") Long userId,@Param("level") Integer level);


    //更新最后登录信息
    boolean updateLastLoginInfo(@Param("userId") Long userId,@Param("ip") String ip,@Param("addr") String addr,@Param("time") Long time);


    boolean updatePassLevel(@Param("userId") Long userId,@Param("passLevel") Integer passLevel);

    //更新来源渠道
    boolean updateChannel(@Param("userId") Long userId,@Param("channel") Integer channel);

    //更新进站时间
    boolean updateLastEnterTime(@Param("userId") Long userId,@Param("time") Long time);




}
