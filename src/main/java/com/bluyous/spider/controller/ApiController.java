package com.bluyous.spider.controller;

import com.bluyous.spider.service.ApiService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-30
 */
@CrossOrigin
@RestController
public class ApiController {
    private final ApiService apiService;
    
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }
    
    @GetMapping("/api/talksNum")
    public Integer getTalksNum() {
        return apiService.getTalksNum();
    }
    @GetMapping("/api/topTopics")
    public List<Map<String, Object>> getTopTopics() {
        return apiService.getTopTopics();
    }
}
