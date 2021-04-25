package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.stereotype.Component;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 根据一些参数进行评分，例如所有rss公用的评分：在同一个时间段内出现的次数、点赞数等
 * 时效性 等等（第一个提出来的就分值高）
 */
@Component
public class QualityEvalProcess implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {
    }
}
