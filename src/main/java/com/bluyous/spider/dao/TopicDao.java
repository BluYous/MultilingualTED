package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Topic;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-20
 */
public interface TopicDao {
    void saveOrUpdate(List<Topic> topics);
}
