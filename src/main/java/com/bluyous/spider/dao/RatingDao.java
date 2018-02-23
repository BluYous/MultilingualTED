package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Rating;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-22
 */
public interface RatingDao {
    void saveOrUpdate(List<Rating> ratings);
}
