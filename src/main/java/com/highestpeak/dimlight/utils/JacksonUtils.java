package com.highestpeak.dimlight.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import com.highestpeak.dimlight.model.entity.Topic;

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

    public static ObjectNode tagToObjectNode(RSSSourceTag tag, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        currNode.put("name", tag.getName());
        currNode.put("descUser", tag.getDescUser());
        return currNode;
    }

    public static ObjectNode topicToObjectNode(Topic topic, ObjectMapper mapper) {
        ObjectNode currNode = mapper.createObjectNode();
        currNode.put("name", topic.getName());
        currNode.put("descUser", topic.getDescUser());
        return currNode;
    }

    public static ArrayNode listToObjectNode(List<Integer> list, ObjectMapper mapper) {
        ArrayNode currNode = mapper.createArrayNode();
        list.forEach(currNode::add);
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
                .url(sourceNode.get("url").toString())
                .titleUser(sourceNode.get("titleUser").toString())
                .titleParse(sourceNode.get("titleParse").toString())
                .descUser(sourceNode.get("descUser").toString())
                .descParse(sourceNode.get("descParse").toString())
                .link(sourceNode.get("link").toString())
                .image(sourceNode.get("image").toString())
                .generator(sourceNode.get("generator").toString())
                .jsonOptionalExtraFields(sourceNode.get("jsonOptionalExtraFields").toString())
                .fetchAble(sourceNode.get("fetchAble").asBoolean())
                .build();
        return rssSource;
    }

    public static String ifNullThenStr(String jsonStr){
        if (jsonStr==null || "null".equals(jsonStr) || "NULL".equals(jsonStr)){
            return null;
        }
        return jsonStr;
    }
}
