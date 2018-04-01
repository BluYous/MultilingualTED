package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Talk;
import com.bluyous.spider.bean.TalkMultiLang;

import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-21
 */
public interface TalkDao {
    void saveOrUpdate(Talk talk);
    
    void saveOrUpdateTalkSpeakerRef(List<Map<String, Object>> mapList);
    
    void saveOrUpdateTalkTopicRef(List<Map<String, Object>> mapList);
    
    void saveOrUpdateTalkMultiLang(List<TalkMultiLang> talkMultiLangs);
    
    Integer getTalkNum();
    
    List<Map<String,Object>> getEvents();
}
