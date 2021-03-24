package com.highestpeak.dimlight.model.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author highestpeak
 */
@Data
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_id")
    @GenericGenerator(name = "custom_id", strategy = "com.highestpeak.dimlight.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * Create time.
     */
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * Update time.
     */
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @PrePersist
    protected void prePersist() {
        Date now = new Date();
        if (createTime == null) {
            createTime = now;
        }

        if (updateTime == null) {
            updateTime = now;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updateTime = new Date();
    }

    @PreRemove
    protected void preRemove() {
        updateTime = new Date();
    }
}
