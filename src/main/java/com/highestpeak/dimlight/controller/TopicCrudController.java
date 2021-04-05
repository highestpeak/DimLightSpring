package com.highestpeak.dimlight.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.highestpeak.dimlight.model.params.GetListBodyParams;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.highestpeak.dimlight.model.enums.TopicAndTagSearchType;
import com.highestpeak.dimlight.model.params.DeleteTopicParams;
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

    @DeleteMapping("/${url.token}")
    public Object delTopic(@Validated @RequestBody DeleteTopicParams topicParams) {
        return topicService.deleteTopic(topicParams);
    }

    @PutMapping
    public Object updateTopic(@Validated @RequestBody TopicParams topicParams) {
        return topicService.newOrUpdateTopic(topicParams);
    }

    @PostMapping
    public Object newTopic(@Validated @RequestBody TopicParams topicParams) {
        return topicService.newOrUpdateTopic(topicParams);
    }

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

    private <T> List<T> getParamsValueList(Object paramsValue) {
        if (paramsValue.getClass().isArray()) {
            return (List<T>) Arrays.asList(paramsValue);
        }
        return new ArrayList<>();
    }
}
