package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 去除所有html标签
 * https://stackoverflow.com/questions/240546/remove-html-tags-from-a-string/4095615
 */
@Component
public class HtmlTagRemoveProcess implements InfoProcess {
    @Override
    public void process(ProcessContext processContext) {
        // 去除所有html标签, 采用jsoup去除
        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();
        for (Map.Entry<Integer, RSSXml.RSSXmlItem> xmlItemEntry : xmlItemList.entrySet()) {
            RSSXml.RSSXmlItem rssXmlItem = xmlItemEntry.getValue();
            String htmlDesc = rssXmlItem.getDescription();
            String parsedHtmlDesc = Jsoup.parse(htmlDesc).text();
            rssXmlItem.setDescription(parsedHtmlDesc);
        }
    }
}
