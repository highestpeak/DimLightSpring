package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.MobiusContentItem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobiusContentItemRepository extends PagingAndSortingRepository<MobiusContentItem,Integer> {

    List<MobiusContentItem> findByOriginContentTypeAndOriginSourceIdIn(int originContentType, List<Integer> originSourceId);
}
