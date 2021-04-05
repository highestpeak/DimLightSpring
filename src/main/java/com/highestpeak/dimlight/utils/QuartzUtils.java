package com.highestpeak.dimlight.utils;

import org.quartz.*;

import java.util.Map;

public class QuartzUtils {
    public static JobDataMap getJobDataMap(Map<?, ?> map) {
        return map == null ? new JobDataMap() : new JobDataMap(map);
    }

    /**
     * 获取定时任务的定义
     * JobDetail是任务的定义,Job是任务的执行逻辑
     *
     * @param jobKey      定时任务的名称组名
     * @param description 定时任务的描述
     * @param jobDataMap  定时任务的元数据
     * @param jobClass    {@link org.quartz.Job} 定时任务真正执行逻辑定义类
     */
    public static JobDetail getJobDetail(JobKey jobKey, String description, JobDataMap jobDataMap, Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .withDescription(description)
                .setJobData(jobDataMap)
                .usingJobData(jobDataMap)
                .requestRecovery() // 当recovery和fail-over情形发生时重新执行任务
                .storeDurably() // 当没有trigger指向时,作业不被删除而被保留
                .build();
    }

    /**
     * 获取Trigger(Job的触发器,执行规则)
     *
     * @param jobKey         定时任务的名称组名
     * @param description    定时任务的描述
     * @param jobDataMap     定时任务的元数据
     * @param cronExpression 定时任务的执行cron表达式
     */
    public static Trigger getTrigger(JobKey jobKey, String description, JobDataMap jobDataMap, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withDescription(description)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .usingJobData(jobDataMap)
                .build();
    }
}
