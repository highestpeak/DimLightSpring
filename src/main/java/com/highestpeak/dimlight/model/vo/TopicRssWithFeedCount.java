package com.highestpeak.dimlight.model.vo;

import com.highestpeak.dimlight.model.entity.RSSSource;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicRssWithFeedCount {
    private RSSSource rssSource;
    private int feedCount;
}
