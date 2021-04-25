package com.highestpeak.dimlight.model.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RssFetchTaskParams extends BaseTaskParams{

    /**
     * 启动task但是不一定立刻拉取
     */
    private boolean startTask=true;
    /**
     * 立刻拉取
     */
    private boolean fetchNow=true;
    /**
     * 与之关联的rssId
     */
    private Integer rssSourceId;
}
