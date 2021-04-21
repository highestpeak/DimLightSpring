package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.stereotype.Component;

/**
 * 分词处理
 * 分词并进行排序
 * todo: 排序算法：
 *  1. 越靠前的给越高的权重（但是是分阶梯的，例如256个字符为一个阶梯，每个阶梯权重不一样，可以让第一阶梯权重较高）
 *  2. 靠数量加权(去除停用词后的)
 *  3. 带时间加权，时间越靠前的，在最近出现的越多的，给权重越高（可以存一个表，是热词表）
 */
@Component
public class WordSegmentProcess implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {

    }
}
