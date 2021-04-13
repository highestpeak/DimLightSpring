package com.highestpeak.dimlight.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.entity.BaseEntity;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.Topic;
import com.highestpeak.dimlight.model.params.DeleteTopicParams;
import com.highestpeak.dimlight.model.params.TopicParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.vo.TopicRssWithFeedCount;
import com.highestpeak.dimlight.repository.TopicRepository;
import com.highestpeak.dimlight.utils.JacksonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-13
 */
@Service
public class TopicService {

    @Resource
    private TopicRepository topicRepository;

    public Object newOrUpdateTopic(TopicParams topicParams) {
        ErrorMessages msg = new ErrorMessages();
        Topic topic = topicRepository.findByName(topicParams.getName());

        int id = -1;
        Date originCreatetime = null;
        if (topic != null) {
            id = topic.getId();
            originCreatetime = topic.getCreateTime();
        }
        topic = Topic.builder()
                .name(topicParams.getName())
                .descUser(topicParams.getDesc())
                .build();
        if (id != -1) {
            topic.setId(id);
            topic.setCreateTime(originCreatetime);
        }

        try {
            Topic savedTopic = topicRepository.save(topic);
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("保存 Topic 时发生错误", e));
        }
        return msg;
    }

    public Object deleteTopic(DeleteTopicParams deleteTopicParams) {
        ErrorMessages msg = new ErrorMessages();
        try {
            if (deleteTopicParams.getId() != null) {
                topicRepository.deleteById(deleteTopicParams.getId());
            } else {
                topicRepository.deleteByName(deleteTopicParams.getName());
            }
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("删除 Topic 时发生错误", e));
        }
        return msg;
    }

    public Object getTopicListByName(int pageNumber, int pageSize, List<String> names) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return topicRepository.findByNames(pageable, names);
    }

    public Object getRssSourceByTopicName(int pageNumber, int pageSize, List<String> topicNames) {
        List<Topic> rssByTopicNames = topicRepository.findRssByTopicNames(topicNames);
        return rssByTopicNames.stream().flatMap(topic -> topic.getRssSources().stream()).collect(Collectors.toList());
    }

    public Object getContentItemsByTopicName(int pageNum, int pageSize, List<String> topicNames) {
        List<Topic> itemsByTopic = topicRepository.findItemsByTopicNames(topicNames);
        return itemsByTopic.stream().flatMap(topic -> topic.getRssContentItems().stream()).collect(Collectors.toList());
    }

    public Object getTopicList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        Page<Topic> topicPage = topicRepository.findList(pageable);
        topicPage.getContent().forEach(Topic::removeItemsFromEntity);
        return topicPage;
    }

    public Object getTopicRssGroup() {
        Map<Integer, List<Integer>> topicRssMap = Maps.newHashMap();
        Map<Integer, Topic> topicMap = Maps.newHashMap();
        Map<Integer, RSSSource> rssSourceMap = Maps.newHashMap();

        topicRepository.findAll().forEach(topic -> {
            topicMap.put(topic.getId(), topic);
            ArrayList<Integer> rssIdList = Lists.newArrayList();
            List<RSSSource> rssSources = topic.getRssSources();
            for (RSSSource rssSource : rssSources) {
                rssSourceMap.put(rssSource.getId(), rssSource);
                rssIdList.add(rssSource.getId());
            }
            topicRssMap.put(topic.getId(), rssIdList);
        });

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        // {topic id : topic}
        Map<String, ObjectNode> topicNodeMap = Maps.newHashMap();
        topicMap.forEach((topicId, topic) -> {
            topicNodeMap.put(topicId.toString(), JacksonUtils.topicToObjectNode(topic, mapper));
        });
        rootNode.set("topics", JacksonUtils.mapToObjectNode(topicNodeMap, mapper));
        // {topic id : [rss id 1,rss id 2,rss id 3...]]
        Map<String, ArrayNode> topicRssNodeMap = Maps.newHashMap();
        topicRssMap.forEach((topicId, rssIds) -> {
            topicRssNodeMap.put(topicId.toString(), JacksonUtils.listToObjectNode(rssIds, mapper));
        });
        rootNode.set("topicRss", JacksonUtils.arrayMapToObjectNode(topicRssNodeMap, mapper));
        // {rss id:TopicRssWithFeedCount}
        Map<String, ObjectNode> rssNodeMap = Maps.newHashMap();
        rssSourceMap.forEach((rssId, rssSource) -> {
            rssNodeMap.put(rssId.toString(), JacksonUtils.rssSourceCountToObjectNode(rssSource, mapper));
        });
        rootNode.set("rss", JacksonUtils.mapToObjectNode(rssNodeMap, mapper));

        return rootNode;
    }
}
