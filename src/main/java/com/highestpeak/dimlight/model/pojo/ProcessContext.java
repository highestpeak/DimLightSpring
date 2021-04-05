package com.highestpeak.dimlight.model.pojo;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import com.highestpeak.dimlight.model.entity.Topic;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProcessContext {
    private RSSSource rssSource;
    private RSSXml originXml;

    private List<XmlItemWithId> xmlItemList;
    private Map<Integer, List<RSSSourceTag>> xmlTags;
    private Map<Integer, List<Topic>> xmlTopics;

    @Data
    public static class XmlItemWithId {
        private Integer id;
        private RSSXml.RSSXmlItem rssXmlItem;
    }

    public List<RSSSourceTag> getXmlTag(Integer id) {
        return xmlTags.get(id);
    }

    public List<Topic> getXmlTopic(Integer id) {
        return xmlTopics.get(id);
    }
}
