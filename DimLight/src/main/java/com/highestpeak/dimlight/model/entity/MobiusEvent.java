package com.highestpeak.dimlight.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity(name = "mobius_event")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobiusEvent extends BaseEntity{
    /**
     * 事件名称
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 触发事件的Task的id
     */
    @Column(name = "trigger_task_id", nullable = false)
    private Integer triggerTaskId;

    /**
     * 触发事件的信息源，例如rss的id、爬虫的id
     */
    @Column(name = "trigger_source_id", nullable = false)
    private Integer triggerSourceId;

    /**
     * 事件信息
     */
    @Lob
    @Column(name = "json_msg", nullable = false)
    private String jsonMsg;

    /**
     * {@link com.highestpeak.dimlight.model.enums.LogLevel}
     */
    @Column(name = "log_level", nullable = false)
    private Integer logLevel;
}
