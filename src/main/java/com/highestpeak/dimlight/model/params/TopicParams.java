package com.highestpeak.dimlight.model.params;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-13
 */
@Data
public class TopicParams {
    @NotEmpty
    private String name;

    private String desc;
}
