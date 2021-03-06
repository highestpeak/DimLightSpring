package com.highestpeak.dimlight.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.highestpeak.dimlight.service.info.process.InfoProcess;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessUtils {
    public static Queue<InfoProcess> buildProcessQueue(JsonNode jsonProcess){
        return new LinkedList<>();
    }
}
