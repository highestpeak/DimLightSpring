package com.highestpeak.dimlight.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "JpaDataSourceORMInspection"})
@Entity(name = "rss_source_tag")
@Table(name = "rss_source_tag", indexes = {
        @Index(name = "rss_source_tag_name", columnList = "name")})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RSSSourceTag implements Comparable<RSSSourceTag> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_id")
    @GenericGenerator(name = "custom_id", strategy = "com.highestpeak.dimlight.model.entity.support.CustomIdGenerator")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "desc")
    private String desc;

    @ManyToMany(mappedBy = "rssSourceTags")
    private List<RSSSource> rssSources;

    @ManyToMany(mappedBy = "rssItemTags")
    private List<RSSContentItem> rssContentItems;

    @Override
    public int compareTo(@NotNull RSSSourceTag o) {
        return name.compareTo(o.name);
    }
}
