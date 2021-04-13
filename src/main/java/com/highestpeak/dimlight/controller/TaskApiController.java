package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.model.params.DeleteTaskParams;
import com.highestpeak.dimlight.model.params.GetListBodyParams;
import com.highestpeak.dimlight.model.params.OtherTaskParams;
import com.highestpeak.dimlight.model.params.RssFetchTaskParams;
import com.highestpeak.dimlight.service.TaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/rss/task")
public class TaskApiController {
    @Resource
    private TaskService taskService;
    
    @DeleteMapping("/${url.token}")
    public Object delTask(@Validated @RequestBody DeleteTaskParams taskParams) {
        return taskService.deleteTask(taskParams);
    }

    @PutMapping
    public Object updateTask(@Validated @RequestBody RssFetchTaskParams rssFetchTaskParams) {
        return taskService.newOrUpdateRssTask(rssFetchTaskParams);
    }

    @PutMapping("/update_all_rss")
    public Object allRssFetchNow(){
        return taskService.allRssFetchNow();
    }

    /**
     * 仅仅是rss抓取task
     */
    @PostMapping
    public Object newRssTask(@Validated @RequestBody RssFetchTaskParams rssFetchTaskParams) {
        return taskService.newOrUpdateRssTask(rssFetchTaskParams);
    }

    /**
     * 对指定rss更新处理链需要在rssSource更新处更新json信息
     * future: 其他task：例如
     *  3. 定时删除过期item的task
     *  2. 定时统计的task，以便于调整抓取参数---频率,计算权重从而便于之后依据权重进行排序
     */
    @PostMapping("/other")
    public Object newOtherTask(@Validated @RequestBody OtherTaskParams taskParams) {
        return null;
    }

    /**
     * 现阶段先返回所有Task
     */
    @PostMapping("/get")
    public Object getTask(@RequestBody GetListBodyParams getListBodyParams) {
        //int pageSize = getListBodyParams.getPageSize();
        //int pageNum = getListBodyParams.getPageNum();
        //int type = getListBodyParams.getType();
        //Map<String, Object> typeValue = getListBodyParams.getTypeValue();
        //
        //if (type == TaskSearchType.NORMAL_LIST.getValue()) {
        //    return taskService.getTaskList(pageNum, pageSize);
        //}
        return taskService.getAllTask();
    }

    private <T> List<T> getParamsValueList(Object paramsValue) {
        if (paramsValue.getClass().isArray()) {
            return (List<T>) Arrays.asList(paramsValue);
        }
        return new ArrayList<>();
    }
}
