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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bluyous.spider.service.TedSpiderService.*;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-01
 */
@Service
public class TedSpiderTransactionalHelpService {
    private static final Logger logger = LoggerFactory.getLogger(TedSpiderTransactionalHelpService.class);
    private final EventDao eventDao;
    private final LanguageDao languageDao;
    private final TopicDao topicDao;
    private final TedSpiderTransactionalHelp4TalkService transactionalHelp4TalkService;
    
    @Autowired
    public TedSpiderTransactionalHelpService(EventDao eventDao, LanguageDao languageDao, TopicDao topicDao, TedSpiderTransactionalHelp4TalkService transactionalHelp4TalkService) {
        this.eventDao = eventDao;
        this.languageDao = languageDao;
        this.topicDao = topicDao;
        this.transactionalHelp4TalkService = transactionalHelp4TalkService;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synEvents() {
        final String reqUrl = "https://www.ted.com/talks/events";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://www.ted.com/talks");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        Connection.Response res;
        
        int maxErrorTimes = MAX_ERROR_TIMES;
        while (true) {
            try {
                res = Jsoup.connect(reqUrl).headers(headers).timeout(CONNECTION_TIME_OUT_SECONDS * 1000).maxBodySize(0).ignoreContentType(true).ignoreHttpErrors(true).execute();
                if (maxErrorTimes <= 0) {
                    logger.error("Error URL: {}, check that the URL is correct", reqUrl);
                    break;
                }
                if (res != null && (res.statusCode() == 404 || res.statusCode() == 504)) {
                    maxErrorTimes--;
                }
                if (res != null && res.statusCode() == 200) {
                    String jsonStr = res.body();
                    JSONArray jsonArray = JSON.parseArray(jsonStr);
                    
                    
                    List<Event> events = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Event event = new Event();
                        JSONObject temp = jsonArray.getJSONObject(i);
                        event.setEventId(temp.getString("value"));
                        event.setLabel(temp.getString("label"));
                        event.setYear(temp.getInteger("year"));
                        events.add(event);
                    }
                    eventDao.saveOrUpdate(events);
                    return;
                }
            } catch (IOException e) {
                logger.error("Timeout URL: {}, maxErrorTimes is left {}, will retry after {} seconds", reqUrl, --maxErrorTimes, RECONNECT_MILLIS / 1000);
                logger.error("Error stack trace: ", e);
                try {
                    Thread.sleep(RECONNECT_MILLIS);
                } catch (InterruptedException e1) {
                    logger.error("Error stack trace: ", e1);
                }
            }
        }
        
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synLanguages() {
        final String reqUrl = "https://www.ted.com/languages/combo.json?per_page=1000";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://www.ted.com/talks");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        Connection.Response res;
        
        int maxErrorTimes = MAX_ERROR_TIMES;
        while (true) {
            try {
                res = Jsoup.connect(reqUrl).headers(headers).timeout(CONNECTION_TIME_OUT_SECONDS * 1000).maxBodySize(0).ignoreContentType(true).ignoreHttpErrors(true).execute();
                if (maxErrorTimes <= 0) {
                    logger.error("Error URL: {}, check that the URL is correct", reqUrl);
                    break;
                }
                if (res != null && (res.statusCode() == 404 || res.statusCode() == 504)) {
                    maxErrorTimes--;
                }
                if (res != null && res.statusCode() == 200) {
                    String jsonStr = res.body();
                    JSONArray jsonArray = JSON.parseArray(jsonStr);
                    
                    List<Language> languages = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Language language = new Language();
                        JSONObject temp = jsonArray.getJSONObject(i);
                        language.setLanguageCode(temp.getString("value"));
                        language.setLanguageName(temp.getString("label"));
                        languages.add(language);
                    }
                    languageDao.saveOrUpdateBasicInfo(languages);
                    return;
                }
            } catch (IOException e) {
                logger.error("Timeout URL: {}, maxErrorTimes is left {}, will retry after {} seconds", reqUrl, --maxErrorTimes, RECONNECT_MILLIS / 1000);
                logger.error("Error stack trace: ", e);
                try {
                    Thread.sleep(RECONNECT_MILLIS);
                } catch (InterruptedException e1) {
                    logger.error("Error stack trace: ", e1);
                }
            }
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synTopics() {
        final String reqUrl = "https://www.ted.com/topics/combo?models=Talks";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Referer", "https://www.ted.com/talks");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        
        Connection.Response res;
        
        int maxErrorTimes = MAX_ERROR_TIMES;
        while (true) {
            try {
                res = Jsoup.connect(reqUrl).headers(headers).timeout(CONNECTION_TIME_OUT_SECONDS * 1000).maxBodySize(0).ignoreContentType(true).ignoreHttpErrors(true).execute();
                if (maxErrorTimes <= 0) {
                    logger.error("Error URL: {}, check that the URL is correct", reqUrl);
                    break;
                }
                if (res != null && (res.statusCode() == 404 || res.statusCode() == 504)) {
                    maxErrorTimes--;
                }
                if (res != null && res.statusCode() == 200) {
                    String jsonStr = res.body();
                    JSONArray jsonArray = JSON.parseArray(jsonStr);
                    
                    List<Topic> topics = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Topic topic = new Topic();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        topic.setTopicId(jsonObject.getString("value"));
                        topic.setLabel(jsonObject.getString("label"));
                        topics.add(topic);
                    }
                    topicDao.saveOrUpdate(topics);
                    return;
                }
            } catch (IOException e) {
                logger.error("Timeout URL: {}, maxErrorTimes is left {}, will retry after {} seconds", reqUrl, --maxErrorTimes, RECONNECT_MILLIS / 1000);
                logger.error("Error stack trace: ", e);
                try {
                    Thread.sleep(RECONNECT_MILLIS);
                } catch (InterruptedException e1) {
                    logger.error("Error stack trace: ", e1);
                }
            }
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void synTalks() {
        Map<String, Integer> talksPageInfo = getTalksPageInfo();
        if (talksPageInfo != null) {
            final Integer firstPage = talksPageInfo.get("firstPage");
            final Integer lastPage = talksPageInfo.get("lastPage");
            
            for (int page = firstPage; page <= lastPage; page++) {
                logger.info("page = " + page + ", lastPage = " + lastPage);
                Document doc = getTalksDoc(page);
                Elements links;
                if (doc != null) {
                    links = doc.select(".media__message a");
                    for (Element link : links) {
                        String url = link.attr("abs:href");
                        transactionalHelp4TalkService.synTalk(url);
                    }
                }
            }
        }
    }
    
    private Map<String, Integer> getTalksPageInfo() {
        Document talksDoc = getTalksDoc(null);
        Map<String, Integer> pageInfoMap = new HashMap<>();
        if (talksDoc != null) {
            Element firstPageNode = talksDoc.select(".pagination__item").first();
            Integer firstPage = Integer.parseInt(firstPageNode.text());
            Element lastPageNode = talksDoc.select(".pagination__item").last();
            Integer lastPage = Integer.parseInt(lastPageNode.text());
            
            pageInfoMap.put("firstPage", firstPage);
            pageInfoMap.put("lastPage", lastPage);
            return pageInfoMap;
        }
        return null;
    }
    
    private Document getTalksDoc(Integer page) {
        final String reqUrl = "https://www.ted.com/talks";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,en-US;q=0.8,zh;q=0.6,en;q=0.4");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        
        Connection connection = Jsoup.connect(reqUrl).headers(headers).timeout(CONNECTION_TIME_OUT_SECONDS * 1000).maxBodySize(0).ignoreHttpErrors(true);
        if (page != null) {
            // add ?page=xxx
            connection.data("page", String.valueOf(page));
        }
        
        Document doc;
        
        int maxErrorTimes = MAX_ERROR_TIMES;
        while (true) {
            try {
                Thread.sleep(NEXT_REQ_MILLIS);
                Connection.Response res = connection.execute();
                if (maxErrorTimes <= 0) {
                    logger.error("Error URL: {}, check that the URL is correct", reqUrl);
                    break;
                }
                if (res != null && (res.statusCode() == 404 || res.statusCode() == 504)) {
                    maxErrorTimes--;
                }
                if (res != null && res.statusCode() == 200) {
                    doc = connection.get();
                    Element firstPageNode = doc.select(".pagination__item").first();
                    if (firstPageNode != null) {
                        return doc;
                    }
                }
            } catch (IOException | InterruptedException e) {
                logger.error("Timeout URL: {}, maxErrorTimes is left {}, will retry after {} seconds", reqUrl, --maxErrorTimes, RECONNECT_MILLIS / 1000);
                logger.error("Error stack trace: ", e);
                try {
                    Thread.sleep(RECONNECT_MILLIS);
                } catch (InterruptedException e1) {
                    logger.error("Error stack trace: ", e1);
                }
            }
        }
        return null;
    }
}