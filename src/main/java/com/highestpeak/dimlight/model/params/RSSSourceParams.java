package com.highestpeak.dimlight.model.params;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

import com.highestpeak.dimlight.model.params.validation.ImageValidator;
import com.highestpeak.dimlight.model.params.validation.JsonValidator;

import lombok.Data;

/**
 * @author highestpeak
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
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

    @ImageValidator
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
}
