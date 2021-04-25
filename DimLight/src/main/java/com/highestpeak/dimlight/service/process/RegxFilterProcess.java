package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.support.RssJsonExtraFieldsHelp;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 */
@Component
public class RegxFilterProcess implements InfoProcess {
    @Override
    public void process(ProcessContext processContext) {
        RSSSource rssSource = processContext.getRssSource();
        List<String> regxFilterPattern = RssJsonExtraFieldsHelp.regxFilterPattern(rssSource);

        List<Pattern> patternList = regxFilterPattern.stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();
        xmlItemList.entrySet().removeIf(xmlItemEntry -> {
            RSSXml.RSSXmlItem rssXmlItem = xmlItemEntry.getValue();
            for (Pattern pattern : patternList) {
                Matcher matcher = pattern.matcher(rssXmlItem.getDescription());
                if (matcher.matches()) {
                    // true then remove
                    return true;
                }
            }
            return false;
        });
    }
}
