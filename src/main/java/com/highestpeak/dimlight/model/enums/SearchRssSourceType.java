package com.highestpeak.dimlight.model.enums;

import lombok.Getter;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-13
 */
@Getter
public enum SearchRssSourceType {
    NORMAL_LIST(1, "普通分页查找list"),
    TITLE(2, "搜素对应标题的Rss"),
    //    FULL_TEXT_SEARCH(3, "全文搜索，部分匹配TITLE和DESC等"), // desc 的搜索就是全文搜索 title 到时候也可以在这里
    CONTENT_ITEMS(4, "某个Rss列表的所有items"),
    ;
    private final int num;
    private final String desc;

    SearchRssSourceType(int num, String desc) {
        this.num = num;
        this.desc = desc;
    }

    public int getValue() {
        return num;
    }
}
