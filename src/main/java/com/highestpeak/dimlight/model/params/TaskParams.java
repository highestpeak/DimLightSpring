package com.highestpeak.dimlight.model.params;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.highestpeak.dimlight.model.InputConverter;
import com.highestpeak.dimlight.model.entity.Task;
import com.highestpeak.dimlight.model.enums.TaskStatus;
import com.highestpeak.dimlight.model.params.validation.CronValidator;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author highestpeak
 */
@Data
public class TaskParams implements InputConverter<Task> {
    /**
     * 是否启动 task
     *
     * @see RSSSourceParams#getTaskParams()
     */
    @NotNull(groups = {NewTask.class})
    private boolean startTask;

    /**
     * cron 表达式
     */
    @NotBlank(groups = {NewTask.class})
    @CronValidator(groups = {NewTask.class})
    private String schedule;

    @NotBlank(groups = {NewTask.class})
    private String title;
    private String desc;

    /**
     * 使用秒数代表时间段（从时间点A到时间点B） 使用特殊数字表示永久 例如 -1 等
     */
    @NotNull(groups = {NewTask.class})
    private int lifeCycle;
    @NotNull(groups = {NewTask.class})
    private int cacheCycle;

    @NotNull(groups = {NewTask.class})
    private boolean autoDownload;

    /**
     * 清除创建的 events
     */
    private boolean clearEventsCreated;

    /**
     * 设定下一次 event 启动时间
     * 可以为 null blank 因为不一定设置这个值
     * tmpdoc 所以这个注解可以这么用？ valid库和jackson库能这么结合用？ 对注解的了解还是太少了
     */
    @Future(groups = {UpdateTask.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextEventSet;

    @Override
    public Task convertTo() {
        // todo 和quartz编写任务的逻辑一起写
        Task task = InputConverter.super.convertTo();
        task.setStatus(TaskStatus.DISABLE);
        return task;
    }

    public interface NewTask {
    }

    public interface UpdateTask {
    }
}
