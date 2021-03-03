package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author highestpeak
 * todo websocket listen to task status https://spring.io/guides/gs/messaging-stomp-websocket/
 *  连接前后端时再写这个
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@RestController
@CrossOrigin
@RequestMapping("/task/api")
public class TaskApiController {
    @Autowired
    private TaskService taskService;

    @PostMapping("new_rss_listener_task")
    public Object newRSSListenerTask(@RequestBody Map<String, Object> taskArgs){
        return null;
    }
}
