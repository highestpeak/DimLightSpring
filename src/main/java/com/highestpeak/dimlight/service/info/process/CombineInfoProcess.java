package com.highestpeak.dimlight.service.info.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.stereotype.Component;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-07
 * 信息组合
 * 基于相似度聚合（标题相同，但是有不同的内容）
 * 文档合成
 */
@Component
public class CombineInfoProcess implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {
    }
}
