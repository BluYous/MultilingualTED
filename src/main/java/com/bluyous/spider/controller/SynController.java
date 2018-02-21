package com.bluyous.spider.controller;

import com.bluyous.spider.service.TedSpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-20
 */
@RestController
public class SynController {
    private final TedSpiderService tedSpiderService;
    
    @Autowired
    public SynController(TedSpiderService tedSpiderService) {
        this.tedSpiderService = tedSpiderService;
    }
    
    @GetMapping("/syn")
    public void syn() {
        tedSpiderService.runSpider();
    }
}
