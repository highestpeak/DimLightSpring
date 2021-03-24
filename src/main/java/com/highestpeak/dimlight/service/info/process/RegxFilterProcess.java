package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 */
public class RegxFilterProcess implements InfoProcess {
    @Override
    public List<RSSContentItemProcess> process(List<RSSContentItemProcess> rssXmlItemList, RSSSource rssSource) {
        JSONObject filterRegx = new JSONObject(rssSource.getJsonOptionalExtraFields()).getJSONObject("filterRegx");
        String titleRegx = filterRegx.getString("titleRegx");
        String descriptionRegx = filterRegx.getString("descriptionRegx");
        String linkRegx = filterRegx.getString("linkRegx");
        String categoryRegx = filterRegx.getString("categoryRegx");
        String authorRegx = filterRegx.getString("authorRegx");
        return rssXmlItemList.stream()
                .filter(rssContentItem -> Pattern.matches(titleRegx, rssContentItem.getTitle()))
                .filter(rssContentItem -> Pattern.matches(descriptionRegx, rssContentItem.getDescription()))
                .filter(rssContentItem -> Pattern.matches(linkRegx, rssContentItem.getLink()))
                .filter(rssContentItem -> {
                    for (String category : rssContentItem.getCategory()) {
                        if (Pattern.matches(categoryRegx, category)) {
                            return true;
                        }
                    }
                    return false;
                })
                .filter(rssContentItem -> Pattern.matches(authorRegx, rssContentItem.getAuthor()))
                .collect(Collectors.toList());
    }
}
