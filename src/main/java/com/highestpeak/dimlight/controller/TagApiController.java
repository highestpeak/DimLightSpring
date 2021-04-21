package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.factory.MessageFactory;
import com.highestpeak.dimlight.model.enums.TopicAndTagSearchType;
import com.highestpeak.dimlight.model.params.GetListBodyParams;
import com.highestpeak.dimlight.model.params.TagParams;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
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
@RequestMapping(value = {"/api/rss/tag", "/api/tag"})
public class TagApiController {

    @Resource
    private TagService tagService;

    /**
     * 删除tag
     */
    @DeleteMapping("/")
    public Object delTag(@RequestParam("id") int id) {
        return tagService.deleteTag(id);
    }

    /**
     * 更新Tag
     */
    @PutMapping
    public Object updateTag(@Validated @RequestBody TagParams tagParams) {
        return tagService.newOrUpdateTag(tagParams);
    }

    /**
     * 新Tag
     */
    @PostMapping
    public Object newTag(@Validated @RequestBody TagParams tagParams) {
        return tagService.newOrUpdateTag(tagParams);
    }

    /**
     * 所有tag分页
     */
    @GetMapping("/all")
    public Object getAllTag(@RequestParam("pageNum") int pageNum,@RequestParam("pageSize") int pageSize) {
        try {
            return tagService.getTagList(pageNum, pageSize);
        } catch (Exception e) {
            return new InfoMessages(InfoMessages.buildExceptionMsg("服务器发生错误", e));
        }
    }

    /**
     * 返回指定id的tag
     */
    @GetMapping("/id")
    public Object getTagById(@RequestParam("id") String id) {
        try {
            return tagService.getTagById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return MessageFactory.PARAMETER_ERROR_MSG;
        }
    }

    /**
     * 搜索指定内容的Tag
     * todo
     */
    @GetMapping("/search")
    public Object searchTag(@RequestParam("content") String content) {
        try {
            return tagService.searchByContent(content);
        } catch (Exception e) {
            return new InfoMessages(InfoMessages.buildExceptionMsg("服务器发生错误", e));
        }
    }

    /**
     * 废弃
     */
    @PostMapping("/get")
    @Deprecated
    public Object getTag(@RequestBody GetListBodyParams getListBodyParams) {
        int pageSize = getListBodyParams.getPageSize();
        int pageNum = getListBodyParams.getPageNum();
        int type = getListBodyParams.getType();
        Map<String, Object> typeValue = getListBodyParams.getTypeValue();

        if (type == TopicAndTagSearchType.NORMAL_LIST.getValue() || type == TopicAndTagSearchType.NAME.getValue()) {
            return tagService.getTagList(pageNum, pageSize);
        }
        if (type == TopicAndTagSearchType.RSS_SOURCES.getValue()) {
            List<String> topicNames = getParamsValueList(typeValue.get("names"));
            return tagService.getRssSourceByTagName(pageNum, pageSize, topicNames);
        }
        if (type == TopicAndTagSearchType.CONTENT_ITEMS.getValue()) {
            List<String> topicNames = getParamsValueList(typeValue.get("names"));
            return tagService.getContentItemsByTagName(pageNum, pageSize, topicNames);
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
