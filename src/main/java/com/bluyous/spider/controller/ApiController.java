package com.bluyous.spider.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    @Value("${resources.defaultPath}")
    private String defaultPath;
    
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
    public List<Map<String, Object>> getFilterResults(String filter) {
        JSONObject json = JSON.parseObject(filter);
        return apiService.getFilterResults(json);
    }
    
    @GetMapping("/languages")
    public List<Map<String, Object>> getLanguages() {
        return apiService.getLanguages();
    }
    
    @GetMapping("/events")
    public List<Map<String, Object>> getEvents() {
        return apiService.getEvents();
    }
    
    @GetMapping("/resource")
    public void getTalkSummary(String fileName, HttpServletResponse response, String quality) {
        File file = new File(defaultPath + File.separator + fileName);
        
        response.setContentType("image/jpeg");
        byte[] buffer = new byte[1024 * 1024];
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
            OutputStream os = response.getOutputStream();
            if (quality == null) {
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } else if ("low".equals(quality)) {
                Thumbnails.of(file).size(288, 162).outputFormat("jpg").toOutputStream(os);
            }
        } catch (IOException e) {
            log.error("Error stack trace: ", e);
        }
    }
    
    @GetMapping("/talk/{talkId}")
    public Map<String, Object> getTalk(@PathVariable("talkId") String talkId, String languageCode) {
        return apiService.getTalk(talkId, languageCode);
    }
    
    @GetMapping("/subtitleLanguages/{talkId}")
    public List<Map<String, Object>> getSubtitleLanguages(@PathVariable("talkId") String talkId) {
        return apiService.getSubtitleLanguages(talkId);
    }
    
    @PostMapping("/subtitles/{talkId}")
    public List<List<Map<String, Object>>> getSubtitles(@PathVariable("talkId") String talkId, String languages) {
        JSONArray json = JSON.parseArray(languages);
        return apiService.getSubtitles(talkId, json);
    }
    
    @PostMapping("/download/subtitles/{talkId}")
    public void downloadSubtitles(@PathVariable("talkId") String talkId, String languages,
                                  HttpServletResponse response) {
        JSONArray json = JSON.parseArray(languages);
        
        response.setContentType("text/vtt; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;fileName=" + talkId + ".vtt");
        try {
            OutputStream os = response.getOutputStream();
            for (int i = 0; i < json.size(); i++) {
                String languageCode = json.getString(i);
                String fileName = defaultPath + File.separator + "talks/" + talkId + "/subtitles/" + languageCode + "/full.vtt";
                File file = new File(fileName);
                
                try (FileInputStream in = new FileInputStream(file); InputStreamReader inReader = new InputStreamReader(
                        in); BufferedReader bufReader = new BufferedReader(inReader)) {
                    String line = null;
                    int startLine = 2;
                    int curLine = 0;
                    while ((line = bufReader.readLine()) != null) {
                        if (i == 1 && ++curLine <= startLine) {
                            continue;
                        }
                        String t = line + "\n";
                        os.write(t.getBytes());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error stack trace: ", e);
        }
    }
}
