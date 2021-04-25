package com.highestpeak.dimlight.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.highestpeak.dimlight.factory.MessageFactory;
import com.highestpeak.dimlight.model.params.GetListBodyParams;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.highestpeak.dimlight.model.enums.TopicAndTagSearchType;
import com.highestpeak.dimlight.model.params.TopicParams;
import com.highestpeak.dimlight.service.TopicService;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
@RestController
@CrossOrigin
@RequestMapping("/api/rss/topic")
public class TopicCrudController {
    @Resource
    private TopicService topicService;

    /**
     * 删除Topic
     */
    @DeleteMapping("/")
    public Object delTopic(@RequestParam("id") int id) {
        return topicService.deleteTopic(id);
    }

    /**
     * 更新Topic
     */
    @PutMapping
    public Object updateTopic(@Validated @RequestBody TopicParams topicParams) {
        return topicService.newOrUpdateTopic(topicParams);
    }

    /**
     * 新的Topic
     */
    @PostMapping
    public Object newTopic(@Validated @RequestBody TopicParams topicParams) {
        return topicService.newOrUpdateTopic(topicParams);
    }

    /**
     * 获取所有的Topic
     */
    @GetMapping("/all")
    public Object getAllTopic(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize) {
        return topicService.getTopicList(pageNum, pageSize);
    }

    /**
     * 获取指定id的Topic
     */
    @GetMapping("/id")
    public Object getTopicById(@RequestParam("id") String id) {
        try {
            return topicService.getTopicById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return MessageFactory.PARAMETER_ERROR_MSG;
        }
    }

    /**
     * 搜索指定内容的Topic
     * todo
     */
    @GetMapping("/search")
    public Object searchTag(@RequestParam("content") String content) {
        try {
            return topicService.searchByContent(content);
        } catch (Exception e) {
            return new InfoMessages(InfoMessages.buildExceptionMsg("服务器发生错误", e));
        }
    }

    /**
     * 废弃
     */
    @Deprecated
    @PostMapping("/get")
    public Object getTopic(@RequestBody GetListBodyParams getListBodyParams) {
        int pageSize = getListBodyParams.getPageSize();
        int pageNum = getListBodyParams.getPageNum();
        int type = getListBodyParams.getType();
        Map<String, Object> typeValue = getListBodyParams.getTypeValue();

        if (type == TopicAndTagSearchType.NORMAL_LIST.getValue() || type== TopicAndTagSearchType.NAME.getValue()) {
            return topicService.getTopicList(pageNum, pageSize);
        }
        if (type== TopicAndTagSearchType.RSS_SOURCES.getValue()){
            List<String> topicNames = getParamsValueList(typeValue.get("names"));
            return topicService.getRssSourceByTopicName(pageNum,pageSize,topicNames);
        }
        if (type== TopicAndTagSearchType.CONTENT_ITEMS.getValue()){
            List<String> topicNames = getParamsValueList(typeValue.get("names"));
            return topicService.getContentItemsByTopicName(pageNum,pageSize,topicNames);
        }
        return null;
    }

    @GetMapping("/topic_rss_group")
    public Object getTopicRssGroup() {
        return topicService.getTopicRssGroup();
    }

    private <T> List<T> getParamsValueList(Object paramsValue) {
        if (paramsValue.getClass().isArray()) {
            return (List<T>) Arrays.asList(paramsValue);
        }
        return new ArrayList<>();
    }
}
