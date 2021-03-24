package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.model.enums.SearchRssSourceType;
import com.highestpeak.dimlight.model.params.DeleteRssParams;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.params.validation.JsonValidator;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.service.RSSSourceService;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    @PostMapping("/json")
    public Object rssImportByJson(@JsonValidator @RequestBody String json) {
        // file string
        return rssSourceService.addRssSourceFromJson(json);
    }

    @GetMapping("/json")
    public Object rssExportJson(HttpServletResponse response) {
        try {
            response.getWriter().write(rssSourceService.exportRssSourceAsJson().toString());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
        return null;
    }

    /**
     * 可测试 opml 列表
     * https://raw.githubusercontent.com/lotosbin/opml-list/master/generate/all.opml
     * https://gist.github.com/webpro/5907452
     */
    @PostMapping("/opml")
    public Object rssImportByOpml(
            @RequestParam("file") MultipartFile opmlFile,
            @RequestParam("string") String opmlString,
            @RequestParam("urls") List<String> opmlUrls
    ) {
        ErrorMessages errorMessages = new ErrorMessages();
        if (opmlFile == null || opmlFile.isEmpty()) {
            return "file is empty";
        } else {
            File file = new File(Objects.requireNonNull(opmlFile.getOriginalFilename()));
            try {
                opmlFile.transferTo(file);
                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                errorMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document),"opml in file");
            } catch (IOException | DocumentException e) {
                // log exception
            }
        }

        if (StringUtils.isNotBlank(opmlString)) {
            SAXReader reader = new SAXReader();
            try {
                Document document = reader.read(opmlString);
                errorMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document),"opml in string");
            } catch (DocumentException e) {
                // log exception
            }
        }

        if (opmlUrls != null) {
            opmlUrls.parallelStream().map(s -> {
                try {
                    return new SAXReader().read(s);
                } catch (DocumentException e) {
                    // log exception
                    errorMessages.addMsg("opml in url read error: url:"+s);
                    return null;
                }
            }).filter(Objects::nonNull).forEach(document -> {
                errorMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document),"opml in url");
            });
        }

        return errorMessages;
    }

    @GetMapping("/opml")
    public Object rssExportOpml(HttpServletResponse response) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(response.getOutputStream(), format);
            writer.write(rssSourceService.exportRssSourceAsOpml());
            response.flushBuffer();
        } catch (Exception ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
        return null;
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
            return rssSourceService.getContentItems(pageNum, pageSize, titleUsers, titleParses, ids);
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
        return rssSourceService.fetchRSSFromParams(rssSourceParams);
    }
}
