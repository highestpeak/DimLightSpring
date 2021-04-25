package com.highestpeak.dimlight.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OriginContentTypeEnum {
    RSS(0,"内容是由某个RSS生成的"),
    SPIDER(1,"内容是由某个爬虫生成的"),
    ;
    private int type;
    private String desc;
}
