package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.EsContent;
import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.ESContentRepository;
import com.highestpeak.dimlight.repository.RSSContentItemRepository;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import com.highestpeak.dimlight.service.info.process.InfoProcess;
import com.highestpeak.dimlight.utils.RSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author highestpeak
 */
@SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaClassNamingShouldBeCamel"})
@Service
public class RSSSourceService {
    @Autowired
    private ESContentRepository esContentRepository;
    @Autowired
    private RSSSourceRepository rssSourceRepository;
    @Autowired
    private RSSContentItemRepository contentItemRepository;
    @Autowired
    private ProcessService processService;

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

    public ErrorMessages fetchRSS(List<RSSSourceParams> rssSourceToEmit){
        ErrorMessages msg = new ErrorMessages();
        // fetch rssSourceToEmit from database
        for (RSSSourceParams rssSource : rssSourceToEmit) {
            RSSSource source = rssSourceRepository.findByUrl(rssSource.getUrl());
            if (source!=null && source.isFetchAble()){
                fetchRSSHelp(source);
            }
        }
        // todo error msg
        return msg;
    }

    /**
     * process chain from jsonOptionalExtraFields
     * jsonOptionalExtraFields 的字段解析
     * "processChain":[
     *   {
     *       "process": "CombineInfoProcess",
     *       "args": ...
     *   },
     *   {...},
     *   {
     *       "process": "DuplicateRemoveProcess",
     *       "args": ...
     *   }
     * ]
     * @see RSSSourceParams#getJsonOptionalExtraFields()
     * @see RSSSource#getJsonOptionalExtraFields()
     */
    @SuppressWarnings("DuplicatedCode")
    private void fetchRSSHelp(RSSSource rssSource){
        // fetch new content
        RSSXml rssXml = RSSUtils.getRSSXml(rssSource.getUrl());
        List<RSSXml.RSSXmlItem> rssXmlItems = rssXml.getItems();
        if (rssXmlItems==null||rssXmlItems.size()<=0){
            return;
        }

        // get process chain from jsonOptionalExtraFields
        Queue<InfoProcess> infoProcessQueue = processService.buildProcessQueue(rssSource);

        // process rss xml items
        List<RSSContentItem> rssContentItems = null;
        if (infoProcessQueue==null) {
            rssContentItems = convertNoProcessRSSXmlItem(rssSource,rssXmlItems);
        }else {
            List<RSSContentItemProcess> rssContentItemProcesses = convertRSSXmlItem(rssXmlItems);
            while (infoProcessQueue.size() > 0){
                InfoProcess nextProcess = infoProcessQueue.poll();
                // process 完后 可能会产生新的数据
                rssContentItemProcesses = nextProcess.process(rssContentItemProcesses, rssSource, esContentRepository);
                if (rssContentItemProcesses.size()<=0){
                    return;
                }
            }
            rssContentItems = convertRSSContentItem(rssSource,rssContentItemProcesses);
        }

        // save content
        // todo: 这里可以采用向 elasticSearch 保存数据 idnex? property?
        esContentRepository.saveAll(convertToEsContent(rssContentItems));
        // contentItemRepository.saveAll(rssContentItems);
    }

    private List<RSSContentItemProcess> convertRSSXmlItem(List<RSSXml.RSSXmlItem> rssXmlItems){
        List<RSSContentItemProcess> rssContentItems = new ArrayList<>(rssXmlItems.size());
        for (RSSXml.RSSXmlItem rssXmlItem: rssXmlItems) {
            rssContentItems.add(RSSContentItemProcess.builder()
                    .title(rssXmlItem.getTitle())
                    .description(rssXmlItem.getDescription())
                    .link(rssXmlItem.getLink())
                    .guid(rssXmlItem.getGuid())
                    .pubDate(rssXmlItem.getPubDate())
                    .author(rssXmlItem.getAuthor())
                    .build()
            );
        }
        return rssContentItems;
    }

    private List<EsContent> convertToEsContent(List<RSSContentItem> rssContentItems){
        List<EsContent> esContents = new ArrayList<>(rssContentItems.size());
        for (RSSContentItem rssContentItem: rssContentItems) {
            esContents.add(EsContent.builder()
                    .titleParse(rssContentItem.getTitleParse())
                    .descParse(rssContentItem.getDescParse())
                    .author(rssContentItem.getAuthor())
                    .link(rssContentItem.getLink())
                    .build()
            );
        }
        return esContents;
    }

    private List<RSSContentItem> convertNoProcessRSSXmlItem(RSSSource rssSource, List<RSSXml.RSSXmlItem> rssXmlItems) {
        List<RSSContentItem> rssContentItems = new ArrayList<>(rssXmlItems.size());
        for (RSSXml.RSSXmlItem rssXmlItem: rssXmlItems) {
            rssContentItems.add(RSSContentItem.builder()
                    .titleParse(rssXmlItem.getTitle())
                    .descParse(rssXmlItem.getDescription())
                    .link(rssXmlItem.getLink())
                    .guid(rssXmlItem.getGuid())
                    .pubDate(rssXmlItem.getPubDate())
                    .author(rssXmlItem.getAuthor())
                    .rssSource(rssSource)
                    .build()
            );
        }
        return rssContentItems;
    }

    private List<RSSContentItem> convertRSSContentItem(RSSSource rssSource, List<RSSContentItemProcess> rssXmlItems) {
        List<RSSContentItem> rssContentItems = new ArrayList<>(rssXmlItems.size());
        for (RSSContentItemProcess rssXmlItem: rssXmlItems) {
            rssContentItems.add(RSSContentItem.builder()
                    .titleParse(rssXmlItem.getTitle())
                    .descParse(rssXmlItem.getDescription())
                    .link(rssXmlItem.getLink())
                    .guid(rssXmlItem.getGuid())
                    .pubDate(rssXmlItem.getPubDate())
                    .author(rssXmlItem.getAuthor())
                    .jsonOptionalExtraFields(rssXmlItem.getJsonOptionalExtraFields())
                    .rssSource(rssSource)
                    .build()
            );
        }
        return rssContentItems;
    }
}
