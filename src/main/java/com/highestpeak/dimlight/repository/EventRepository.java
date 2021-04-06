package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event,Integer> {
    @Query("select i from event i")
    Page<Event> findList(Pageable pageable);
}
