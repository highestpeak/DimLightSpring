package com.highestpeak.dimlight.model;

import java.util.List;

/**
 * @author highestpeak
 * 实现这个接口的类可以通过这个接口来提供他自己的 tag
 */
public interface TagExtractor {
    /**
     * 返回本实现类能提供的 tag
     * @return tag list
     */
    List<Object> extractTag();
}
