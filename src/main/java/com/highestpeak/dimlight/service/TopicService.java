package com.highestpeak.dimlight.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.highestpeak.dimlight.model.entity.Topic;
import com.highestpeak.dimlight.model.params.DeleteTopicParams;
import com.highestpeak.dimlight.model.params.TopicParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.repository.TopicRepository;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-13
 */
@Service
public class TopicService {

    public static final String DEL_TOPIC_ERROR_MSG = "删除 Topic 时发生错误;TopicService:deleteTopic(..)";
    public static final String SAVE_TOPIC_ERROR_MSG = "保存 Topic 时发生错误;TopicService:newOrUpdateTopic(..)";
    @Resource
    private TopicRepository topicRepository;

    public Object newOrUpdateTopic(TopicParams topicParams) {
        ErrorMessages msg = new ErrorMessages();
        Topic topic = topicRepository.findByName(topicParams.getName());

        boolean isTopicExist = false;
        if (topic == null) {
            topic = Topic.builder()
                    .name(topicParams.getName())
                    .desc(topicParams.getDesc())
                    .build();
            isTopicExist = true;
        }

        try {
            Topic savedTopic = topicRepository.save(topic);
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(SAVE_TOPIC_ERROR_MSG, e));
        }
        return msg;
    }

    public Object deleteTopic(DeleteTopicParams deleteTopicParams) {
        ErrorMessages msg = new ErrorMessages();
        try {
            if (deleteTopicParams.getId() != null) {
                topicRepository.deleteById(deleteTopicParams.getId());
            } else {
                topicRepository.deleteByName(deleteTopicParams.getName());
            }
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(DEL_TOPIC_ERROR_MSG, e));
        }
        return msg;
    }

    public Object getTopicListByName(int pageNumber, int pageSize, List<String> names){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        
        return null;
    }
}
