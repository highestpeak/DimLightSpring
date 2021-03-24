package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;

import java.util.List;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 信息组合
 * 基于相似度聚合（标题相同，但是有不同的内容）
 */
public class CombineInfoProcess implements InfoProcess{
    @Override
    public List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource) {
        return rssXmlItemList;
    }
}
