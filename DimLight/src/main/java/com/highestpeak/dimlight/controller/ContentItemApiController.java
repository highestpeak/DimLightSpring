package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.service.ContentItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin
@RequestMapping("/api/content_item/")
public class ContentItemApiController {

    @Resource
    private ContentItemService contentItemService;

    /**
     * 获取所有的RSSContentItem
     */
    @GetMapping("/target_topic")
    public Object getContentItemList(@RequestParam("topicId") int topicId,@RequestParam("num") int num) {
        return contentItemService.getTargetRssContentItem(topicId, num);
    }

}
