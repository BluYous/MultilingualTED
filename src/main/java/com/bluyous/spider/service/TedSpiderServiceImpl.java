package com.bluyous.spider.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TedSpiderServiceImpl implements TedSpiderService {
    private static final Logger logger = LoggerFactory.getLogger(TedSpiderServiceImpl.class);
    private final TedSpiderTransactionalHelpService transactionalHelpService;
    
    @Autowired
    public TedSpiderServiceImpl(TedSpiderTransactionalHelpService transactionalHelpService) {
        this.transactionalHelpService = transactionalHelpService;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void runSpider() {
        logger.info("开始同步 Events");
        transactionalHelpService.synEvents();
        logger.info("开始同步 Languages");
        transactionalHelpService.synLanguages();
        logger.info("开始同步 Topics");
        transactionalHelpService.synTopics();
        
        logger.info("开始同步 Talks");
        transactionalHelpService.synTalks();
        logger.info("同步完成");
    }
}