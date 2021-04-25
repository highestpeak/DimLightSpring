package com.highestpeak.dimlight.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Queues;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.service.process.InfoProcess;

import java.util.Map;
import java.util.Queue;

public class ProcessUtils {
    /**
     * 传入带有process列表的json字符串
     * process的字符串是有顺序的
     * {...,"process":["xxx","yyy",...],...}
     * future:暂时只做顺序处理，不做并行的process
     */
    public static Queue<InfoProcess> buildProcessQueue(String jsonContainsProcess, Map<String, InfoProcess> processMap)
            throws JsonProcessingException {
        Queue<InfoProcess> processQueue = Queues.newArrayDeque();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonContainsProcess);
        JsonNode process = jsonNode.get("process");
        if (process==null) {
            return processQueue;
        }
        for (final JsonNode processNameNode : process) {
            String processName = processNameNode.textValue();
            if (processMap.containsKey(processName)) {
                processQueue.add(processMap.get(processName));
            } else {
                throw new ErrorMsgException("处理名不存在 处理名：" + processName);
            }
        }
        return processQueue;
    }

    public static String remainProcessNames(Queue<InfoProcess> processQueue) {
        StringBuilder builder = new StringBuilder();
        for (InfoProcess infoProcess : processQueue) {
            builder.append(infoProcess.getClass().getName());
            builder.append(',');
        }
        return builder.subSequence(0, builder.length() - 1).toString();
    }
}
