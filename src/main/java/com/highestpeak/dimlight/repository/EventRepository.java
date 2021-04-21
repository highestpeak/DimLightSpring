package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.MobiusEvent;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends PagingAndSortingRepository<MobiusEvent,Integer> {

}
