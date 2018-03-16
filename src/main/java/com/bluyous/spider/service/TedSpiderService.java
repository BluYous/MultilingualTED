package com.bluyous.spider.service;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-01
 */
public interface TedSpiderService {
    int CONNECTION_TIME_OUT_MILLIS = 1000 * 60;
    int NEXT_REQ_MILLIS = 600;
    int MAX_ERROR_TIMES = 10;
    
    void runSpider();
}
