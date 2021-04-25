package com.highestpeak.dimlight.job;

import com.highestpeak.dimlight.repository.TopicRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

/**
 * 每次从数据库读取task执行链，解析然后执行
 */
public class TestPrintJob extends QuartzJobBean {
    @Resource
    private TopicRepository topicRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("schedule print:");
        topicRepository.findAll().forEach(topic -> System.out.println(topic.getName()));
    }
}
