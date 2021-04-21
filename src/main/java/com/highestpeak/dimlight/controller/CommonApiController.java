package com.highestpeak.dimlight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.highestpeak.dimlight.model.enums.TaskOutputCacheCycleEnum;
import com.highestpeak.dimlight.utils.JacksonUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/common")
public class CommonApiController {
    /**
     * {@link TaskOutputCacheCycleEnum}
     */
    @GetMapping("/task_output_cache_cycle")
    public Object taskOutputCacheCycle(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        ArrayNode rootSourceArrayNode = rootNode.putArray("cacheCycleEnum");
        List<ObjectNode> cacheCycleNodeList = Arrays.stream(TaskOutputCacheCycleEnum.values()).map(
                taskOutputCacheCycle -> JacksonUtils.cacheCycleToObjectNode(taskOutputCacheCycle, mapper)
        ).collect(Collectors.toList());
        rootSourceArrayNode.addAll(cacheCycleNodeList);

        return rootNode;
    }

    /*
     * future: 稍后再读
     */
}
