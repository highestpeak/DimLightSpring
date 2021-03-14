package com.highestpeak.dimlight.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.highestpeak.dimlight.model.enums.SearchRssSourceType;
import com.highestpeak.dimlight.model.params.DeleteRssParams;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.service.RSSSourceService;

/**
 * @author highestpeak
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@RestController
@CrossOrigin
@RequestMapping("/api/rss/source")
public class RSSSourceApiController {
    @Autowired
    private RSSSourceService rssSourceService;

    @GetMapping("/es")
    public Object esTest() {
        // feature
        SearchRequest searchRequest;
        BoolQueryBuilder outerQuery = QueryBuilders.boolQuery();
        return null;
    }

    /**
     * 下面这个是个固定的key(假装有一个key)
     */
    @DeleteMapping("/s86h2xd93j")
    public Object delRSSSource(@Validated @RequestBody DeleteRssParams deleteRssParams) {
        return rssSourceService.deleteRSSSource(deleteRssParams);
    }

    @PutMapping
    public Object updateRSSSource(@Validated @RequestBody RSSSourceParams rssSourceParams) {
        return rssSourceService.newOrUpdateRSSSource(rssSourceParams);
    }

    @PostMapping
    public Object newRSSSource(@Validated @RequestBody RSSSourceParams rssSourceParams) {
        return rssSourceService.newOrUpdateRSSSource(rssSourceParams);
    }

    /**
     * 1. 单纯返回某个page的List
     * 2. 返回符合titleUser列表或者titleParse列表的集合
     * 3. 返回给定id集合/title集合rssSource的所有contentItem，按照page分页
     */
    @GetMapping
    public Object getRSSSource(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize,
            @RequestParam(value = "searchType", defaultValue = "1") int type,
            @RequestParam("typeValue") Map<String, Object> typeValue) {
        if (type == SearchRssSourceType.NORMAL_LIST.getValue()) {
            return rssSourceService.getRSSList(pageNum, pageSize);
        }
        if (type == SearchRssSourceType.TITLE.getValue()) {
            List<String> titleUsers = getParamsValueList(typeValue.get("titleUsers"));
            List<String> titleParses = getParamsValueList(typeValue.get("titleParses"));
            return rssSourceService.getRSSListByTitle(pageNum, pageSize, titleUsers, titleParses);
        }
        //        if (type == SearchRssSourceType.FULL_TEXT_SEARCH.getValue()) {
        //            return rssSourceService.getRSSList(pageNum, pageSize);
        //        }
        if (type == SearchRssSourceType.CONTENT_ITEMS.getValue()) {
            List<String> titleUsers = getParamsValueList(typeValue.get("titleUsers"));
            List<String> titleParses = getParamsValueList(typeValue.get("titleParses"));
            List<Integer> ids = getParamsValueList(typeValue.get("ids"));
            return rssSourceService.getContentItems(pageNum, pageSize,titleUsers,titleParses,ids);
        }
        return null;
    }

    private <T> List<T> getParamsValueList(Object paramsValue) {
        if (paramsValue.getClass().isArray()) {
            return (List<T>) Arrays.asList(paramsValue);
        }
        return new ArrayList<>();
    }

    /**
     * 暂时采取 huginn 触发抓取
     * huginn 触发抓取后，这些抓取到内容就保存在本地，但是会加一个没有完成的标识，之后Process模块直接从本地读取数据就可以了
     * 采用 spring 抓取而不是 huginn 抓取，是因为这样的话可以方便的修改 rssSource 的信息，并且可以减少串数数据量
     */
    @PostMapping("huginn/emit/fetch")
    public Object emitItemFetch(@Validated List<RSSSourceParams> rssSourceParams) {
        return rssSourceService.fetchRSS(rssSourceParams);
    }
}
