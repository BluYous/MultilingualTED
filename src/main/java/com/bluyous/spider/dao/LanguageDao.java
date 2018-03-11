package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Language;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-20
 */
public interface LanguageDao {
    void saveOrUpdateBasicInfo(List<Language> languages);
    
    void saveOrUpdate(List<Language> languages);
}
