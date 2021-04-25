package com.highestpeak.dimlight.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.MobiusTopic;
import com.highestpeak.dimlight.model.params.TopicParams;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.repository.TopicRepository;
import com.highestpeak.dimlight.utils.JacksonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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
        InfoMessages msg = new InfoMessages();
        MobiusTopic mobiusTopic = topicRepository.findByName(topicParams.getName());

        int id = -1;
        Date originCreatetime = null;
        if (mobiusTopic != null) {
            id = mobiusTopic.getId();
            originCreatetime = mobiusTopic.getCreateTime();
        }
        mobiusTopic = MobiusTopic.builder()
                .name(topicParams.getName())
                .descUser(topicParams.getDesc())
                .build();
        if (id != -1) {
            mobiusTopic.setId(id);
            mobiusTopic.setCreateTime(originCreatetime);
        }

        try {
            MobiusTopic savedMobiusTopic = topicRepository.save(mobiusTopic);
        } catch (Exception e) {
            msg.addErrorMsg(InfoMessages.buildExceptionMsg("保存 Topic 时发生错误", e));
        }
        return msg;
    }

    public Object deleteTopic(int id) {
        InfoMessages msg = new InfoMessages();
        try {
            Optional<MobiusTopic> mobiusTopicOptional = topicRepository.findById(id);
            if (!mobiusTopicOptional.isPresent()) {
                msg.addErrorMsg("topic不存在");
                return msg;
            }
            MobiusTopic mobiusTopic = mobiusTopicOptional.get();
            topicRepository.save(mobiusTopic);
            mobiusTopic.getRssSources().forEach(rssSource -> {
                List<MobiusTopic> rssMobiusTopics = rssSource.getRssMobiusTopics();
                rssMobiusTopics.removeIf(rssTopic -> rssTopic.getId().equals(mobiusTopic.getId()));
            });
            topicRepository.deleteById(id);

        } catch (Exception e) {
            msg.addErrorMsg(InfoMessages.buildExceptionMsg("删除 Topic 时发生错误", e));
        }
        return msg;
    }

    public Object getTopicListByName(int pageNumber, int pageSize, List<String> names) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        // todo: 废弃，改用搜索
        //return topicRepository.findByNameIn(names,pageable);
        return null;
    }

    public Object getRssSourceByTopicName(int pageNumber, int pageSize, List<String> topicNames) {
        //List<MobiusTopic> rssByMobiusTopicNames = topicRepository.findRssByTopicNames(topicNames);
        // todo: 废弃，改用搜索
        //return rssByMobiusTopicNames.stream().flatMap(topic -> topic.getRssSources().stream()).collect(Collectors.toList());
        return null;
    }

    public Object getContentItemsByTopicName(int pageNum, int pageSize, List<String> topicNames) {
        //List<MobiusTopic> itemsByMobiusTopic = topicRepository.findItemsByTopicNames(topicNames);
        // todo: 废弃，改用搜索
        return null;
    }

    public Object getTopicList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        Page<MobiusTopic> topicPage = topicRepository.findAll(pageable);
        //todo:通过json构造正确的返回体
        //topicPage.getContent().forEach(MobiusTopic::removeItemsFromEntity);
        return topicPage;
    }

    public Object getTopicRssGroup() {
        Map<Integer, List<Integer>> topicRssMap = Maps.newHashMap();
        Map<Integer, MobiusTopic> topicMap = Maps.newHashMap();
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

    public Object getTopicById(int topicId) {
        return topicRepository.findById(topicId).orElse(null);
    }

    public Object searchByContent(String content) {
        return null;
    }
}
