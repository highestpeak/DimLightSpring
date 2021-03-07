package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author highestpeak
 */
@Data
@Builder
@NoArgsConstructor
public abstract class InfoProcess {
    private Object args;

    private InfoProcess before;
    private InfoProcess after;

    /**
     * 流程处理的通用逻辑,返回处理后的 rssXmlItem
     * @param rssXmlItemList
     */
    public void process(List<RSSContentItemProcess> rssXmlItemList){
        Object beforeResult = null;
        if (before!=null){
             beforeResult = before.process();
        }
        Object currResult = process();
        Object afterResult = after.process();

        List<Object> results = new ArrayList<>(3);
        results.add(beforeResult);
        results.add(currResult);
        results.add(afterResult);
    }

    /**
     * 传递参数
     */
    protected abstract void processArgs();

    /**
     * 仅供自类重写
     * @return todo: 需要的结果
     */
    protected abstract Object process();
}
