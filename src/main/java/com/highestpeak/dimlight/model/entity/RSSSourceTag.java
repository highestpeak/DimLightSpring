package com.highestpeak.dimlight.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "JpaDataSourceORMInspection"})
@Entity(name = "rss_source_tag")
@Table(name = "rss_source_tag", indexes = {
        @Index(name = "rss_source_tag_name", columnList = "name")})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RSSSourceTag extends BaseEntity implements Comparable<RSSSourceTag> {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "desc_user")
    private String descUser;

    @ToString.Exclude
    @JsonIgnoreProperties("rssSourceTags")
    @ManyToMany(mappedBy = "rssSourceTags")
    private List<RSSSource> rssSources;

    @ToString.Exclude
    @JsonIgnoreProperties("rssItemTags")
    @ManyToMany(mappedBy = "rssItemTags")
    private List<RSSContentItem> rssContentItems;

    @Override
    public int compareTo(@NotNull RSSSourceTag o) {
        return name.compareTo(o.name);
    }

    public RSSSourceTag removeItemsFromEntity() {
        this.rssSources.clear();
        this.rssContentItems.clear();
        return this;
    }
}
