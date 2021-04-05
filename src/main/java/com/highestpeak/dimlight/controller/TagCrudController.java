package com.highestpeak.dimlight.controller;

import com.google.common.collect.Lists;
import com.highestpeak.dimlight.model.enums.TopicAndTagSearchType;
import com.highestpeak.dimlight.model.params.DeleteTagParams;
import com.highestpeak.dimlight.model.params.GetListBodyParams;
import com.highestpeak.dimlight.model.params.TagParams;
import com.highestpeak.dimlight.service.TagService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/rss/tag")
public class TagCrudController {

    @Resource
    private TagService tagService;

    @DeleteMapping("/${url.token}")
    public Object delTag(@Validated @RequestBody DeleteTagParams tagParams) {
        return tagService.deleteTag(tagParams);
    }

    @PutMapping
    public Object updateTag(@Validated @RequestBody TagParams tagParams) {
        return tagService.newOrUpdateTag(tagParams);
    }

    @PostMapping
    public Object newTag(@Validated @RequestBody TagParams tagParams) {
        return tagService.newOrUpdateTag(tagParams);
    }

    @PostMapping("/get")
    public Object getTag(@RequestBody GetListBodyParams getListBodyParams) {
        int pageSize = getListBodyParams.getPageSize();
        int pageNum = getListBodyParams.getPageNum();
        int type = getListBodyParams.getType();
        Map<String, Object> typeValue = getListBodyParams.getTypeValue();

        if (type == TopicAndTagSearchType.NORMAL_LIST.getValue() || type==TopicAndTagSearchType.NAME.getValue()) {
            return tagService.getTagList(pageNum, pageSize);
        }
        if (type==TopicAndTagSearchType.RSS_SOURCES.getValue()){
            List<String> topicNames = getParamsValueList(typeValue.get("names"));
            return tagService.getRssSourceByTagName(pageNum,pageSize,topicNames);
        }
        if (type==TopicAndTagSearchType.CONTENT_ITEMS.getValue()){
            List<String> topicNames = getParamsValueList(typeValue.get("names"));
            return tagService.getContentItemsByTagName(pageNum,pageSize,topicNames);
        }
        return null;
    }

    @GetMapping("/waiting_read")
    public Object waitingRead() {
        int pageSize = 100;
        int pageNum = 0;
        return tagService.getContentItemsByTagName(pageNum,pageSize, Lists.newArrayList("稍后再读"));
    }

    private <T> List<T> getParamsValueList(Object paramsValue) {
        if (paramsValue.getClass().isArray()) {
            return (List<T>) Arrays.asList(paramsValue);
        }
        return new ArrayList<>();
    }
}
