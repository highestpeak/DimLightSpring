package com.highestpeak.dimlight.repository;

import com.highestpeak.dimlight.model.entity.EsContent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-08
 * todo https://www.cnblogs.com/cjsblog/p/9756978.html
 */
@Repository
public interface ESContentRepository extends ElasticsearchRepository<EsContent,String> {
}
