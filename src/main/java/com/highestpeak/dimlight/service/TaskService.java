package com.highestpeak.dimlight.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.job.RssFetchJob;
import com.highestpeak.dimlight.model.entity.Task;
import com.highestpeak.dimlight.model.enums.TaskStatus;
import com.highestpeak.dimlight.model.params.DeleteTaskParams;
import com.highestpeak.dimlight.model.params.RssFetchTaskParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.QuartzTask;
import com.highestpeak.dimlight.repository.TaskRepository;
import com.highestpeak.dimlight.utils.QuartzUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Optional;

@Service
public class TaskService {
    @Resource
    private QuartzManager quartzManager;
    @Resource
    private TaskRepository taskRepository;

    public Object newOrUpdateRssTask(RssFetchTaskParams taskParams) {
        ErrorMessages msg = new ErrorMessages();

        // 查找现有id
        Task originTask = null;
        if (taskParams.getId() != null) {
            originTask = taskRepository.findById(taskParams.getId()).orElse(null);
        }
        if (originTask == null && taskParams.getName() != null) {
            originTask = taskRepository.findByName(taskParams.getName());
        }

        // 构建task
        Task taskToSave = Task.builder()
                .name(taskParams.getName())
                .group(taskParams.getGroup())
                .schedule(taskParams.getSchedule())
                .autoDownload(taskParams.isAutoDownload())
                .descUser(taskParams.getDescUser())
                .status(TaskStatus.DEFAULT_VALUE)
                .cacheCycle(taskParams.getCacheCycle())
                .build();
        if (originTask != null) {
            taskToSave.setId(originTask.getId());
            taskToSave.setCreateTime(originTask.getCreateTime());
            taskToSave.setEventsCreated(originTask.getEventsCreated());
            taskToSave.setStatus(originTask.getStatus());
            // 关闭原来的task
            try {
                quartzManager.removeJob(getTaskJobKey(taskToSave));
            } catch (SchedulerException e) {
                msg.addMsg(ErrorMessages.buildExceptionMsg("关闭原来Task时发生错误", e));
            }
        }

        try {
            // 保存到数据库
            Task savedTask = taskRepository.save(taskToSave);

            // 调度task执行
            if (taskParams.isStartTask()) {
                try {
                    HashMap<String, Object> dataMap = Maps.newHashMap();
                    Integer rssSourceId = taskParams.getRssSourceId();
                    if (rssSourceId == null) {
                        throw new ErrorMsgException(new ErrorMessages("rssSource未指定"));
                    }
                    dataMap.put("rssSourceId", rssSourceId);
                    dataMap.put("taskId", savedTask.getId());
                    JobKey taskJobKey = getTaskJobKey(savedTask);
                    quartzManager.scheduleJob(QuartzTask.builder()
                            .cronExpression(savedTask.getSchedule())
                            .description(savedTask.getDescUser())
                            .jobClass(RssFetchJob.class)
                            .jobDataMap(QuartzUtils.getJobDataMap(dataMap))
                            .jobKey(taskJobKey)
                            .build());
                    if (taskParams.isFetchNow()){
                        quartzManager.triggerJobNow(taskJobKey);
                    }
                } catch (SchedulerException e) {
                    msg.addMsg(ErrorMessages.buildExceptionMsg("启动Task时发生错误", e));
                }
            }
        } catch (ErrorMsgException e) {
            msg.mergeMsg(e.getErrorMessages());
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("保存Task时发生错误", e));
        }

        return msg;
    }

    public Object deleteTask(DeleteTaskParams taskParams) {
        ErrorMessages msg = new ErrorMessages();

        Optional<Task> taskOptional = taskRepository.findById(taskParams.getId());
        if (!taskOptional.isPresent()) {
            msg.addMsg("未找到task");
            return msg;
        }
        Task task = taskOptional.get();

        try {
            quartzManager.removeJob(getTaskJobKey(task));
        } catch (SchedulerException e) {
            msg.addMsg("停止job错误");
            return msg;
        }

        try {
            taskRepository.deleteById(taskParams.getId());
        } catch (Exception e) {
            msg.addMsg("删除task错误");
        }

        return msg;
    }

    public Object getTaskList(int pageNum, int pageSize) {
        return null;
    }

    public Object getAllTask() {
        return Lists.newLinkedList(taskRepository.findAll());
    }

    private JobKey getTaskJobKey(Task task) {
        return JobKey.jobKey(task.getName(), task.getGroup());
    }

    public Object allRssFetchNow() {
        ErrorMessages msg = new ErrorMessages();
        Iterable<Task> all = taskRepository.findAll();
        all.forEach(task -> {
            try {
                quartzManager.triggerJobNow(getTaskJobKey(task));
            } catch (SchedulerException e) {
                msg.addMsg("trigger task failed. task:"+task.getName());
            }
        });
        return msg;
    }
}
