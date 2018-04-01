package com.bluyous.spider.service;

import com.bluyous.spider.dao.LanguageDao;
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
    private final LanguageDao languageDao;
    
    public ApiServiceImpl(TalkDao talkDao, com.bluyous.spider.dao.TopicDao topicDao, LanguageDao languageDao) {
        this.talkDao = talkDao;
        TopicDao = topicDao;
        this.languageDao = languageDao;
    }
    
    @Override
    public Integer getTalksNum() {
        return talkDao.getTalkNum();
    }
    
    @Override
    public List<Map<String, Object>> getTopics() {
        return TopicDao.getTopics();
    }
    
    @Override
    public List<Map<String, Object>> getLanguages() {
        return languageDao.getLanguages();
    }
    
    @Override
    public List<Map<String, Object>> getEvents() {
        return talkDao.getEvents();
    }
}
