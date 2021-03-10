package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.model.params.DeleteParams;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.service.RSSSourceService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @GetMapping("/es")
    public Object esTest(){
        SearchRequest searchRequest;
        BoolQueryBuilder outerQuery = QueryBuilders.boolQuery();
        return null;
    }

    /**
     * 下面这个是个固定的key(假装有一个key)
     */
    @DeleteMapping("/s86h2xd93j")
    public Object delRSSSource(@Validated @RequestBody DeleteParams deleteParams){
        return rssSourceService.deleteRSSSource(deleteParams);
    }

    @PutMapping
    public Object updateRSSSource(@Validated @RequestBody RSSSourceParams rssSourceParams){
        return rssSourceService.newOrUpdateRSSSource(rssSourceParams);
    }

    @PostMapping
    public Object newRSSSource(@Validated @RequestBody RSSSourceParams rssSourceParams){
        return rssSourceService.newOrUpdateRSSSource(rssSourceParams);
    }

    /**
     * android 获取 rssSource
     * 返回去掉了 contentItem 的数据库 entity 就行了
     */
    @GetMapping
    public Object getRSSSource(){

        return null;
    }

    /**
     * 暂时采取 huginn 触发抓取
     * huginn 触发抓取后，这些抓取到内容就保存在本地，但是会加一个没有完成的标识，之后Process模块直接从本地读取数据就可以了
     * 采用 spring 抓取而不是 huginn 抓取，是因为这样的话可以方便的修改 rssSource 的信息，并且可以减少串数数据量
     */
    @PostMapping("huginn/emit/fetch")
    public Object emitItemFetch(@Validated List<RSSSourceParams> rssSourceParams){
        return rssSourceService.fetchRSS(rssSourceParams);
    }
}
