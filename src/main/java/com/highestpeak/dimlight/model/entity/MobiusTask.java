package com.highestpeak.dimlight.model.entity;

import com.highestpeak.dimlight.model.enums.TaskEnum;
import com.highestpeak.dimlight.model.enums.TaskOutputCacheCycleEnum;
import com.highestpeak.dimlight.model.enums.TaskStatus;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * @author highestpeak
 * 不通过quartz来进行task的crud是因为两个原因：
 *  1. 可能通过外部的任务管理器来管理任务，如huginn
 *  2. quartz的表应当是仅是quartz紧密相关的quartz内部自己需要的，隔离两个系统
 * 一个 task 可以是通过 json rpc、flask rest api 等调用的比如 python 的东西，不一定必须对应一个源，许多task可以形成一条链，即它的处理链
 */
@Entity(name = "mobius_task")
@Table(name = "mobius_task", indexes = {
        @Index(name = "task_schedule", columnList = "schedule_cron"),
        @Index(name = "task_name", columnList = "name")})
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobiusTask extends BaseEntity {

    /**
     * 每隔多长时间进行一次调度
     * cron 表达式
     */
    @Column(name = "schedule_cron", nullable = false)
    private String schedule;

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 默认组名 default
     */
    @Column(name = "group_name")
    @ColumnDefault("'default'")
    private String group;

    @Lob
    @Column(name = "desc_user")
    private String descUser;

    /**
     * 缓存信息保存多长时间
     * {@link TaskOutputCacheCycleEnum#DEFAULT_VALUE}
     */
    @Column(name = "cache_cycle", nullable = false)
    @ColumnDefault("7")
    private int cacheCycle;

    /**
     * 上次任务时间
     * 和updateTime不同,updateTime是该Task的配置的更新时间,lastEvent是上次激发任务的时间
     */
    @Column(name = "last_event")
    @Temporal(TemporalType.TIMESTAMP)
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

    /**
     * @see TaskStatus
     * {@link TaskStatus#DEFAULT_VALUE}
     */
    @Column(name = "status", nullable = false)
    @ColumnDefault("1")
    private int status;

    /**
     * 任务类型（具体什么任务）
     * 可以通过该字段判断数据源是哪里，进而读取taskOperator的值
     * @see com.highestpeak.dimlight.model.enums.TaskEnum
     */
    @Column(name = "task_type", nullable = false)
    @ColumnDefault("1")
    private int taskType;

    /**
     * 任务和哪种源互相关联，是RSS还是爬虫还是定时
     */
    @Column(name = "task_operator", nullable = false)
    private int taskOperator;

    /**
     * 是否是RSS产生的task
     */
    public boolean isSourceRss() {
        return TaskEnum.rssSourceTaskType().contains(taskType);
    }
}
