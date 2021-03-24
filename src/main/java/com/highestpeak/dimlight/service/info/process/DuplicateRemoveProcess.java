package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;

import java.util.List;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 去重
 * https://www.elastic.co/guide/cn/elasticsearch/guide/current/match-multi-word.html
 * 一定时间内（一天内、一周内），相似度去重
 *
 */
public class DuplicateRemoveProcess implements InfoProcess{
    @Override
    public List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource) {
        return rssXmlItemList;
    }
}
