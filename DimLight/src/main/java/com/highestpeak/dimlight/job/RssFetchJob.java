package com.highestpeak.dimlight.job;

import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.model.entity.MobiusEvent;
import com.highestpeak.dimlight.model.entity.MobiusTask;
import com.highestpeak.dimlight.model.enums.LogLevel;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.EventRepository;
import com.highestpeak.dimlight.repository.TaskRepository;
import com.highestpeak.dimlight.service.ProcessService;
import com.highestpeak.dimlight.service.RSSSourceService;
import com.highestpeak.dimlight.service.RssContentItemService;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

public class RssFetchJob extends QuartzJobBean {
    private static final String EVENT_LOG_NAME = "rss定时拉取任务";

    @Resource
    private RSSSourceService rssSourceService;
    @Resource
    private ProcessService processService;
    @Resource
    private EventRepository eventRepository;
    @Resource
    private TaskRepository taskRepository;
    @Resource
    private RssContentItemService rssContentItemService;
    /**
     * todo： 无用，请在测试后删除
     */
    @Value("${processOpen:false}")
    private boolean processOpen = false;

    @Setter
    private int rssSourceId;
    @Setter
    private int taskId;

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        InfoMessages infoMessages = new InfoMessages();
        Optional<MobiusTask> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            infoMessages.addErrorMsg("任务已经不存在！但是定时任务没有删除");
            logEvent(infoMessages);
            return;
        }
        MobiusTask mobiusTask = taskOptional.get();

        try {
            ProcessContext processContext = ProcessContext.builder()
                    .rssId(rssSourceId)
                    .build();
            // 抓取rss
            rssSourceService.fetchTargetRSS(processContext);
            // 单独保存原始结果,并填充xmlItem的process上下文
            rssContentItemService.saveRssXml(processContext);

            // 进行处理
            if (RSSXml.isItemsBlank(processContext.getOriginXml())) {
                infoMessages.addInfoMsg("没有拉取到内容 id:" + rssSourceId);
            } else if (processOpen){
                // 处理前准备
                RSSXml originXml = processContext.getOriginXml();
                // 进行处理
                InfoMessages processMsg = processService.processRssXmlNew(processContext);
                infoMessages.mergeMsg(processMsg);
            }
        } catch (ErrorMsgException e) {
            infoMessages.mergeMsg(e.getInfoMessages());
        } catch (Exception e) {
            infoMessages.addErrorMsg(e.getMessage());
        }

        // 更新task参数
        try {
            mobiusTask.setLastEvent(new Date());
            mobiusTask.setEventsCreated(mobiusTask.getEventsCreated() + 1);
            taskRepository.save(mobiusTask);
        } catch (Exception e) {
            infoMessages.addErrorMsg("更新task参数时发生错误 task:" + mobiusTask.getId());
        }

        logEvent(infoMessages);
    }

    /**
     * 记录event到数据库，相当于另一种日志
     */
    private void logEvent(InfoMessages infoMessages) {
        int logLevel = infoMessages.hasError() ? LogLevel.ERROR.getLevelId() : LogLevel.INFO.getLevelId();
        MobiusEvent mobiusEvent = MobiusEvent.builder()
                .name(EVENT_LOG_NAME)
                .logLevel(logLevel)
                .triggerSourceId(rssSourceId)
                .triggerTaskId(taskId)
                .jsonMsg(infoMessages.toJson())
                .build();
        eventRepository.save(mobiusEvent);
    }

}
