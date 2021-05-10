package com.highestpeak.dimlight;

import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.RSSContentItemRepository;
import com.highestpeak.dimlight.service.process.ContentSummaryExtractProcess;
import com.highestpeak.dimlight.service.process.HtmlTagRemoveProcess;
import com.highestpeak.dimlight.support.TextRank;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class DimLightRssAdminApplicationTests {

    @Resource
    private RSSContentItemRepository contentItemRepository;
    @Resource
    private HtmlTagRemoveProcess htmlTagRemoveProcess;

    @Test
    void contextLoads() {
        //RSSContentItem rssContentItem = contentItemRepository.findById(51).orElse(null);
        //String htmlDesc = rssContentItem.getDescParse();
        //String parsedHtmlDesc = Jsoup.parse(htmlDesc).text();
        //// 双向最大匹配算法：BidirectionalMaximumMatching即Ngram算法
        //List<Word> words = WordSegmenter.seg(parsedHtmlDesc, SegmentationAlgorithm.BidirectionalMaximumMatching);
        //System.out.println("end");
        // resort
        // ,,,
        ////词频统计设置
        //WordFrequencyStatistics wordFrequencyStatistics = new WordFrequencyStatistics();
        //wordFrequencyStatistics.setRemoveStopWord(true);
        //wordFrequencyStatistics.setSegmentationAlgorithm(SegmentationAlgorithm.MaxNgramScore);
        //wordFrequencyStatistics.reset();
        ////开始分词
        //wordFrequencyStatistics.seg(parsedHtmlDesc);
        //wordFrequencyStatistics.dump();
    }

    @Value("${docSummaryMaxLen:256}")
    private int docSummaryMaxLen = 256;

    @Test
    void extractSummary() {
        RSSContentItem rssContentItem = contentItemRepository.findById(51).orElse(null);
        if (rssContentItem==null) {
            return;
        }
        TextRank.TextRankParams textRankParams = TextRank.TextRankParams.builder()
                .inputContent(rssContentItem.getDescParse())
                .maxLen(docSummaryMaxLen)
                .build();
        TextRank textrank = new TextRank();
        try {
            String summarize = textrank.summarize(textRankParams);
            System.out.println("test");
        } catch (IOException e) {
            System.out.println("文档摘要生成错误 contentItem:" + rssContentItem.getId());
        }
        System.out.println("test");
    }


    @Test
    void findSourceByUrl() {
    }

    @Test
    void findSourceByName() {
    }

    @Test
    void findTagByNameList() {
    }

    @Test
    void findTaskByType() {
    }

    @Test
    void findTaskByTypeAndExcutor() {
    }

    @Test
    void findItemByGuid() {
    }

    @Test
    void delOutOfTimeContentItem() {
    }

    @Test
    void fetchRss() {
    }

    @Test
    void fetchRssProxy() {
    }

    @Test
    void fetchRssCron() {
    }

    @Test
    void terminateTask() {
    }

    @Test
    void delTask() {
    }

    @Test
    void resumeTask() {
    }

    @Test
    void segmentTitle() {
    }

    @Test
    void segmentDescContent() {
    }

    @Test
    void regxFilter() {
    }

    @Test
    void htmlTagRemove() {
    }

    @Test
    void resortNews() {
    }

}
