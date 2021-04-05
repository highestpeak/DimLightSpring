package com.highestpeak.dimlight.service;

import com.highestpeak.dimlight.model.entity.RSSSourceTag;
import com.highestpeak.dimlight.model.params.DeleteTagParams;
import com.highestpeak.dimlight.model.params.TagParams;
import com.highestpeak.dimlight.model.pojo.ErrorMessages;
import com.highestpeak.dimlight.repository.RSSSourceTagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Resource
    private RSSSourceTagRepository sourceTagRepository;

    public Object deleteTag(DeleteTagParams tagParams) {
        ErrorMessages msg = new ErrorMessages();
        try {
            if (tagParams.getId() != null) {
                sourceTagRepository.deleteById(tagParams.getId());
            } else {
                sourceTagRepository.deleteByName(tagParams.getName());
            }
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("删除 Topic 时发生错误", e));
        }
        return msg;
    }

    public Object newOrUpdateTag(TagParams tagParams) {
        ErrorMessages msg = new ErrorMessages();
        RSSSourceTag sourceTag = sourceTagRepository.findByName(tagParams.getName());

        int id = -1;
        Date originCreatetime = null;
        if (sourceTag != null) {
            id = sourceTag.getId();
            originCreatetime = sourceTag.getCreateTime();
        }
        sourceTag = RSSSourceTag.builder()
                .name(tagParams.getName())
                .descUser(tagParams.getDesc())
                .build();
        if (id != -1) {
            sourceTag.setId(id);
            sourceTag.setCreateTime(originCreatetime);
        }

        try {
            RSSSourceTag savedTopic = sourceTagRepository.save(sourceTag);
        } catch (Exception e) {
            msg.addMsg(ErrorMessages.buildExceptionMsg("保存 Tag 时发生错误", e));
        }
        return msg;
    }

    public Object getTagListByName(int pageNumber, int pageSize, List<String> names) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return sourceTagRepository.findByNames(pageable, names);
    }

    public Object getRssSourceByTagName(int pageNumber, int pageSize, List<String> topicNames) {
        List<RSSSourceTag> rssByTopicNames = sourceTagRepository.findRssByTagNames(topicNames);
        return rssByTopicNames.stream().flatMap(topic -> topic.getRssSources().stream()).collect(Collectors.toList());
    }

    public Object getContentItemsByTagName(int pageNum, int pageSize, List<String> topicNames) {
        List<RSSSourceTag> itemsByTopic = sourceTagRepository.findItemsByTagNames(topicNames);
        return itemsByTopic.stream().flatMap(topic -> topic.getRssContentItems().stream()).collect(Collectors.toList());
    }

    public Object getTagList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        Page<Integer> idList = sourceTagRepository.findList(pageable);
        List<RSSSourceTag> rssSources = pageToTagList(idList);
        return rssSources;
    }

    private List<RSSSourceTag> pageToTagList(Page<Integer> tagIdList) {
        List<Integer> idList = tagIdList.getContent();
        List<RSSSourceTag> rssSources = idList.stream()
                .map(sourceTagRepository::findById)
                .map(Optional::get)
                .map(RSSSourceTag::removeItemsFromEntity)
                .collect(Collectors.toList());
        return rssSources;
    }
}
