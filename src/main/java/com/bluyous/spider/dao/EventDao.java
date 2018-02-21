package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Event;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-20
 */
public interface EventDao {
    void saveOrUpdate(List<Event> events);
}
