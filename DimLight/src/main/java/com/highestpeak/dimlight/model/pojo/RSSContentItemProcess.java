package com.highestpeak.dimlight.model.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Builder
@Data
public class RSSContentItemProcess{
    private String title;
    private String description;
    private String link;
    private String guid;
    private Date pubDate;
    private List<String> category;
    private String author;
    private String comments;
    private String jsonOptionalExtraFields;
}
