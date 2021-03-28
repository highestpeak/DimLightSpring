package com.highestpeak.dimlight.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.highestpeak.dimlight.exception.ErrorMsgException;
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
import com.highestpeak.dimlight.utils.JacksonUtils;
import com.highestpeak.dimlight.utils.RSSUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.dom4j.*;
import org.dom4j.tree.AbstractElement;
import org.dom4j.tree.DefaultElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
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

        // 查找现有id
        int id = -1;
        Date originCreatetime = null;
        RSSSource rssSource = rssSourceRepository.findByTitleUser(rssSourceParams.getTitleUser());
        if (rssSource == null) {
            rssSource = rssSourceRepository.findTopByUrl(rssSourceParams.getUrl());
            if (rssSource != null) {
                id = rssSource.getId();
                originCreatetime = rssSource.getCreateTime();
            }
        } else {
            id = rssSource.getId();
            originCreatetime = rssSource.getCreateTime();
        }

        // 构建rssSource
        rssSource = RSSSource.builder()
                .url(rssSourceParams.getUrl())
                .titleUser(rssSourceParams.getTitleUser())
                .descUser(rssSourceParams.getDescUser())
                .image(rssSourceParams.getImage())
                .generator(rssSourceParams.getGenerator())
                .jsonOptionalExtraFields(rssSourceParams.getJsonOptionalExtraFields())
                .fetchAble(rssSourceParams.isFetchAble())
                .build();
        if (id != -1) {
            rssSource.setId(id);
            rssSource.setCreateTime(originCreatetime == null ? new Date() : originCreatetime);
        }

        // 更新tags
        List<String> tags = rssSourceParams.getTags();
        if (tags != null) {
            Set<String> nextTagNameSet = new HashSet<>(tags);
            List<RSSSourceTag> rssSourceTags = nextTagNameSet.stream()
                    .map(this::fetchOrCreateTag)
                    .collect(Collectors.toList());
            rssSource.setRssSourceTags(rssSourceTags);
        }

        // 更新topics
        List<String> topics = rssSourceParams.getTopics();
        if (topics != null) {
            Set<String> nextTopicNameSet = new HashSet<>(topics);
            List<Topic> rssSourceTopics = nextTopicNameSet.stream()
                    .map(this::fetchOrCreateTopic)
                    .collect(Collectors.toList());
            rssSource.setRssTopics(rssSourceTopics);
        }

        try {
            // 查找rss的xml中的内容
            if (rssSource.getTitleParse() == null) {
                ImmutablePair<RSSXml, ErrorMessages> result = RSSUtils.getRSSXml(rssSource.getUrl());
                RSSXml rssXml = result.getLeft();
                if (RSSXml.isRSSXMLNotGet(rssXml)) {
                    throw new Exception();
                }
                ErrorMessages resultMsg = result.getRight();
                msg.mergeMsg(resultMsg);

                rssSource.setTitleParse(rssXml.getTitle());
                rssSource.setDescParse(rssXml.getDescription());
            }
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
            rssSourceTag = RSSSourceTag.builder().name(tagName).descUser(desc).build();
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
            topic = Topic.builder().name(topicName).descUser(desc).build();
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
        Page<Integer> idList = rssSourceRepository.findList(pageable);
        List<RSSSource> rssSources = pageToRssSourceList(idList);
        return rssSources;
    }

    public Object getRSSListByTitle(int pageNumber, int pageSize, List<String> titleUsers, List<String> titleParses) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        Page<Integer> idList = rssSourceRepository.getRSSListByTitle(pageable, titleUsers, titleParses);
        List<RSSSource> rssSources = pageToRssSourceList(idList);
        return rssSources;
    }

    private List<RSSSource> pageToRssSourceList(Page<Integer> rssSourceIdList) {
        List<Integer> idList = rssSourceIdList.getContent();
        // todo 同样要避免 tag 和 topic 对 contentitem 递归查询
        List<RSSSource> rssSources = idList.stream()
                .map(rssSourceRepository::findById)
                .map(Optional::get)
                .map(RSSSource::removeItemsFromEntity)
                .collect(Collectors.toList());
        return rssSources;
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

    private static final int DEFAULT_RSS_URL_POOL_CORE_SIZE = 32;
    private static final int DEFAULT_RSS_URL_POOL_MAX_SIZE = 64;
    private ExecutorService rssFetchPool = new ThreadPoolExecutor(
            DEFAULT_RSS_URL_POOL_CORE_SIZE, DEFAULT_RSS_URL_POOL_MAX_SIZE, 5L, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>());

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

        // todo:快速的拉取rss源的信息 目标：10s内600个源被获取(应该在这里计算一个统计数据)
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("rssFeedTestFetch");
        // save rssSource here
        List<Callable<ErrorMessages>> xmlCallableList = outlines.parallelStream()
                .map(outline -> (Callable<ErrorMessages>) () -> {
                    ErrorMessages rssMsg = new ErrorMessages();
                    try {
                        RSSSource rssSource = opmlOutlineToRssSource(outline);
                        rssSourceRepository.save(rssSource);
                    } catch (ErrorMsgException e) {
                        rssMsg.mergeMsg(e.getErrorMessages());
                    } catch (Exception e) {
                        rssMsg.addMsg("save rss error, rss:" + outline.get("xmlUrl") + ", exception:" + e.getMessage());
                    }
                    return rssMsg;
                    // todo: 不再写msg，直接把msg记录到一个地方，前端定时请求，返回给前端，每一次记一个 requestId UUID 然后每次请求这个UUID的
                    //  外部应该能够终止这个传输,前端是通过定时刷新来获取结果的
                    //  暂时不做这个处理，先把代码流程跑通
                }).collect(Collectors.toList());
        List<Future<ErrorMessages>> futureList = xmlCallableList.stream()
                .map(rssFetchPool::submit)
                .collect(Collectors.toList());
        for (Future<ErrorMessages> errorMessagesFuture : futureList) {
            try {
                msg.mergeMsg(errorMessagesFuture.get());
            } catch (Exception e) {
                msg.addMsg("addRssSourceFromOpml error: error to save rssSource to db");
            }
        }
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

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
    private RSSSource opmlOutlineToRssSource(Map<String, String> outline) throws ErrorMsgException {
        // xmlUrl和htmlUrl分别表示这个outline的RSS地址和网站地址,不是所有的RSS都有htmlUrl
        String rssUrl = outline.get("xmlUrl");
        ImmutablePair<RSSXml, ErrorMessages> result = RSSUtils.getRSSXml(rssUrl);
        RSSXml rssXml = result.getLeft();
        ErrorMessages errorMessages = result.getRight();
        if (RSSXml.isRSSXMLNotGet(rssXml)) {
            throw new ErrorMsgException(errorMessages);
        }

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
        rssSources.parallelStream().forEach(rssSource -> body
                .addElement("outline")
                .addAttribute("type", "rss")
                .addAttribute("text", rssSource.getTitleUser())
                .addAttribute("xmlUrl", rssSource.getUrl())
                .addAttribute("htmlUrl", rssSource.getLink())
        );

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
    public ErrorMessages addRssSourceFromJson(String json) throws JsonProcessingException {
        ErrorMessages msg = new ErrorMessages();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        // JsonNode head = jsonNode.getJSONObject("head");

        // 创建tag,解析tag和rss的映射
        JsonNode tags = jsonNode.get("tags");
        Map<Integer, RSSSourceTag> tagsMap = Maps.newHashMap();
        tags.fieldNames().forEachRemaining((tagId) -> {
            JsonNode tagNode = tags.get(tagId);
            String name = tagNode.get("name").asText();
            String descUser = tagNode.get("descUser").asText();
            try {
                RSSSourceTag rssSourceTag = fetchOrCreateTag(
                        JacksonUtils.ifNullThenStr(name),
                        JacksonUtils.ifNullThenStr(descUser)
                );
                tagsMap.put(Integer.parseInt(tagId), rssSourceTag);
            } catch (Exception e) {
                msg.addMsg("create tag failed, json tag id:" + tagId);
            }
        });
        JsonNode rssTags = jsonNode.get("rssSourceTags");
        Map<Integer, List<RSSSourceTag>> rssWithTagIdsMap = Maps.newHashMap();
        rssTags.fieldNames().forEachRemaining((rssId) -> {
            List<RSSSourceTag> currList = Lists.newArrayList();
            JsonNode tagNodeList = rssTags.get(rssId);
            for (final JsonNode tagIdNode : tagNodeList) {
                currList.add(tagsMap.get(tagIdNode.intValue()));
            }
            rssWithTagIdsMap.put(Integer.parseInt(rssId), currList);
        });

        // 创建topic,解析topic和rss的映射
        JsonNode topics = jsonNode.get("topics");
        Map<Integer, Topic> topicsMap = Maps.newHashMap();
        topics.fieldNames().forEachRemaining((topicId) -> {
            JsonNode topicNode = topics.get(topicId);
            String name = topicNode.get("name").asText();
            String descUser = topicNode.get("descUser").asText();
            try {
                Topic topic = fetchOrCreateTopic(
                        JacksonUtils.ifNullThenStr(name),
                        JacksonUtils.ifNullThenStr(descUser)
                );
                topicsMap.put(Integer.parseInt(topicId), topic);
            } catch (Exception e) {
                msg.addMsg("create topic failed, json topic id:" + topicId);
            }
        });
        JsonNode rssTopic = jsonNode.get("rssTopics");
        Map<Integer, List<Topic>> rssWithTopicIdsMap = Maps.newHashMap();
        rssTopic.fieldNames().forEachRemaining((rssId) -> {
            List<Topic> currList = Lists.newArrayList();
            JsonNode topicNodeList = rssTopic.get(rssId);
            for (final JsonNode topicIdNode : topicNodeList) {
                currList.add(topicsMap.get(topicIdNode.intValue()));
            }
            rssWithTopicIdsMap.put(Integer.parseInt(rssId), currList);
        });

        // 构建source并保存source
        JsonNode sources = jsonNode.get("sources");
        if (sources.isArray()){
            for (final JsonNode sourceNode : sources) {
                int id = sourceNode.get("id").intValue();
                RSSSource rssSource = JacksonUtils.objectNodeToRssSource(sourceNode);
                rssSource.setRssTopics(rssWithTopicIdsMap.get(id));
                rssSource.setRssSourceTags(rssWithTagIdsMap.get(id));
                try {
                    rssSourceRepository.save(rssSource);
                } catch (Exception e) {
                    msg.addMsg("create rss failed, in json rssid:" + id + ", titleUser:" + rssSource.getTitleUser());
                }
            }
        }

        return msg;
    }

    public ObjectNode exportRssSourceAsJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        // 所有rss列表
        Iterable<RSSSource> all = rssSourceRepository.findAll();
        ArrayList<RSSSource> rssSources = Lists.newArrayList(all);
        List<ObjectNode> rssSourcesObjectNode = rssSources.stream()
                .map(rssSource -> JacksonUtils.rssSourceToObjectNode(rssSource, mapper))
                .collect(Collectors.toList());
        ArrayNode rootSourceArrayNode = rootNode.putArray("sources");
        rootSourceArrayNode.addAll(rssSourcesObjectNode);

        // tag和topic以及tagId,topicId和rssId对应
        Map<String, ObjectNode> tagMap = Maps.newHashMap();
        Map<String, ObjectNode> topicMap = Maps.newHashMap();
        Map<String, List<Integer>> rssSourceTagsMap = Maps.newHashMapWithExpectedSize(rssSources.size());
        Map<String, List<Integer>> rssTopicsMap = Maps.newHashMapWithExpectedSize(rssSources.size());
        for (RSSSource rssSource : rssSources) {
            List<Integer> tagIds = Lists.newArrayListWithCapacity(rssSource.getRssSourceTags().size());
            rssSource.getRssSourceTags().forEach(rssSourceTag -> {
                tagMap.putIfAbsent(rssSourceTag.getId().toString(), JacksonUtils.tagToObjectNode(rssSourceTag, mapper));
                tagIds.add(rssSourceTag.getId());
            });
            if (!tagIds.isEmpty()) {
                rssSourceTagsMap.put(rssSource.getId().toString(), tagIds);
            }

            List<Integer> topicIds = Lists.newArrayListWithCapacity(rssSource.getRssTopics().size());
            rssSource.getRssTopics().forEach(topic -> {
                topicMap.putIfAbsent(topic.getId().toString(), JacksonUtils.topicToObjectNode(topic, mapper));
                topicIds.add(topic.getId());
            });
            if (!topicIds.isEmpty()) {
                rssTopicsMap.put(rssSource.getId().toString(), topicIds);
            }
        }

        rootNode.set("tags", JacksonUtils.mapToObjectNode(tagMap, mapper));
        rootNode.set("topics", JacksonUtils.mapToObjectNode(topicMap, mapper));

        Map<String, ArrayNode> rssSourceTagsNode = rssSourceTagsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> JacksonUtils.listToObjectNode(e.getValue(), mapper)
                ));
        rootNode.set("rssSourceTags", JacksonUtils.arrayMapToObjectNode(rssSourceTagsNode, mapper));
        Map<String, ArrayNode> rssTopicsNode = rssTopicsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> JacksonUtils.listToObjectNode(e.getValue(), mapper)
                ));
        rootNode.set("rssTopics", JacksonUtils.arrayMapToObjectNode(rssTopicsNode, mapper));

        return rootNode;
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
            RSSSource source = rssSourceRepository.findTopByUrl(rssSource.getUrl());
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
            try {
                fetchRSSHelp(rssSource);
            } catch (ErrorMsgException e) {
                msg.mergeMsg(e.getErrorMessages());
            }
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
        ImmutablePair<RSSXml, ErrorMessages> result = RSSUtils.getRSSXml(rssSource.getUrl());
        RSSXml rssXml = result.getLeft();
        ErrorMessages errorMessages = result.getRight();
        if (RSSXml.isRSSXMLNotGet(rssXml)) {
            throw new ErrorMsgException(errorMessages);
        }

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
