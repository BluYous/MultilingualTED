package com.bluyous.spider.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-30
 */
@CrossOrigin
@RestController
public class ApiController {
    @GetMapping("/api")
    public Map<String, String> test() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "kobe");
        map.put("age", "18");
        map.put("address", "四川");
        return map;
    }
}
