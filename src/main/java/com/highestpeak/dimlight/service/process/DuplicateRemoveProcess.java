package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.stereotype.Component;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 去重
 * https://www.elastic.co/guide/cn/elasticsearch/guide/current/match-multi-word.html
 * 一定时间内（一天内、一周内），相似度去重
 *
 */
@Component
public class DuplicateRemoveProcess implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {
    }
}
