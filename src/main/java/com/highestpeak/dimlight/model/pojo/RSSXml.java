package com.highestpeak.dimlight.model.pojo;

import lombok.*;

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
    private String title;
    private String description;
    private String link;
    private String copyright;
    private String pubDate;
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
    public static class RSSXmlImage{
        private String title;
        private String link;
        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RSSXmlItem{
        private String title;
        private String description;
        private String link;
        private String guid;
        private String pubDate;
        private String category;
        private String author;
        private String comments;
    }

    public String get(String key){
        // todo: 通过反射获取各个变量
        return "value:not impl";
    }
}
