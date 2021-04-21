package com.highestpeak.dimlight.model.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProxyJsonPojo {

    private String hostname;

    private int port;

    private String scheme;

}
