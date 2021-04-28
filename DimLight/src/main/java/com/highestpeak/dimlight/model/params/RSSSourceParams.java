package com.highestpeak.dimlight.model.params;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.URL;

import lombok.Data;

/**
 * @author highestpeak
 */
@Data
public class RSSSourceParams {
    @URL
    @NotBlank
    private String url;

    /**
     * 用户输入的 title
     */
    @NotBlank
    private String titleUser;
    private String descUser;

    //@ImageValidator
    /**
     * todo: 可以采用json格式解析image
     */
    private String image;

    @NotBlank
    private String generator;

    private List<String> tags;

    private List<String> topics;

    private boolean fetchAble = true;

    /**
     * json 格式的额外字段
     * 校验是否符合 json 格式
     */
    private String jsonOptionalExtraFields;

    /**
     * 是否立刻启动task
     * todo： 这里暂时是写死的
     */
    private boolean startTaskNow = true;
}
