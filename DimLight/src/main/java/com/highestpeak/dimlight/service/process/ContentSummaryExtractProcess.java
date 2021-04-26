package com.highestpeak.dimlight.service.process;

import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.support.TextRank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 文档摘要生成
 * reference: https://github.com/PKULCWM/PKUSUMSUM
 */
@Component
public class ContentSummaryExtractProcess implements InfoProcess {
    @Value("${docSummaryMaxLen:256}")
    private int docSummaryMaxLen = 256;

    @Override
    public void process(ProcessContext processContext) {
        if (processContext.getSummaryMap() == null) {
            processContext.setSummaryMap(Maps.newHashMap());
        }
        Map<Integer, String> summaryMap = processContext.getSummaryMap();
        TextRank textrank = new TextRank();
        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();
        for (Map.Entry<Integer, RSSXml.RSSXmlItem> xmlItem : xmlItemList.entrySet()) {
            RSSXml.RSSXmlItem rssXmlItem = xmlItem.getValue();
            TextRank.TextRankParams textRankParams = TextRank.TextRankParams.builder()
                    .inputContent(rssXmlItem.getDescription())
                    .maxLen(docSummaryMaxLen)
                    .build();
            try {
                String summarize = textrank.summarize(textRankParams);
                summaryMap.put(xmlItem.getKey(), summarize);
            } catch (IOException e) {
                if (processContext.getInfoMessages() == null) {
                    processContext.setInfoMessages(new InfoMessages());
                }
                processContext.getInfoMessages().addErrorMsg("文档摘要生成错误 contentItem:" + xmlItem.getKey());
            }
        }
    }
}
