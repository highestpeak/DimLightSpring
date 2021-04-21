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
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.MobiusTag;
import com.highestpeak.dimlight.model.entity.MobiusTopic;
import com.highestpeak.dimlight.model.params.RSSSourceParams;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProcessContext;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.repository.RSSSourceRepository;
import com.highestpeak.dimlight.repository.RSSSourceTagRepository;
import com.highestpeak.dimlight.repository.TopicRepository;
import com.highestpeak.dimlight.utils.JacksonUtils;
import org.dom4j.*;
import org.dom4j.tree.AbstractElement;
import org.dom4j.tree.DefaultElement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@Service
public class RSSSourceService {
    @Resource
    private RSSSourceRepository rssSourceRepository;
    @Resource
    private RSSSourceTagRepository rssSourceTagRepository;
    @Resource
    private TopicRepository topicRepository;
    @Resource
    private RssFetchService rssFetchService;

    //----------------crud----------------//

    public InfoMessages newOrUpdateRSSSource(RSSSourceParams rssSourceParams) {
        InfoMessages msg = new InfoMessages();

        // 查找现有id
        int id = -1;
        Date originCreatetime = null;
        String originLink = null;
        RSSSource rssSource = rssSourceRepository.findByTitleUser(rssSourceParams.getTitleUser());
        if (rssSource == null) {
            rssSource = rssSourceRepository.findTopByUrl(rssSourceParams.getUrl());
            if (rssSource != null) {
                id = rssSource.getId();
                originCreatetime = rssSource.getCreateTime();
                originLink = rssSource.getLink();
            }
        } else {
            id = rssSource.getId();
            originCreatetime = rssSource.getCreateTime();
            originLink = rssSource.getLink();
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
            rssSource.setLink(originLink);
        }

        // 更新tags
        List<String> tags = rssSourceParams.getTags();
        if (tags != null) {
            Set<String> nextTagNameSet = new HashSet<>(tags);
            List<MobiusTag> rssSourceMobiusTags = nextTagNameSet.stream()
                    .map(this::fetchOrCreateTag)
                    .collect(Collectors.toList());
            rssSource.setMobiusTags(rssSourceMobiusTags);
        }

        // 更新topics
        List<String> topics = rssSourceParams.getTopics();
        if (topics != null) {
            Set<String> nextTopicNameSet = new HashSet<>(topics);
            List<MobiusTopic> rssSourceMobiusTopics = nextTopicNameSet.stream()
                    .map(this::fetchOrCreateTopic)
                    .collect(Collectors.toList());
            rssSource.setRssMobiusTopics(rssSourceMobiusTopics);
        }

        try {
            // 查找rss的xml中的内容
            if (rssSource.getTitleParse() == null) {
                RSSXml rssXml = rssFetchService.getRSSXml(rssSource);
                if (RSSXml.isRssXMLNotGet(rssXml)) {
                    throw new Exception();
                }

                rssSource.setTitleParse(rssXml.getTitle());
                rssSource.setDescParse(rssXml.getDescription());
            }
            RSSSource savedSource = rssSourceRepository.save(rssSource);
        } catch (ErrorMsgException e) {
            msg.mergeMsg(e.getInfoMessages());
        } catch (Exception e) {
            msg.addErrorMsg(InfoMessages.buildExceptionMsg("保存 RSSSource 时发生错误", e));
        }
        return msg;
    }

    private MobiusTag fetchOrCreateTag(String tagName) {
        return fetchOrCreateTag(tagName, null);
    }

    private MobiusTag fetchOrCreateTag(String tagName, String desc) {
        MobiusTag mobiusTag = rssSourceTagRepository.findByName(tagName);
        if (mobiusTag == null) {
            mobiusTag = MobiusTag.builder().name(tagName).descUser(desc).build();
            mobiusTag = rssSourceTagRepository.save(mobiusTag);
        }
        return mobiusTag;
    }

    private MobiusTopic fetchOrCreateTopic(String topicName) {
        return fetchOrCreateTopic(topicName, null);
    }

    private MobiusTopic fetchOrCreateTopic(String topicName, String desc) {
        MobiusTopic mobiusTopic = topicRepository.findByName(topicName);
        if (mobiusTopic == null) {
            mobiusTopic = MobiusTopic.builder().name(topicName).descUser(desc).build();
            mobiusTopic = topicRepository.save(mobiusTopic);
        }
        return mobiusTopic;
    }

    public Object deleteRSSSource(int deleteRssParams) {
        InfoMessages msg = new InfoMessages();
        try {
            rssSourceRepository.deleteById(deleteRssParams);
        } catch (Exception e) {
            msg.addErrorMsg(InfoMessages.buildExceptionMsg("删除 RSSSource 时发生错误", e));
        }
        return msg;
    }

