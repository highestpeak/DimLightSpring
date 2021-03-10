package com.highestpeak.dimlight.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 这些可以用 elasticSearch 来做 一个单机节点就够了
 */
@RestController
@CrossOrigin
@RequestMapping("/api/process")
public class ProcessController {
    /**
     * 去重
     */
    @PutMapping("remove_duplicates")
    public Object removeDuplicates(){
        return null;
    }

    /**
     * 信息组合
     */
    @PutMapping("info_combination")
    public Object infoCombination(){
        return null;
    }

    /**
     * 打上分组标签
     */
    @PutMapping("mark_group_tag")
    public Object markGroupTag(){
        return null;
    }

    /**
     * 打上标签
     */
    @PutMapping("sort")
    public Object sortCurrentContent(){
        return null;
    }
}
