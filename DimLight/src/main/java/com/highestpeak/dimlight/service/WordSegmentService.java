package com.highestpeak.dimlight.service;

import org.apdplat.word.corpus.Bigram;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.SegmentationFactory;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.PartOfSpeechTagging;
import org.apdplat.word.util.WordConfTools;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class WordSegmentService {
    //分词器
    private final Segmentation segmentation =
            SegmentationFactory.getSegmentation(SegmentationAlgorithm.BidirectionalMaximumMatching);

    @PostConstruct
    private void init() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("初始化分词器");
        //WordSegmenter.seg("初始化分词器", SegmentationAlgorithm.BidirectionalMaximumMatching);
        //stopWatch.stop();
        //System.out.println("初始化分词器完成");
        //stopWatch.prettyPrint();
        //强制设置
        WordConfTools.set("dic.path", "classpath:nlpconfig/my_dic.txt,classpath:dic.txt");
        WordConfTools.set("stopwords.path", "classpath:nlpconfig/my_stopwords.txt,classpath:stopwords.txt");
        WordConfTools.set("ngram", "yes");
        WordConfTools.set("person.name.recognize", "true");
        WordConfTools.set("recognition.tool.enabled", "true");
        DictionaryFactory.reload();

        segmentation.seg("初始化这该死的分词器");
        stopWatch.stop();
        Bigram.reload();
        System.out.println("我他妈要看看这该死的分词器有没有加载成功");
        stopWatch.prettyPrint();
    }

    public List<Word> segString(String content) {
        List<Word> words = segmentation.seg(content);
        PartOfSpeechTagging.process(words);
        return words;
    }
}
