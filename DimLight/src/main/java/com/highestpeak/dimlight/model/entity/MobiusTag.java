package com.highestpeak.dimlight.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.List;

/**
 * @author highestpeak
 * 小标签，作为标注信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity(name = "mobius_tag")
@Table(name = "mobius_tag", indexes = {
        @Index(name = "mobius_tag_name", columnList = "name")})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobiusTag extends BaseEntity implements Comparable<MobiusTag> {

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "desc_user")
    private String descUser;

    @ToString.Exclude
    @JsonIgnoreProperties("mobiusTags")
    @ManyToMany(mappedBy = "mobiusTags")
    private List<RSSSource> rssSources;

    @ToString.Exclude
    @JsonIgnoreProperties("mobiusTags")
    @ManyToMany(mappedBy = "mobiusTags")
    private List<MobiusTopic> topics;

    @Override
    public int compareTo(@NotNull MobiusTag o) {
        return name.compareTo(o.name);
    }

}
