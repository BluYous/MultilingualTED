package com.bluyous.spider.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-30
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api")
public class ApiController {
    private final ApiService apiService;
    
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }
    
    @GetMapping("/talksNum")
    public Integer getTalksNum() {
        return apiService.getTalksNum();
    }
    
    @GetMapping("/topics")
    public List<Map<String, Object>> getTopics() {
        return apiService.getTopics();
    }
    
    @PostMapping("/filterResults")
    public void getFilterResults(String filter) {
        JSONObject json = JSON.parseObject(filter);
        log.info(json.toString());
    }
    
    @GetMapping("/languages")
    public List<Map<String, Object>> getLanguages() {
        return apiService.getLanguages();
    }
    
    @GetMapping("/events")
    public List<Map<String, Object>> getEvents() {
        return apiService.getEvents();
    }
}