    public Page<RSSSource> getRSSList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        Page<RSSSource> rssSourcePage = rssSourceRepository.findAll(pageable);
        // todo： 通过json构造正确的返回体
        //rssSourcePage.getContent().forEach(RSSSource::removeItemsFromEntity);
        return rssSourcePage;
    }

    public Object getRSSListByTitle(int pageNumber, int pageSize, List<String> titleUsers, List<String> titleParses) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        // todo: 废弃，采用搜索来查找
        //Page<RSSSource> rssSourcePage = rssSourceRepository.getRSSListByTitle(pageable, titleUsers, titleParses);
        Page<RSSSource> rssSourcePage = null;
        // todo： 通过json构造正确的返回体
        //rssSourcePage.getContent().forEach(RSSSource::removeItemsFromEntity);
        return rssSourcePage;
    }

    public Object getContentItems(int pageNumber, int pageSize, List<String> titleUsers, List<String> titleParses,
                                  List<Integer> ids) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        // todo: 废弃，采用搜索来查找
        //return rssSourceRepository.getTargetRSSForContentItems(pageable, titleUsers, titleParses, ids);
        return null;
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
     * opml规范
     * https://webcache.googleusercontent.com/search?q=cache:03ayRFIKowoJ:https://www.cnblogs
     * .com/dandandan/archive/2006/04/16/376691.html+&cd=2&hl=zh-CN&ct=clnk&gl=hk
     * http://dev.opml.org/spec1.html
     * https://jiongks.name/blog/2011-01-09/
     * dom4j https://dom4j.github.io/
     */
    public InfoMessages addRssSourceFromOpml(Document document) {
        InfoMessages msg = new InfoMessages();
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

        // future: 快速的拉取rss源的信息 目标：10s内600个源被获取(应该在这里计算一个统计数据)
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("rssFeedTestFetch");
        // save rssSource here
        List<Callable<InfoMessages>> xmlCallableList = outlines.parallelStream()
                .map(outline -> (Callable<InfoMessages>) () -> {
                    InfoMessages rssMsg = new InfoMessages();
                    try {
                        RSSSource rssSource = opmlOutlineToRssSource(outline);
                        rssSourceRepository.save(rssSource);
                    } catch (ErrorMsgException e) {
                        rssMsg.mergeMsg(e.getInfoMessages());
                    } catch (Exception e) {
                        rssMsg.addErrorMsg("save rss error, rss:" + outline.get("xmlUrl") + ", exception:" + e.getMessage());
                    }
                    return rssMsg;
                    // future: 不再写msg，直接把msg记录到一个地方，前端定时请求，返回给前端，每一次记一个 requestId UUID 然后每次请求这个UUID的
                    //  外部应该能够终止这个传输,前端是通过定时刷新来获取结果的
                    //  暂时不做这个处理，先把代码流程跑通
                }).collect(Collectors.toList());
        List<Future<InfoMessages>> futureList = xmlCallableList.stream()
                .map(rssFetchPool::submit)
                .collect(Collectors.toList());
        for (Future<InfoMessages> errorMessagesFuture : futureList) {
            try {
                msg.mergeMsg(errorMessagesFuture.get());
            } catch (Exception e) {
                msg.addErrorMsg("addRssSourceFromOpml error: error to save rssSource to db");
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
        RSSSource rssSource = RSSSource.builder()
                .url(rssUrl)
                .titleUser(outline.get("text"))
                .link(outline.get("htmlUrl"))
                .fetchAble(true)
                .build();
        RSSXml rssXml = rssFetchService.getRSSXml(rssSource);
        if (RSSXml.isRssXMLNotGet(rssXml)) {
            throw new ErrorMsgException("没有找到对应的rssFeed");
        }

        rssSource.setTitleParse(rssXml.getTitle());
        rssSource.setDescParse(rssXml.getDescription());
        rssSource.setGenerator(rssXml.getTitle());

        return rssSource;
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
     * todo 如果rss已经存在，但是topic和json中的不同了，这个时候再通过json导入的话，topic会被创建，但是rss和topic的关系不会被创建
     */
    public InfoMessages addRssSourceFromJson(String json) throws JsonProcessingException {
        InfoMessages msg = new InfoMessages();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        // JsonNode head = jsonNode.getJSONObject("head");

        // 创建tag,解析tag和rss的映射
        JsonNode tags = jsonNode.get("tags");
        Map<Integer, MobiusTag> tagsMap = Maps.newHashMap();
        tags.fieldNames().forEachRemaining((tagId) -> {
            JsonNode tagNode = tags.get(tagId);
            String name = tagNode.get("name").asText();
            String descUser = tagNode.get("descUser").asText();
            try {
                MobiusTag mobiusTag = fetchOrCreateTag(
                        JacksonUtils.ifNullThenStr(name),
                        JacksonUtils.ifNullThenStr(descUser)
                );
                tagsMap.put(Integer.parseInt(tagId), mobiusTag);
            } catch (Exception e) {
                msg.addErrorMsg("create tag failed, json tag id:" + tagId);
            }
        });
        JsonNode rssTags = jsonNode.get("rssSourceTags");
        Map<Integer, List<MobiusTag>> rssWithTagIdsMap = Maps.newHashMap();
        rssTags.fieldNames().forEachRemaining((rssId) -> {
            List<MobiusTag> currList = Lists.newArrayList();
            JsonNode tagNodeList = rssTags.get(rssId);
            for (final JsonNode tagIdNode : tagNodeList) {
                currList.add(tagsMap.get(tagIdNode.intValue()));
            }
            rssWithTagIdsMap.put(Integer.parseInt(rssId), currList);
        });

        // 创建topic,解析topic和rss的映射
        JsonNode topics = jsonNode.get("topics");
        Map<Integer, MobiusTopic> topicsMap = Maps.newHashMap();
        topics.fieldNames().forEachRemaining((topicId) -> {
            JsonNode topicNode = topics.get(topicId);
            String name = topicNode.get("name").asText();
            String descUser = topicNode.get("descUser").asText();
            try {
                MobiusTopic mobiusTopic = fetchOrCreateTopic(
                        JacksonUtils.ifNullThenStr(name),
                        JacksonUtils.ifNullThenStr(descUser)
                );
                topicsMap.put(Integer.parseInt(topicId), mobiusTopic);
            } catch (Exception e) {
                msg.addErrorMsg("create topic failed, json topic id:" + topicId);
            }
        });
        JsonNode rssTopic = jsonNode.get("rssTopics");
        Map<Integer, List<MobiusTopic>> rssWithTopicIdsMap = Maps.newHashMap();
        rssTopic.fieldNames().forEachRemaining((rssId) -> {
            List<MobiusTopic> currList = Lists.newArrayList();
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
                rssSource.setRssMobiusTopics(rssWithTopicIdsMap.get(id));
                rssSource.setMobiusTags(rssWithTagIdsMap.get(id));
                try {
                    rssSourceRepository.save(rssSource);
                } catch (Exception e) {
                    msg.addErrorMsg("create rss failed, in json rssid:" + id + ", titleUser:" + rssSource.getTitleUser());
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
            List<Integer> tagIds = Lists.newArrayListWithCapacity(rssSource.getMobiusTags().size());
            rssSource.getMobiusTags().forEach(rssSourceTag -> {
                tagMap.putIfAbsent(rssSourceTag.getId().toString(), JacksonUtils.tagToObjectNode(rssSourceTag, mapper));
                tagIds.add(rssSourceTag.getId());
            });
            if (!tagIds.isEmpty()) {
                rssSourceTagsMap.put(rssSource.getId().toString(), tagIds);
            }

            List<Integer> topicIds = Lists.newArrayListWithCapacity(rssSource.getRssMobiusTopics().size());
            rssSource.getRssMobiusTopics().forEach(topic -> {
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

    /**
     * 通过id抓取rss
     */
    @Transactional
    public void fetchTargetRSS(ProcessContext processContext) {
        // 数据库查找rss
        RSSSource rssSource = rssSourceRepository.findById(processContext.getRssId()).orElse(null);
        processContext.setRssSource(rssSource);
        if (rssSource == null || !rssSource.isFetchAble()) {
            throw new ErrorMsgException(new InfoMessages("rss不存在，请检查id是否正确"));
        }

        // 拉取rss内容
        RSSXml rssXml = rssFetchService.getRSSXml(rssSource);
        if (RSSXml.isRssXMLNotGet(rssXml)) {
            throw new ErrorMsgException("没有找到对应的rssFeed");
        }
        List<RSSXml.RSSXmlItem> rssXmlItems = rssXml.getItems();
        if (rssXmlItems == null || rssXmlItems.size() <= 0) {
            throw new ErrorMsgException(new InfoMessages("没有找到rss内容"));
        }

        processContext.setOriginXml(rssXml);
    }

    public Object getRSSById(Integer id) {
        return rssSourceRepository.findById(id).orElse(null);
    }

    public Object searchByContent(String content) {
        return null;
    }
}
