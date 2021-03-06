package com.highestpeak.dimlight.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.MobiusTag;
import com.highestpeak.dimlight.model.entity.MobiusTopic;
import com.highestpeak.dimlight.model.enums.TaskOutputCacheCycleEnum;
import com.highestpeak.dimlight.model.pojo.InfoMessages;

import java.util.List;
import java.util.Map;

public class JacksonUtils {
    public static ObjectNode rssSourceToObjectNode(RSSSource rssSource, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        currNode.put("id", rssSource.getId());
        currNode.put("createTime", rssSource.getCreateTime().toString());
        currNode.put("updateTime", rssSource.getUpdateTime().toString());
        currNode.put("url", rssSource.getUrl());
        currNode.put("titleUser", rssSource.getTitleUser());
        currNode.put("titleParse", rssSource.getTitleParse());
        currNode.put("descUser", rssSource.getDescUser());
        currNode.put("descParse", rssSource.getDescParse());
        currNode.put("link", rssSource.getLink());
        currNode.put("image", rssSource.getImage());
        currNode.put("generator", rssSource.getGenerator());
        currNode.put("jsonOptionalExtraFields", rssSource.getJsonOptionalExtraFields());
        currNode.put("fetchAble", rssSource.isFetchAble());
        return currNode;
    }

    public static ObjectNode tagToObjectNode(MobiusTag mobiusTag, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        currNode.put("name", mobiusTag.getName());
        currNode.put("descUser", mobiusTag.getDescUser());
        return currNode;
    }

    public static ObjectNode topicToObjectNode(MobiusTopic mobiusTopic, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        currNode.put("name", mobiusTopic.getName());
        currNode.put("descUser", mobiusTopic.getDescUser());
        return currNode;
    }

    public static ArrayNode listToObjectNode(List<Integer> list, ObjectMapper mapper) {
        ArrayNode currNode = mapper.createArrayNode();
        list.forEach(currNode::add);
        return currNode;
    }

    public static ArrayNode errorMsgToObjectNode(InfoMessages msg, ObjectMapper mapper) {
        ArrayNode currNode = mapper.createArrayNode();
        msg.getErrorMsg().forEach(currNode::add);
        msg.getInfoMsg().forEach(currNode::add);
        return currNode;
    }

    public static ObjectNode mapToObjectNode(Map<String, ObjectNode> map, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        map.forEach(currNode::set);
        return currNode;
    }

    public static ObjectNode arrayMapToObjectNode(Map<String, ArrayNode> map, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        map.forEach(currNode::set);
        return currNode;
    }

    public static RSSSource objectNodeToRssSource(JsonNode sourceNode){
        RSSSource rssSource = RSSSource.builder()
                .url(rssFieldRightStr("url",sourceNode))
                .titleUser(rssFieldRightStr("titleUser",sourceNode))
                .titleParse(rssFieldRightStr("titleParse",sourceNode))
                .descUser(rssFieldRightStr("descUser",sourceNode))
                .descParse(rssFieldRightStr("descParse",sourceNode))
                .link(rssFieldRightStr("link",sourceNode))
                .image(rssFieldRightStr("image",sourceNode))
                .generator(rssFieldRightStr("generator",sourceNode))
                .jsonOptionalExtraFields(rssFieldRightStr("jsonOptionalExtraFields",sourceNode))
                .fetchAble(sourceNode.get("fetchAble").asBoolean())
                .build();
        return rssSource;
    }

    private static String rssFieldRightStr(String fieldName, JsonNode sourceNode) {
        return ifNullThenStr(sourceNode.get(fieldName).asText());
    }

    public static String ifNullThenStr(String jsonStr){
        if (jsonStr==null || "null".equals(jsonStr) || "NULL".equals(jsonStr)){
            return null;
        }
        return jsonStr;
    }

    public static ObjectNode cacheCycleToObjectNode(TaskOutputCacheCycleEnum taskOutputCacheCycleEnum, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        currNode.put("days", taskOutputCacheCycleEnum.getDays());
        currNode.put("desc", taskOutputCacheCycleEnum.getDesc());
        return currNode;
    }

    public static ObjectNode contentItemToObjectNode(RSSContentItem contentItem, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        currNode.put("id", contentItem.getId());
        currNode.put("createTime", contentItem.getCreateTime().toString());
        currNode.put("updateTime", contentItem.getUpdateTime().toString());
        currNode.put("titleParse", contentItem.getTitleParse());
        currNode.put("descParse", contentItem.getDescParse());
        currNode.put("link", contentItem.getLink());
        currNode.put("guid", contentItem.getGuid());
        currNode.put("pubDate", contentItem.getPubDate().toString());
        currNode.put("author", contentItem.getAuthor());
        currNode.put("jsonOptionalExtraFields", contentItem.getJsonOptionalExtraFields());
        currNode.put("rssId", contentItem.getRssSource().getId());
        currNode.put("sourceName", contentItem.getRssSource().getTitleParse());
        return currNode;
    }

    public static ObjectNode rssSourceCountToObjectNode(RSSSource rssSource, ObjectMapper mapper) {
        ObjectNode rssSourceNode = rssSourceToObjectNode(rssSource, mapper);
        rssSourceNode.put("count", rssSource.getContentItems().size());
        return rssSourceNode;
    }
}
