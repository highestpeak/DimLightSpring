package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.model.params.*;
import com.highestpeak.dimlight.service.TaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/task")
public class TaskApiController {
    @Resource
    private TaskService taskService;

    /**
     * 删除task并停止任务
     */
    @DeleteMapping("/")
    public Object delTask(@RequestParam("id") int id) {
        return taskService.deleteTask(id);
    }

    /**
     * 更新task
     */
    @PutMapping
    public Object updateTask(@Validated(BaseTaskParams.Update.class) @RequestBody RssFetchTaskParams rssFetchTaskParams) {
        return taskService.newOrUpdateRssTask(rssFetchTaskParams);
    }

    /**
     * 新的RSS抓取Task
     */
    @PostMapping("/rss_task/new")
    public Object newRssTask(@Validated @RequestBody RssFetchTaskParams rssFetchTaskParams) {
        return taskService.newOrUpdateRssTask(rssFetchTaskParams);
    }

    /**
     * 定期删除过期Item的Task
     * todo rss的id或者爬虫的id，或者topic的id
     */
    @PostMapping("/schedule_del_task")
    public Object scheduleDelTask() {
        return null;
    }

    /**
     * 现阶段先返回所有Task
     */
    @GetMapping("/all")
    public Object getTask(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize) {
        return taskService.getAllTask(pageNum, pageSize);
    }

}
