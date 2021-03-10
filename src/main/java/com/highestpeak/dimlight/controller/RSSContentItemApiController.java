package com.highestpeak.dimlight.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin
@RequestMapping("/api/rss/content_item")
public class RSSContentItemApiController {
    /**
     * 由 web 前端页面控制台触发，删除指定的 ContentItem
     */
    @DeleteMapping
    public Object delContentItem(){
        return null;
    }

    /**
     * huginn 定时获取数量来删除过多的数量,如果过多就触发删除 contentItem
     * 删除可以有不同的逻辑和表达式
     * 例如 超过 默认/专属 设定的最大保存时间就删除
     */
    @PutMapping("count_whether_need_del")
    public Object countItemWhetherDel(){
        return null;
    }

    /**
     * android 端获取 contentItem
     */
    @GetMapping("list")
    public Object getContentItemList(){
        return null;
    }
}
