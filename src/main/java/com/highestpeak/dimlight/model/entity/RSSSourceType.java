package com.highestpeak.dimlight.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 * 这个 type 可作为最后的标签继承
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Entity(name = "rss_source_type")
@Table(name = "rss_source_type", indexes = {
        @Index(name = "rss_source_type_name", columnList = "name")})
@Data
@Builder
@AllArgsConstructor
public class RSSSourceType implements Comparable<RSSSourceType> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_id")
    @GenericGenerator(name = "custom_id", strategy = "com.highestpeak.dimlight.model.entity.support.CustomIdGenerator")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "desc")
    private String desc;

    @ManyToMany(mappedBy = "rssSourceTypes")
    private List<RSSSource> rssSources;

    @Override
    public int compareTo(@NotNull RSSSourceType o) {
        return name.compareTo(o.name);
    }
}
