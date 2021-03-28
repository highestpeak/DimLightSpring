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

    /**
     * 使用top防止由多次插入的情况出现
     */
    RSSSource findTopByUrl(String url);

    RSSSource findByTitleUser(String titleUser);

    void deleteByTitleUser(String titleUser);

    @Query("select r.id from rss_source r")
    Page<Integer> findList(Pageable pageable);

    @Query("select r.id from rss_source r "
            + "where r.titleUser in :titleUsers or r.titleParse in :titleParses")
    Page<Integer> getRSSListByTitle(Pageable pageable, List<String> titleUsers, List<String> titleParses);

    @Query("select r.contentItems from rss_source r where r.id in :ids and r.titleUser in :titleUsers or r.titleParse in :titleParses")
    Page<RSSContentItem> getTargetRSSForContentItems(Pageable pageable, List<String> titleUsers, List<String> titleParses,
            List<Integer> ids);
}
