package com.highestpeak.dimlight.controller;

import com.highestpeak.dimlight.repository.EventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @GetMapping("/list")
    public Object getEvents(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.Direction.ASC, "id");
        return eventRepository.findList(pageable);
    }

    @PostMapping("/clear")
    public Object clearEvents(){
        try {
            eventRepository.deleteAll();
        }catch (Exception e){
            return "failed";
        }
        return "success";
    }
}
