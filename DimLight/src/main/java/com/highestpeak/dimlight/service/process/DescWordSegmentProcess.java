package com.highestpeak.dimlight.service.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.service.WordSegmentService;
import lombok.Builder;
import lombok.Data;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.segmentation.Word;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 分词处理
 * 分词并进行排序
 * 处理的是内容的分词
 */
@Component
public class DescWordSegmentProcess implements InfoProcess {
    private static int WORD_COUNT_POLL = 100;

    @Override
    public void process(ProcessContext processContext) {
        if (processContext.getTopWordFreq() == null) {
            processContext.setTopWordFreq(Maps.newHashMapWithExpectedSize(WORD_COUNT_POLL));
        }
        Map<Integer, List<WordInfo>> xmlWithItWordFreq = processContext.getTopWordFreq();

        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();
        for (Map.Entry<Integer, RSSXml.RSSXmlItem> xmlItemEntry : xmlItemList.entrySet()) {
            RSSXml.RSSXmlItem rssXmlItem = xmlItemEntry.getValue();
            String description = rssXmlItem.getDescription();
            List<WordInfo> wordInfoList = processDescription(description);
            // 记到context中
            xmlWithItWordFreq.put(xmlItemEntry.getKey(), wordInfoList);
        }
    }

    @Resource
    private WordSegmentService wordSegmentService;

    public static final Set<String> mingciPosStrSet = Sets.newHashSet("n","nr","ns","nt","nz");

    public List<WordInfo> processDescription(String description) {
        Map<String, WordInfo> wordFreqMap = Maps.newHashMap();
        // 注意是必须是反序， 这样写是最大堆
        PriorityQueue<WordInfo> wordHeap = new PriorityQueue<>((x, y) -> Integer.compare(y.count, x.count));
        // 分词
        List<Word> words = wordSegmentService.segString(description);
        // 计数
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            // 只保留名词
            String pos = word.getPartOfSpeech().getPos();
            if (!mingciPosStrSet.contains(pos)) {
                continue;
            }

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
            pollCount++;
        }

        return wordInfoList;
    }

    @Data
    @Builder
    public static class WordInfo {
        private String word;
        private int count = 0;
        /**
         * 指的是词语的位置
         */
        @JsonIgnore
        private int firstShow = -1;
        /**
         * 词向量权重
         */
        @JsonIgnore
        private int weight = 1;
    }
}
