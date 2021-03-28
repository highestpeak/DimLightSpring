package com.highestpeak.dimlight.model.pojo;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author highestpeak
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class RSSXml {
    public static final RSSXml DEFAULT_RSS_XML = new RSSXml();

    private String title;
    private String description;
    private String link;
    private String copyright;

    /**
     * todo 时区问题
     */
    private Date pubDate;
    private String ttl;
    private RSSXmlImage image;
    private String language;
    private String generator;
    private List<String> category;
    private List<RSSXmlItem> items;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RSSXmlImage {
        private String title;
        private String link;
        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RSSXmlItem {
        private String title;
        /**
         * future: 有的 entry 的 description 包含 image 标签 这个需要在呈现端即android上呈现出来
         * https://rsshub.app/bilibili/bangumi/media/9192 例如这个就有
         */
        private String description;
        private String link;
        private String guid;
        private Date pubDate;
        private List<String> category;
        private String author;
        private String comments;
    }

    public String get(String key) {
        // future: 通过反射获取各个变量
        return "value:not impl";
    }

    public static boolean isRSSXMLNotGet(RSSXml rssXml) {
        return rssXml == null || rssXml == DEFAULT_RSS_XML;
    }
}
