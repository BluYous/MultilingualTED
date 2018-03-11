package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Resource;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-02
 */
public interface ResourceDao {
    List<Resource> getResourceListForReq(List<Resource> resourcesWithUrl);
    
    void saveOrUpdate(List<Resource> resources);
}
