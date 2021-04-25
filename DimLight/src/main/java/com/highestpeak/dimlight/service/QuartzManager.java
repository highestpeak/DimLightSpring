package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.pojo.QuartzTask;
import com.highestpeak.dimlight.utils.QuartzUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 参考https://github.com/EalenXie/springboot-quartz-simple
 */
@Service
public class QuartzManager {
    @Resource
    private Scheduler scheduler;

    /**
     * 创建和启动job
     * @param quartzTask job参数
     */
    public void scheduleJob(QuartzTask quartzTask) throws SchedulerException {
        //1.定时任务 的 名字和组名
        JobKey jobKey = quartzTask.getJobKey();
        //2.定时任务 的 元数据
        JobDataMap jobDataMap = QuartzUtils.getJobDataMap(quartzTask.getJobDataMap());
        //3.定时任务 的 描述
        String description = quartzTask.getDescription();
        //4.定时任务 的 逻辑实现类
        Class<? extends Job> jobClass = quartzTask.getJobClass();
        //5.定时任务 的 cron表达式
        String cron = quartzTask.getCronExpression();

        JobDetail jobDetail = QuartzUtils.getJobDetail(jobKey, description, jobDataMap, jobClass);
        Trigger trigger = QuartzUtils.getTrigger(jobKey, description, jobDataMap, cron);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 暂停job
     * 在Quartz中暂停作业不会暂停当前正在运行的作业，而只是阻止该作业在将来运行
     * fixme: 是否会把所有的trigger暂停，而影响那个trigger的其他任务
     */
    public void pauseJob(JobKey jobKey) throws SchedulerException {
        scheduler.pauseJob(jobKey);
    }

    /**
     * 重启job
     */
    public void resumeJob(JobKey jobKey) throws SchedulerException {
        scheduler.resumeJob(jobKey);
    }

    /**
     * 修改任务时间
     */
    public void modifyJobTimeAndRestart(QuartzTask quartzTask) throws SchedulerException {
        String cronExpression = quartzTask.getCronExpression();
        //1.如果cron表达式的格式不正确,则返回修改失败
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("cron表达式格式不正确");
        }
        JobKey jobKey = quartzTask.getJobKey();
        TriggerKey triggerKey = new TriggerKey(jobKey.getName(), jobKey.getGroup());
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        JobDataMap jobDataMap = QuartzUtils.getJobDataMap(quartzTask.getJobDataMap());
        //2.如果cron发生变化了,则按新cron触发 进行重新启动定时任务
        if (!cronTrigger.getCronExpression().equalsIgnoreCase(cronExpression)) {
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .usingJobData(jobDataMap)
                    .build();
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }

    public void removeJob(JobKey jobKey) throws SchedulerException {
        scheduler.deleteJob(jobKey);
    }

    public void triggerJobNow(JobKey jobKey) throws SchedulerException {
        scheduler.triggerJob(jobKey);
    }

}
