package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.params.TaskParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.service.RSSSourceService;
import com.highestpeak.dimlight.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author highestpeak
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@RestController
@CrossOrigin
@RequestMapping("/api/rss/source")
public class RSSSourceApiController {
    @Autowired
    private RSSSourceService rssSourceService;
    @Autowired
    private TaskService taskService;

    @PostMapping("new")
    public Object newRSSSource(@Validated({TaskParams.NewTask.class}) @RequestBody RSSSourceParams rssSourceParams){
        // 根据 args 创建新的 RSSSource 并且插入数据库
        ErrorMessages msg = rssSourceService.newRSSSource(rssSourceParams);

        if (rssSourceParams.isCreateTask()){
            ErrorMessages taskMsg = taskService.saveRSSTask(rssSourceParams.getTaskParams());
            msg.mergeMsg(taskMsg);
        }

        return msg;
    }

    // 更新的时候也传过来 params 对象， 但是 id 另外传过来一个参数 学习
    // E:\_code\code_github\halo-master\src\main\java\run\halo\app\controller\admin\api\PostController.java
}
