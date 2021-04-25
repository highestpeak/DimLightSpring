package com.highestpeak.dimlight.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.service.RssContentItemService;
import com.highestpeak.dimlight.service.RSSSourceService;
import com.highestpeak.dimlight.service.TopicService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/api/android/")
@Deprecated
public class AndroidController {
    @Resource
    private RSSSourceService rssSourceService;
    @Resource
    private RssContentItemService rssContentItemService;
    @Resource
    private TopicService topicService;

    @GetMapping("android_rss_info")
    public Object androidRssInfo() {
        try {
            return rssSourceService.exportRssSourceAsJson();
        } catch (JsonProcessingException e) {
            return new InfoMessages("Json处理异常");
        }
    }

    @GetMapping("android_feed_item")
    public Object androidFeedItem() {
        return rssContentItemService.getAll();
    }

    @GetMapping("android_topic_rss_group")
    public Object androidTopicRssGroup(){
        return topicService.getTopicRssGroup();
    }

}
