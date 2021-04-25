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
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import com.highestpeak.dimlight.utils.JacksonUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.highestpeak.dimlight.repository.RSSContentItemRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-20
 */
@Service
public class RssContentItemService {

    public static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    @Resource
    private RSSContentItemRepository contentItemRepository;
    @Resource
    private RSSSourceRepository rssSourceRepository;

    public InfoMessages delContentByIdList(Integer delId) {
        InfoMessages msg = new InfoMessages();
        try {
            contentItemRepository.deleteById(delId);
        } catch (Exception e) {
            msg.addErrorMsg("删除RssItem异常, id:" + delId);
        }
        return msg;
    }

    public InfoMessages delContentOutOfTime(Date earliestTimeToLive) {
        InfoMessages msg = new InfoMessages();
        try {
            contentItemRepository.deleteByCreateTimeBefore(earliestTimeToLive);
        } catch (Exception e) {
            msg.addErrorMsg("删除过期RssItem异常 earliestTimeToLive:" + earliestTimeToLive);
        }
        return msg;
    }

    public InfoMessages delTargetRssContentOutOfTime(RSSSource rssSource, Date earliestTimeToLive) {
        InfoMessages msg = new InfoMessages();
        try {
            contentItemRepository.deleteByRssSourceIsAndCreateTimeBefore(rssSource, earliestTimeToLive);
        } catch (Exception e) {
            msg.addErrorMsg("删除过期RssItem异常 rss:" + rssSource + ", earliestTimeToLive:" + earliestTimeToLive);
        }
        return msg;
    }

    public Object getContentItemList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return contentItemRepository.findAll(pageable);
    }

    @Deprecated
    public Object getAll() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode rootSourceArrayNode = rootNode.putArray("feedItemBeans");
        Pageable pageable = PageRequest.of(0,40, Sort.Direction.ASC, "id");
        ArrayList<RSSContentItem> rssContentItems = Lists.newArrayList(contentItemRepository.findAll(pageable));
        List<ObjectNode> contentItemNodes = rssContentItems.stream()
                .map(rssContentItem -> JacksonUtils.contentItemToObjectNode(rssContentItem, mapper))
                .collect(Collectors.toList());
        rootSourceArrayNode.addAll(contentItemNodes);
        return rootNode;
    }

    public void saveRssXml(RSSSource rssSource, List<ProcessContext.XmlItemWithId> xmlItemWithIds) {
        String itemJsonExtra = rssSource.getJsonOptionalExtraFields();

        List<RSSContentItem> rssContentItems = xmlItemWithIds.stream()
                .map(xmlItemWithId -> {
                    RSSXml.RSSXmlItem rssXmlItem = xmlItemWithId.getRssXmlItem();

                    return RSSContentItem.builder()
                            .titleParse(rssXmlItem.getTitle())
                            .descParse(rssXmlItem.getDescription())
                            .link(rssXmlItem.getLink())
                            .guid(rssXmlItem.getGuid())
                            .pubDate(Optional.ofNullable(rssXmlItem.getPubDate()).orElseGet(Date::new))
                            .author(rssXmlItem.getAuthor())
                            .jsonOptionalExtraFields(itemJsonExtra)
                            .rssSource(rssSource)
                            //.rssItemMobiusTags(processContext.getXmlTag(id))
                            //.itemMobiusTopics(processContext.getXmlTopic(id))
                            .build();
                }).collect(Collectors.toList());
        //saveContentItems(rssContentItems);
    }

    public void saveRssXml(ProcessContext processContext) {
        RSSSource rssSource = processContext.getRssSource();
        RSSXml originRssXml = processContext.getOriginXml();
        List<RSSXml.RSSXmlItem> originRssXmlItems = originRssXml.getItems();
        Map<Integer, RSSXml.RSSXmlItem> idXmlMap = Maps.newHashMapWithExpectedSize(originRssXmlItems.size());
        Map<Integer, RSSContentItem> contentItemMap = Maps.newHashMapWithExpectedSize(originRssXmlItems.size());

        for (int i = 0; i < originRssXmlItems.size(); i++) {
            RSSXml.RSSXmlItem originRssXmlItem = originRssXmlItems.get(i);
            idXmlMap.put(i, originRssXmlItem);
            contentItemMap.put(i, RSSContentItem.builder()
                    .titleParse(originRssXmlItem.getTitle())
                    .descParse(originRssXmlItem.getDescription())
                    .link(originRssXmlItem.getLink())
                    .guid(originRssXmlItem.getGuid())
                    .pubDate(Optional.ofNullable(originRssXmlItem.getPubDate()).orElseGet(Date::new))
                    .author(originRssXmlItem.getAuthor())
                    .rssSource(rssSource)
                    .build()
            );
        }

        InfoMessages msg = new InfoMessages();
        if (processContext.getXmlItemList()==null) {
            processContext.setXmlItemList(Maps.newHashMapWithExpectedSize(originRssXmlItems.size()));
        }
        Map<Integer, RSSXml.RSSXmlItem> xmlItemList = processContext.getXmlItemList();
        contentItemMap.forEach((id, rssContentItem) -> {
            try {
                RSSContentItem firstByGuid = contentItemRepository.findFirstByGuid(rssContentItem.getGuid());
                if (firstByGuid != null) {
                    return;
                }
                RSSContentItem savedItem = contentItemRepository.save(rssContentItem);
                xmlItemList.put(savedItem.getId(), idXmlMap.get(id));
            } catch (Exception e) {
                msg.addErrorMsg("保存RssItem异常" + rssContentItem);
            }
        });
        if (msg.hasError()) {
            throw new ErrorMsgException(msg);
        }
    }

    @Deprecated
    private void saveContentItems(List<RSSContentItem> contentItemList) {
        //InfoMessages msg = new InfoMessages();
        //contentItemList.forEach(rssContentItem -> {
        //    try {
        //        RSSContentItem firstByGuid = contentItemRepository.findFirstByGuid(rssContentItem.getGuid());
        //        if (firstByGuid != null) {
        //            return;
        //        }
        //        contentItemRepository.save(rssContentItem);
        //    } catch (Exception e) {
        //        msg.addErrorMsg("保存RssItem异常" + rssContentItem);
        //    }
        //});
        //if (msg.hasError()) {
        //    throw new ErrorMsgException(msg);
        //}
    }

    /**
     * todo:
     */
    public Object delContentItemBefore(Date delBarrier) {
        return null;
    }

    @Transactional
    public Object delTargetRssContentItem(int rssId) {
        RSSSource rssSource = RSSSource.builder().build();
        rssSource.setId(rssId);
        contentItemRepository.deleteByRssSourceIs(rssSource);
        return null;
    }

    public Object getTargetRssContentItem(int rssId, int num) {
        Optional<RSSSource> rssSourceOptional = rssSourceRepository.findById(rssId);
        if (!rssSourceOptional.isPresent()) {
            throw new ErrorMsgException("该rss不存在");
        }
        RSSSource rssSource = rssSourceOptional.get();
        List<RSSContentItem> rssContentItems = rssSource.getContentItems().stream().limit(num).collect(Collectors.toList());
        return rssContentItems;
    }
}
