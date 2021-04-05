package com.highestpeak.dimlight.controller;

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
}
