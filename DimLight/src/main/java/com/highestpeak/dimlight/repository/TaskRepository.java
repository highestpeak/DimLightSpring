package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.MobiusTask;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TaskRepository extends PagingAndSortingRepository<MobiusTask,Integer> {

    List<MobiusTask> findByTaskType(int type);

    MobiusTask findTopByTaskTypeAndTaskOperator(int taskType, int taskOperator);
}
