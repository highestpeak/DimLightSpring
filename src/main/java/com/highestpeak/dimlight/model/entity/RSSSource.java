package com.highestpeak.dimlight.model.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 * RSS来源(可以添加的RSS订阅)（用户可控的）
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Entity(name = "rss_source")
@Table(name = "rss_source", indexes = {
        @Index(name = "rss_source_generator", columnList = "generator"),
        @Index(name = "rss_source_title_user", columnList = "title_user")})
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RSSSource extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_id")
    @GenericGenerator(name = "custom_id", strategy = "com.highestpeak.dimlight.model.entity.support.CustomIdGenerator")
    private Integer id;

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

    @Column(name = "desc_user", nullable = false)
    private String descUser;

    @Column(name = "desc_parse", nullable = false)
    private String descParse;

    /**
     * 网站主页链接
     */
    @Column(name = "link", nullable = false)
    private String link;

    /**
     * 描述图片连接
     */
    @Column(name = "image")
    private String image;

    /**
     * RSSSource 作者
     */
    @Column(name = "generator", nullable = false)
    private String generator;

    /**
     * 可选的（除 item 上述标签 外，所有的标签，保存到的一个额外字段）
     * 例如 copyright、pubDate、ttl、language、generator、category
     */
    @Column(name = "json_optional_extra_fields")
    private String jsonOptionalExtraFields;

    /**
     * 由这个源生成的内容项
     */
    @OneToMany(mappedBy = "rssSource")
    private List<RSSContentItem> contentItems;

    @ManyToMany
    @JoinTable(
            name = "source_type",
            joinColumns = @JoinColumn(name = "source_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id"))
    private List<RSSSourceType> rssSourceTypes;
}
