package com.ai.serviceuser.controller;


import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.core.vo.user.TaskVO;
import com.ai.serviceuser.handler.TaskHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "任务")
@RequestMapping("/task")
public class TaskController {


    @Autowired
    private TaskHandler taskHandler;


    @Operation(summary = "查询任务",description = "")
    @GetMapping("/select")
    public BaseVO select() throws Exception{
        List<TaskVO> result = taskHandler.select();
        return BaseVO.ok(result);
    }

    @Operation(summary = "查询云豆任务完成数量",description = "")
    @GetMapping("/countGoldTask")
    public BaseVO countGoldTask() throws Exception{
        Integer result = taskHandler.countGoldTask();
        return BaseVO.ok(result);
    }







}
