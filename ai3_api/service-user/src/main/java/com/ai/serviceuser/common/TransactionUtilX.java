package com.ai.serviceuser.common;

import com.ai.basecommon.core.dto.TransactionResultDTO;
import com.ai.basecommon.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class TransactionUtilX {

    @Autowired
    private TransactionTemplate transactionTemplate;



    /**

     TransactionResultDTO transactionResultDTO = transactionUtilX.execute(()->{
        userMapper.updateLevel(userId, num);
     });
     if (!transactionResultDTO.isSuccess()) {
        LogUtil.log("⚠️ 事务回滚 : " + transactionResultDTO.getMessage());
     }

     */

    public TransactionResultDTO execute(Runnable task) {
        return transactionTemplate.execute(status -> {
            try {
                task.run();
                return new TransactionResultDTO(true, "事务执行成功");
            } catch (Exception e) {
                status.setRollbackOnly();
                String errorMessage = "事务执行失败：" + e.getMessage();
                LogUtil.log(errorMessage);
                return new TransactionResultDTO(false, errorMessage);
            }
        });
    }


    /**
     Long userId = transactionUtilX.executeReturn(() -> {
     User user = new User();
     user.setName("张三");
     userRepository.save(user);
     return user.getId();
     });
     */
    public <T> T executeReturn(Supplier<T> task) {
        return transactionTemplate.execute(status -> {
            try {
                return task.get();
            } catch (Exception e) {
                status.setRollbackOnly();
                LogUtil.log("事务执行失败：" + e.getMessage());
                throw e;
            }
        });
    }



}
