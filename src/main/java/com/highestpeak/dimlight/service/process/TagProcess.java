package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.stereotype.Component;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 打上分组标签 标签
 */
@Component
public class TagProcess implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {
        //JSONArray sourceTags = new JSONObject(rssSource.getJsonOptionalExtraFields()).getJSONArray("itemTags");
        //for (RSSContentItemProcess rssContentItem : processContext) {
        //    JSONObject itemJson = new JSONObject(rssContentItem.getJsonOptionalExtraFields());
        //    JSONArray tags = itemJson.getJSONArray("tags");
        //    for (int i = 0; i < sourceTags.length(); i++) {
        //        tags.put(sourceTags.get(i));
        //    }
        //}
        //return processContext;
    }
}
