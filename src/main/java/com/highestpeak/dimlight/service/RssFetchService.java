package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.config.GlobalProxyConfig;
import com.highestpeak.dimlight.exception.ErrorMsgException;
import com.highestpeak.dimlight.model.entity.MobiusTag;
import com.highestpeak.dimlight.model.entity.RSSSource;
import com.highestpeak.dimlight.model.entity.support.RssJsonExtraFieldsHelp;
import com.highestpeak.dimlight.model.enums.SpecialTagEnum;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.model.pojo.ProxyJsonPojo;
import com.highestpeak.dimlight.model.pojo.RSSXml;
import com.highestpeak.dimlight.utils.RssUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author highestpeak
 */
@Service
public class RssFetchService {

    @Resource
    private GlobalProxyConfig proxyConfig;

    /**
     * 10s超时
     */
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(10 * 1000).build();

    private static DefaultProxyRoutePlanner globalProxyRouterPlaner;

    @PostConstruct
    private void init() {
        HttpHost proxy = new HttpHost(proxyConfig.getHostname(), proxyConfig.getPort(), proxyConfig.getScheme());
        globalProxyRouterPlaner = new DefaultProxyRoutePlanner(proxy);
    }

    /**
     * @return 每个 rss 标签和它对应的值
     */
    public RSSXml getRSSXml(RSSSource rssSource) {
        String url = rssSource.getUrl();

        // future: 可以根据tag判断是否是rsshub等，决定是否在失败时采用循环拉取
        // future: 可以采用if not modified 进行拉取
        try (CloseableHttpClient client = getHttpClient(rssSource)) {
            HttpUriRequest request = new HttpGet(url);
            try (
                    CloseableHttpResponse response = client.execute(request);
                    InputStream stream = response.getEntity().getContent()
            ) {
                SyndFeedInput input = new SyndFeedInput();
                // future: 会关闭链接，所以需要多尝试几次 抄一下线程池
                SyndFeed feed = input.build(new XmlReader(stream));
                RSSXml rssXml = RssUtils.syndFeedToRSSXml(feed);
                return rssXml;
            }
        } catch (IOException e) {
            throw new ErrorMsgException(InfoMessages.buildExceptionMsg("拉取Feed时网络请求错误", e));
        } catch (FeedException e) {
            throw new ErrorMsgException(InfoMessages.buildExceptionMsg("生成和解析Feed时发生错误", e));
        }

    }

    /**
     * 根据不同的rssSource设置不同的代理
     * https://stackoverflow.com/questions/4955644/apache-httpclient-4-1-proxy-settings
     * https://github.com/rometools/rome/issues/276
     */
    private CloseableHttpClient getHttpClient(RSSSource rssSource) {
        for (MobiusTag mobiusTag : rssSource.getMobiusTags()) {
            // 根据tag判断是否需要proxy
            if (mobiusTag.getName().equals(SpecialTagEnum.GLOBAL_PROXY.getName())) {
                return HttpClients.custom()
                        .setRoutePlanner(globalProxyRouterPlaner)
                        .setDefaultRequestConfig(REQUEST_CONFIG)
                        .build();
            } else if (mobiusTag.getName().equals(SpecialTagEnum.TARGET_PROXY.getName())) {
                ProxyJsonPojo proxyJsonPojo = RssJsonExtraFieldsHelp.proxyJsonPojo(rssSource);
                HttpHost proxy = new HttpHost(
                        proxyJsonPojo.getHostname(), proxyJsonPojo.getPort(), proxyJsonPojo.getScheme()
                );
                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                return HttpClients.custom()
                        .setRoutePlanner(routePlanner)
                        .setDefaultRequestConfig(REQUEST_CONFIG)
                        .build();
            }
        }

        return HttpClients.custom()
                .setRoutePlanner(null)
                .setDefaultRequestConfig(REQUEST_CONFIG)
                .build();
    }

}
