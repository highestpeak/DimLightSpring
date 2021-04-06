package com.highestpeak.dimlight.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.highestpeak.dimlight.model.entity.Topic;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
public interface TopicRepository extends CrudRepository<Topic, Integer> {
    Topic findByName(String topicName);

    @Query("select t.id,t.name,t.descUser,t.createTime,t.updateTime from topic t where t.name in :topicNames")
    Page<Topic> findByNames(Pageable pageable,List<String> topicNames);

    @Query("select t.id,t.name,t.rssSources from topic t where t.name in :topicNames")
    List<Topic> findRssByTopicNames(List<String> topicNames);

    @Query("select t.id,t.name,t.rssContentItems from topic t where t.name in :topicNames")
    List<Topic> findItemsByTopicNames(List<String> topicNames);

    void deleteByName(String topicName);

    @Query("select r from topic r")
    Page<Topic> findList(Pageable pageable);
}
