package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import com.highestpeak.dimlight.repository.ESContentRepository;

import java.util.List;

/**
 * @author highestpeak
 */
public interface InfoProcess {
    /**
     * 流程处理的通用逻辑,返回处理后的 rssXmlItem
     * @param rssXmlItemList
     * @param rssSource
     * @param esContentRepository
     * @return
     */
    List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource, ESContentRepository esContentRepository);
}
