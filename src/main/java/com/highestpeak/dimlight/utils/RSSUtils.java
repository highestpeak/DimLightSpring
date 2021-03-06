package com.highestpeak.dimlight.utils;

import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "AlibabaLowerCamelCaseVariableNaming"})
public class RSSUtils {

    public static final String GET_RSS_URL_ERROR = "url error:RSSUtils:getRSSXml";
    public static final String GET_RSS_FEED_ERROR = "can not parse or generate a feed:RSSUtils:getRSSXml";

    /**
     * @param url rss url
     * @return 每个 rss 标签和它对应的值
     */
    @SuppressWarnings({"AlibabaRemoveCommentedCode"})
    public static RSSXml getRSSXml(String url) {
        // String url = "https://stackoverflow.com/feeds/tag?tagnames=rome";
        RSSXml rssXml = new RSSXml();

        ErrorMessages msg = new ErrorMessages();
        // future: configure detail of the request.
        //  如果设置了代理，需要在 client 这里设置访问代理
        //  https://stackoverflow.com/questions/4955644/apache-httpclient-4-1-proxy-settings
        //  https://github.com/rometools/rome/issues/276
        HttpHost proxy = new HttpHost("127.0.0.1", 7890, "http");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        try (
                CloseableHttpClient client = HttpClients.custom()
                        .setRoutePlanner(routePlanner)
                        .build()
        ) {
            HttpUriRequest request = new HttpGet(url);
            try (
                    CloseableHttpResponse response = client.execute(request);
                    InputStream stream = response.getEntity().getContent()
            ) {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(stream));
                RSSXml rssXml1 = syndFeedToRSSXml(feed);
                return syndFeedToRSSXml(feed);
            }
        } catch (IOException e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(GET_RSS_URL_ERROR, e));
        } catch (FeedException e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(GET_RSS_FEED_ERROR, e));
        }
        // todo msg 没有返回
        return rssXml;
    }

    /**
     * @param syndFeed rome 格式
     * @return 内部格式
     */
    public static RSSXml syndFeedToRSSXml(SyndFeed syndFeed) {
        RSSXml.RSSXmlBuilder rssXmlBuilder = RSSXml.builder();

        rssXmlBuilder.title(syndFeed.getTitle())
                .category(
                        syndFeed.getCategories().stream()
                                .map(SyndCategory::getName)
                                .collect(Collectors.toList())
                ).copyright(syndFeed.getCopyright())
                .description(syndFeed.getDescription())
                .generator(syndFeed.getEncoding());

        SyndImage syndFeedImage = syndFeed.getImage();
        RSSXml.RSSXmlImage rssXmlImage = RSSXml.RSSXmlImage.builder()
                .title(syndFeedImage.getTitle())
                .link(syndFeedImage.getLink())
                .url(syndFeedImage.getUrl())
                .build();
        rssXmlBuilder.image(rssXmlImage)
                .language(syndFeed.getLanguage())
                .link(syndFeed.getLink())
                .pubDate(syndFeed.getPublishedDate());

        List<SyndEntry> entryList = syndFeed.getEntries();
        List<RSSXml.RSSXmlItem> rssXmlItems = new ArrayList<>(entryList.size());
        for (SyndEntry syndEntry :
                entryList) {
            RSSXml.RSSXmlItem rssXmlItem = RSSXml.RSSXmlItem.builder()
                    .author(syndEntry.getAuthor())
                    .link(syndEntry.getLink())
                    .category(
                            syndEntry.getCategories().stream()
                                    .map(SyndCategory::getName)
                                    .collect(Collectors.toList())
                    )
                    .description(syndEntry.getDescription().toString())
                    .pubDate(syndEntry.getPublishedDate())
                    .title(syndEntry.getTitle())
                    .guid(syndEntry.getUri())
                    .build();
            rssXmlItems.add(rssXmlItem);
        }
        rssXmlBuilder.items(rssXmlItems);

        return rssXmlBuilder.build();
    }
}
