package com.highestpeak.dimlight.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * todo:特殊Tag可以通过前端强制添加，后端也进行一次检测，强制添加
 *  但这种添加可以在返回用户显示时选择不显示
 * future: tag也可以分为是手动添加的，还是自动添加的
 */
@AllArgsConstructor
@Getter
public enum SpecialTagEnum {
    RSSHUB(0, "RSSHUB", "RSSHUB相关，rssSource被此标记后可以方便的替换RSSHUB前缀，使用多RSSHUB服务器等"),
    GLOBAL_PROXY(1, "global_proxy", "代理标记，表明该源需要使用全局代理去抓取"),
    TARGET_PROXY(2, "target_proxy", "代理标记，表明该源需要使用指定代理抓取，代理值存在实体字段中"),
    ;
    private int typeValue;
    private String name;
    private String desc;
}
