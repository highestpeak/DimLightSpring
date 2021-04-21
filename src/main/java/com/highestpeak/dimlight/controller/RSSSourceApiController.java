package com.highestpeak.dimlight.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highestpeak.dimlight.factory.MessageFactory;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.enums.SearchRssSourceType;
import com.highestpeak.dimlight.model.params.GetListBodyParams;
import com.highestpeak.dimlight.model.params.OpmlAddParams;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.params.validation.JsonValidator;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.service.RSSSourceService;
import com.highestpeak.dimlight.utils.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author highestpeak
 */
@RestController
@CrossOrigin
@RequestMapping("/api/rss/source")
public class RSSSourceApiController {
    @Resource
    private RSSSourceService rssSourceService;

    /**
     * 通过id删除RSS
     */
    @DeleteMapping("/")
    public Object delRSSSource(@RequestParam("id") int id) {
        return rssSourceService.deleteRSSSource(id);
    }

    /**
     * 更新RSS
     */
    @PutMapping
    public Object updateRSSSource(@Validated @RequestBody RSSSourceParams rssSourceParams) {
        return rssSourceService.newOrUpdateRSSSource(rssSourceParams);
    }

    /**
     * 新RSS
     */
    @PostMapping
    public Object newRSSSource(@Validated @RequestBody RSSSourceParams rssSourceParams) {
        return rssSourceService.newOrUpdateRSSSource(rssSourceParams);
    }

    /**
     * json导入
     */
    @PostMapping("/json")
    public Object rssImportByJson(@JsonValidator @RequestBody String json) throws JsonProcessingException {
        // file string
        return rssSourceService.addRssSourceFromJson(json);
    }

    /**
     * json导出
     */
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

    /**
     * opml文件导入rss
     * fixme: multiFile 还没有运行过一次， 运行不成功
     */
    @PostMapping("/opml/file")
    public Object rssImportByOpml(MultipartFile opmlFile) {
        InfoMessages infoMessages = new InfoMessages();
        if (opmlFile != null && !opmlFile.isEmpty()) {
            File file = new File(Objects.requireNonNull(opmlFile.getOriginalFilename()));
            try {
                opmlFile.transferTo(file);
                SAXReader reader = new SAXReader();
                Document document = reader.read(file);
                infoMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document), "opml in file");
            } catch (IOException | DocumentException e) {
                // log exception
                infoMessages.addErrorMsg("opml add from file exception");
            }
        } else {
            infoMessages.addErrorMsg("no opml content find");
        }
        return infoMessages;
    }

    /**
     * opml导入
     */
    @PostMapping("/opml")
    public Object rssImportByOpml(@RequestBody OpmlAddParams opmlAddParams) {
        InfoMessages infoMessages = new InfoMessages();
        boolean hasOpmlContent = false;
        String opmlString = opmlAddParams.getOpmlString();
        List<String> opmlUrls = opmlAddParams.getOpmlUrls();

        if (StringUtils.isNotBlank(opmlString)) {
            SAXReader reader = new SAXReader();
            try {
                Document document = reader.read(new InputSource(new StringReader(opmlString)));
                infoMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document), "opml in string");
                hasOpmlContent = true;
            } catch (DocumentException e) {
                // log exception
                infoMessages.addErrorMsg("解析opml字符串错误");
            }
        }

        if (opmlUrls != null) {
            // future: 这个地方可能也会太慢了，应该开启多线程，http的请求总是很慢，前端链接早就关闭了
            //  并且应该能够开启进度提示
            opmlUrls.parallelStream().map(s -> {
                try {
                    return new SAXReader().read(new URL(s));
                } catch (DocumentException e) {
                    // log exception
                    infoMessages.addErrorMsg("opml in url read error: url:" + s);
                    return null;
                } catch (MalformedURLException e) {
                    infoMessages.addErrorMsg("opml in url read error, url format wrong: url:" + s);
                    return null;
                }
            }).filter(Objects::nonNull).forEach(document -> {
                infoMessages.mergeMsgWithExtraTag(rssSourceService.addRssSourceFromOpml(document), "opml in url");
            });
            hasOpmlContent = true;
        }

        if (!hasOpmlContent) {
            infoMessages.addErrorMsg("no opml content find");
        }
        // future: msg应当返回 rss标题 链接 + msg，只返回msg，信息太乱
        return infoMessages;
    }

    /**
     * opml导出
     */
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
     * 返回所有RSS的分页
     */
    @GetMapping("/all")
    public Object getRSSSourceAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        try {
            return rssSourceService.getRSSList(pageNum, pageSize);
        } catch (Exception e) {
            return new InfoMessages(InfoMessages.buildExceptionMsg("服务器发生错误", e));
        }
    }

    /**
     * 返回指定id的RSS
     * 传过来不区分titleUser和titleParse
     */
    @GetMapping("/id")
    public Object getRSSSourceById(@RequestParam("id") String id) {
        try {
            return rssSourceService.getRSSById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return MessageFactory.PARAMETER_ERROR_MSG;
        }
    }

    /**
     * 返回指定content的RSS列表
     * todo:可进行分词召回,搜索召回, get的content能有多长？
     * 传过来不区分titleUser和titleParse
     */
    @GetMapping("/search")
    public Object searchRSSSource(@RequestParam("content") String content) {
        try {
            return rssSourceService.searchByContent(content);
        } catch (Exception e) {
            return new InfoMessages(InfoMessages.buildExceptionMsg("服务器发生错误", e));
        }
    }

    @GetMapping("/fetch_rss_now")
    public Object fetchTargetRssNow(@RequestParam("id") int id) {
        // todo: json解析
        ProcessContext processContext = ProcessContext.builder().rssId(id).build();
        return processContext.getOriginXml();
    }

    /**
     * 废弃
     * 1. 单纯返回某个page的List
     * 2. 返回符合titleUser列表或者titleParse列表的集合
     * 3. 返回给定id集合/title集合rssSource的所有contentItem，按照page分页
     */
    @PostMapping("/get")
    @Deprecated
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

}
