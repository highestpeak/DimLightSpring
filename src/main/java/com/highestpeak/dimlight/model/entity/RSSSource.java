package com.highestpeak.dimlight.model.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 * RSS来源(可以添加的RSS订阅)（用户可控的）
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "JpaDataSourceORMInspection"})
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

    @Column(name = "desc_user")
    private String descUser;

    @Column(name = "desc_parse")
    private String descParse;

    /**
     * 网站主页链接
     */
    @Column(name = "link")
    private String link;

    /**
     * 描述图片连接
     */
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
    @OneToMany(mappedBy = "rssSource")
    private List<RSSContentItem> contentItems;

    /**
     * 标签
     */
    @ManyToMany
    @JoinTable(
            name = "source_and_tag",
            joinColumns = @JoinColumn(name = "source_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id"))
    private List<RSSSourceTag> rssSourceTags;

    /**
     * 分组/主题：topic
     */
    @ManyToMany
    @JoinTable(
            name = "source_and_topic",
            joinColumns = @JoinColumn(name = "source_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> rssTopics;
}
