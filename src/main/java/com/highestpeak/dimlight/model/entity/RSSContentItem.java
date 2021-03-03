package com.highestpeak.dimlight.model.entity;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author highestpeak
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Entity(name = "rss_content_item")
@Table(name = "rss_content_item", indexes = {
        @Index(name = "rss_content_item_pub_date", columnList = "pub_date"),
        @Index(name = "rss_content_item_author", columnList = "author"),
        @Index(name = "rss_content_item_title", columnList = "title_parse")})
@Data
@Builder
public class RSSContentItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_id")
    @GenericGenerator(name = "custom_id", strategy = "com.highestpeak.dimlight.model.entity.support.CustomIdGenerator")
    private Integer id;

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
     * tmpdoc: 可以从RSS来源继承，也可以解析item的标签，也可以用户指定
     *  在解析出一个RSSContentItem的时候再设置这个项
     */
    @Column(name = "author", nullable = false)
    private String author;

    /**
     * 可选的（除必要的标签外，所有的标签，都提取出来，保存到一个额外字段）
     * 例如 category、comments 等
     */
    @Column(name = "json_optional_extra_fields")
    private String jsonOptionalExtraFields;

    @ManyToOne
    @JoinColumn(name="rss_source_id", nullable=false)
    private RSSSource rssSource;
}
