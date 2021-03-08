package com.highestpeak.dimlight.model.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * spring 设置了 rssSource 不可抓取，但是 huginn 没有取消定时任务
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity(name = "out_of_time_task_warning")
@Table(name = "rss_source", indexes = {
        @Index(name = "rss_source_generator", columnList = "generator"),
        @Index(name = "rss_source_title_user", columnList = "title_user")})
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OutOfTimeTaskWarning extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom_id")
    @GenericGenerator(name = "custom_id", strategy = "com.highestpeak.dimlight.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * RSS Source 地址
     */
    @Column(name = "url", nullable = false)
    private String url;
}
