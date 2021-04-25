package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.RSSSource;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author highestpeak
 * tmpdoc CrudRepository PagingAndSortingRepository JpaRepository
 */
public interface RSSSourceRepository extends PagingAndSortingRepository<RSSSource, Integer> {

    /**
     * 使用top防止由多次插入的情况出现
     */
    RSSSource findTopByUrl(String url);

    RSSSource findByTitleUser(String titleUser);

}
