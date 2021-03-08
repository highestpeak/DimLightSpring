package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import com.highestpeak.dimlight.repository.ESContentRepository;

import java.util.List;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-08
 * 排序
 */
public class ResortService implements InfoProcess{
    @Override
    public List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource, ESContentRepository esContentRepository) {
        return rssXmlItemList;
    }
}
