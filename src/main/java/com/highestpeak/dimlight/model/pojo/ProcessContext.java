package com.highestpeak.dimlight.model.pojo;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.MobiusTag;
import com.highestpeak.dimlight.model.entity.MobiusTopic;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProcessContext {
    private Integer rssId;
    private RSSSource rssSource;
    private RSSXml originXml;

    private List<XmlItemWithId> xmlItemList;
    private Map<Integer, List<MobiusTag>> xmlTags;

    @Data
    public static class XmlItemWithId {
        private Integer id;
        private RSSXml.RSSXmlItem rssXmlItem;
    }
}
