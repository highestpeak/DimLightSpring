package com.highestpeak.dimlight.model.enums;

import lombok.Getter;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-14
 */
@Getter
public enum TopicAndTagSearchType {
    NORMAL_LIST(1, "普通分页查找list"),
    NAME(2, "搜素对应名称的Topic或Tag"),
    //    FULL_TEXT_SEARCH(3, "全文搜索，部分匹配TITLE和DESC等"), // desc 的搜索就是全文搜索 title 到时候也可以在这里
    CONTENT_ITEMS(4, "某个Topic或Tag列表的所有items"),
    RSS_SOURCES(5, "某个Topic或Tag列表的所有RSSSource"),
    ;
    private final int num;
    private final String desc;

    TopicAndTagSearchType(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }

    public int getValue() {
        return num;
    }
}
