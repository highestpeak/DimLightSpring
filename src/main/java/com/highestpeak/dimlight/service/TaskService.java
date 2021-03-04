package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.Task;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.params.TaskParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.repository.TaskRepository;
import com.highestpeak.dimlight.service.job.SimpleRSSJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author highestpeak
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    /*
    一个 task 可以立即启动, 也可以只创建好了但是不启动这个 task
    一个 task 可以保存到本地, 方便关闭后或者重启应用后重新启动
     */

    public static final String SAVE_TASK_ERROR_MSG = "保存 Task 时发生错误;TaskService:saveRSSTask(..)";
    public static final String START_RSS_TASK_ERROR_MSG = "启动 RSSTask 时发生错误;TaskService:saveRSSTask(..)";

    public ErrorMessages saveRSSTask(RSSSourceParams rssSourceParams, TaskParams taskParams){
        ErrorMessages msg = new ErrorMessages();

        // 创建 task 保存到数据库
        Task savedTask = null;
        try{
            savedTask = taskRepository.save(taskParams.convertTo());
        }catch (Exception e){
            msg.addMsg(ErrorMessages.buildExceptionMsg(SAVE_TASK_ERROR_MSG,e));
        }

        // 启动 task
        if (taskParams.isStartTask() && msg.hasNoError()){
            try {
                ErrorMessages startMsg = startRSSTask(savedTask,rssSourceParams.convertTo());
                msg.mergeMsg(startMsg);
            } catch (SchedulerException e) {
                msg.addMsg(ErrorMessages.buildExceptionMsg(START_RSS_TASK_ERROR_MSG,e));
            }
        }

        return msg;
    }

    /**
     * 这里面 task 的 id 都是已经在数据库中的
     * 只需要对比 task 信息和数据库中的 task 信息 然后进行 update 处理
     */
    public Object updateTask(List<Task> tasks){
        return null;
    }

    /**
     * 是简单的启动已有的 task
     * @param task 数据库中查询出来的 task
     */
    public ErrorMessages startRSSTask(Task task, RSSSource rssSource) throws SchedulerException {
        // todo 这个是不是应该在全局
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        // define the job and tie it to our HelloJob class
        // todo 任务内容
        JobDetail job = newJob(SimpleRSSJob.class)
                .withIdentity("myJob", "rssTask")
                // todo 把 simple rss job 需要的参数传进去
                .usingJobData("rssSource",rssSource)
                .build();

        // Trigger the job to run now, and then every 40 seconds
        // todo 设置触发参数 corn定时器 task 在此使用
        Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "rssTaskTrigger")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(40)
                        .repeatForever())
                .build();
        // scheduler.shutdown();

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);
        return null;
    }

}
