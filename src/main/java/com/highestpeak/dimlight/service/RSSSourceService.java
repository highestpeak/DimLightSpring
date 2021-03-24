package com.highestpeak.dimlight.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.highestpeak.dimlight.model.entity.RSSContentItem;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import com.highestpeak.dimlight.model.entity.Topic;
import com.highestpeak.dimlight.model.params.DeleteRssParams;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.RSSContentItemProcess;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import com.highestpeak.dimlight.repository.RSSSourceTagRepository;
import com.highestpeak.dimlight.repository.TopicRepository;
import com.highestpeak.dimlight.service.info.process.InfoProcess;
import com.highestpeak.dimlight.utils.RSSUtils;
import org.dom4j.*;
import org.dom4j.tree.AbstractElement;
import org.dom4j.tree.DefaultElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaClassNamingShouldBeCamel"})
@Service
public class RSSSourceService {
    @Resource
    private RSSSourceRepository rssSourceRepository;
    @Resource
    private RSSSourceTagRepository rssSourceTagRepository;
    @Resource
    private TopicRepository topicRepository;
    @Resource
    private ProcessService processService;

    //----------------crud----------------//

    public ErrorMessages newOrUpdateRSSSource(RSSSourceParams rssSourceParams) {
        ErrorMessages msg = new ErrorMessages();
        RSSSource rssSource = rssSourceRepository.findByTitleUser(rssSourceParams.getTitleUser());

        boolean isRssSourceExist = true;
        if (rssSource == null) {
            rssSource = RSSSource.builder()
                    .url(rssSourceParams.getUrl())
                    .titleUser(rssSourceParams.getTitleUser())
                    .descUser(rssSourceParams.getDescUser())
                    .image(rssSourceParams.getImage())
                    .generator(rssSourceParams.getGenerator())
                    .jsonOptionalExtraFields(rssSourceParams.getJsonOptionalExtraFields())
                    .fetchAble(rssSourceParams.isFetchAble())
                    .build();
            isRssSourceExist = false;
        }

        List<String> tags = rssSourceParams.getTags();
        if (tags != null && tags.size() > 0) {
            Set<String> nextTagNameSet = new HashSet<>(tags);
            List<RSSSourceTag> rssSourceTags = null;
            if (isRssSourceExist) {
                List<RSSSourceTag> currTags = rssSource.getRssSourceTags();
                Set<String> currTagNameSet = currTags.stream().map(RSSSourceTag::getName).collect(Collectors.toSet());
                nextTagNameSet.retainAll(currTagNameSet);
            }
            rssSourceTags = nextTagNameSet.stream().map(this::fetchOrCreateTag).collect(Collectors.toList());
            rssSource.setRssSourceTags(rssSourceTags);
        }

        List<String> topics = rssSourceParams.getTopics();
        if (topics != null && topics.size() > 0) {
            Set<String> nextTopicNameSet = new HashSet<>(topics);
            List<Topic> rssSourceTopics = null;
            if (isRssSourceExist) {
                List<Topic> currTopics = rssSource.getRssTopics();
                Set<String> currTopicNameSet = currTopics.stream().map(Topic::getName).collect(Collectors.toSet());
                nextTopicNameSet.retainAll(currTopicNameSet);
            }
            rssSourceTopics = nextTopicNameSet.stream().map(this::fetchOrCreateTopic).collect(Collectors.toList());
            rssSource.setRssTopics(rssSourceTopics);
        }

        try {
            RSSSource savedSource = rssSourceRepository.save(rssSource);
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("保存 RSSSource 时发生错误", e));
        }
        return msg;
    }

    private RSSSourceTag fetchOrCreateTag(String tagName) {
        return fetchOrCreateTag(tagName, null);
    }

    private RSSSourceTag fetchOrCreateTag(String tagName, String desc) {
        RSSSourceTag rssSourceTag = rssSourceTagRepository.findByName(tagName);
        if (rssSourceTag == null) {
            rssSourceTag = RSSSourceTag.builder().name(tagName).desc(desc).build();
            rssSourceTag = rssSourceTagRepository.save(rssSourceTag);
        }
        return rssSourceTag;
    }

    private Topic fetchOrCreateTopic(String topicName) {
        return fetchOrCreateTopic(topicName, null);
    }

    private Topic fetchOrCreateTopic(String topicName, String desc) {
        Topic topic = topicRepository.findByName(topicName);
        if (topic == null) {
            topic = Topic.builder().name(topicName).desc(desc).build();
            topic = topicRepository.save(topic);
        }
        return topic;
    }

    public Object deleteRSSSource(DeleteRssParams deleteRssParams) {
        ErrorMessages msg = new ErrorMessages();
        try {
            if (deleteRssParams.getId() != null) {
                rssSourceRepository.deleteById(deleteRssParams.getId());
            } else {
                rssSourceRepository.deleteByTitleUser(deleteRssParams.getTitleUser());
            }
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("删除 RSSSource 时发生错误", e));
        }
        return msg;
    }

    public Object getRSSList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return rssSourceRepository.findList(pageable);
    }

    public Object getRSSListByTitle(int pageNumber, int pageSize, List<String> titleUsers, List<String> titleParses) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return rssSourceRepository.getRSSListByTitle(pageable, titleUsers, titleParses);
    }

    public Object getContentItems(int pageNumber, int pageSize, List<String> titleUsers, List<String> titleParses,
                                  List<Integer> ids) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return rssSourceRepository.getTargetRSSForContentItems(pageable, titleUsers, titleParses, ids);
    }

    //----------------opml----------------//

    // xPath能够方便的替换选取表达式
    private static final String OPML_HEAD_XPATH = "opml/head";
    private static final String OPML_OUTLINES_XPATH = "opml/body/outline";
    private static final Set<String> OPML_RSS_OUTLINE_ATTRIBUTE_MUST_HAVE = Sets.newHashSet("text", "type", "xmlUrl");

    /**
     * 规范
     * https://webcache.googleusercontent.com/search?q=cache:03ayRFIKowoJ:https://www.cnblogs
     * .com/dandandan/archive/2006/04/16/376691.html+&cd=2&hl=zh-CN&ct=clnk&gl=hk
     * http://dev.opml.org/spec1.html
     * https://jiongks.name/blog/2011-01-09/
     * dom4j https://dom4j.github.io/
     */
    public ErrorMessages addRssSourceFromOpml(Document document) {
        ErrorMessages msg = new ErrorMessages();

        // 头部节点
        Node headNode = document.selectSingleNode(OPML_HEAD_XPATH);
        Map<String, String> headValues = null;
        if (headNode != null && headNode.hasContent() && headNode instanceof DefaultElement) {
            DefaultElement headElement = (DefaultElement) headNode;
            List<Node> content = headElement.content();
            headValues = content.parallelStream()
                    .filter(node -> node instanceof DefaultElement)
                    .map(node -> (DefaultElement) node)
                    .collect(Collectors.toMap(AbstractElement::getName, DefaultElement::getText));
        }

        // 所有携带rss订阅源信息的outline节点
        List<Node> list = document.selectNodes(OPML_OUTLINES_XPATH);
        List<Map<String, String>> outlines = Lists.newArrayListWithCapacity(list.size());
        for (Node node : list) {
            List<Attribute> attributes = ((DefaultElement) node).attributes();
            Map<String, String> outline = attributes.parallelStream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getText));
            if (isLegalRssOutline(outline)) {
                outlines.add(outline);
            }
        }

        // save rssSource here
        try {
            outlines.parallelStream().map(this::opmlOutlineToRssSource).map(rssSourceRepository::save).close();
        } catch (Exception e) {
            msg.addMsg("addRssSourceFromOpml error: error to save rssSource to db");
        }

        return msg;
    }

    /**
     * text/type/xmlUrl是必须项
     * title/description/htmlUrl都是可选的
     * rss的type必须是"rss"
     */
    private boolean isLegalRssOutline(Map<String, String> outline) {
        return outline.keySet().containsAll(OPML_RSS_OUTLINE_ATTRIBUTE_MUST_HAVE) && outline.get("type").equals("rss");
    }

    /**
     * 认为text总是存在,当text/title同时存在时,以text为准
     */
    private RSSSource opmlOutlineToRssSource(Map<String, String> outline) {
        // xmlUrl和htmlUrl分别表示这个outline的RSS地址和网站地址,不是所有的RSS都有htmlUrl
        String rssUrl = outline.get("xmlUrl");
        RSSXml rssXml = RSSUtils.getRSSXml(rssUrl);
        return RSSSource.builder()
                .url(rssUrl)
                .titleUser(outline.get("text"))
                .titleParse(rssXml.getTitle())
                .descParse(rssXml.getDescription())
                .generator(rssXml.getGenerator())
                .link(outline.get("htmlUrl"))
                .fetchAble(true)
                .build();
    }

    public Document exportRssSourceAsOpml() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("opml");

        Element head = root.addElement("head");
        // build head
        Element headTitle = head.addElement("title");
        headTitle.addText("dim light export");

        // build body
        Element body = root.addElement("body");
        Iterable<RSSSource> all = rssSourceRepository.findAll();
        ArrayList<RSSSource> rssSources = Lists.newArrayList(all);
        rssSources.parallelStream().map(rssSource -> body
                .addElement("outline")
                .addAttribute("type", "rss")
                .addAttribute("text", rssSource.getTitleUser())
                .addAttribute("xmlUrl", rssSource.getUrl())
        ).close();

        return document;
    }

    //----------------json----------------//

    /**
     * {
     * "head": {},
     * "tags": [ { "id":xxx, "name":"xxx", "desc":"xxx" } ],
     * "topics": [ { "id":xxx, "name":"xxx", "desc":"xxx" } ],
     * "sources": [ { RSSSource属性名:值 } ],
     * "rssSourceTags": [ { "rssId":xxx, "tagIds":[1,2,3,4....] } ],
     * "rssTopics": [ { "rssId":xxx, "topicIds":[1,2,3,4....] } ]
     * }
     */
    public ErrorMessages addRssSourceFromJson(String json) {
        ErrorMessages msg = new ErrorMessages();
        JSONObject jsonObject = new JSONObject(json);
        // JSONObject head = jsonObject.getJSONObject("head");

        // 创建tag,解析tag和rss的映射
        JSONArray tags = jsonObject.getJSONArray("tags");
        Map<Integer, RSSSourceTag> tagsMap = Maps.newHashMapWithExpectedSize(tags.length());
        for (int i = 0; i < tags.length(); i++) {
            JSONObject tagJSONObject = tags.getJSONObject(i);
            try {
                RSSSourceTag rssSourceTag = fetchOrCreateTag(
                        tagJSONObject.getString("name"),
                        tagJSONObject.getString("desc")
                );
                tagsMap.put(tagJSONObject.getInt("id"), rssSourceTag);
            } catch (Exception e) {
                msg.addMsg("create tag:" + tagJSONObject.getString("name") + " failed");
            }
        }
        JSONArray rssWithTagIds = jsonObject.getJSONArray("rssSourceTags");
        Map<Integer, List<RSSSourceTag>> rssWithTagIdsMap = Maps.newHashMapWithExpectedSize(rssWithTagIds.length());
        for (int i = 0; i < rssWithTagIds.length(); i++) {
            JSONObject rssWithTagIdJSONObject = rssWithTagIds.getJSONObject(i);
            JSONArray tagIdsJSONObject = rssWithTagIdJSONObject.getJSONArray("tagIds");
            List<RSSSourceTag> rssSourceTags = tagIdsJSONObject.toList().parallelStream()
                    .map(o -> (Integer) o)
                    .map(tagsMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            rssWithTagIdsMap.put(rssWithTagIdJSONObject.getInt("rssId"), rssSourceTags);
        }

        // 创建topic,解析topic和rss的映射
        JSONArray topics = jsonObject.getJSONArray("topics");
        Map<Integer, Topic> topicsMap = Maps.newHashMapWithExpectedSize(topics.length());
        for (int i = 0; i < topics.length(); i++) {
            JSONObject topicsJSONObject = topics.getJSONObject(i);
            try {
                Topic topic = fetchOrCreateTopic(
                        topicsJSONObject.getString("name"),
                        topicsJSONObject.getString("desc")
                );
                topicsMap.put(topicsJSONObject.getInt("id"), topic);
            } catch (Exception e) {
                msg.addMsg("create tag:" + topicsJSONObject.getString("name") + " failed");
            }
        }
        JSONArray rssWithTopicIds = jsonObject.getJSONArray("rssTopics");
        Map<Integer, List<Topic>> rssWithTopicIdsMap = Maps.newHashMapWithExpectedSize(rssWithTopicIds.length());
        for (int i = 0; i < rssWithTopicIds.length(); i++) {
            JSONObject rssWithTopicIdJSONObject = rssWithTopicIds.getJSONObject(i);
            JSONArray topicIdsJSONObject = rssWithTopicIdJSONObject.getJSONArray("topicIds");
            List<Topic> rssSourceTopics = topicIdsJSONObject.toList().parallelStream()
                    .map(o -> (Integer) o)
                    .map(topicsMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            rssWithTopicIdsMap.put(rssWithTopicIdJSONObject.getInt("rssId"), rssSourceTopics);
        }

        // 构建source并保存source
        JSONArray sources = jsonObject.getJSONArray("sources");
        for (int i = 0; i < sources.length(); i++) {
            JSONObject rssJSONObject = sources.getJSONObject(i);
            Integer rssId = rssJSONObject.getInt("id");
            RSSSource rssSource = RSSSource.builder()
                    .url(rssJSONObject.getString("url"))
                    .titleUser(rssJSONObject.getString("titleUser"))
                    .titleParse(rssJSONObject.getString("titleParse"))
                    .descUser(rssJSONObject.getString("descUser"))
                    .descParse(rssJSONObject.getString("descParse"))
                    .link(rssJSONObject.getString("link"))
                    .image(rssJSONObject.getString("image"))
                    .generator(rssJSONObject.getString("generator"))
                    .jsonOptionalExtraFields(rssJSONObject.getString("jsonOptionalExtraFields"))
                    .fetchAble(rssJSONObject.getBoolean("fetchAble"))
                    .rssSourceTags(rssWithTagIdsMap.get(rssId))
                    .rssTopics(rssWithTopicIdsMap.get(rssId))
                    .build();
            try {
                rssSourceRepository.save(rssSource);
            } catch (Exception e) {
                msg.addMsg("create rssId:" + rssId + ", titleUser " + rssSource.getTitleUser() + " failed");
            }
        }
        return msg;
    }

    public JSONObject exportRssSourceAsJson() {
        JSONObject outputJson = new JSONObject();
        outputJson.put("head", new Object());

        Iterable<RSSSource> all = rssSourceRepository.findAll();
        ArrayList<RSSSource> rssSources = Lists.newArrayList(all);
        JSONArray rssJsonArray = new JSONArray(rssSources);
        outputJson.put("sources", rssJsonArray);

        Map<Integer, RSSSourceTag> tagMap = Maps.newHashMap();
        Map<Integer, Topic> topicMap = Maps.newHashMap();
        Map<Integer, List<Integer>> rssSourceTagsMap = Maps.newHashMapWithExpectedSize(rssSources.size());
        Map<Integer, List<Integer>> rssTopicsMap = Maps.newHashMapWithExpectedSize(rssSources.size());
        for (RSSSource rssSource : rssSources) {
            List<Integer> tagIds = Lists.newArrayListWithCapacity(rssSource.getRssSourceTags().size());
            rssSource.getRssSourceTags().forEach(rssSourceTag -> {
                tagMap.putIfAbsent(rssSourceTag.getId(), rssSourceTag);
                tagIds.add(rssSourceTag.getId());
            });
            rssSourceTagsMap.put(rssSource.getId(), tagIds);

            List<Integer> topicIds = Lists.newArrayListWithCapacity(rssSource.getRssTopics().size());
            rssSource.getRssTopics().forEach(topic -> {
                topicMap.putIfAbsent(topic.getId(), topic);
                topicIds.add(topic.getId());
            });
            rssTopicsMap.put(rssSource.getId(), topicIds);
        }

        outputJson.put("tags", tagMap);
        outputJson.put("topics", topicMap);
        outputJson.put("rssSourceTags", rssSourceTagsMap);
        outputJson.put("rssTopics", rssTopicsMap);

        return outputJson;
    }

    //----------------fetchRSS----------------//

    public ErrorMessages fetchRSSFromInternal(List<RSSSource> rssSourceToEmit) {
        return fetchRSS(rssSourceToEmit);
    }

    public ErrorMessages fetchRSSFromParams(List<RSSSourceParams> rssSourceToEmit) {
        ErrorMessages msg = new ErrorMessages();
        // fetch rssSourceToEmit from database
        List<RSSSource> rssSources = Lists.newArrayListWithCapacity(rssSourceToEmit.size());
        for (RSSSourceParams rssSource : rssSourceToEmit) {
            RSSSource source = rssSourceRepository.findByUrl(rssSource.getUrl());
            if (source != null && source.isFetchAble()) {
                rssSources.add(source);
            } else {
                msg.addMsg("source not found, source:" + rssSource.getUrl());
            }
        }
        msg.mergeMsg(fetchRSS(rssSources));
        return msg;
    }

    private ErrorMessages fetchRSS(List<RSSSource> rssSources) {
        ErrorMessages msg = new ErrorMessages();
        for (RSSSource rssSource : rssSources) {
            fetchRSSHelp(rssSource);
        }
        return msg;
    }

    /**
     * process chain from jsonOptionalExtraFields
     * jsonOptionalExtraFields 的字段解析
     * "processChain":[
     * {
     * "process": "CombineInfoProcess",
     * "args": ...
     * },
     * {...},
     * {
     * "process": "DuplicateRemoveProcess",
     * "args": ...
     * }
     * ]
     *
     * @see RSSSourceParams#getJsonOptionalExtraFields()
     * @see RSSSource#getJsonOptionalExtraFields()
     */
    @SuppressWarnings("DuplicatedCode")
    private void fetchRSSHelp(RSSSource rssSource) {
        // fetch new content
        RSSXml rssXml = RSSUtils.getRSSXml(rssSource.getUrl());
        List<RSSXml.RSSXmlItem> rssXmlItems = rssXml.getItems();
        if (rssXmlItems == null || rssXmlItems.size() <= 0) {
            return;
        }

        // get process chain from jsonOptionalExtraFields
        Queue<InfoProcess> infoProcessQueue = processService.buildProcessQueue(rssSource);

        // process rss xml items
        List<RSSContentItem> rssContentItems = null;
        if (infoProcessQueue == null) {
            rssContentItems = convertNoProcessRSSXmlItem(rssSource, rssXmlItems);
        } else {
            List<RSSContentItemProcess> rssContentItemProcesses = convertRSSXmlItem(rssXmlItems);
            while (infoProcessQueue.size() > 0) {
                InfoProcess nextProcess = infoProcessQueue.poll();
                // process 完后 可能会产生新的数据
                rssContentItemProcesses = nextProcess.process(rssContentItemProcesses, rssSource);
                if (rssContentItemProcesses.size() <= 0) {
                    return;
                }
            }
            rssContentItems = convertRSSContentItem(rssSource, rssContentItemProcesses);
        }

        // save content
        // todo: 这里必须要向 es 写入一份数据
        // esContentRepository.saveAll(convertToEsContent(rssContentItems));
        // contentItemRepository.saveAll(rssContentItems);
    }

    private List<RSSContentItemProcess> convertRSSXmlItem(List<RSSXml.RSSXmlItem> rssXmlItems) {
        List<RSSContentItemProcess> rssContentItems = new ArrayList<>(rssXmlItems.size());
        for (RSSXml.RSSXmlItem rssXmlItem : rssXmlItems) {
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

    private List<RSSContentItem> convertNoProcessRSSXmlItem(RSSSource rssSource, List<RSSXml.RSSXmlItem> rssXmlItems) {
        List<RSSContentItem> rssContentItems = new ArrayList<>(rssXmlItems.size());
        for (RSSXml.RSSXmlItem rssXmlItem : rssXmlItems) {
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
        for (RSSContentItemProcess rssXmlItem : rssXmlItems) {
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
