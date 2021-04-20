package com.highestpeak.dimlight.repository;

import java.util.Date;
import java.util.List;

import com.highestpeak.dimlight.model.entity.RSSSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.highestpeak.dimlight.model.entity.RSSContentItem;

public interface RSSContentItemRepository extends CrudRepository<RSSContentItem, Integer> {
    String FIELDS_EXCEPT_ITEMS_STR =
            "select i.id,i.createTime,i.updateTime,i.rssSourceTags,i.rssSource,i.link,i.jsonOptionalExtraFields,i"
                    + ".descParse,i.titleParse,i.author,i.guid,i.itemTopics,i.pubDate ";

    void deleteByCreateTimeBefore(Date createTime);

    @Query("select i from rss_content_item i")
    Page<RSSContentItem> findList(Pageable pageable);

    void deleteByRssSourceIsAndCreateTimeBefore(RSSSource rssSource,Date createTime);

    RSSContentItem findFirstByGuid(String guid);

    @Query("select i from rss_content_item i order by i.createTime desc")
    List<RSSContentItem> findAllLimitSize(Pageable pageable);
}
