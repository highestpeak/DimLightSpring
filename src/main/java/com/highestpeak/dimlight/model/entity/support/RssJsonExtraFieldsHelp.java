package com.highestpeak.dimlight.model.entity.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.ProxyJsonPojo;

/**
 * 解析RSSSource的JsonExtra字段
 */
public class RssJsonExtraFieldsHelp {
    public static ProxyJsonPojo proxyJsonPojo(RSSSource rssSource) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(rssSource.getJsonOptionalExtraFields());
            if (jsonNode == null) {
                throw new ErrorMsgException("proxy的json配置不存在 rss:" + rssSource.getId());
            }
            JsonNode proxyNode = jsonNode.get("proxy");
            return ProxyJsonPojo.builder()
                    .hostname(proxyNode.get("hostname").asText())
                    .port(proxyNode.get("port").asInt())
                    .scheme(proxyNode.get("scheme").asText())
                    .build();
        } catch (JsonProcessingException e) {
            throw new ErrorMsgException("解析rssSource的json字段出现错误 " + e.getMessage());
        }
    }
}
