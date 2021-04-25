package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.MobiusTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
public interface RSSSourceTagRepository extends PagingAndSortingRepository<MobiusTag, Integer> {

    MobiusTag findByName(String name);

    Page<MobiusTag> findByNameIn(List<String> names, Pageable pageable);
}
