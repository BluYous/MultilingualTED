package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Language;

import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-20
 */
public interface LanguageDao {
    void saveOrUpdateBasicInfo(List<Language> languages);
    
    void saveOrUpdate(List<Language> languages);
    
    List<Map<String,Object>> getLanguages();
    
    List<Map<String, Object>> getSubtitleLanguages(List<String> languageCodeList);
}
