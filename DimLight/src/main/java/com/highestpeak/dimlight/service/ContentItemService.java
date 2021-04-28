package com.highestpeak.dimlight.service;

import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.entity.*;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.repository.MobiusContentItemRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContentItemService {
    @Resource
    private TopicService topicService;
    @Resource
    private MobiusContentItemRepository mobiusContentItemRepository;

    public void convertToContentItemAndSave(ProcessContext processContext) {
        // todo
    }

    public Object getTargetRssContentItem(int topicId, int num) {
        MobiusTopic mobiusTopic = topicService.getTopicById(topicId);
        Map<Integer, RSSSource> rssSourceMap = mobiusTopic.getRssSources()
                .stream()
                .collect(Collectors.toMap(
                        BaseEntity::getId,
                        Function.identity()
                ));
        //.map(BaseEntity::getId)
        //.collect(Collectors.toList());
        Map<Integer, List<RSSContentItem>> contentItemMap = Maps.newHashMap();
        for (Map.Entry<Integer, RSSSource> rssSourceEntry : rssSourceMap.entrySet()) {
            contentItemMap.put(rssSourceEntry.getKey(), rssSourceEntry.getValue().getContentItems());
        }

        List<RSSContentItem> rssContentItems = contentItemMap.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        Map<Integer, List<Integer>> rssWithItsItems = contentItemMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        (entry) -> entry.getValue().stream().map(BaseEntity::getId).collect(Collectors.toList())
                ));

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("rssSource", rssSourceMap);
        resultMap.put("contentItem", rssContentItems);
        resultMap.put("rssWithItsItems", rssWithItsItems);
        return resultMap;
    }
}
