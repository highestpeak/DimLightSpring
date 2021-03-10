package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.RSSSource;
import org.springframework.data.repository.CrudRepository;

/**
 * @author highestpeak
 * tmpdoc CrudRepository PagingAndSortingRepository JpaRepository
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public interface RSSSourceRepository extends CrudRepository<RSSSource,Integer> {
    RSSSource findByUrl(String url);
    RSSSource findByTitleUser(String titleUser);
    void deleteByTitleUser(String titleUser);
}
