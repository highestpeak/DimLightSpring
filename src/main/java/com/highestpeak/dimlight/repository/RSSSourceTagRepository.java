package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import org.springframework.data.repository.CrudRepository;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-10
 */
public interface RSSSourceTagRepository extends CrudRepository<RSSSourceTag,Integer> {
    RSSSourceTag findByName(String name);
}
