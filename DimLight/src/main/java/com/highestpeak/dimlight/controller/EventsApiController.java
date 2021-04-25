package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.service.EventService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/api/events/")
public class EventsApiController {
    @Resource
    private EventService eventService;

    /**
     * 返回所有Events
     */
    @GetMapping("/all")
    public Object getEvents(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        return eventService.getEventList(pageNum, pageSize);
    }

    /**
     * 指定RSS的Events
     */
    @GetMapping("/rss")
    public Object getRssEvents(@RequestParam("rssId") int rssId) {
        return eventService.getRssEvents(rssId);
    }

    /**
     * 清除所有的Events
     */
    @PutMapping("/clear_all")
    public Object clearEvents() {
        return eventService.clearAllEvents();
    }

    /**
     * 清除特定RSS的Events
     */
    @PutMapping("/clear_target_rss_event")
    public Object clearTargetRssEvent(@RequestParam("rssId") int rssId) {
        return eventService.clearTargetRssEvent(rssId);
    }

    /**
     * 清除特定Topic的Events
     */
    @PutMapping("/clear_target_topic_event")
    public Object clearTargetTopicEvent(@RequestParam("topicId") int topicId) {
        return eventService.clearTargetTopicEvent(topicId);
    }

    /**
     * 清除特定Task的Events
     */
    @PutMapping("/clear_target_task_event")
    public Object clearTargetTaskEvent(@RequestParam("taskId") int taskId) {
        return eventService.clearTargetTaskEvent(taskId);
    }
}
