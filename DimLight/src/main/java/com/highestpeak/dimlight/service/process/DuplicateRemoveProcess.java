package com.highestpeak.dimlight.service.process;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 去重
 * 一定时间内（一天内、一周内），相似度去重
 */
@Component
public class DuplicateRemoveProcess implements InfoProcess {
    private SimHashProcess simHashProcess = new SimHashProcess();

    /**
     * 相似度阈值
     */
    @Value("${scoreThresholdRate:0.7}")
    private double scoreThresholdRate = 0.7;

    @Override
    public void process(ProcessContext processContext) {
        // 取得simhash在上下文中
        simHashProcess.process(processContext);
        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();

        // 重复内容记录 按重复度排序
        if (processContext.getSimhashDuplicateXmlItemList()==null) {
            processContext.setSimhashDuplicateXmlItemList(Queues.newPriorityQueue());
        }
        PriorityQueue<SimhashDuplicateXmlItemList> duplicateQueue = processContext.getSimhashDuplicateXmlItemList();

        // 两两比较simhash
        processContext.getSimhashMap().forEach((id, simhash) -> {
            if (!xmlItemList.containsKey(id)) {
                return;
            }

            SimhashDuplicateXmlItemList simhashDuplicateXmlItemList = new SimhashDuplicateXmlItemList();
            List<RSSXml.RSSXmlItem> rssXmlItemList = simhashDuplicateXmlItemList.getRssXmlItemList();
            processContext.getSimhashMap().forEach((innerId, innerSimhash) -> {
                if (!xmlItemList.containsKey(innerId)) {
                    return;
                }
                // 汉明距离
                int hammingDistance = hammingDistance(simhash, innerSimhash);
                int maxDistance = simhash.length();
                double score = (1 - hammingDistance / (double) maxDistance);
                score = (int) (score * 1000000 + 0.5) / (double) 1000000;
                // 判断为文本相似
                if (score >= scoreThresholdRate) {
                    xmlItemList.remove(id);
                    xmlItemList.remove(innerId);
                    // 相似的内容项加入到同一个列表中
                    if (xmlItemList.containsKey(id)){
                        rssXmlItemList.add(xmlItemList.get(id));
                    }
                    rssXmlItemList.add(xmlItemList.get(innerId));
                }
            });
            duplicateQueue.add(simhashDuplicateXmlItemList);
        });

    }

    public int hammingDistance(String simHash1, String simHash2) {
        if (simHash1.length() != simHash2.length()) {
            return -1;
        }
        int distance = 0;
        int len = simHash1.length();
        for (int i = 0; i < len; i++) {
            if (simHash1.charAt(i) != simHash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

    /**
     * 重复的内容项
     */
    @Data
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @Builder
    public static class SimhashDuplicateXmlItemList implements Comparable<SimhashDuplicateXmlItemList>{
        private int score;
        private List<RSSXml.RSSXmlItem> rssXmlItemList;

        public SimhashDuplicateXmlItemList() {
            rssXmlItemList = Lists.newArrayList();
        }

        @Override
        public int compareTo(@NotNull DuplicateRemoveProcess.SimhashDuplicateXmlItemList o) {
            return this.score - o.score;
        }
    }
}
