package com.highestpeak.dimlight.model.params;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BaseTaskParams {
    @NotNull(groups = Update.class)
    private Integer id = null;

    private String schedule;
    private String name;
    private String group;
    private String descUser;
    private int cacheCycle;
    private boolean autoDownload;

    public interface Update {}
}
