package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.MobiusEvent;
import com.highestpeak.dimlight.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * todo
 */
@Service
public class EventService {
    @Resource
    private EventRepository eventRepository;

    public Object getEventList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        Page<MobiusEvent> eventPage = eventRepository.findAll(pageable);
        return eventPage;
    }

    public Object getRssEvents(int rssId) {
        return null;
    }

    public Object clearAllEvents() {
        return null;
    }

    public Object clearTargetRssEvent(int rssId) {
        return null;
    }

    public Object clearTargetTaskEvent(int taskId) {
        return null;
    }

    public Object clearTargetTopicEvent(int topicId) {
        return null;
    }
}
