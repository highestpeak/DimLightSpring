package com.highestpeak.dimlight.repository;

import java.util.Date;

import com.highestpeak.dimlight.model.entity.RSSSource;

import com.highestpeak.dimlight.model.entity.RSSContentItem;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RSSContentItemRepository extends PagingAndSortingRepository<RSSContentItem, Integer> {

    void deleteByCreateTimeBefore(Date createTime);

    void deleteByRssSourceIsAndCreateTimeBefore(RSSSource rssSource,Date createTime);

    RSSContentItem findFirstByGuid(String guid);

    void deleteByRssSourceIs(RSSSource rssSource);
}
