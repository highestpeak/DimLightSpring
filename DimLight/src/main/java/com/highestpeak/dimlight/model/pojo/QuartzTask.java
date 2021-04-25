package com.highestpeak.dimlight.model.pojo;

import lombok.Builder;
import lombok.Data;
import org.quartz.Job;
import org.quartz.JobKey;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Quartz job 所需的属性封装
 */
@Data
@Builder
public class QuartzTask {

    /**
     * 任务默认组名
     */
    public static final String DEFAULT_GROUP_NAME = "default";

    /**
     * 定时任务的名字和分组名
     */
    @NotNull(message = "定时任务的名字和组名不能为空")
    private JobKey jobKey;

    /**
     * 定时任务的描述(可以定时任务本身的描述,也可以是触发器的)
     * {@link org.quartz.JobDetail} description ; {@link org.quartz.Trigger} description
     */
    private String description;

    /**
     * 定时任务的执行cron (Trigger的CronScheduleBuilder的cronExpression)
     * {@link org.quartz.Trigger} CronScheduleBuilder {@link org.quartz.CronScheduleBuilder}
     */
    @NotEmpty(message = "定时任务的执行cron不能为空")
    private String cronExpression;

    /**
     * 定时任的元数据
     * {@link org.quartz.JobDataMap}
     */
    private Map<?, ?> jobDataMap;

    /**
     * 定时任务的具体执行逻辑类
     * {@link org.quartz.Job}
     */
    @NotNull(message = "定时任务的具体执行逻辑类不能为空")
    private Class<? extends Job> jobClass;
}
