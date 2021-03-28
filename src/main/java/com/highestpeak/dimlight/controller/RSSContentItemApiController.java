package com.highestpeak.dimlight.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.highestpeak.dimlight.service.ContentItemService;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin
@RequestMapping("/api/rss/content_item")
public class RSSContentItemApiController {

    @Autowired
    private ContentItemService contentItemService;

    /**
     * 由 web 前端页面控制台触发，删除指定的 ContentItem
     */
    @DeleteMapping("/${url.token}")
    public Object delContentItem(List<Integer> delIdList){
        contentItemService.delContentByIdList(delIdList);
        return null;
    }

    /**
     * huginn 定时获取数量来删除过多的数量,如果过多就触发删除 contentItem
     * 删除可以有不同的逻辑和表达式
     * 例如 超过 默认/专属 设定的最大保存时间就删除
     */
    @PutMapping("count_whether_need_del")
    public Object countItemWhetherDel(String earliestTimeToLive){
        contentItemService.delContentOutOfTime(earliestTimeToLive);
        return null;
    }

    /**
     * android 端获取 contentItem
     * 所有的 item 列表
     */
    @GetMapping("list")
    public Object getContentItemList(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize){
        return contentItemService.getContentItemList(pageNum, pageSize);
    }
}
