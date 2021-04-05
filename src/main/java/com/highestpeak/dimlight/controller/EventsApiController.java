package com.highestpeak.dimlight.controller;

import com.google.common.collect.Lists;
import com.highestpeak.dimlight.model.params.GetListBodyParams;
import com.highestpeak.dimlight.repository.EventRepository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/api/events/")
public class EventsApiController {
    @Resource
    private EventRepository eventRepository;

    /**
     * 现阶段先返回所有Events
     */
    @PostMapping("/get")
    public Object getEvents(@RequestBody GetListBodyParams getListBodyParams) {
        return Lists.newArrayList(eventRepository.findAll());
    }

}
