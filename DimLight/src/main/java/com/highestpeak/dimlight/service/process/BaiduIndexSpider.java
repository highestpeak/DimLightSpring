package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;

/**
 * todo: 百度指数爬虫，可以来提取内容的百度指数，做一个图出来，也可以作为内容评价的一个指标
 * 暂时作为时效性检测的依据
 */
public class BaiduIndexSpider implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {
        // https://github.com/liuhuanyong/BaiduIndexSpyder/blob/master/BaiduIndex.py
    }
}
