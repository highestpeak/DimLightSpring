package com.highestpeak.dimlight.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.highestpeak.dimlight.repository.RSSContentItemRepository;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-20
 */
@Service
public class ContentItemService {

    public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    @Autowired
    private RSSContentItemRepository contentItemRepository;

    public ErrorMessages delContentByIdList(List<Integer> delIdList) {
        ErrorMessages msg = new ErrorMessages();
        delIdList.forEach(id -> {
            try {
                contentItemRepository.deleteById(id);
            } catch (Exception e) {
                msg.addMsg("删除RssItem异常, id:" + id);
            }
        });
        return msg;
    }

    public ErrorMessages delContentOutOfTime(Date earliestTimeToLive) {
        ErrorMessages msg = new ErrorMessages();
        try {
            contentItemRepository.deleteByCreateTimeBefore(earliestTimeToLive);
        } catch (Exception e) {
            msg.addMsg("删除过期RssItem异常 earliestTimeToLive:" + earliestTimeToLive);
        }
        return msg;
    }

    public ErrorMessages delTargetRssContentOutOfTime(RSSSource rssSource, Date earliestTimeToLive) {
        ErrorMessages msg = new ErrorMessages();
        try {
            contentItemRepository.deleteByRssSourceIsAndCreateTimeBefore(rssSource, earliestTimeToLive);
        } catch (Exception e) {
            msg.addMsg("删除过期RssItem异常 rss:" + rssSource + ", earliestTimeToLive:" + earliestTimeToLive);
        }
        return msg;
    }

    public Object getContentItemList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return contentItemRepository.findList(pageable);
    }

    @Deprecated
    public Object getAll() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode rootSourceArrayNode = rootNode.putArray("feedItemBeans");
        ArrayList<RSSContentItem> rssContentItems = Lists.newArrayList(contentItemRepository.findAllLimitSize(PageRequest.of(0,40)));
        List<ObjectNode> contentItemNodes = rssContentItems.stream()
                .map(rssContentItem -> JacksonUtils.contentItemToObjectNode(rssContentItem, mapper))
                .collect(Collectors.toList());
        rootSourceArrayNode.addAll(contentItemNodes);
        return rootNode;
    }

    public ErrorMessages saveRssXml(ProcessContext processContext) {
        ErrorMessages msg = new ErrorMessages();
        RSSSource rssSource = processContext.getRssSource();
        String itemJsonExtra = rssSource.getJsonOptionalExtraFields();

        List<RSSContentItem> rssContentItems = processContext.getXmlItemList().stream()
                .map(xmlItemWithId -> {
                    RSSXml.RSSXmlItem rssXmlItem = xmlItemWithId.getRssXmlItem();
                    Integer id = xmlItemWithId.getId();

                    return RSSContentItem.builder()
                            .titleParse(rssXmlItem.getTitle())
                            .descParse(rssXmlItem.getDescription())
                            .link(rssXmlItem.getLink())
                            .guid(rssXmlItem.getGuid())
                            .pubDate(Optional.ofNullable(rssXmlItem.getPubDate()).orElseGet(Date::new))
                            .author(rssXmlItem.getAuthor())
                            .jsonOptionalExtraFields(itemJsonExtra)
                            .rssSource(rssSource)
                            .rssItemTags(processContext.getXmlTag(id))
                            .itemTopics(processContext.getXmlTopic(id))
                            .build();
                }).collect(Collectors.toList());
        ErrorMessages errorMessages = saveContentItems(rssContentItems);
        msg.mergeMsg(errorMessages);
        return msg;
    }

    public ErrorMessages saveRssXml(RSSSource rssSource, RSSXml originRssXml) {
        ErrorMessages msg = new ErrorMessages();
        List<RSSContentItem> contentItemList =
                originRssXml.getItems().stream().map(rssXmlItem -> RSSContentItem.builder()
                .titleParse(rssXmlItem.getTitle())
                .descParse(rssXmlItem.getDescription())
                .link(rssXmlItem.getLink())
                .guid(rssXmlItem.getGuid())
                .pubDate(Optional.ofNullable(rssXmlItem.getPubDate()).orElseGet(Date::new))
                .author(rssXmlItem.getAuthor())
                .rssSource(rssSource)
                .build()).collect(Collectors.toList());
        ErrorMessages errorMessages = saveContentItems(contentItemList);
        msg.mergeMsg(errorMessages);
        return msg;
    }

    private ErrorMessages saveContentItems(List<RSSContentItem> contentItemList) {
        ErrorMessages msg = new ErrorMessages();
        contentItemList.forEach(rssContentItem -> {
            try {
                RSSContentItem firstByGuid = contentItemRepository.findFirstByGuid(rssContentItem.getGuid());
                if (firstByGuid != null) {
                    return;
                }
                contentItemRepository.save(rssContentItem);
            } catch (Exception e) {
                msg.addMsg("保存RssItem异常" + rssContentItem);
            }
        });
        return msg;
    }
}
