package com.highestpeak.dimlight.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.model.entity.*;
import com.highestpeak.dimlight.model.entity.support.RssJsonExtraFieldsHelp;
import com.highestpeak.dimlight.model.enums.TaskEnum;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import com.highestpeak.dimlight.service.process.DescWordSegmentProcess;
import com.highestpeak.dimlight.service.process.DuplicateRemoveProcess;
import com.highestpeak.dimlight.service.process.InfoProcess;
import com.highestpeak.dimlight.service.process.SimHashProcess;
import com.highestpeak.dimlight.support.TextRank;
import com.highestpeak.dimlight.utils.ProcessUtils;
import com.mchange.v2.collection.MapEntry;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apdplat.word.analysis.*;
import org.apdplat.word.segmentation.Word;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProcessService {
    @Resource
    private Map<String, InfoProcess> processMap;
    @Resource
    private TaskService taskService;
    @Resource
    private QuartzManager quartzManager;
    @Resource
    private RssContentItemService rssContentItemService;
    @Resource
    private RSSSourceRepository rssSourceRepository;
    @Resource
    private ContentItemService contentItemService;
    @Resource
    private TopicService topicService;

    @Resource
    private WordSegmentService wordSegmentService;

    /**
     * 处理特定rss被拉取到的item
     */
    public InfoMessages processRssXmlNew(ProcessContext processContext) {
        RSSSource rssSource = processContext.getRssSource();
        RSSXml originRssXml = processContext.getOriginXml();
        if (originRssXml == null || rssSource == null) {
            throw new NullPointerException("ProcessService:originXml和rssSource不能null");
        }

        if (StringUtils.isBlank(rssSource.getJsonOptionalExtraFields())) {
            InfoMessages infoMessages = new InfoMessages();
            infoMessages.addInfoMsg("因为没有处理链，没有经过处理");
            return infoMessages;
        }

        InfoMessages infoMessages = new InfoMessages();

        // 构建处理队列
        Queue<InfoProcess> infoProcessQueue;
        try {
            // 读取rss的jsonExtra参数进行process
            infoProcessQueue = ProcessUtils.buildProcessQueue(
                    rssSource.getJsonOptionalExtraFields(), processMap
            );
            if (infoProcessQueue.isEmpty()) {
                infoMessages.addInfoMsg("rss没有json处理字段,不需要进行任何任务处理, rss:" + rssSource.getId());
                return infoMessages;
            }
        } catch (JsonProcessingException e) {
            throw new ErrorMsgException(
                    InfoMessages.buildExceptionMsg("解析rss的json字段的任务处理字段时出现错误, rss:" + rssSource.getId(), e)
            );
        }

        // 按顺序处理xml,遇到错误即终止处理
        InfoProcess nextProcess = infoProcessQueue.peek();
        try {
            while (infoProcessQueue.size() > 0) {
                nextProcess = infoProcessQueue.poll();
                nextProcess.process(processContext);
                // 结果全都被处理没了，所以不需要继续处理了
                if (processContext.isResultEmpty()) {
                    infoMessages.addInfoMsg(
                            "rssXml已经全部被处理，剩余未进行的任务处理：" + ProcessUtils.remainProcessNames(infoProcessQueue)
                    );
                    break;
                }
            }
        } catch (Exception exception) {
            String processClassName = nextProcess.getClass().getName();
            String exceptionMsg = InfoMessages.buildExceptionMsg(
                    "rssXml处理错误,处理名:" + processClassName +
                            ", 未进行的任务处理:" + ProcessUtils.remainProcessNames(infoProcessQueue),
                    exception
            );
            throw new ErrorMsgException(exceptionMsg);
        }

        // 记录处理后结果到数据库
        contentItemService.convertToContentItemAndSave(processContext);

        infoMessages.addInfoMsg("处理完成");
        return infoMessages;
    }

    /**
     * 触发所有RSS拉取任务
     */
    public Object fetchAllRssNow() {
        InfoMessages msg = new InfoMessages();
        // 全部拉取
        List<MobiusTask> allTask = taskService.getAllTaskByType(TaskEnum.SIMPLE_RSS_TASK);
        allTask.forEach(mobiusTask -> {
            try {
                quartzManager.triggerJobNow(taskService.getTaskJobKey(mobiusTask));
                msg.addErrorMsg("fetch rss:" + mobiusTask + " succeed!");
            } catch (SchedulerException e) {
                msg.addErrorMsg("trigger task failed. task:" + mobiusTask);
            }
        });
        return msg;
    }

    /**
     * 触发单个RSS拉取任务
     */
    public Object fetchRssNow(Integer id) {
        RSSSource rssSource = rssSourceRepository.findById(id).orElse(null);
        InfoMessages msg = new InfoMessages();
        // 指定id拉取
        MobiusTask taskById = taskService.getTaskByRssId(id);
        if (taskById == null) {
            msg.addErrorMsg("rss不存在抓取任务,请先创建抓取任务，否则不允许抓取");
            return msg;
        }
        try {
            quartzManager.triggerJobNow(taskService.getTaskJobKey(taskById));
            msg.addInfoMsg("fetch rss:" + taskById + " succeed!");
        } catch (SchedulerException e) {
            msg.addErrorMsg("trigger task failed. task:" + taskById);
        }
        return msg;
    }

    /**
     * 可选Process的名称
     */
    public Set<String> optionalProcessNames() {
        return processMap.keySet();
    }

    public Object summaryExtractOfRss(String rssId) {
        List<RSSContentItem> targetRssContentItem =
                rssContentItemService.getTargetRssContentItem(Integer.parseInt(rssId), 20);
        Map<Integer, String> summaryMap = Maps.newHashMap();

        for (RSSContentItem rssContentItem : targetRssContentItem) {
            TextRank.TextRankParams textRankParams = TextRank.TextRankParams.builder()
                    .inputContent(Jsoup.parse(rssContentItem.getDescParse()).text())
                    .maxLen(256)
                    .build();
            try {
                TextRank textrank = new TextRank();
                String summarize = textrank.summarize(textRankParams);
                summaryMap.put(rssContentItem.getId(), summarize);
            } catch (IOException e) {
                System.out.println("文档摘要生成错误 contentItem:" + rssContentItem.getGuid());
            }
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("originContentItem", targetRssContentItem);
        resultMap.put("summary", summaryMap);
        return resultMap;
    }

    public Object titleWordSegment(String rssId) {
        List<RSSContentItem> targetRssContentItem =
                rssContentItemService.getTargetRssContentItem(Integer.parseInt(rssId), 20);
        Map<Integer, List<String>> wordSegmentMap = Maps.newHashMap();
        for (RSSContentItem rssContentItem : targetRssContentItem) {
            List<Word> words = wordSegmentService.segString(rssContentItem.getTitleParse());
            List<String> wordSet = Sets.newHashSet(words).stream()
                    .map(Word::getText)
                    // 分词去重
                    .distinct()
                    .collect(Collectors.toList());
            wordSegmentMap.put(rssContentItem.getId(), wordSet);
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("originContentItem", targetRssContentItem);
        resultMap.put("wordSegment", wordSegmentMap);
        return resultMap;
    }

    public Object descWordSegment(String rssId) {
        int WORD_COUNT_POLL = 100;
        List<RSSContentItem> targetRssContentItem =
                rssContentItemService.getTargetRssContentItem(Integer.parseInt(rssId), 20);
        Map<Integer, List<String>> wordSegmentMap = Maps.newHashMap();
        for (RSSContentItem rssContentItem : targetRssContentItem) {

            Map<String, DescWordSegmentProcess.WordInfo> wordFreqMap = Maps.newHashMap();
            PriorityQueue<DescWordSegmentProcess.WordInfo> wordHeap =
                    new PriorityQueue<>(Comparator.comparingInt(DescWordSegmentProcess.WordInfo::getCount));
            String description = rssContentItem.getDescParse();
            // 分词
            List<Word> words = wordSegmentService.segString(description);
            // 计数
            for (int i = 0; i < words.size(); i++) {
                Word word = words.get(i);
                String wordText = word.getText();
                if (!wordFreqMap.containsKey(wordText)) {
                    wordFreqMap.put(wordText, DescWordSegmentProcess.WordInfo.builder().word(wordText).build());
                }
                DescWordSegmentProcess.WordInfo wordInfo = wordFreqMap.get(wordText);
                wordInfo.setCount(wordInfo.getCount() + 1);
                // 记录第一次出现的位置
                if (wordInfo.getFirstShow() == -1) {
                    wordInfo.setFirstShow(i);
                }

                wordHeap.remove(wordInfo);
                wordHeap.add(wordInfo);
            }
            // 取前100个频率最高的词
            List<DescWordSegmentProcess.WordInfo> wordInfoList = Lists.newArrayListWithCapacity(WORD_COUNT_POLL);
            int pollCount = 0;
            while (!wordHeap.isEmpty() && pollCount <= WORD_COUNT_POLL) {
                DescWordSegmentProcess.WordInfo wordInfo = wordHeap.poll();
                wordInfoList.add(wordInfo);
                pollCount++;
            }

            List<String> wordTextList =
                    wordInfoList.stream().map(DescWordSegmentProcess.WordInfo::getWord).collect(Collectors.toList());
            wordSegmentMap.put(rssContentItem.getId(), wordTextList);
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("originContentItem", targetRssContentItem);
        resultMap.put("wordSegment", wordSegmentMap);
        return resultMap;
    }

    public Object rssHtmlTagRemove(String rssId) {
        List<RSSContentItem> targetRssContentItem =
                rssContentItemService.getTargetRssContentItem(Integer.parseInt(rssId), 20);
        Map<Integer, String> descAfterTagRemove = Maps.newHashMap();
        for (RSSContentItem rssContentItem : targetRssContentItem) {
            String parsedHtmlDesc = Jsoup.parse(rssContentItem.getDescParse()).text();
            if (parsedHtmlDesc.length()<256) {
                descAfterTagRemove.put(rssContentItem.getId(), parsedHtmlDesc);
            } else {
                descAfterTagRemove.put(rssContentItem.getId(), parsedHtmlDesc.substring(0, 256));
            }
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("originContentItem", targetRssContentItem);
        resultMap.put("descAfterTagRemove", descAfterTagRemove);
        return resultMap;
    }

    @Resource
    private DescWordSegmentProcess descWordSegmentProcess;
    @Resource
    private SimHashProcess simHashProcess;
    private double scoreThresholdRate = 0.75;

    public Object topicDuplicateRemove(String topicId) {
        MobiusTopic mobiusTopic = topicService.getTopicById(Integer.parseInt(topicId));
        Map<Integer, RSSSource> rssSourceMap = mobiusTopic.getRssSources()
                .stream()
                .collect(Collectors.toMap(
                        BaseEntity::getId,
                        Function.identity()
                ));

        Map<Integer, RSSContentItem> rssContentItemMap = rssSourceMap.values()
                .stream()
                .map(RSSSource::getContentItems)
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        BaseEntity::getId,
                        Function.identity()
                ));
        List<RSSContentItem> rssContentItems = Lists.newArrayList(rssContentItemMap.values());
        Map<Integer, Set<String>> wordSplitMap = Maps.newHashMap();
        for (RSSContentItem rssContentItem : rssContentItems) {
            String titleParse = rssContentItem.getTitleParse();
            List<DescWordSegmentProcess.WordInfo> wordInfos = descWordSegmentProcess.processDescription(titleParse);
            Set<String> wordSplitList =
                    wordInfos.stream().map(DescWordSegmentProcess.WordInfo::getWord).collect(Collectors.toSet());
            wordSplitMap.put(rssContentItem.getId(), wordSplitList);
        }

        HashSet<Integer> remainIdSet = Sets.newHashSet(wordSplitMap.keySet());

        Map<Integer, Set<Integer>> simIdSetMap = Maps.newHashMap();
        for (Map.Entry<Integer, Set<String>> wordSplitEntry : wordSplitMap.entrySet()) {
            Integer id = wordSplitEntry.getKey();
            if (!remainIdSet.contains(id)) {
                continue;
            }
            Set<String> words = wordSplitEntry.getValue();
            if (words.isEmpty()) {
                continue;
            }
            Set<Integer> simIdSet = Sets.newHashSet();
            for (Map.Entry<Integer, Set<String>> oneByOneEntry : wordSplitMap.entrySet()) {
                Integer innerId = oneByOneEntry.getKey();
                if (id.equals(innerId)) {
                    continue;
                }
                Set<String> wordList = oneByOneEntry.getValue();
                Sets.SetView<String> intersection = Sets.intersection(words, wordList);
                if (intersection.size()>=1) {
                    simIdSet.add(innerId);
                }
            }

            if (!simIdSet.isEmpty()) {
                remainIdSet.remove(id);
                remainIdSet.removeAll(simIdSet);
                simIdSetMap.put(id,simIdSet);
            }
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("originContent", rssContentItemMap);
        resultMap.put("remainContent", remainIdSet);
        resultMap.put("duplisContent", simIdSetMap);
        return resultMap;
    }


    public Object topicDuplicateRemoveBefore(String topicId) {
        MobiusTopic mobiusTopic = topicService.getTopicById(Integer.parseInt(topicId));
        Map<Integer, RSSSource> rssSourceMap = mobiusTopic.getRssSources()
                .stream()
                .collect(Collectors.toMap(
                        BaseEntity::getId,
                        Function.identity()
                ));

        Map<Integer, RSSContentItem> rssContentItemMap = rssSourceMap.values()
                .stream()
                .map(RSSSource::getContentItems)
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        BaseEntity::getId,
                        Function.identity()
                ));
        List<RSSContentItem> rssContentItems = Lists.newArrayList(rssContentItemMap.values());
        Map<Integer, String> contentItemSimhashMap = Maps.newHashMap();
        Map<Integer, List<DescWordSegmentProcess.WordInfo>> contentWordDebugMap = Maps.newHashMap();
        Map<Integer, String> topWordStringMap = Maps.newHashMapWithExpectedSize(rssContentItems.size());
        for (RSSContentItem rssContentItem : rssContentItems) {
            String parsedHtmlDesc = Jsoup.parse(rssContentItem.getDescParse()).text();
            List<DescWordSegmentProcess.WordInfo> wordInfos = descWordSegmentProcess.processDescription(parsedHtmlDesc);
            contentWordDebugMap.put(rssContentItem.getId(), wordInfos);
            contentItemSimhashMap.put(rssContentItem.getId(), simHashProcess.simHash(wordInfos));
            String topWordStr =
                    wordInfos.stream().map(DescWordSegmentProcess.WordInfo::getWord).collect(Collectors.joining());
            topWordStringMap.put(rssContentItem.getId(), parsedHtmlDesc);
        }

        int contentNum = contentItemSimhashMap.size();
        //多算法对比
        //TextSimilarity cosineTextSimilarity = new CosineTextSimilarity();
        //TextSimilarity simpleTextSimilarity = new SimpleTextSimilarity();
        //TextSimilarity editDistanceTextSimilarity = new EditDistanceTextSimilarity();
        //TextSimilarity simHashPlusHammingDistanceTextSimilarity = new SimHashPlusHammingDistanceTextSimilarity();
        //TextSimilarity jaccardTextSimilarity = new JaccardTextSimilarity();
        //TextSimilarity euclideanDistanceTextSimilarity = new EuclideanDistanceTextSimilarity();
        //TextSimilarity manhattanDistanceTextSimilarity = new ManhattanDistanceTextSimilarity();
        //TextSimilarity jaroDistanceTextSimilarity = new JaroDistanceTextSimilarity();
        //TextSimilarity sørensenDiceCoefficientTextSimilarity = new SørensenDiceCoefficientTextSimilarity();
        //Map<Integer,Map<Integer,List<Double>>> idPairSimScore = Maps.newHashMapWithExpectedSize
        // (contentNum*contentNum);
        //topWordStringMap.forEach((id,topWordStr)->{
        //    Map<Integer,List<Double>> simScoreMap = Maps.newHashMapWithExpectedSize(contentNum);
        //    topWordStringMap.forEach((innerId,innerTopWordStr)->{
        //        List<Double> scoreList = Lists.newArrayList();
        //        scoreList.add(cosineTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(simpleTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(editDistanceTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(simHashPlusHammingDistanceTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(jaccardTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(euclideanDistanceTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(manhattanDistanceTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(jaroDistanceTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        scoreList.add(sørensenDiceCoefficientTextSimilarity.similarScore(topWordStr,innerTopWordStr));
        //        simScoreMap.put(innerId, scoreList);
        //    });
        //    idPairSimScore.put(id,simScoreMap);
        //});

        //两两相似度比较
        TextSimilarity simHashPlusHammingDistanceTextSimilarity = new SimHashPlusHammingDistanceTextSimilarity();
        // todo: 自己写的可能有问题，先用库里的
        Map<Integer, Map<Integer, Double>> idPairSimScore = Maps.newHashMapWithExpectedSize(contentNum * contentNum);
        topWordStringMap.forEach((id, topWordStr) -> {
            Map<Integer, Double> simScoreMap = Maps.newHashMapWithExpectedSize(contentNum);
            topWordStringMap.forEach((innerId, innerTopWordStr) -> {
                double similarScore = simHashPlusHammingDistanceTextSimilarity.similarScore(topWordStr,
                        innerTopWordStr);
                simScoreMap.put(innerId, similarScore);
            });
            idPairSimScore.put(id, simScoreMap);
        });

        //相似度大于阈值的加入到同一队列
        HashSet<Integer> remainIdSet = Sets.newHashSet(idPairSimScore.keySet());

        List<Set<Integer>> simIdSetList = Lists.newArrayList();
        for (Map.Entry<Integer, Map<Integer, Double>> simScoreEntry : idPairSimScore.entrySet()) {
            Integer id = simScoreEntry.getKey();
            if (!remainIdSet.contains(id)) {
                continue;
            }
            Map<Integer, Double> simScoreMap = simScoreEntry.getValue();
            Set<Integer> simIdSet = Sets.newHashSet();
            for (Map.Entry<Integer, Double> oneByOneScoreEntry : simScoreMap.entrySet()) {
                Integer innerId = oneByOneScoreEntry.getKey();
                Double score = oneByOneScoreEntry.getValue();
                if (score >= scoreThresholdRate) {
                    simIdSet.add(innerId);
                    remainIdSet.remove(innerId);
                }
            }
            if (!simIdSet.isEmpty()) {
                simIdSet.add(id);
                remainIdSet.remove(id);
                simIdSetList.add(simScoreMap.keySet());
            }
        }

        //PriorityQueue<SimhashDuplicateItemList> duplicateQueue = Queues.newPriorityQueue();
        //contentItemSimhashMap.forEach((id, simhash) -> {
        //    if (!rssContentItemMap.containsKey(id)) {
        //        return;
        //    }
        //
        //    AtomicReference<Double> avgSimScore = new AtomicReference<>((double) 0);
        //    SimhashDuplicateItemList simhashDuplicateXmlItemList = new SimhashDuplicateItemList();
        //    List<RSSContentItem> rssContentItemList = simhashDuplicateXmlItemList.getRssContentItemList();
        //    contentItemSimhashMap.forEach((innerId, innerSimhash) -> {
        //        if (!rssContentItemMap.containsKey(innerId) || id.equals(innerId)) {
        //            return;
        //        }
        //        // 汉明距离
        //        int hammingDistance = duplicateRemoveProcess.hammingDistance(simhash, innerSimhash);
        //        int maxDistance = simhash.length();
        //        double score = (1 - hammingDistance / (double) maxDistance);
        //        // 判断为文本相似
        //        if (score >= scoreThresholdRate) {
        //            rssContentItemMap.remove(id);
        //            // 相似的内容项加入到同一个列表中
        //            if (rssContentItemMap.containsKey(id)){
        //                rssContentItemList.add(rssContentItemMap.get(id));
        //            }
        //            rssContentItemList.add(rssContentItemMap.get(innerId));
        //            rssContentItemMap.remove(innerId);
        //            double nextScore = avgSimScore.get() + score;
        //            avgSimScore.updateAndGet(v -> nextScore);
        //        }
        //    });
        //    if (!rssContentItemList.isEmpty()) {
        //        avgSimScore.updateAndGet(v->v/rssContentItemList.size());
        //        simhashDuplicateXmlItemList.setScore(avgSimScore.get());
        //        duplicateQueue.add(simhashDuplicateXmlItemList);
        //    }
        //});

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("originContent", rssContentItemMap);
        resultMap.put("remainContent", remainIdSet);
        resultMap.put("duplisContent", simIdSetList);
        return resultMap;
    }

    public Object rssItemRegxFilter(String rssId) {
        RSSSource rssSource = rssSourceRepository.findById(Integer.parseInt(rssId)).orElse(null);
        if (rssSource == null) {
            return null;
        }
        List<String> regxFilterPattern = RssJsonExtraFieldsHelp.regxFilterPattern(rssSource);

        List<Pattern> patternList = regxFilterPattern.stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
        List<RSSContentItem> contentItems = rssSource.getContentItems();
        contentItems.removeIf(contentItem -> {
            for (Pattern pattern : patternList) {
                Matcher matcher = pattern.matcher(contentItem.getDescParse());
                if (matcher.matches()) {
                    // true then remove
                    return true;
                }
            }
            return false;
        });

        return contentItems;
    }

    /**
     * 重复的内容项
     */
    @Data
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @Builder
    public static class SimhashDuplicateItemList implements Comparable<SimhashDuplicateItemList> {
        private double score;
        private List<RSSContentItem> rssContentItemList;

        public SimhashDuplicateItemList() {
            rssContentItemList = Lists.newArrayList();
        }

        @Override
        public int compareTo(@NotNull SimhashDuplicateItemList o) {
            return Double.compare(this.score, o.score);
        }
    }
}
