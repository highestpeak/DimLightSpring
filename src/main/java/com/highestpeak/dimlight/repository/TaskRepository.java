package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task,Integer> {
}
