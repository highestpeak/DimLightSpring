package com.highestpeak.dimlight.model.entity;

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
public class Topic extends BaseEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "desc")
    private String desc;

    @ManyToMany(mappedBy = "rssTopics")
    private List<RSSSource> rssSources;

    @ManyToMany(mappedBy = "itemTopics")
    private List<RSSContentItem> rssContentItems;
}
