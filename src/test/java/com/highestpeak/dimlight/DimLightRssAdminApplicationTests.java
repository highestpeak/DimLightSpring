package com.highestpeak.dimlight;

import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.RSSContentItemRepository;
import com.highestpeak.dimlight.service.process.HtmlTagRemoveProcess;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
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
		RSSContentItem rssContentItem = contentItemRepository.findById(51).orElse(null);
		//ProcessContext processContext = ProcessContext.builder().build();
		//htmlTagRemoveProcess.process();
		//List<ProcessContext.XmlItemWithId> xmlItemList = processContext.getXmlItemList();
		//for (ProcessContext.XmlItemWithId xmlItemWithId : xmlItemList) {
		//	RSSXml.RSSXmlItem rssXmlItem = xmlItemWithId.getRssXmlItem();
			String htmlDesc = rssContentItem.getDescParse();
			String parsedHtmlDesc = Jsoup.parse(htmlDesc).text();
			//rssXmlItem.setDescription(parsedHtmlDesc);
			System.out.println("end test");
		//}
	}

}
