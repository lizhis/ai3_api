package com.ai.serviceuser.handler;

import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.core.param.user.addr.UserAddrAddParam;
import com.ai.basecommon.core.param.user.addr.UserAddrEditParam;
import com.ai.basecommon.core.po.user.UserAddrPO;
import com.ai.basecommon.core.vo.user.UserAddrVO;
import com.ai.basecommon.enums.IsEnum;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import com.ai.serviceuser.common.TransactionUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.UserAddrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAddrHandler {

    @Autowired
    private UserAddrMapper userAddrMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private TransactionUtilX transactionUtilX;


    //添加收货地址
    public boolean add(UserAddrAddParam param) throws Exception{

        Long userId = userUtilX.getUserId();
        Long time = System.currentTimeMillis();

        UserAddrPO po = new UserAddrPO();
        po.setUserId(userId);
        po.setReceiver(param.getReceiver());
        po.setMobile(param.getMobile());
        po.setProvince(param.getProvince());
        po.setCity(param.getCity());
        po.setDistrict(param.getDistrict());
        po.setDetail(param.getDetail());
        po.setCode(param.getCode());
        po.setIsDefault(param.getIsDefault());
        po.setCreateTime(time);
        po.setUpdateTime(time);



        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{

            //如果是默认地址 则取消之前的默认
            if(IsEnum.YES.getCode().equals(po.getIsDefault())){
                userAddrMapper.isDefaultOff(userId);
            }else{
                //如果不是默认地址 则查询是否已有默认地址 没有就强制默认
                int c = userAddrMapper.countForDefault(userId);
                if(0 == c){
                    po.setIsDefault(IsEnum.YES.getCode());
                }
                else{
                    po.setIsDefault(IsEnum.NO.getCode());
                }
            }
            userAddrMapper.insert(po);

        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            return false;
        }

        return true;
    }


    //更新收货地址
    public boolean edit(UserAddrEditParam param) throws Exception{

        Long userId = userUtilX.getUserId();
        Long time = System.currentTimeMillis();

        Long id = param.getId();


        UserAddrPO po = userAddrMapper.findById(id);
        if(null == po){
            BaseException.error(StatusCodeEnum.NO_AUTH);
        }
        if(!po.getUserId().equals(userId)){
            return false;
        }

        po.setReceiver(param.getReceiver());
        po.setMobile(param.getMobile());
        po.setProvince(param.getProvince());
        po.setCity(param.getCity());
        po.setDistrict(param.getDistrict());
        po.setDetail(param.getDetail());
        po.setCode(param.getCode());
        po.setIsDefault(param.getIsDefault());
        po.setUpdateTime(time);



        TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{
            //如果是默认地址 则取消之前的默认
            if(IsEnum.YES.getCode().equals(po.getIsDefault())){
                userAddrMapper.isDefaultOff(userId);
            }else{
                //如果不是默认地址 则查询是否已有默认地址 没有就强制默认
                int c = userAddrMapper.countForDefault(userId);
                if(0 == c){
                    po.setIsDefault(IsEnum.YES.getCode());
                }
                else{
                    po.setIsDefault(IsEnum.NO.getCode());
                }
            }
            userAddrMapper.update(po);
        });

        if (!transactionResultDTO.isSuccess()) {
            LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
            return false;
        }

        return true;
    }


    //查询
    @ReadOnly
    public List<UserAddrVO> select() throws Exception{
        Long userId = userUtilX.getUserId();
        return userAddrMapper.select(userId);
    }

    //地址详情
    @ReadOnly
    public UserAddrVO detail(Long id) throws Exception{
        Long userId = userUtilX.getUserId();
        return userAddrMapper.findByMyAddr(userId,id);
    }


    //查询默认地址
    @ReadOnly
    public UserAddrVO defaultAddr() throws Exception{
        Long userId = userUtilX.getUserId();
        return userAddrMapper.findDefaultAddr(userId);
    }

    //查询默认地址
    @ReadOnly
    public UserAddrVO defaultAddrByApi(Long userId) throws Exception{
        if(null == userId){
            return null;
        }
        return userAddrMapper.findDefaultAddr(userId);
    }



    //查询收货地址
    @ReadOnly
    public UserAddrPO findById(Long id) throws Exception{
        if(null == id || id < 1){
            return null;
        }
        return userAddrMapper.findById(id);
    }


}
