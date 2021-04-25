package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.stereotype.Component;

/**
 * @author highestpeak
 */
@Component
public interface InfoProcess {
    /**
     * 流程处理的通用逻辑,返回处理后的 rssXmlItem
     * @param processContext 拉取到的rss xml内容
     */
    void process(ProcessContext processContext);
}
