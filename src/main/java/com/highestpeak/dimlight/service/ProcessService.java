package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.service.info.process.CombineInfoProcess;
import com.highestpeak.dimlight.service.info.process.DuplicateRemoveProcess;
import com.highestpeak.dimlight.service.info.process.InfoProcess;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProcessService {
    public static final Map<String, InfoProcess> processMap = new HashMap<String, InfoProcess>(){{
        put(DuplicateRemoveProcess.class.getName(),new DuplicateRemoveProcess());
        put(CombineInfoProcess.class.getName(),new CombineInfoProcess());
    }};

    public Queue<InfoProcess> buildProcessQueue(RSSSource rssSource){
        Queue<InfoProcess> processQueue = new LinkedList<>();

        JSONArray processChainJson = new JSONObject(rssSource.getJsonOptionalExtraFields()).getJSONArray("processChain");
        for (int i = 0; i < processChainJson.length(); i++) {
            JSONObject jsonObject = processChainJson.getJSONObject(i);
            InfoProcess infoProcess = buildOneProcess(jsonObject);

            // 只有一级前后处理
            JSONObject before = jsonObject.getJSONObject("before");
            JSONObject after = jsonObject.getJSONObject("after");
            infoProcess.setBefore(buildOneProcess(before));
            infoProcess.setBefore(buildOneProcess(after));

            processQueue.add(infoProcess);
        }

        return processQueue;
    }

    private InfoProcess buildOneProcess(JSONObject jsonObject){
        String processName = jsonObject.getString("process");

        InfoProcess infoProcess = processMap.get(processName);

        JSONObject args = jsonObject.getJSONObject("args");
        infoProcess.setArgs(args);

        return infoProcess;
    }
}
