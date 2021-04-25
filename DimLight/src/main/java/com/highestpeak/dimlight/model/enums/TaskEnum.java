package com.highestpeak.dimlight.model.enums;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public enum TaskEnum {
    SIMPLE_RSS_TASK(0,"简单RSS定时任务"),
    SIMPLE_HISTORY_SPIDER_TASK(1,"简单历史爬虫采集任务"),
    CONTENT_ITEM_SCHEDULE_DEL_TASK(2, "定时删除过期内容项ContentItem任务,不包括inbox、star等中内容"),
    ;

    private int type;
    private String desc;

    /**
     * 源是RSS的task的type的列表
     */
    public static Set<Integer> rssSourceTaskType() {
        return Sets.newHashSet(SIMPLE_RSS_TASK.type);
    }
}
