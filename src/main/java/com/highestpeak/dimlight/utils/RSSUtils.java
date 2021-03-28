package com.highestpeak.dimlight.utils;

import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author highestpeak
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "AlibabaLowerCamelCaseVariableNaming"})
public class RSSUtils {

    public static final String GET_RSS_URL_ERROR = "url error:RSSUtils:getRSSXml";
    public static final String GET_RSS_FEED_ERROR = "can not parse or generate a feed:RSSUtils:getRSSXml";

    /**
     * 10s超时
     */
    private static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10 * 1000).build();
    /**
     * @param url rss url
     * @return 每个 rss 标签和它对应的值
     */
    @SuppressWarnings({"AlibabaRemoveCommentedCode"})
    public static ImmutablePair<RSSXml, ErrorMessages> getRSSXml(String url) {
        // String url = "https://stackoverflow.com/feeds/tag?tagnames=rome";
        RSSXml rssXml = RSSXml.DEFAULT_RSS_XML;

        ErrorMessages msg = new ErrorMessages();
        // future: configure detail of the request.
        //  如果设置了代理，需要在 client 这里设置访问代理
        //  https://stackoverflow.com/questions/4955644/apache-httpclient-4-1-proxy-settings
        //  https://github.com/rometools/rome/issues/276
        //  根据不同的rssSource设置不同的代理
        HttpHost proxy = new HttpHost("127.0.0.1", 7890, "http");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        try (
                CloseableHttpClient client = HttpClients.custom()
                        .setRoutePlanner(null)
                        .setDefaultRequestConfig(requestConfig)
                        .build()
        ) {
            HttpUriRequest request = new HttpGet(url);
            try (
                    CloseableHttpResponse response = client.execute(request);
                    InputStream stream = response.getEntity().getContent()
            ) {
                SyndFeedInput input = new SyndFeedInput();
                // todo: 会关闭链接，所以需要多尝试几次 抄一下线程池
                SyndFeed feed = input.build(new XmlReader(stream));
                rssXml = syndFeedToRSSXml(feed);
            }
        } catch (IOException e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(GET_RSS_URL_ERROR, e));
        } catch (FeedException e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(GET_RSS_FEED_ERROR, e));
        }

        return new ImmutablePair<>(rssXml, msg);
    }

    /**
     * @param syndFeed rome 格式
     * @return 内部格式
     */
    public static RSSXml syndFeedToRSSXml(SyndFeed syndFeed) {
        // 针对多种不同的rss进行多种测试，多测试一些rssurl，把这里做的健壮一点
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
        if (syndFeedImage!=null){
            RSSXml.RSSXmlImage rssXmlImage = RSSXml.RSSXmlImage.builder()
                    .title(syndFeedImage.getTitle())
                    .link(syndFeedImage.getLink())
                    .url(syndFeedImage.getUrl())
                    .build();
            rssXmlBuilder.image(rssXmlImage)
                    .language(syndFeed.getLanguage())
                    .link(syndFeed.getLink())
                    .pubDate(syndFeed.getPublishedDate());
        }

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
