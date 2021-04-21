package com.highestpeak.dimlight.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 * RSS信息源
 */
@Entity(name = "rss_source")
@Table(name = "rss_source", indexes = {
        @Index(name = "rss_source_generator", columnList = "generator"),
        @Index(name = "rss_source_title_user", columnList = "title_user")})
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RSSSource extends BaseEntity {

    /**
     * RSS Source 地址
     */
    @Lob
    @Column(name = "url", nullable = false)
    private String url;

    /**
     * 用户编辑的 RSSSource 名称
     */
    @Column(name = "title_user", nullable = false)
    private String titleUser;

    /**
     * 从 xml 解析的 RSSSource 名称
     */
    @Column(name = "title_parse", nullable = false)
    private String titleParse;

    @Lob
    @Column(name = "desc_user")
    private String descUser;

    @Lob
    @Column(name = "desc_parse")
    private String descParse;

    /**
     * 网站主页链接
     */
    @Lob
    @Column(name = "link")
    private String link;

    /**
     * 描述图片连接
     */
    @Lob
    @Column(name = "image")
    private String image;

    /**
     * RSSSource 作者
     */
    @Column(name = "generator")
    private String generator;

    /**
     * 可选的（除 item 上述标签 外，所有的标签，保存到的一个额外字段）
     * 例如 copyright、pubDate、ttl、language、generator、category
     */
    @Lob
    @Column(name = "json_optional_extra_fields")
    private String jsonOptionalExtraFields;

    /**
     * 当可以 fetch 并且 huginn 设定了定时任务时
     * 才会触发
     */
    @Column(name = "fetch_able", nullable = false)
    private boolean fetchAble;

    /**
     * 由这个源生成的内容项
     */
    @ToString.Exclude
    @JsonIgnoreProperties("rssSource")
    @JsonIgnore
    @OneToMany(mappedBy = "rssSource", fetch = FetchType.LAZY)
    private List<RSSContentItem> contentItems;

    /**
     * 标签
     */
    @ToString.Exclude
    @JsonIgnoreProperties({"rssSources", "topics"})
    @ManyToMany
    @JoinTable(
            name = "rss_and_tag",
            joinColumns = @JoinColumn(name = "rss_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<MobiusTag> mobiusTags;

    /**
     * 分组/主题：topic
     */
    @ToString.Exclude
    @JsonIgnoreProperties({"rssSources", "mobiusTags"})
    @ManyToMany
    @JoinTable(
            name = "rss_and_topic",
            joinColumns = @JoinColumn(name = "rss_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<MobiusTopic> rssMobiusTopics;

}
