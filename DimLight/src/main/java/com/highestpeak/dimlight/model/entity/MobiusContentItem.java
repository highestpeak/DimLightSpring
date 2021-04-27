package com.highestpeak.dimlight.model.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.List;

/**
 * @author highestpeak
 * 最终阅读的条目
 */
@Entity(name = "mobius_content_item")
@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobiusContentItem extends BaseEntity{
    /**
     * 描述图片链接
     */
    @Lob
    @Column(name = "image")
    private String image;

    /**
     * 文档摘要
     * 有最长限制 ${docSummaryMaxLen:256}
     */
    @Lob
    @Column(name = "content_summary")
    private String contentSummary;

    /**
     * 分词结果
     * 标题分词结果
     * 标题所有分词
     */
    @Lob
    @Column(name = "title_segment")
    private List<String> titleSegment;

    /**
     * 分词结果
     * 描述/内容分词结果
     * 取最重要的前100个词
     */
    @Lob
    @Column(name = "word_segment")
    private List<String> wordSegments;

    /**
     * 文档simhash
     * 用于去重
     */
    @Lob
    @Column(name = "content_simhash")
    private String simHash;

    /**
     * 源内容类型
     * 原始数据，未经过处理的数据的类型
     * {@link com.highestpeak.dimlight.model.enums.OriginContentTypeEnum}
     */
    @Column(name = "origin_content_type", nullable = false)
    @ColumnDefault("1")
    private int originContentType;

    /**
     * 源内容id
     * 源产生的内容的id
     */
    @Column(name = "origin_content_id", nullable = false)
    private int originContentId;

    /**
     * 源id
     * 产生内容的源的id
     */
    @Column(name = "origin_source_id", nullable = false)
    private int originSourceId;

}
