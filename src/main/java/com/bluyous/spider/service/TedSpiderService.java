package com.bluyous.spider.service;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-01
 */
public interface TedSpiderService {
    int SLEEP_TIME = 600;
    int MAX_ERROR_TIMES = 10;
    
    void runSpider();
}
