package com.highestpeak.dimlight.repository;

import org.springframework.data.repository.CrudRepository;

import com.highestpeak.dimlight.model.entity.Topic;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
public interface TopicRepository extends CrudRepository<Topic, Integer> {
    Topic findByName(String topicName);

    void deleteByName(String topicName);
}
