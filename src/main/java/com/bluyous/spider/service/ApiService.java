package com.bluyous.spider.service;

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
}
