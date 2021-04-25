package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.factory.MessageFactory;
import com.highestpeak.dimlight.service.ProcessService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@CrossOrigin
@RequestMapping("/api/process")
public class ProcessController {
    @Resource
    private ProcessService processService;

    /**
     * 返回所有可选的 process
     */
    @GetMapping("optional")
    public Object optionalProcessList() {
        return processService.optionalProcessNames();
    }

    /**
     * 拉取所有RSS源
     */
    @PutMapping("/fetch_all_rss_now")
    public Object fetchRssNow(){
        return processService.fetchAllRssNow();
    }

    /**
     * 拉取指定RSS源
     * future: 这个不应该用get，但是时间所迫，就先这样了
     */
    @GetMapping("/fetch_rss_now")
    public Object fetchRssNow(@RequestParam("id") String id){
        try {
            return processService.fetchRssNow(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return MessageFactory.PARAMETER_ERROR_MSG;
        }
    }

}
