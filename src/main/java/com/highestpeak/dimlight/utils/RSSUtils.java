package com.highestpeak.dimlight.utils;

import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author highestpeak
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "AlibabaLowerCamelCaseVariableNaming"})
public class RSSUtils {

    public static final String GET_RSS_URL_ERROR = "url error:RSSUtils:getRSSXml";
    public static final String GET_RSS_FEED_ERROR = "can not parse or generate a feed:RSSUtils:getRSSXml";

    /**
     * todo
     *
     *
     * @param url rss url
     * @return 每个 rss 标签和它对应的值
     */
    @SuppressWarnings({"AlibabaRemoveCommentedCode", "CommentedOutCode"})
    public static RSSXml getRSSXml(String url) {
        // String url = "https://stackoverflow.com/feeds/tag?tagnames=rome";
        RSSXml rssXml=new RSSXml();

        ErrorMessages msg = new ErrorMessages();
        // tmpdoc: configure detail of the request.
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
                // todo: 反射 按理说可以写个适配器
                rssXml.setTitle(feed.getTitle());
                // author 是 item 的
//                System.out.println(feed.getAuthor());
//                System.out.println(feed.getCategories());
                // no idea
//                System.out.println(feed.getContributors());
//                System.out.println(feed.getCopyright());
//                System.out.println(feed.getDescription());
                // no idea
//                System.out.println(feed.getDocs());
//                System.out.println(feed.getEncoding());
                // todo: 有的 entry 的 description 包含 image 标签 这个需要在呈现端即android上呈现出来
                //  https://rsshub.app/bilibili/bangumi/media/9192 例如这个就有

                // todo: entry 的每个字段的打印
                List<SyndEntry> entries = feed.getEntries();

                // rss_2.0 之类的
//                System.out.println(feed.getFeedType());
                // no idea
//                System.out.println(feed.getForeignMarkup());
//                System.out.println(feed.getGenerator());
                // no idea
//                System.out.println(feed.getIcon());
                // 设置参数
//                System.out.println(feed.getImage());
//                System.out.println(feed.getLanguage());
//                System.out.println(feed.getLink());
                // 暂时无用
//                System.out.println(feed.getManagingEditor());
//                System.out.println(feed.getModule("copyright"));
                // 时区问题
//                System.out.println(feed.getPublishedDate());
                // 这个好像没什么东西，对我暂时无用
//                System.out.println(feed.getStyleSheet());
//                System.out.println(feed.getSupportedFeedTypes());

//                System.out.println(feed.getTitle());
                // 这个好像没什么东西，对我暂时无用
//                System.out.println(feed.getTitleEx());

                // uri 是 guid？ doc 还行
//                System.out.println(feed.getUri());
//                System.out.println(feed.getWebMaster());

                System.out.println("==========");
                // entries
                // syndLinks
                // modules
//                SyndEntry syndEntry = entries.get(0);
//                System.out.println(syndEntry.getAuthor());
//                // link
//                System.out.println(syndEntry.getLink());
//                System.out.println(syndEntry.getCategories());
//                // desc
//                System.out.println(syndEntry.getDescription());
//                // date
//                System.out.println(syndEntry.getPublishedDate());
//                // title
//                System.out.println(syndEntry.getTitle());
//                // uri
//                System.out.println(syndEntry.getUri());
            }
        } catch (IOException e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(GET_RSS_URL_ERROR,e));
        } catch (FeedException e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg(GET_RSS_FEED_ERROR,e));
        }
        // todo msg 没有返回
        return rssXml;
    }
}
