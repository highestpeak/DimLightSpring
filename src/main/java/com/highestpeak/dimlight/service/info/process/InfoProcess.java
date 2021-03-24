package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;

import java.util.List;

/**
 * @author highestpeak
 */
public interface InfoProcess {
    /**
     * 流程处理的通用逻辑,返回处理后的 rssXmlItem
     * @param rssXmlItemList
     * @param rssSource
     * @return
     */
    List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource);
}
