package com.highestpeak.dimlight.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan
@Configuration(value = "proxy")
@Getter
public class GlobalProxyConfig {
    @Value("${hostname:127.0.0.1}")
    private String hostname;

    @Value("${port:1080}")
    private int port;

    @Value("${scheme:http}")
    private String scheme;
}
