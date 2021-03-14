package com.highestpeak.dimlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.entity.RSSSource;

/**
 * @author highestpeak
 * tmpdoc CrudRepository PagingAndSortingRepository JpaRepository
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public interface RSSSourceRepository extends CrudRepository<RSSSource, Integer> {
    String FIELDS_EXCEPT_ITEMS_STR =
            "select r.id,r.titleParse,r.titleUser,r.createTime,r.descParse,r.descUser,r.fetchAble,r.generator,r.image,"
                    + "r.jsonOptionalExtraFields,r.link,r.url,r.updateTime,r.rssSourceTags,r.rssTopics ";

    RSSSource findByUrl(String url);

    RSSSource findByTitleUser(String titleUser);

    void deleteByTitleUser(String titleUser);

    @Query(FIELDS_EXCEPT_ITEMS_STR + "from rss_source r")
    Page<RSSSource> findList(Pageable pageable);

    @Query(FIELDS_EXCEPT_ITEMS_STR + "from rss_source r "
            + "where r.titleUser in :titleUsers or r.titleParse in :titleParses")
    Page<RSSSource> getRSSListByTitle(Pageable pageable, List<String> titleUsers, List<String> titleParses);

    @Query("select r.contentItems from rss_source r where r.id in :ids and r.titleUser in :titleUsers or r.titleParse in :titleParses")
    Page<RSSContentItem> getTargetRSSForContentItems(Pageable pageable, List<String> titleUsers, List<String> titleParses,
            List<Integer> ids);
}
