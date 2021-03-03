package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.Task;
import com.highestpeak.dimlight.model.params.TaskParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public ErrorMessages saveRSSTask(TaskParams taskParams){
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
            ErrorMessages startMsg = startRSSTask(savedTask);
            msg.mergeMsg(startMsg);
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
    public ErrorMessages startRSSTask(Task task){
        return null;
    }
}
