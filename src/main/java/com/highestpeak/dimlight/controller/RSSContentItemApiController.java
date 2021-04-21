package com.highestpeak.dimlight.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.*;

import com.highestpeak.dimlight.service.RssContentItemService;

import javax.annotation.Resource;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin
@RequestMapping("/api/rss/content_item")
public class RSSContentItemApiController {

    @Resource
    private RssContentItemService rssContentItemService;

    /**
     * 删除指定的 ContentItem
     */
    @DeleteMapping("/")
    public Object delContentItem(Integer delId) {
        return rssContentItemService.delContentByIdList(delId);
    }

    /**
     * 删除此时间点之前的所有的RSSContentItem
     */
    @PutMapping("/del_before")
    public Object delContentItemBefore(@RequestParam("delBarrier") Date delBarrier) {
        return rssContentItemService.delContentItemBefore(delBarrier);
    }

    /**
     * 删除指定RSS的contentItem
     * future: getmapping to putmapping
     */
    @GetMapping("/del_target_rss")
    public Object delTargetRssContentItem(@RequestParam("id") int id) {
        return rssContentItemService.delTargetRssContentItem(id);
    }

    /**
     * 获取所有的RSSContentItem
     */
    @GetMapping("/list")
    public Object getContentItemList(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return rssContentItemService.getContentItemList(pageNum, pageSize);
    }

    /**
     * 获取特定RSS的RSSContentItem
     */
    @GetMapping("/target_rss")
    public Object getTargetRssContentItem(@RequestParam("rssId") int rssId,@RequestParam("num") int num) {
        return rssContentItemService.getTargetRssContentItem(rssId, num);
    }
}
