package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event,Integer> {
}
