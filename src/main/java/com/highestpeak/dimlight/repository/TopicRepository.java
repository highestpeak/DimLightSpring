package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.MobiusTopic;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
public interface TopicRepository extends PagingAndSortingRepository<MobiusTopic, Integer> {

    MobiusTopic findByName(String topicName);

}
