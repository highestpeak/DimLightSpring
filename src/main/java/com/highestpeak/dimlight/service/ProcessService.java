package com.highestpeak.dimlight.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.service.info.process.InfoProcess;
import com.highestpeak.dimlight.utils.ProcessUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ProcessService {
    @Resource
    private Map<String, InfoProcess> processMap;

    /**
     * 处理特定rss被拉取到的item
     *
     * @return 处理后的内容
     */
    public ErrorMessages processRssXml(ProcessContext processContext) {
        RSSSource rssSource = processContext.getRssSource();
        RSSXml originXml = processContext.getOriginXml();

        if (originXml == null || rssSource == null) {
            throw new NullPointerException("originXml和rssSource不能null");
        }

        ErrorMessages msg = new ErrorMessages();

        // 构建处理队列
        Queue<InfoProcess> infoProcessQueue = null;
        try {
            infoProcessQueue = ProcessUtils.buildProcessQueue(rssSource.getJsonOptionalExtraFields(), processMap);
        } catch (JsonProcessingException e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("rss jsonFields parse error, rss:"+rssSource.getId(),e));
            return msg;
        }

        List<ProcessContext.XmlItemWithId> result;
        // 按顺序处理xml,遇到错误即终止处理
        while (infoProcessQueue.size() > 0) {
            InfoProcess nextProcess = infoProcessQueue.poll();
            try {
                nextProcess.process(processContext);
                result = processContext.getXmlItemList();
            } catch (Exception exception) {
                String processClassName = nextProcess.getClass().getName();
                msg.addMsg(ErrorMessages.buildExceptionMsg(
                        "process error, process name:" + processClassName +
                                ", remain process:" + ProcessUtils.remainProcessNames(infoProcessQueue),
                        exception
                ));
                return msg;
            }
            if (result.size() <= 0) {
                return msg;
            }
        }

        return msg;
    }

    public Set<String> optionalProcessNames(){
        return processMap.keySet();
    }
}
