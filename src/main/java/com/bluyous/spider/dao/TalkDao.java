package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Talk;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-21
 */
public interface TalkDao {
    void saveOrUpdateBasicInfo(List<Talk> talks);
}
