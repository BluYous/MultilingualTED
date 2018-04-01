package com.bluyous.spider.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用来同步 TED 网站
 *
 * @author BluYous
 * @version 1.0
 * @since 2018-02-19
 */
@Service
@Slf4j
public class TedSpiderServiceImpl implements TedSpiderService {
    private final TedSpiderTransactionalHelpService transactionalHelpService;
    
    @Autowired
    public TedSpiderServiceImpl(TedSpiderTransactionalHelpService transactionalHelpService) {
        this.transactionalHelpService = transactionalHelpService;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void runSpider() {
        log.info("开始同步 Events");
        transactionalHelpService.synEvents();
        log.info("开始同步 Languages");
        transactionalHelpService.synLanguages();
        log.info("开始同步 Topics");
        transactionalHelpService.synTopics();
        
        log.info("开始同步 Talks");
        transactionalHelpService.synTalks();
        log.info("同步完成");
    }
}