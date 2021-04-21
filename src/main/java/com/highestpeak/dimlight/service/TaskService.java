package com.highestpeak.dimlight.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.job.RssFetchJob;
import com.highestpeak.dimlight.model.dto.TaskDto;
import com.highestpeak.dimlight.model.entity.MobiusTask;
import com.highestpeak.dimlight.model.enums.TaskEnum;
import com.highestpeak.dimlight.model.enums.TaskStatus;
import com.highestpeak.dimlight.model.params.BaseTaskParams;
import com.highestpeak.dimlight.model.params.RssFetchTaskParams;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.QuartzTask;
import com.highestpeak.dimlight.repository.TaskRepository;
import com.highestpeak.dimlight.utils.QuartzUtils;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Resource
    private QuartzManager quartzManager;
    @Resource
    private TaskRepository taskRepository;

    /**
     * 新建和更新RSSTask
     */
    public Object newOrUpdateRssTask(RssFetchTaskParams taskParams) {
        try {
            // 保存Task
            TaskDto taskDto = TaskDto.builder()
                    .baseTaskParams(taskParams)
                    .taskType(TaskEnum.SIMPLE_RSS_TASK)
                    .taskOperator(taskParams.getRssSourceId())
                    .build();
            MobiusTask savedMobiusTask = newOrUpdateTask(taskDto);
            // 调度Task执行
            if (taskParams.isStartTask()) {
                startRssFetchTask(taskParams.getRssSourceId(), savedMobiusTask, taskParams.isFetchNow());
            }
            return savedMobiusTask;
        } catch (ErrorMsgException e) {
            return e.getInfoMessages();
        }
    }

    /**
     * 删除task
     */
    public Object deleteTask(int taskId) {
        InfoMessages msg = new InfoMessages();

        Optional<MobiusTask> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            msg.addErrorMsg("未找到task");
            return msg;
        }
        MobiusTask mobiusTask = taskOptional.get();

        try {
            quartzManager.removeJob(getTaskJobKey(mobiusTask));
        } catch (SchedulerException e) {
            msg.addErrorMsg("停止task错误");
            return msg;
        }

        try {
            taskRepository.deleteById(taskId);
        } catch (Exception e) {
            msg.addErrorMsg("删除task错误");
        }

        return msg;
    }

    // ============================================================================================================== //

    public List<MobiusTask> getAllTask(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.Direction.ASC, "id");
        Page<MobiusTask> mobiusTasks = taskRepository.findAll(pageable);
        // todo: task的返回参数json格式
        return Lists.newLinkedList(mobiusTasks);
    }

    public List<MobiusTask> getAllTaskByType(TaskEnum taskEnum) {
        return Lists.newLinkedList(taskRepository.findByTaskType(taskEnum.getType()));
    }

    public MobiusTask getTaskById(Integer id) {
        return taskRepository.findById(id).orElse(null);
    }

    public MobiusTask getTaskByRssId(Integer rssId) {
        return taskRepository.findByTaskTypeAndTaskOperator(TaskEnum.SIMPLE_RSS_TASK.getType(), rssId);
    }

    /**
     * Task的quartz的JobKet
     */
    public JobKey getTaskJobKey(MobiusTask mobiusTask) {
        return JobKey.jobKey(mobiusTask.getName(), mobiusTask.getGroup());
    }

    /**
     * 通用Task更新逻辑
     */
    private MobiusTask newOrUpdateTask(TaskDto taskDto) {
        BaseTaskParams baseTaskParams = taskDto.getBaseTaskParams();
        // 构建新的task
        MobiusTask mobiusTaskToSave = MobiusTask.builder()
                .name(baseTaskParams.getName())
                .group(baseTaskParams.getGroup())
                .schedule(baseTaskParams.getSchedule())
                .autoDownload(baseTaskParams.isAutoDownload())
                .descUser(baseTaskParams.getDescUser())
                .status(TaskStatus.DEFAULT_VALUE)
                .cacheCycle(baseTaskParams.getCacheCycle())
                .taskType(taskDto.getTaskType().getType())
                .taskOperator(taskDto.getTaskOperator())
                .build();

        // 判断是否是更新请求
        boolean updateRss = baseTaskParams.getId() != null;
        if (updateRss) {
            // 查找现有id
            MobiusTask originMobiusTask = taskRepository.findById(baseTaskParams.getId()).orElse(null);
            if (originMobiusTask == null) {
                throw new ErrorMsgException(new InfoMessages("指定id不存在 id:" + baseTaskParams.getId()));
            }
            // 设置进新Task不变的值
            mobiusTaskToSave.setId(originMobiusTask.getId());
            mobiusTaskToSave.setCreateTime(originMobiusTask.getCreateTime());
            mobiusTaskToSave.setEventsCreated(originMobiusTask.getEventsCreated());
            mobiusTaskToSave.setStatus(originMobiusTask.getStatus());
            // 关闭原来的task
            try {
                quartzManager.removeJob(getTaskJobKey(mobiusTaskToSave));
            } catch (SchedulerException e) {
                throw new ErrorMsgException(new InfoMessages("关闭原来Task时发生错误"));
            }
        }

        try {
            // 保存到数据库
            MobiusTask savedMobiusTask = taskRepository.save(mobiusTaskToSave);
            return savedMobiusTask;
        } catch (Exception e) {
            throw new ErrorMsgException(new InfoMessages("保存Task时发生错误"));
        }
    }

    /**
     * 启动RSS拉取Task
     */
    private void startRssFetchTask(Integer rssSourceId, MobiusTask mobiusTask, boolean fetchNow) {
        if (rssSourceId == null) {
            throw new ErrorMsgException(new InfoMessages("rssSource未指定,不能启动task"));
        }
        try {
            HashMap<String, Object> dataMap = Maps.newHashMap();
            dataMap.put("rssSourceId", rssSourceId);
            dataMap.put("taskId", mobiusTask.getId());
            JobKey taskJobKey = getTaskJobKey(mobiusTask);
            quartzManager.scheduleJob(QuartzTask.builder()
                    .cronExpression(mobiusTask.getSchedule())
                    .description(mobiusTask.getDescUser())
                    .jobClass(RssFetchJob.class)
                    .jobDataMap(QuartzUtils.getJobDataMap(dataMap))
                    .jobKey(taskJobKey)
                    .build());
            if (fetchNow) {
                quartzManager.triggerJobNow(taskJobKey);
            }
        } catch (SchedulerException e) {
            InfoMessages startErrorMsg = new InfoMessages(
                    InfoMessages.buildExceptionMsg("启动Task时发生错误", e)
            );
            throw new ErrorMsgException(startErrorMsg);
        }
    }

}
