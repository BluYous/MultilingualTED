package com.bluyous.spider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.bean.Event;
import com.bluyous.spider.bean.Language;
import com.bluyous.spider.bean.Talk;
import com.bluyous.spider.bean.Topic;
import com.bluyous.spider.dao.EventDao;
import com.bluyous.spider.dao.LanguageDao;
import com.bluyous.spider.dao.TalkDao;
import com.bluyous.spider.dao.TopicDao;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用来同步 TED 网站
 *
 * @author BluYous
 * @version 1.0
 * @since 2018-02-19
 */
@Service
public class TedSpiderService {
    private static final int TIMEOUT = 120000;
    private final EventDao eventDao;
    private final LanguageDao languageDao;
    private final TopicDao topicDao;
    private final TalkDao talkDao;
    
    @Autowired
    public TedSpiderService(EventDao eventDao, LanguageDao languageDao, TopicDao topicDao, TalkDao talkDao) {
        this.eventDao = eventDao;
        this.languageDao = languageDao;
        this.topicDao = topicDao;
        this.talkDao = talkDao;
    }
    
    @Transactional
    public void runSpider() {
        /* 代理地址 */
        final String PROXY_IP = "223.241.116.140";
        final String PROXY_PORT = "8010";
        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("http.proxyHost", PROXY_IP);
        System.getProperties().setProperty("http.proxyPort", PROXY_PORT);
        
        // synEvents();
        // synLanguages();
        // synTopics();
        // synTalksList();
        synTalkDetail();
    }
    
    private void synTalkDetail() {
    
    }
    
    private void synTalksList() {
        Map<String, Integer> talksPageInfo = getTalksPageInfo();
        final Integer firstPage = talksPageInfo.get("firstPage");
        // todo 测试代码，页数少了，之后删除
        final Integer lastPage = talksPageInfo.get("lastPage") - 74;
        System.out.println();
        
        List<Talk> talks = new ArrayList<>();
        for (int page = firstPage; page <= lastPage; page++) {
            System.out.println("lastPage = " + lastPage + ", page = " + page);
            Document doc = getTalksDoc(page);
            Elements links = doc.select(".media__message a");
            for (Element link : links) {
                String url = link.attr("abs:href");
                JSONObject json = getTalkDetailJson(url);
                // 得到视频的 ID
                Integer talkId = null;
                String talkSlug = null;
                String talkDefaultLanguageCode = null;
                if (json != null) {
                    talkId = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getInteger("id");
                    talkSlug = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getString("slug");
                    talkDefaultLanguageCode = json.getJSONObject("__INITIAL_DATA__").getString("language");
                }
                
                Talk talk = new Talk();
                talk.setTalkId(talkId);
                talk.setTalkUrl(url);
                talk.setTalkSlug(talkSlug);
                talk.setTalkDefaultLanguageCode(talkDefaultLanguageCode);
                talks.add(talk);
            }
        }
        
        talkDao.saveOrUpdateBasicInfo(talks);
    }
    
    private Map<String, Integer> getTalksPageInfo() {
        Document talksDoc = getTalksDoc(null);
        Map<String, Integer> pageInfoMap = new HashMap<>();
        Element firstPageNode = talksDoc.select(".pagination__item").first();
        Integer firstPage = Integer.parseInt(firstPageNode.text());
        Element lastPageNode = talksDoc.select(".pagination__item").last();
        Integer lastPage = Integer.parseInt(lastPageNode.text());
        
        pageInfoMap.put("firstPage", firstPage);
        pageInfoMap.put("lastPage", lastPage);
        return pageInfoMap;
    }
    
    private Document getTalksDoc(Integer page) {
        final String reqURL = "https://www.ted.com/talks";
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,en-US;q=0.8,zh;q=0.6,en;q=0.4");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        Document doc = null;
        try {
            Connection connection = Jsoup.connect(reqURL).headers(headers).timeout(TIMEOUT);
            // add ?page=xxx
            if (page != null) {
                connection.data("page", String.valueOf(page));
            }
            doc = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
    
    
    private JSONObject getTalkDetailJson(String talkReqUrl) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,en-US;q=0.8,zh;q=0.6,en;q=0.4");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("X-Requested-With", "XMLHttpRequest");
        
        Document doc = null;
        try {
            Connection connection = Jsoup.connect(talkReqUrl).headers(headers).timeout(TIMEOUT);
            doc = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            return null;
        }
        
        Elements scripts = doc.select("script");
        for (Element script : scripts) {
            if (script.html().startsWith("q(\"")) {
                //解决无法多行匹配的问题
                String str = script.html().replace("\n", "");
                
                // 得到 JSON
                Pattern pattern = Pattern.compile("^q\\(\"talkPage.init\", (.*)\\)$");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String jsonStr = matcher.group(1);
                    JSONObject json = JSON.parseObject(jsonStr);
                    return json;
                }
            }
        }
        return null;
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
                event.setEventId(temp.getString("value"));
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
                language.setLanguageCode(temp.getString("value"));
                language.setLanguageName(temp.getString("label"));
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
                topic.setTopicId(jsonObject.getString("value"));
                topic.setLabel(jsonObject.getString("label"));
                topics.add(topic);
            }
        }
        topicDao.saveOrUpdate(topics);
    }
}
