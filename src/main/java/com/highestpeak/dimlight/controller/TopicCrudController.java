package com.highestpeak.dimlight.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 * todo
 */
@RestController
@CrossOrigin
@RequestMapping("/rss/api/topic")
public class TopicCrudController {
    @DeleteMapping
    public Object delTopic(){
        return null;
    }

    @GetMapping
    public Object getTopic(){
        return null;
    }

    @PutMapping
    public Object updateTopic(){
        return null;
    }

    @PostMapping
    public Object postTopic(){
        return null;
    }
}
