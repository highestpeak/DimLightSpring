package com.highestpeak.dimlight.service.job;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.Task;
import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 简单定时抓取
 * @author highestpeak
 */
@Data
public class SimpleRSSJob implements Job {

    private Task task;
    private RSSSource rssSource;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    }
}
