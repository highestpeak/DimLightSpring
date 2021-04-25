package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.MobiusTag;
import com.highestpeak.dimlight.model.params.TagParams;
import com.highestpeak.dimlight.model.pojo.InfoMessages;
import com.highestpeak.dimlight.repository.RSSSourceTagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class TagService {
    @Resource
    private RSSSourceTagRepository sourceTagRepository;

    public Object deleteTag(int tagId) {
        InfoMessages msg = new InfoMessages();
        try {
            sourceTagRepository.deleteById(tagId);
        } catch (Exception e) {
            msg.addErrorMsg(InfoMessages.buildExceptionMsg("删除 Topic 时发生错误", e));
        }
        return msg;
    }

    public Object newOrUpdateTag(TagParams tagParams) {
        InfoMessages msg = new InfoMessages();
        MobiusTag sourceMobiusTag = sourceTagRepository.findByName(tagParams.getName());

        int id = -1;
        Date originCreatetime = null;
        if (sourceMobiusTag != null) {
            id = sourceMobiusTag.getId();
            originCreatetime = sourceMobiusTag.getCreateTime();
        }
        sourceMobiusTag = MobiusTag.builder()
                .name(tagParams.getName())
                .descUser(tagParams.getDesc())
                .build();
        if (id != -1) {
            sourceMobiusTag.setId(id);
            sourceMobiusTag.setCreateTime(originCreatetime);
        }

        try {
            MobiusTag savedTopic = sourceTagRepository.save(sourceMobiusTag);
        } catch (Exception e) {
            msg.addErrorMsg(InfoMessages.buildExceptionMsg("保存 Tag 时发生错误", e));
        }
        return msg;
    }

    public Object getTagListByName(int pageNumber, int pageSize, List<String> names) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return sourceTagRepository.findByNameIn(names, pageable);
    }

    public Object getRssSourceByTagName(int pageNumber, int pageSize, List<String> topicNames) {
        return null;
    }

    public Object getContentItemsByTagName(int pageNumber, int pageSize, List<String> topicNames) {
        return null;
    }

    public Object getTagList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        Page<MobiusTag> sourceTagPage = sourceTagRepository.findAll(pageable);
        // todo： 通过json构造正确的返回体
        //sourceTagPage.getContent().forEach(MobiusTag::removeItemsFromEntity);
        return sourceTagPage;
    }

    public Object getTagById(int parseInt) {
        return sourceTagRepository.findById(parseInt).orElse(null);
    }

    public Object searchByContent(String content) {
        return null;
    }
}
