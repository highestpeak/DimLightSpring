package com.highestpeak.dimlight.service.job;

import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.utils.RSSUtils;
import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 简单定时抓取
 * @author highestpeak
 */
@Data
public class SimpleRSSJob implements Job {

    private String rssUrl;
    public static final String FIELD_NAME_RSS_URL = "rssUrl";

    private String jsonOptionalExtraFields;
    public static final String FIELD_NAME_JSON_OPTIONS = "jsonOptionalExtraFields";

    /**
     * todo
     * @param context
     */
    @Override
    public void execute(JobExecutionContext context) {
        // fetch new content
        RSSXml rssXml = RSSUtils.getRSSXml(rssUrl);

        /*
        process content
        filter 不是 job
        filter 使用 groovy 等脚本进行过滤
        设置这些脚本的执行顺序

        数据库中存储一个处理过程，即一个task的调用链条，然后有一个解析这个 if else 等等语句的部分，分别调用各个过程
        在前端就选择好 if xxx 就 xxx 等等
        然后在这里启动
         */


        // save content
    }
}
