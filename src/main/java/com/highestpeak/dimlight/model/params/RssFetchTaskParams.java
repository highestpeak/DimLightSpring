package com.highestpeak.dimlight.model.params;

import lombok.Data;

@Data
public class RssFetchTaskParams {
    private Integer id;

    private String schedule;
    private String name;
    private String group;
    private String descUser;
    private int cacheCycle;
    private boolean autoDownload;

    private boolean startTask=true;
    /**
     * 与之关联的rssId
     */
    private Integer rssSourceId;
    private boolean fetchNow=true;
}
