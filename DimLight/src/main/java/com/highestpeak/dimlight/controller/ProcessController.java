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

    @GetMapping("/rss_item_summary")
    public Object summaryExtractOfRss(@RequestParam("rssId") String rssId) {
        return processService.summaryExtractOfRss(rssId);
    }

    @GetMapping("/rss_item_title_word_segment")
    public Object titleWordSegment(@RequestParam("rssId") String rssId) {
        return processService.titleWordSegment(rssId);
    }

    @GetMapping("/rss_item_desc_word_segment")
    public Object descWordSegment(@RequestParam("rssId") String rssId) {
        return processService.descWordSegment(rssId);
    }

    @GetMapping("/rss_item_html_tag_remove")
    public Object rssHtmlTagRemove(@RequestParam("rssId") String rssId) {
        return processService.rssHtmlTagRemove(rssId);
    }

    @GetMapping("/rss_item_regx_filter")
    public Object rssItemRegxFilter(@RequestParam("rssId") String rssId) {
        return processService.rssItemRegxFilter(rssId);
    }

    @GetMapping("/topic_duplicate_remove")
    public Object topicDuplicateRemove(@RequestParam("topicId") String topicId) {
        return processService.topicDuplicateRemove(topicId);
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
