package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
public interface RSSSourceTagRepository extends CrudRepository<RSSSourceTag,Integer> {
    RSSSourceTag findByName(String name);

    void deleteByName(String name);

    @Query("select t.id,t.name,t.descUser,t.createTime,t.updateTime from rss_source_tag t where t.name in :names")
    Page<RSSSourceTag> findByNames(Pageable pageable, List<String> names);

    @Query("select t.id,t.name,t.rssSources from rss_source_tag t where t.name in :tagNames")
    List<RSSSourceTag> findRssByTagNames(List<String> tagNames);

    @Query("select t.id,t.name,t.rssContentItems from rss_source_tag t where t.name in :tagNames")
    List<RSSSourceTag> findItemsByTagNames(List<String> tagNames);

    @Query("select r from rss_source_tag r")
    Page<RSSSourceTag> findList(Pageable pageable);
}
