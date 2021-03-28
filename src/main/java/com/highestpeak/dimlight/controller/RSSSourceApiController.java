package com.highestpeak.dimlight.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.highestpeak.dimlight.model.enums.SearchRssSourceType;
import com.highestpeak.dimlight.model.params.DeleteRssParams;
import com.highestpeak.dimlight.model.params.GetListBodyParams;
import com.highestpeak.dimlight.model.params.OpmlAddParams;
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
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
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

    /**
     * 下面这个是个固定的key(假装有一个key)
     */
    @DeleteMapping("/${url.token}")
    public Object delRSSSource(@RequestBody DeleteRssParams deleteRssParams) {
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
    public Object rssImportByJson(@JsonValidator @RequestBody String json) throws JsonProcessingException {
        // file string
        return rssSourceService.addRssSourceFromJson(json);
    }

    @GetMapping("/json")
    public Object rssExportJson(HttpServletResponse response) {
        try {
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(rssSourceService.exportRssSourceAsJson().toString());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
        return null;
    }

    /*
     * 可测试 opml 列表
     * https://raw.githubusercontent.com/lotosbin/opml-list/master/generate/all.opml
     * https://gist.github.com/webpro/5907452
     */

    // fixme: multiFile 还没有运行过一次， 运行不成功
    @PostMapping("/opml/file")
    public Object rssImportByOpml(MultipartFile opmlFile) {
        ErrorMessages errorMessages = new ErrorMessages();
        if (opmlFile != null && !opmlFile.isEmpty()) {
            File file = new File(Objects.requireNonNull(opmlFile.getOriginalFilename()));
            try {
                opmlFile.transferTo(file);
                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                errorMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document), "opml in file");
            } catch (IOException | DocumentException e) {
                // log exception
                errorMessages.addMsg("opml add from file exception");
            }
        } else {
            errorMessages.addMsg("no opml content find");
        }
        return errorMessages;
    }

    @PostMapping("/opml")
    public Object rssImportByOpml(@RequestBody OpmlAddParams opmlAddParams) {
        ErrorMessages errorMessages = new ErrorMessages();
        boolean hasOpmlContent = false;
        String opmlString = opmlAddParams.getOpmlString();
        List<String> opmlUrls = opmlAddParams.getOpmlUrls();

        if (StringUtils.isNotBlank(opmlString)) {
            SAXReader reader = new SAXReader();
            try {
                Document document = reader.read(new InputSource(new StringReader(opmlString)));
                errorMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document), "opml in string");
                hasOpmlContent = true;
            } catch (DocumentException e) {
                // log exception
                errorMessages.addMsg("opml in string read error");
            }
        }

        if (opmlUrls != null) {
            // todo 这个地方可能也会太慢了，应该开启多线程，http的请求总是很慢，前端链接早就关闭了
            //  并且应该能够开启进度提示
            opmlUrls.parallelStream().map(s -> {
                try {
                    return new SAXReader().read(new URL(s));
                } catch (DocumentException e) {
                    // log exception
                    errorMessages.addMsg("opml in url read error: url:" + s);
                    return null;
                } catch (MalformedURLException e) {
                    errorMessages.addMsg("opml in url read error, url format wrong: url:" + s);
                    return null;
                }
            }).filter(Objects::nonNull).forEach(document -> {
                errorMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document), "opml in url");
            });
            hasOpmlContent = true;
        }

        if (!hasOpmlContent) {
            errorMessages.addMsg("no opml content find");
        }
        // todo: msg应当返回 rss标题 链接 + msg，只返回msg，信息太乱
        return errorMessages;
    }

    @GetMapping("/opml")
    public Object rssExportOpml(HttpServletResponse response) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(response.getOutputStream(), format);
            Document document = rssSourceService.exportRssSourceAsOpml();
            writer.write(document);
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
    @PostMapping("/get")
    public Object getRSSSource(@RequestBody GetListBodyParams getListBodyParams) {
        int pageSize = getListBodyParams.getPageSize();
        int pageNum = getListBodyParams.getPageNum();
        int type = getListBodyParams.getType();
        Map<String, Object> typeValue = getListBodyParams.getTypeValue();

        if (type == SearchRssSourceType.NORMAL_LIST.getValue()) {
            return rssSourceService.getRSSList(pageNum, pageSize);
        }
        if (type == SearchRssSourceType.TITLE.getValue()) {
            List<String> titleUsers = getParamsValueList(typeValue.get("titleUsers"));
            List<String> titleParses = getParamsValueList(typeValue.get("titleParses"));
            return rssSourceService.getRSSListByTitle(pageNum, pageSize, titleUsers, titleParses);
        }
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
    @PostMapping("huginn/emit/fetch/${url.token}")
    public Object emitItemFetch(@Validated List<RSSSourceParams> rssSourceParams) {
        return rssSourceService.fetchRSSFromParams(rssSourceParams);
    }
}
