package com.bluyous.spider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.bean.*;
import com.bluyous.spider.dao.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
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
    private static final int TIMEOUT = 10000;
    private final EventDao eventDao;
    private final LanguageDao languageDao;
    private final TopicDao topicDao;
    private final TalkDao talkDao;
    private final SpeakerDao speakerDao;
    private final RatingDao ratingDao;
    private final TalkDownloadDao talkDownloadDao;
    private final TranscriptDao transcriptDao;
    
    @Autowired
    public TedSpiderService(EventDao eventDao, LanguageDao languageDao, TopicDao topicDao, TalkDao talkDao, SpeakerDao speakerDao, RatingDao ratingDao, TalkDownloadDao talkDownloadDao, TranscriptDao transcriptDao) {
        this.eventDao = eventDao;
        this.languageDao = languageDao;
        this.topicDao = topicDao;
        this.talkDao = talkDao;
        this.speakerDao = speakerDao;
        this.ratingDao = ratingDao;
        this.talkDownloadDao = talkDownloadDao;
        this.transcriptDao = transcriptDao;
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
        // synTalkDetail();
        synTalkTranscript();
        
        System.out.println("同步完成！");
    }
    
    private void synTalkTranscript() {
        List<Transcript> toSynSubtitleList = transcriptDao.listToSynSubtitleList();
        for (Transcript toSynSubtitle : toSynSubtitleList) {
            final Integer talkId = toSynSubtitle.getTalkId();
            final String languageCode = toSynSubtitle.getLanguageCode();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject json = getTranscriptJson(talkId, languageCode);
            if (json == null) {
                continue;
            }
            
            List<Transcript> transcripts = new ArrayList<>();
            Integer sid = 0;
            
            Integer paragraph = 0;
            JSONArray paragraphsArray = json.getJSONArray("paragraphs");
            for (int i = 0; i < paragraphsArray.size(); i++) {
                JSONArray cuesArray = paragraphsArray.getJSONObject(i).getJSONArray("cues");
                paragraph++;
                for (int j = 0; j < cuesArray.size(); j++) {
                    Integer subtitleTime = cuesArray.getJSONObject(j).getInteger("time");
                    String subtitleText = cuesArray.getJSONObject(j).getString("text");
                    
                    Transcript transcript = new Transcript();
                    transcript.setTalkId(talkId);
                    transcript.setLanguageCode(languageCode);
                    transcript.setSid(++sid);
                    transcript.setParagraph(paragraph);
                    transcript.setSubtitleTime(subtitleTime);
                    transcript.setSubtitleText(subtitleText);
                    
                    transcripts.add(transcript);
                }
            }
            transcriptDao.saveOrUpdate(transcripts);
            transcriptDao.deleteRedundantTranscript(transcripts.get(transcripts.size() - 1));
        }
    }
    
    private JSONObject getTranscriptJson(Integer talkId, String languageCode) {
        final String reqUrl = "https://www.ted.com/talks/" + talkId + "/transcript.json?language=" + languageCode;
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
        if (res != null) {
            String json = res.body();
            return JSON.parseObject(json);
        }
        return null;
    }
    
    private void synTalkDetail() {
        Set<Language> languages = new HashSet<>();
        List<Talk> talks = talkDao.listTalkBasicInfo();
        for (Talk talk : talks) {
            final Integer talkId = talk.getTalkId();
            final String talkUrl = talk.getTalkUrl();
            final String talkDefaultLanguageCode = talk.getTalkDefaultLanguageCode();
            
            JSONObject json = getTalkDetailJson(talkUrl, talkDefaultLanguageCode);
            if (json == null) {
                continue;
            }
            
            speakerDao.saveOrUpdate(parseSpeakerList(json));
            talkDao.saveOrUpdateTalkSpeakerRef(parseTalkSpeakerRefList(talkId, json));
            ratingDao.saveOrUpdate(parseRatingList(talkId, json));
            parseLanguageSet(json, languages);
            talkDao.saveOrUpdateTalkTopicRef(parseTalkTopicRefList(talkId, json));
            talkDownloadDao.saveOrUpdate(parseTalkDownloadList(talkId, json));
            talkDao.saveOrUpdateExtraInfo(parseTalkExtraInfoList(talkId, json));
            talkDao.saveOrUpdateTalkMultiLang(parseTalkMutiLangList(talkId, json, talkUrl, talkDefaultLanguageCode));
        }
        
        languageDao.saveOrUpdate(languages);
    }
    
    private JSONObject getTalkDetailJson(String talkReqUrl, String languageCode) {
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
            if (languageCode != null) {
                connection.data("language", languageCode);
            }
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
    
    private List<Speaker> parseSpeakerList(JSONObject json) {
        List<Speaker> speakers = new ArrayList<>();
        JSONArray speakersJsonArray = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getJSONArray("speakers");
        for (int i = 0; i < speakersJsonArray.size(); i++) {
            JSONObject speakerJson = speakersJsonArray.getJSONObject(i);
            Speaker speaker = new Speaker();
            speaker.setSpeakerId(speakerJson.getInteger("id"));
            speaker.setSlug(speakerJson.getString("slug"));
            speaker.setFirstName(speakerJson.getString("firstname"));
            speaker.setLastName(speakerJson.getString("lastname"));
            speaker.setDescription(speakerJson.getString("description"));
            speaker.setPhotoUrl(speakerJson.getString("photo_url"));
            
            String photoUrl = speaker.getPhotoUrl();
            Pattern pattern = Pattern.compile("^https://pe.tedcdn.com/images/ted/(.*)$");
            Matcher matcher = pattern.matcher(photoUrl);
            if (matcher.find()) {
                speaker.setPhotoSlug(matcher.group(1));
            }
            
            speaker.setWhatOthersSay(speakerJson.getString("whatotherssay"));
            speaker.setWhoTheyAre(speakerJson.getString("whotheyare"));
            speaker.setWhyListen(speakerJson.getString("whylisten"));
            speaker.setTitle(speakerJson.getString("title"));
            speaker.setMiddleInitial(speakerJson.getString("middleinitial"));
            
            speakers.add(speaker);
        }
        return speakers;
    }
    
    private List<Map<String, Object>> parseTalkSpeakerRefList(Integer talkId, JSONObject json) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        JSONArray talkSpeakerRefJsonArray = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getJSONArray("speakers");
        for (int i = 0; i < talkSpeakerRefJsonArray.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("talkId", talkId);
            map.put("speakerId", talkSpeakerRefJsonArray.getJSONObject(i).getInteger("id"));
            mapList.add(map);
        }
        return mapList;
    }
    
    private List<Rating> parseRatingList(Integer talkId, JSONObject json) {
        List<Rating> ratings = new ArrayList<>();
        JSONArray ratingsJsonArray = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getJSONArray("ratings");
        for (int i = 0; i < ratingsJsonArray.size(); i++) {
            JSONObject ratingJson = ratingsJsonArray.getJSONObject(i);
            Rating rating = new Rating();
            rating.setTalkId(talkId);
            rating.setRatingId(ratingJson.getInteger("id"));
            rating.setRatingName(ratingJson.getString("name"));
            rating.setRatingCount(ratingJson.getInteger("count"));
            ratings.add(rating);
        }
        return ratings;
    }
    
    private void parseLanguageSet(JSONObject json, Set<Language> languages) {
        JSONArray languagesJsonArray = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getJSONArray("player_talks").getJSONObject(0).getJSONArray("languages");
        for (int i = 0; i < languagesJsonArray.size(); i++) {
            JSONObject languageJson = languagesJsonArray.getJSONObject(i);
            Language language = new Language();
            language.setLanguageCode(languageJson.getString("languageCode"));
            language.setLanguageName(languageJson.getString("languageName"));
            language.setEndonym(languageJson.getString("endonym"));
            language.setIanaCode(languageJson.getString("ianaCode"));
            language.setIsRtl(languageJson.getBoolean("isRtl") ? "Y" : "N");
            languages.add(language);
        }
    }
    
    private List<Map<String, Object>> parseTalkTopicRefList(Integer talkId, JSONObject json) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        JSONArray talkTopicRefJsonArray = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getJSONArray("tags");
        for (int i = 0; i < talkTopicRefJsonArray.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("talkId", talkId);
            map.put("topicId", talkTopicRefJsonArray.getString(i));
            mapList.add(map);
        }
        return mapList;
    }
    
    private List<TalkDownload> parseTalkDownloadList(Integer talkId, JSONObject json) {
        List<TalkDownload> talkDownloads = new ArrayList<>();
        JSONObject talkDownloadsJson = json.getJSONObject("__INITIAL_DATA__").getJSONObject("media").getJSONObject("internal");
        Set<String> downloadTypes = talkDownloadsJson.keySet();
        for (String downloadType : downloadTypes) {
            TalkDownload talkDownload = new TalkDownload();
            talkDownload.setTalkId(talkId);
            talkDownload.setDownloadType(downloadType);
            JSONObject downloadDetailJson = talkDownloadsJson.getJSONObject(downloadType);
            talkDownload.setUri(downloadDetailJson.getString("uri"));
            talkDownload.setFileSizeBytes(downloadDetailJson.getInteger("filesize_bytes"));
            talkDownload.setMimeType(downloadDetailJson.getString("mime_type"));
            
            talkDownloads.add(talkDownload);
        }
        
        return talkDownloads;
    }
    
    private List<Talk> parseTalkExtraInfoList(Integer talkId, JSONObject json) {
        List<Talk> talks = new ArrayList<>();
        Talk talk = new Talk();
        talk.setTalkId(talkId);
        
        final JSONObject talksJson = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0);
        final JSONObject playerTalksJson = talksJson.getJSONArray("player_talks").getJSONObject(0);
        
        final Integer viewedCount = talksJson.getInteger("viewed_count");
        final Timestamp filmedDatetime = new Timestamp(playerTalksJson.getLong("filmed") * 1000);
        final Timestamp publishedDatetime = new Timestamp(playerTalksJson.getLong("published") * 1000);
        final Integer duration = playerTalksJson.getInteger("duration");
        final Float introDuration = playerTalksJson.getFloat("introDuration");
        final Float adDuration = playerTalksJson.getFloat("adDuration");
        final Float postAdDuration = playerTalksJson.getFloat("postAdDuration");
        final String nativeLanguage = playerTalksJson.getString("nativeLanguage");
        final String eventLabel = playerTalksJson.getString("event");
        final String eventBlurb = talksJson.getString("event_blurb");
        final String thumbImgUrl = talksJson.getString("hero");
        
        String thumbImgSlug = null;
        Pattern pattern = Pattern.compile("^https://pe.tedcdn.com/images/ted/(.*)$");
        Matcher matcher = pattern.matcher(thumbImgUrl);
        if (matcher.find()) {
            thumbImgSlug = matcher.group(1);
        }
        
        final Timestamp lastUpdateDatetime = new Timestamp(System.currentTimeMillis());
        
        talk.setViewedCount(viewedCount);
        talk.setFilmedDatetime(filmedDatetime);
        talk.setPublishedDatetime(publishedDatetime);
        talk.setDuration(duration);
        talk.setIntroDuration(introDuration);
        talk.setAdDuration(adDuration);
        talk.setPostAdDuration(postAdDuration);
        talk.setNativeLanguage(nativeLanguage);
        talk.setEventLabel(eventLabel);
        talk.setEventBlurb(eventBlurb);
        talk.setThumbImgUrl(thumbImgUrl);
        talk.setThumbImgSlug(thumbImgSlug);
        talk.setLastUpdateDatetime(lastUpdateDatetime);
        
        talks.add(talk);
        return talks;
    }
    
    private List<TalkMultiLang> parseTalkMutiLangList(Integer talkId, JSONObject json, String talkUrl, String defaultLanguageCode) {
        List<TalkMultiLang> talkMultiLangs = new ArrayList<>();
        
        JSONArray languagesJsonArray = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getJSONArray("player_talks").getJSONObject(0).getJSONArray("languages");
        for (int i = 0; i < languagesJsonArray.size(); i++) {
            JSONObject languageJson = languagesJsonArray.getJSONObject(i);
            String languageCode = languageJson.getString("languageCode");
            
            JSONObject talkMultiLangJson;
            if (defaultLanguageCode.equals(languageCode)) {
                talkMultiLangJson = json;
            } else {
                talkMultiLangJson = getTalkDetailJson(talkUrl, languageCode);
            }
            
            if (talkMultiLangJson == null) {
                continue;
            }
            
            final JSONObject talksJson = talkMultiLangJson.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0);
            final JSONObject playerTalksJson = talksJson.getJSONArray("player_talks").getJSONObject(0);
            
            TalkMultiLang talkMultiLang = new TalkMultiLang();
            talkMultiLang.setTalkId(talkId);
            talkMultiLang.setLanguageCode(languageCode);
            talkMultiLang.setTitle(playerTalksJson.getString("title"));
            talkMultiLang.setSpeaker(playerTalksJson.getString("speaker"));
            talkMultiLang.setDescription(talksJson.getString("description"));
            
            talkMultiLangs.add(talkMultiLang);
        }
        return talkMultiLangs;
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
                JSONObject json = getTalkDetailJson(url, null);
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
        languageDao.saveOrUpdateBasicInfo(languages);
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
