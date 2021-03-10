package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.Topic;
import org.springframework.data.repository.CrudRepository;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
public interface TopicRepository extends CrudRepository<Topic,Integer> {
    Topic findByName(String topicName);
}
