package com.highestpeak.dimlight.model.entity;

import lombok.Builder;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-08
 */
@SuppressWarnings("FieldMayBeFinal")
@Document(indexName = "rss_es_content")
@Builder
public class EsContent {
    @Id
    private String id;
    private String titleParse;
    private String descParse;
    private String link;
    private String author;
}
