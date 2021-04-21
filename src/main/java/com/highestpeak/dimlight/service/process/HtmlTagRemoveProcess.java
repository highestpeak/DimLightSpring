package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 去除所有html标签
 * https://stackoverflow.com/questions/240546/remove-html-tags-from-a-string/4095615
 */
@Component
public class HtmlTagRemoveProcess implements InfoProcess {
    @Override
    public void process(ProcessContext processContext) {
        // 去除所有html标签, 采用jsoup去除
        List<ProcessContext.XmlItemWithId> xmlItemList = processContext.getXmlItemList();
        for (ProcessContext.XmlItemWithId xmlItemWithId : xmlItemList) {
            RSSXml.RSSXmlItem rssXmlItem = xmlItemWithId.getRssXmlItem();
            String htmlDesc = rssXmlItem.getDescription();
            String parsedHtmlDesc = Jsoup.parse(htmlDesc).text();
            rssXmlItem.setDescription(parsedHtmlDesc);
        }
    }
}
