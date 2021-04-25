package com.highestpeak.dimlight.model.entity.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.ProxyJsonPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析RSSSource的JsonExtra字段
 */
public class RssJsonExtraFieldsHelp {

    /**
     * 从json字段中获取proxy代理的信息
     */
    public static ProxyJsonPojo proxyJsonPojo(RSSSource rssSource) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(rssSource.getJsonOptionalExtraFields());
            if (jsonNode==null) {
                throw new ErrorMsgException("json字段初始解析错误 rss:" + rssSource.getId());
            }
            JsonNode proxyNode = jsonNode.get("proxy");
            if (proxyNode == null) {
                //throw new ErrorMsgException("proxy的json配置不存在 rss:" + rssSource.getId());
                return ProxyJsonPojo.builder().build();
            }
            return ProxyJsonPojo.builder()
                    .hostname(proxyNode.get("hostname").asText())
                    .port(proxyNode.get("port").asInt())
                    .scheme(proxyNode.get("scheme").asText())
                    .build();
        } catch (JsonProcessingException e) {
            throw new ErrorMsgException("解析rssSource的json字段出现错误 " + e.getMessage());
        }
    }

    /**
     * 从json中解析正则过滤的表达式
     */
    public static List<String> regxFilterPattern(RSSSource rssSource) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(rssSource.getJsonOptionalExtraFields());
            if (jsonNode==null) {
                throw new ErrorMsgException("json字段初始解析错误 rss:" + rssSource.getId());
            }
            JsonNode regxFilterNodes = jsonNode.get("regxFilter");
            if (regxFilterNodes == null) {
                //throw new ErrorMsgException("proxy的json配置不存在 rss:" + rssSource.getId());
                return Lists.newArrayList();
            }
            ArrayList<String> regxPatternList = Lists.newArrayListWithCapacity(regxFilterNodes.size());
            for (final JsonNode regxNode : regxFilterNodes) {
                regxPatternList.add(regxNode.asText());
            }
            return regxPatternList;
        } catch (JsonProcessingException e) {
            throw new ErrorMsgException("解析rssSource的json字段出现错误 " + e.getMessage());
        }
    }
}
