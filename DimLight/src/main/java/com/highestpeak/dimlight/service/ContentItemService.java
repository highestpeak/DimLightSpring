package com.highestpeak.dimlight.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.entity.BaseEntity;
import com.highestpeak.dimlight.model.entity.MobiusContentItem;
import com.highestpeak.dimlight.model.entity.MobiusTopic;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.enums.OriginContentTypeEnum;
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
        List<MobiusContentItem> mobiusContentItemList = mobiusContentItemRepository.findByOriginContentTypeAndOriginSourceIdIn(
                OriginContentTypeEnum.RSS.getType(), Lists.newArrayList(rssSourceMap.keySet())
        );
        Map<Integer,List<Integer>> rssWithItsItems = Maps.newHashMapWithExpectedSize(mobiusContentItemList.size());
        for (MobiusContentItem mobiusContentItem : mobiusContentItemList) {
            int originSourceId = mobiusContentItem.getOriginSourceId();
            if (!rssWithItsItems.containsKey(originSourceId)) {
                rssWithItsItems.put(originSourceId, Lists.newArrayList());
            }
            List<Integer> itemIds = rssWithItsItems.get(originSourceId);
            itemIds.add(mobiusContentItem.getId());
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("rssSource", rssSourceMap);
        resultMap.put("contentItem", mobiusContentItemList);
        resultMap.put("rssWithItsItems", rssWithItsItems);
        return resultMap;
    }
}
