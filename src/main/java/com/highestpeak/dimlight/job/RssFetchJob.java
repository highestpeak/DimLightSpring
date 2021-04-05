package com.highestpeak.dimlight.job;

import com.google.common.collect.Lists;
import com.highestpeak.dimlight.model.entity.Event;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.Task;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.EventRepository;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import com.highestpeak.dimlight.repository.TaskRepository;
import com.highestpeak.dimlight.service.ContentItemService;
import com.highestpeak.dimlight.service.ProcessService;
import com.highestpeak.dimlight.service.RSSSourceService;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Setter
public class RssFetchJob extends QuartzJobBean {
    @Resource
    private RSSSourceRepository rssSourceRepository;
    @Resource
    private RSSSourceService rssSourceService;
    @Resource
    private ProcessService processService;
    @Resource
    private ContentItemService contentItemService;
    @Resource
    private EventRepository eventRepository;
    @Resource
    private TaskRepository taskRepository;

    private int rssSourceId;
    private int taskId;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        ErrorMessages errorMessages = new ErrorMessages();

        try {
            // 数据库查找rss
            Optional<RSSSource> rssOptional = rssSourceRepository.findById(rssSourceId);
            if (!rssOptional.isPresent()){
                errorMessages.addMsg("未找到指定id的rss rss id:"+rssSourceId);
            }else {
                // 抓取rss
                RSSSource rssSource = rssOptional.get();
                ImmutablePair<Map<Integer, RSSXml>, ErrorMessages> fetchRSSFromInternal =
                        rssSourceService.fetchRSSFromInternal(Lists.newArrayList(rssSource));
                RSSXml originRssXml = fetchRSSFromInternal.getLeft().get(rssSourceId);
                errorMessages.mergeMsg(fetchRSSFromInternal.getRight());

                if (originRssXml.getItems()!=null && originRssXml.getItems().size()>0){
                    if (StringUtils.isNotBlank(rssSource.getJsonOptionalExtraFields())){
                        // 读取rss的jsonExtra参数进行process
                        ProcessContext processContext = ProcessContext.builder()
                                .rssSource(rssSource)
                                .originXml(originRssXml)
                                .build();
                        try {
                            ErrorMessages processMsg = processService.processRssXml(processContext);
                            errorMessages.mergeMsg(processMsg);

                            // 记录结果到数据库
                            ErrorMessages xmlSaveMsg = contentItemService.saveRssXml(processContext);
                            errorMessages.mergeMsg(xmlSaveMsg);
                        } catch (Exception e){
                            // 直接记录原始结果到数据库
                            ErrorMessages xmlSaveMsg = contentItemService.saveRssXml(rssSource,originRssXml);
                            errorMessages.mergeMsg(xmlSaveMsg);
                            errorMessages.addMsg("处理链处理错误，直接保存原始结果，exception:"+e);
                        }
                    }else {
                        // 直接记录原始结果到数据库
                        ErrorMessages xmlSaveMsg = contentItemService.saveRssXml(rssSource,originRssXml);
                        errorMessages.mergeMsg(xmlSaveMsg);
                        errorMessages.addNoErrorMsg("因为没有处理链，没有经过处理，直接保存原始结果");
                    }
                }
            }
        } catch (Exception e){
          errorMessages.addMsg(e.getMessage());
        }

        // 记录event到数据库，相当于另一种日志
        Event event = Event.builder()
                .hasError(errorMessages.hasError())
                .jsonMsg(errorMessages.toJson())
                .trigger(this.getClass().getName())
                .name("FetchRss")
                .build();
        eventRepository.save(event);
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()){
            Task task = taskOptional.get();
            task.setLastEvent(new Date());
            taskRepository.save(task);
        }
    }
}
