package com.highestpeak.dimlight.model.entity;

import com.highestpeak.dimlight.model.enums.TaskStatus;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author highestpeak
 * RSS爬取任务（一个RSS来源必须对应一个任务，一个任务可以不对应一个RSS来源，这个任务可以是聚合任务）（用户可控的）
 * 一个 task 可以是通过 json rpc、flask rest api 等调用的比如 python 的东西，不一定必须对应一个源，许多task可以形成一条链，即它的处理链
 */
@Entity(name = "task")
@Table(name = "task", indexes = {
        @Index(name = "task_schedule", columnList = "schedule"),
        @Index(name = "task_title", columnList = "title")})
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity{

    /**
     * 每隔多长时间进行一次调度
     * 调度时间在几个可供选择的时间点进行,也可以选择自定义
     * 提供几个可选择项主要是为了降低难度
     * cron 表达式
     */
    @Column(name = "schedule", nullable = false)
    private String schedule;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "desc")
    private String desc;

    /**
     * 生命周期，可以存活多长时间
     * 例如可以设置来保证可以一个短的时间段内有用的任务，比如我需要在一周内关注一个词，就可以设置为一周
     * 例如根据动态监测话题，来动态设置这个值
     * 使用秒数代表时间段（从时间点A到时间点B） 使用特殊数字表示永久 例如 -1 等
     */
    @Column(name = "life_cycle", nullable = false)
    @ColumnDefault("-1")
    private int lifeCycle;

    /**
     * 缓存信息保存多长时间
     * 使用秒数代表时间段（从时间点A到时间点B） 使用特殊数字表示永久 例如 -1 0等
     */
    @Column(name = "cache_cycle", nullable = false)
    @ColumnDefault("0")
    private int cacheCycle;

    /**
     * 上次任务时间
     * 和 update 不同,update 是该 Task 的设置的更新时间, lastEvent 是上次激发任务的时间
     */
    @Column(name = "last_event")
    private Date lastEvent;

    /**
     * 自动下载新数据 是/否
     */
    @Column(name = "auto_download", nullable = false)
    @ColumnDefault("false")
    private boolean autoDownload;

    /**
     * 总共激发的任务的数量
     */
    @Column(name = "title_parse", nullable = false)
    @ColumnDefault("0")
    private long eventsCreated;

    @Column(name = "status", nullable = false)
    @ColumnDefault(value = TaskStatus.DEFAULT_VALUE)
    private TaskStatus status;
}
