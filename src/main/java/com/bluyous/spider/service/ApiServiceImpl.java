package com.bluyous.spider.service;

import com.bluyous.spider.dao.TalkDao;
import com.bluyous.spider.dao.TopicDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-30
 */
@Service
public class ApiServiceImpl implements ApiService {
    private final TalkDao talkDao;
    private final TopicDao TopicDao;
    
    public ApiServiceImpl(TalkDao talkDao, com.bluyous.spider.dao.TopicDao topicDao) {
        this.talkDao = talkDao;
        TopicDao = topicDao;
    }
    
    @Override
    public Integer getTalksNum() {
        return talkDao.getTalkNum();
    }
    
    @Override
    public List<Map<String, Object>> getTopTopics() {
        return TopicDao.getTopTopics();
    }
}
