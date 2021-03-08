package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import com.highestpeak.dimlight.repository.ESContentRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 打上分组标签 标签
 */
public class TagProcess implements InfoProcess{
    @Override
    public List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource, ESContentRepository esContentRepository) {
        JSONArray sourceTags = new JSONObject(rssSource.getJsonOptionalExtraFields()).getJSONArray("itemTags");
        for (RSSContentItemProcess rssContentItem : rssXmlItemList) {
            JSONObject itemJson = new JSONObject(rssContentItem.getJsonOptionalExtraFields());
            JSONArray tags = itemJson.getJSONArray("tags");
            for (int i = 0; i < sourceTags.length(); i++) {
                tags.put(sourceTags.get(i));
            }
        }
        return rssXmlItemList;
    }
}
