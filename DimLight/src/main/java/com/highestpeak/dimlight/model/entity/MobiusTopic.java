package com.highestpeak.dimlight.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 * Topic是一个极其相关的信息源的group
 * 本质上Topic才是对用户用的信息源
 */
@Entity(name = "mobius_topic")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobiusTopic extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "desc_user")
    private String descUser;

    @ToString.Exclude
    @JsonIgnoreProperties("rssMobiusTopics")
    @ManyToMany(mappedBy = "rssMobiusTopics", fetch = FetchType.LAZY)
    private List<RSSSource> rssSources;

    /**
     * 一个Topic是一个聚合，所以也需要标签标明聚合的一些特征
     */
    @ToString.Exclude
    @JsonIgnoreProperties({"rssSources", "topics"})
    @ManyToMany
    @JoinTable(
            name = "topic_and_tag",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<MobiusTag> mobiusTags;

    /**
     * 其他控制字段
     */
    @Lob
    @Column(name = "json_optional_extra_fields")
    private String jsonOptionalExtraFields;
}
