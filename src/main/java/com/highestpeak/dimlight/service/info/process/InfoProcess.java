package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author highestpeak
 */
@Data
@Builder
public abstract class InfoProcess {
    private InfoProcess before;
    private InfoProcess after;

    private List<RSSContentItemProcess> rssXmlItemList;

    /**
     * 流程处理的通用逻辑,返回处理后的 rssXmlItem
     */
    public void run(){
        Object beforeResult = before.process();
        Object currResult = process();
        Object afterResult = after.process();

        List<Object> results = new ArrayList<>(3);
        results.add(beforeResult);
        results.add(currResult);
        results.add(afterResult);
    }

    /**
     * 仅供自类重写
     * @return todo: 需要的结果
     */
    protected abstract Object process();
}
