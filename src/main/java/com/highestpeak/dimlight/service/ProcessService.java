package com.highestpeak.dimlight.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.model.entity.MobiusTask;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.enums.TaskEnum;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import com.highestpeak.dimlight.service.process.InfoProcess;
import com.highestpeak.dimlight.utils.ProcessUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ProcessService {
    @Resource
    private Map<String, InfoProcess> processMap;
    @Resource
    private TaskService taskService;
    @Resource
    private QuartzManager quartzManager;
    @Resource
    private RssContentItemService rssContentItemService;
    @Resource
    private RSSSourceRepository rssSourceRepository;

    /**
     * 处理特定rss被拉取到的item
     */
    public InfoMessages processRssXmlNew(ProcessContext processContext) {
        RSSSource rssSource = processContext.getRssSource();
        RSSXml originRssXml = processContext.getOriginXml();
        if (originRssXml == null || rssSource == null) {
            throw new NullPointerException("ProcessService:originXml和rssSource不能null");
        }

        if (StringUtils.isBlank(rssSource.getJsonOptionalExtraFields())) {
            InfoMessages infoMessages = new InfoMessages();
            infoMessages.addInfoMsg("因为没有处理链，没有经过处理");
            return infoMessages;
        }

        InfoMessages infoMessages = new InfoMessages();
        try {
            // 读取rss的jsonExtra参数进行process
            // 构建处理队列
            Queue<InfoProcess> infoProcessQueue = ProcessUtils.buildProcessQueue(
                    rssSource.getJsonOptionalExtraFields(), processMap
            );

            // 按顺序处理xml,遇到错误即终止处理
            List<ProcessContext.XmlItemWithId> result;
            while (infoProcessQueue.size() > 0) {
                InfoProcess nextProcess = infoProcessQueue.poll();
                try {
                    nextProcess.process(processContext);
                    result = processContext.getXmlItemList();
                } catch (Exception exception) {
                    String processClassName = nextProcess.getClass().getName();
                    String exceptionMsg = InfoMessages.buildExceptionMsg(
                            "rssXml处理错误,处理名:" + processClassName +
                                    ", 未进行的任务处理:" + ProcessUtils.remainProcessNames(infoProcessQueue),
                            exception
                    );
                    throw new ErrorMsgException(exceptionMsg);
                }
                // 结果全都被处理没了，所以不需要继续处理了
                if (result.size() <= 0) {
                    infoMessages.addInfoMsg(
                            "rssXml已经全部被处理，剩余未进行的任务处理：" + ProcessUtils.remainProcessNames(infoProcessQueue)
                    );
                    break;
                }
            }

            // 记录处理后结果到数据库
            rssContentItemService.saveRssXml(rssSource, processContext.getXmlItemList());
        } catch (JsonProcessingException e) {
            throw new ErrorMsgException(
                    InfoMessages.buildExceptionMsg("解析rss的json字段的任务处理字段时出现错误, rss:" + rssSource.getId(), e)
            );
        }

        infoMessages.addInfoMsg("处理完成");
        return infoMessages;
    }

    /**
     * 触发所有RSS拉取任务
     */
    public Object fetchAllRssNow() {
        InfoMessages msg = new InfoMessages();
        // 全部拉取
        List<MobiusTask> allTask = taskService.getAllTaskByType(TaskEnum.SIMPLE_RSS_TASK);
        allTask.forEach(mobiusTask -> {
            try {
                quartzManager.triggerJobNow(taskService.getTaskJobKey(mobiusTask));
                msg.addErrorMsg("fetch rss:" + mobiusTask + " succeed!");
            } catch (SchedulerException e) {
                msg.addErrorMsg("trigger task failed. task:" + mobiusTask);
            }
        });
        return msg;
    }

    /**
     * 触发单个RSS拉取任务
     */
    public Object fetchRssNow(Integer id) {
        RSSSource rssSource = rssSourceRepository.findById(id).orElse(null);
        InfoMessages msg = new InfoMessages();
        // 指定id拉取
        MobiusTask taskById = taskService.getTaskByRssId(id);
        if (taskById == null) {
            msg.addErrorMsg("rss不存在抓取任务,请先创建抓取任务，否则不允许抓取");
            return msg;
        }
        try {
            quartzManager.triggerJobNow(taskService.getTaskJobKey(taskById));
            msg.addInfoMsg("fetch rss:" + taskById + " succeed!");
        } catch (SchedulerException e) {
            msg.addErrorMsg("trigger task failed. task:" + taskById);
        }
        return msg;
    }

    /**
     * 可选Process的名称
     */
    public Set<String> optionalProcessNames() {
        return processMap.keySet();
    }
}
