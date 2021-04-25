package com.highestpeak.dimlight.service.process;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import lombok.Builder;
import lombok.Data;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 分词处理
 * 分词并进行排序
 */
@Component
public class WordSegmentProcess implements InfoProcess {
    private static int WORD_COUNT_POLL = 100;

    @Override
    public void process(ProcessContext processContext) {
        if (processContext.getTopWordFreq() == null) {
            processContext.setTopWordFreq(Maps.newHashMapWithExpectedSize(WORD_COUNT_POLL));
        }
        Map<Integer, List<WordInfo>> xmlWithItWordFreq = processContext.getTopWordFreq();

        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();
        for (Map.Entry<Integer, RSSXml.RSSXmlItem> xmlItemEntry : xmlItemList.entrySet()) {
            Map<String, WordInfo> wordFreqMap = Maps.newHashMap();
            PriorityQueue<WordInfo> wordHeap = new PriorityQueue<>(Comparator.comparingInt(word -> word.count));
            RSSXml.RSSXmlItem rssXmlItem = xmlItemEntry.getValue();
            String description = rssXmlItem.getDescription();
            // 分词
            List<Word> words = WordSegmenter.seg(description, SegmentationAlgorithm.BidirectionalMaximumMatching);
            // 计数
            for (int i = 0; i < words.size(); i++) {
                Word word = words.get(i);
                String wordText = word.getText();
                if (!wordFreqMap.containsKey(wordText)) {
                    wordFreqMap.put(wordText, WordInfo.builder().word(wordText).build());
                }
                WordInfo wordInfo = wordFreqMap.get(wordText);
                wordInfo.count++;
                // 记录第一次出现的位置
                if (wordInfo.firstShow == -1) {
                    wordInfo.firstShow = i;
                }

                wordHeap.remove(wordInfo);
                wordHeap.add(wordInfo);
            }
            // 取前100个频率最高的词
            List<WordInfo> wordInfoList = Lists.newArrayListWithCapacity(WORD_COUNT_POLL);
            int pollCount = 0;
            while (!wordHeap.isEmpty() && pollCount <= WORD_COUNT_POLL) {
                WordInfo wordInfo = wordHeap.poll();
                wordInfoList.add(wordInfo);
            }
            // 记到context中
            xmlWithItWordFreq.put(xmlItemEntry.getKey(), wordInfoList);
        }
    }

    @Data
    @Builder
    public static class WordInfo {
        private String word;
        private int count = 0;
        /**
         * 指的是词语的位置
         */
        private int firstShow = -1;
        /**
         * 词向量权重
         */
        private int weight = 1;
    }
}
