package com.highestpeak.dimlight.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity(name = "event")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity{
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "trigger_name", nullable = false)
    private String trigger;

    @Lob
    @Column(name = "json_msg", nullable = false)
    private String jsonMsg;

    @Column(name = "has_error", nullable = false)
    private boolean hasError;
}
