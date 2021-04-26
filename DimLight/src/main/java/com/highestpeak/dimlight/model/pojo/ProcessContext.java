package com.highestpeak.dimlight.model.pojo;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.MobiusTag;
import com.highestpeak.dimlight.service.process.DuplicateRemoveProcess;
import com.highestpeak.dimlight.service.process.DescWordSegmentProcess;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Data
@Builder
public class ProcessContext {
    private Integer rssId;
    private RSSSource rssSource;
    private RSSXml originXml;

    //private List<XmlItemWithId> xmlItemList;
    private Map<Integer, RSSXml.RSSXmlItem> xmlItemList;
    private Map<Integer, List<MobiusTag>> xmlTags;

    private Map<Integer, List<String>> titleWord;
    private Map<Integer, List<DescWordSegmentProcess.WordInfo>> topWordFreq;
    private Map<Integer, String> simhashMap;
    private Map<Integer, String> summaryMap;
    private PriorityQueue<DuplicateRemoveProcess.SimhashDuplicateXmlItemList> simhashDuplicateXmlItemList;

    private InfoMessages infoMessages;

    @Data
    @Builder
    public static class XmlItemWithId {
        /**
         * 这个id对应数据库的id
         */
        private Integer id;
        private RSSXml.RSSXmlItem rssXmlItem;
    }
}
