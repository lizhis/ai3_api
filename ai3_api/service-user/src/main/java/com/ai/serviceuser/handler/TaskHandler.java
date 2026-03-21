package com.ai.serviceuser.handler;

import com.ai.basecommon.constants.RedisKey;
import com.ai.basecommon.core.po.user.TaskPO;
import com.ai.basecommon.core.po.user.UserTaskPO;
import com.ai.basecommon.core.vo.user.TaskVO;
import com.ai.basecommon.enums.StatusEnum;
import com.ai.basecommon.enums.TaskGiveTypeEnum;
import com.ai.basecommon.enums.TaskTypeEnum;
import com.ai.basecommon.utils.DateUtil;
import com.ai.basecommon.utils.LogUtil;
import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.common.UserUtilX;
import com.ai.serviceuser.config.db.ReadOnly;
import com.ai.serviceuser.mapper.TaskMapper;
import com.ai.serviceuser.mapper.UserTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description
 * @Author
 */
@Component
public class TaskHandler {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserTaskMapper userTaskMapper;

    @Autowired
    private UserUtilX userUtilX;

    @Autowired
    private RedisUtilX redisUtilX;


    @ReadOnly
    public List<TaskVO> select() throws Exception{

        List<TaskVO> list = new ArrayList<>();
        List<TaskPO> pos = new ArrayList<>();


        String taskAllKey = RedisKey.task_all_key;
        List<TaskPO> taskAllList = new ArrayList<>();
        if(redisUtilX.hasKey(taskAllKey)){
            taskAllList = redisUtilX.getObjList(taskAllKey,TaskPO.class);
        }
        if(null == taskAllList || taskAllList.isEmpty()){
            taskAllList = taskMapper.selectAll();
            if(null != taskAllList && !taskAllList.isEmpty()){
                redisUtilX.setObj(taskAllKey,taskAllList,600);
            }
        }
        if(null == taskAllList || taskAllList.isEmpty()){
            return list;
        }


        Long userId = userUtilX.getUserIdNotError();
        if(null == userId){
            pos = taskAllList.stream().filter(v->v.getDays() >= 1).limit(3).toList();
        }
        else{
            //查询该用户最后一个任务ID，用ID查该任务信息，
            UserTaskPO userTaskPO = userTaskMapper.findLast(userId);
            if(null == userTaskPO){
                pos = taskAllList.stream().filter(v->v.getDays() >= 1).limit(3).toList();
            }
            else {
                TaskPO taskPO = taskAllList.stream().filter(v->v.getId().equals(userTaskPO.getTaskId())).findAny().orElse(null);
                if(null == taskPO){
                    userTaskMapper.delete(userTaskPO.getId());
                    return this.select();
                }
                if(!StatusEnum.YES.getCode().equals(userTaskPO.getStatus())){
                    pos = taskAllList.stream().filter(v->v.getDays() >= taskPO.getDays()).limit(3).toList();
                }
                else{
                    Integer today = DateUtil.todayDate();
                    if(today.equals(userTaskPO.getYmd())){
                        pos = taskAllList.stream().filter(v->v.getDays() >= taskPO.getDays()).limit(3).toList();
                    }
                    else{
                        int countAll= (int) taskAllList.stream().filter(v-> Objects.equals(v.getDays(), taskPO.getDays())).count();
                        int countFinish = userTaskMapper.countFinishByDays(userId,taskPO.getDays());
                        if(countFinish < countAll){
                            pos = taskAllList.stream().filter(v->v.getDays() >= taskPO.getDays()).limit(3).toList();
                        }
                        else{
                            pos = taskAllList.stream().filter(v->v.getDays() >= (taskPO.getDays()+1)).limit(3).toList();
                        }
                    }
                }
            }
        }
        if(pos.isEmpty()){
            return list;
        }

        List<UserTaskPO> userTaskPOS = new ArrayList<>();
        if(null != userId){
            List<Long> taskIds = pos.stream().map(TaskPO::getId).toList();
            userTaskPOS = userTaskMapper.selectByUserIdTaskIds(userId,taskIds);
        }

        Integer currentDay = pos.get(0).getDays();

        for(TaskPO po : pos){
            Long taskId = po.getId();
            UserTaskPO userTaskPO = userTaskPOS.stream().filter(v->v.getTaskId().equals(taskId)).findFirst().orElse(null);

            Integer progress = 0;
            Integer status = 0;
            if(null != userTaskPO){
                //做过  做完成了1  完成一部分3
                progress = userTaskPO.getProgress();
                status = userTaskPO.getStatus();
            }
            else{
                //没做过  是今天的？ 3   不是今天的  2
                if(currentDay.equals(po.getDays())){
                    status = 3;
                }
                else{
                    status = 2;
                }
            }


            TaskVO vo = new TaskVO();
            vo.setDays(po.getDays());
            vo.setNumber(po.getNumber());
            vo.setTitle(po.getTitle());
            vo.setContent(po.getContent());
            vo.setTaskDescImg(po.getTaskDescImg());
            vo.setTaskDesc(po.getTaskDesc());
            Integer progressAll = 1;
            if(TaskTypeEnum.INVITE.getCode().equals(po.getType()) || TaskTypeEnum.STEP_GOLD_WATER.getCode().equals(po.getType()) || TaskTypeEnum.AI_CHAT.getCode().equals(po.getType()) || TaskTypeEnum.RECHARGE.getCode().equals(po.getType()) || TaskTypeEnum.STEP_GOLD.getCode().equals(po.getType())){
                try{
                    progressAll = Integer.valueOf(po.getContent());
                }catch (Exception e){
                    LogUtil.log(e.getMessage());
                }
            }
            vo.setProgressAll(progressAll);
            vo.setProgress(progress);
            vo.setStatus(status);


            vo.setGiveType(po.getGiveType());
            if(TaskGiveTypeEnum.MONEY.getCode().equals(po.getGiveType())){
                vo.setGiveContent(po.getGiveContent());
            }
            if(TaskGiveTypeEnum.SHOP.getCode().equals(po.getGiveType())){
                vo.setGiveContent(po.getGiveContent());
                /*try{
                    Long id = Long.valueOf(po.getGiveContent());
                    vo.setGiveContent(shopMapper.findNameById(id));
                }catch (Exception e){
                    LogUtil.log(e.getMessage());
                }*/
            }
            list.add(vo);
        }
        return list;

    }

    //查询本轮云豆任务已完成数量
    @ReadOnly
    public Integer countGoldTask() throws Exception{
        Long userId = userUtilX.getUserId();

        UserTaskPO userTaskPO = userTaskMapper.findLastGold(userId);
        int c = 0;
        if(null == userTaskPO){
            c = userTaskMapper.countByUserId(userId);
        }
        else{
            c = userTaskMapper.countPassId(userId,userTaskPO.getId());
        }
        return c;
    }



}
