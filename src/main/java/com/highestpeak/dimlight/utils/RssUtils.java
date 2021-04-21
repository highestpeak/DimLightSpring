package com.highestpeak.dimlight.utils;

import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RssUtils {

    /**
     * @param syndFeed rome 格式
     * @return 内部格式
     */
    public static RSSXml syndFeedToRSSXml(SyndFeed syndFeed) {
        // 针对多种不同的rss进行多种测试，多测试一些rssurl，把这里做的健壮一点
        RSSXml.RSSXmlBuilder rssXmlBuilder = RSSXml.builder();

        rssXmlBuilder.title(syndFeed.getTitle())
                .category(
                        syndFeed.getCategories().stream()
                                .map(SyndCategory::getName)
                                .collect(Collectors.toList())
                ).copyright(syndFeed.getCopyright())
                .description(syndFeed.getDescription())
                .generator(syndFeed.getEncoding());

        SyndImage syndFeedImage = syndFeed.getImage();
        if (syndFeedImage != null) {
            RSSXml.RSSXmlImage rssXmlImage = RSSXml.RSSXmlImage.builder()
                    .title(syndFeedImage.getTitle())
                    .link(syndFeedImage.getLink())
                    .url(syndFeedImage.getUrl())
                    .build();
            rssXmlBuilder.image(rssXmlImage)
                    .language(syndFeed.getLanguage())
                    .link(syndFeed.getLink())
                    .pubDate(syndFeed.getPublishedDate());
        }

        List<SyndEntry> entryList = syndFeed.getEntries();
        List<RSSXml.RSSXmlItem> rssXmlItems = new ArrayList<>(entryList.size());
        for (SyndEntry syndEntry : entryList) {
            RSSXml.RSSXmlItem rssXmlItem = RSSXml.RSSXmlItem.builder()
                    .author(syndEntry.getAuthor())
                    .link(syndEntry.getLink())
                    .category(
                            syndEntry.getCategories().stream()
                                    .map(SyndCategory::getName)
                                    .collect(Collectors.toList())
                    )
                    .description(syndEntry.getDescription().getValue())
                    .pubDate(syndEntry.getPublishedDate())
                    .title(syndEntry.getTitle())
                    .guid(syndEntry.getUri()) // fixme: 必须保证guid全局唯一
                    .build();
            rssXmlItems.add(rssXmlItem);
        }
        rssXmlBuilder.items(rssXmlItems);

        return rssXmlBuilder.build();
    }
}
