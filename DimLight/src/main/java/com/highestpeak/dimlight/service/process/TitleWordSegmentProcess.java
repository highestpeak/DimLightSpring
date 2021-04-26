package com.highestpeak.dimlight.service.process;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理的是标题的分词
 */
@Component
public class TitleWordSegmentProcess implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {
        if (processContext.getTitleWord() == null) {
            processContext.setTitleWord(Maps.newHashMap());
        }
        Map<Integer, List<String>> titleWord = processContext.getTitleWord();

        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();
        for (Map.Entry<Integer, RSSXml.RSSXmlItem> xmlItemEntry : xmlItemList.entrySet()) {
            RSSXml.RSSXmlItem xmlItem = xmlItemEntry.getValue();
            String xmlItemTitle = xmlItem.getTitle();
            // 分词
            List<Word> words = WordSegmenter.seg(xmlItemTitle, SegmentationAlgorithm.BidirectionalMaximumMatching);
            List<String> wordSet = Sets.newHashSet(words).stream()
                    .map(Word::getText)
                    // 分词去重
                    .distinct()
                    .collect(Collectors.toList());
            titleWord.put(xmlItemEntry.getKey(), wordSet);
        }
    }
}
