package com.highestpeak.dimlight.model.params;

import com.highestpeak.dimlight.model.InputConverter;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.params.validation.ImageValidator;
import com.highestpeak.dimlight.model.params.validation.JsonValidator;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.utils.RSSUtils;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author highestpeak
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Data
public class RSSSourceParams implements InputConverter<RSSSource> {
    @URL
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

    /**
     * types 是 type 的数组
     * 可以为 null blank
     */
    private List<String> types;

    /**
     * json 格式的额外字段
     * 校验是否符合 json 格式
     */
    @JsonValidator
    private String jsonOptionalExtraFields;

    @Override
    public RSSSource convertTo() {
        RSSSource rssSource = InputConverter.super.convertTo();
        RSSXml rssXml = RSSUtils.getRSSXml(rssSource.getUrl());
        rssSource.setTitleParse(rssXml.getTitle());
        rssSource.setDescParse(rssXml.getDescription());
        return rssSource;
    }
}
