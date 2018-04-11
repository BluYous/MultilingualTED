package com.bluyous.spider.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-30
 */
public interface ApiService {
    Integer getTalksNum();
    
    List<Map<String, Object>> getTopics();
    
    List<Map<String,Object>> getLanguages();
    
    List<Map<String,Object>> getEvents();
    
    List<Map<String, Object>> getFilterResults(JSONObject json);
    
    Map<String, Object> getTalk(String talkId, String languageCode);
    
    List<Map<String, Object>> getSubtitleLanguages(String talkId);
    
    List<List<Map<String, Object>>> getSubtitles(String talkId, JSONArray json);
}
