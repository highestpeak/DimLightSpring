package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import com.highestpeak.dimlight.repository.ESContentRepository;

import java.util.List;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 根据一些参数进行评分，例如所有rss公用的评分：在同一个时间段内出现的次数、点赞数等
 * 时效性 等等（第一个提出来的就分值高）
 */
public class QualityEvalProcess implements InfoProcess{
    @Override
    public List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource, ESContentRepository esContentRepository) {
        return rssXmlItemList;
    }
}
