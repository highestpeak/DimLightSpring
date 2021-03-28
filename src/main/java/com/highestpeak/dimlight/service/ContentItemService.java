package com.highestpeak.dimlight.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.highestpeak.dimlight.repository.RSSContentItemRepository;

/**
 * @author zhangjike <zhangjike@kuaishou.com>
 * Created on 2021-03-20
 */
@Service
public class ContentItemService {

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    @Autowired
    private RSSContentItemRepository contentItemRepository;

    public void delContentByIdList(List<Integer> delIdList) {
        delIdList.forEach(id -> contentItemRepository.deleteById(id));
    }


    public void delContentOutOfTime(String earliestTimeToLive) {
        try {
            contentItemRepository.deleteByCreateTimeBefore(format.parse(earliestTimeToLive));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Object getContentItemList(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id");
        return contentItemRepository.findList(pageable);
    }
}
