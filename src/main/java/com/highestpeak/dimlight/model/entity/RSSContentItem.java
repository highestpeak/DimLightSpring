package com.highestpeak.dimlight.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author highestpeak
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "JpaDataSourceORMInspection"})
@Entity(name = "rss_content_item")
@Table(name = "rss_content_item", indexes = {
        @Index(name = "rss_content_item_pub_date", columnList = "pub_date"),
        @Index(name = "rss_content_item_author", columnList = "author"),
        @Index(name = "rss_content_item_title", columnList = "title_parse")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RSSContentItem extends BaseEntity{

    @Column(name = "title_parse", nullable = false)
    private String titleParse;

    @Column(name = "desc_parse", nullable = false)
    private String descParse;

    /**
     * 新闻链接 RSSContent链接
     */
    @Column(name = "link", nullable = false)
    private String link;

    /**
     * guid>GUID=Globally Unique Identifier 为当前新闻指定一个全球唯一标示
     */
    @Column(name = "guid", nullable = false)
    private String guid;

    /**
     * 新闻最后发布时间
     */
    @Column(name = "pub_date", nullable = false)
    private Date pubDate;

    /**
     * 作者
     */
    @Column(name = "author", nullable = false)
    private String author;

    /**
     * 可选的（除必要的标签外，所有的标签，都提取出来，保存到一个额外字段）
     * 例如 category、comments 等
     */
    @Column(name = "json_optional_extra_fields")
    private String jsonOptionalExtraFields;

    @ToString.Exclude
    @JsonIgnoreProperties("contentItems")
    @ManyToOne
    @JoinColumn(name="rss_source_id", nullable=false)
    private RSSSource rssSource;

    @ToString.Exclude
    @JsonIgnoreProperties("rssContentItems")
    @ManyToMany
    @JoinTable(
            name = "item_and_tag",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id"))
    private List<RSSSourceTag> rssItemTags;

    /**
     * 分组/主题：topic
     */
    @ToString.Exclude
    @JsonIgnoreProperties("rssContentItems")
    @ManyToMany
    @JoinTable(
            name = "item_and_topic",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private List<Topic> itemTopics;
}
