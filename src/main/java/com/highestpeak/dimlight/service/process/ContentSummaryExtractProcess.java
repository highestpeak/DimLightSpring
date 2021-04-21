package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文档摘要生成
 * reference: https://github.com/PKULCWM/PKUSUMSUM
 */
@Component
public class ContentSummaryExtractProcess implements InfoProcess{
    @Value("${docSummaryMaxLen:256}")
    private int docSummaryMaxLen = 256;

    @Override
    public void process(ProcessContext processContext) {
        //todo
    }
}
