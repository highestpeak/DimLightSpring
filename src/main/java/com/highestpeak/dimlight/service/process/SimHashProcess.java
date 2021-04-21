package com.highestpeak.dimlight.service.process;

import com.highestpeak.dimlight.model.pojo.ProcessContext;
import org.springframework.stereotype.Component;

@Component
public class SimHashProcess implements InfoProcess{
    @Override
    public void process(ProcessContext processContext) {
        // todo https://github.com/sing1ee/simhash-java
        // simhash算法https://www.cnblogs.com/sddai/p/10088007.html
    }
}
