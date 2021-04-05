package com.highestpeak.dimlight.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity(name = "topic")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Topic extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "desc_user")
    private String descUser;

    @ToString.Exclude
    @JsonIgnoreProperties("rssTopics")
    @ManyToMany(mappedBy = "rssTopics")
    private List<RSSSource> rssSources;

    @ToString.Exclude
    @JsonIgnoreProperties("itemTopics")
    @ManyToMany(mappedBy = "itemTopics")
    private List<RSSContentItem> rssContentItems;

    public Topic removeItemsFromEntity() {
        this.rssSources.clear();
        this.rssContentItems.clear();
        return this;
    }
}
