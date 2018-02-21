package com.bluyous.spider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.bean.Event;
import com.bluyous.spider.bean.Language;
import com.bluyous.spider.bean.Topic;
import com.bluyous.spider.dao.EventDao;
import com.bluyous.spider.dao.LanguageDao;
import com.bluyous.spider.dao.TopicDao;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用来同步 TED 网站
 *
 * @author BluYous
 * @version 1.0
 * @since 2018-02-19
 */
@Service
public class TedSpiderService {
    private static final int TIMEOUT = 10000;
    private final EventDao eventDao;
    private final LanguageDao languageDao;
    private final TopicDao topicDao;
    
    @Autowired
    public TedSpiderService(EventDao eventDao, LanguageDao languageDao, TopicDao topicDao) {
        this.eventDao = eventDao;
        this.languageDao = languageDao;
        this.topicDao = topicDao;
    }
    
    @Transactional
    public void runSpider() {
        /* 代理地址 */
        final String PROXY_IP = "223.241.116.140";
        final String PROXY_PORT = "8010";
        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("http.proxyHost", PROXY_IP);
        System.getProperties().setProperty("http.proxyPort", PROXY_PORT);
        
        synEvents();
        synLanguages();
        synTopics();
    }
    
    
    private void synEvents() {
        final String reqUrl = "https://www.ted.com/talks/events";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://www.ted.com/talks");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        Connection.Response res = null;
        try {
            res = Jsoup.connect(reqUrl).headers(headers).timeout(TIMEOUT).ignoreContentType(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res == null) {
            return;
        }
        String json = res.body();
        JSONArray jsonArray = JSON.parseArray(json);
        List<Event> events = new ArrayList<>();
        
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                Event event = new Event();
                JSONObject temp = jsonArray.getJSONObject(i);
                event.setId(temp.getString("value"));
                event.setLabel(temp.getString("label"));
                event.setYear(temp.getInteger("year"));
                events.add(event);
            }
        }
        eventDao.saveOrUpdate(events);
    }
    
    private void synLanguages() {
        final String reqUrl = "https://www.ted.com/languages/combo.json?per_page=1000";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://www.ted.com/talks");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        Connection.Response res = null;
        try {
            res = Jsoup.connect(reqUrl).headers(headers).timeout(TIMEOUT).ignoreContentType(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res == null) {
            return;
        }
        String json = res.body();
        JSONArray jsonArray = JSON.parseArray(json);
        List<Language> languages = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                Language language = new Language();
                JSONObject temp = jsonArray.getJSONObject(i);
                language.setId(temp.getString("value"));
                language.setLabel(temp.getString("label"));
                languages.add(language);
            }
        }
        languageDao.saveOrUpdate(languages);
    }
    
    private void synTopics() {
        final String reqUrl = "https://www.ted.com/topics/combo?models=Talks";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://www.ted.com/talks");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        
        Connection.Response res = null;
        try {
            res = Jsoup.connect(reqUrl).headers(headers).timeout(TIMEOUT).ignoreContentType(true).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res == null) {
            return;
        }
        String json = res.body();
        JSONArray jsonArray = JSON.parseArray(json);
        List<Topic> topics = new ArrayList<>();
        
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                Topic topic = new Topic();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                topic.setId(jsonObject.getString("value"));
                topic.setLabel(jsonObject.getString("label"));
                topics.add(topic);
            }
        }
        topicDao.saveOrUpdate(topics);
    }
}
