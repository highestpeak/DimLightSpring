package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author highestpeak
 */
@SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaClassNamingShouldBeCamel"})
@Service
public class RSSSourceService {
    @Autowired
    private RSSSourceRepository rssSourceRepository;
    @Autowired
    private TaskService taskService;

    public static final String SAVE_RSS_SOURCE_ERROR_MSG = "保存 RSSSource 时发生错误;RSSSourceService:newRSSSource(..)";

    public ErrorMessages newRSSSource(RSSSourceParams rssSourceParams) {
        ErrorMessages msg = new ErrorMessages();
        RSSSource rssSource = rssSourceParams.convertTo();
        try {
            RSSSource savedSource = rssSourceRepository.save(rssSource);
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(SAVE_RSS_SOURCE_ERROR_MSG,e));
        }
        return msg;
    }
}
