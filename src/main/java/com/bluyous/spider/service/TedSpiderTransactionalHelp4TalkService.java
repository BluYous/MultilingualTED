package com.bluyous.spider.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.bean.*;
import com.bluyous.spider.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static com.bluyous.spider.service.TedSpiderService.*;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-01
 */
@Service
@Slf4j
public class TedSpiderTransactionalHelp4TalkService {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    @Value("${resources.defaultPath}")
    private String defaultPath;
    private final LanguageDao languageDao;
    private final TalkDao talkDao;
    private final SpeakerDao speakerDao;
    private final RatingDao ratingDao;
    private final TalkDownloadDao talkDownloadDao;
    private final ResourceDao resourceDao;
    
    @Autowired
    public TedSpiderTransactionalHelp4TalkService(LanguageDao languageDao, TalkDao talkDao, SpeakerDao speakerDao,
                                                  RatingDao ratingDao, TalkDownloadDao talkDownloadDao,
                                                  ResourceDao resourceDao) {
        this.languageDao = languageDao;
        this.talkDao = talkDao;
        this.speakerDao = speakerDao;
        this.ratingDao = ratingDao;
        this.talkDownloadDao = talkDownloadDao;
        this.resourceDao = resourceDao;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void synTalk(String talkUrl) {
        log.info("开始获取 {} 的详细信息", talkUrl);
        JSONObject json = getTalkJson(talkUrl, null);
        
        Talk talk;
        if (json != null) {
            talk = parseTalk(json);
            Integer talkId = talk.getTalkId();
            String talkDefaultLanguageCode = talk.getTalkDefaultLanguageCode();
            
            List<Speaker> speakers = parseSpeakerList(json);
            List<Map<String, Object>> TalkSpeakerRefList = parseTalkSpeakerRefList(talkId, json);
            List<Rating> ratings = parseRatingList(talkId, json);
            List<Map<String, Object>> TalkTopicRefList = parseTalkTopicRefList(talkId, json);
            List<TalkDownload> talkDownloads = parseTalkDownloadList(talkId, json);
            List<TalkMultiLang> talkMultiLangs = parseTalkMultiLangList(talkId, json, talkUrl, talkDefaultLanguageCode);
            List<Language> languages = parseLanguageList(json);
            List<Resource> resources = parseResourceList(talk, speakers, languages);
            
            log.info("开始存储数据：" + talkUrl);
            speakerDao.saveOrUpdate(speakers);
            talkDao.saveOrUpdate(talk);
            talkDao.saveOrUpdateTalkSpeakerRef(TalkSpeakerRefList);
            ratingDao.saveOrUpdate(ratings);
            talkDao.saveOrUpdateTalkTopicRef(TalkTopicRefList);
            talkDownloadDao.saveOrUpdate(talkDownloads);
            talkDao.saveOrUpdateTalkMultiLang(talkMultiLangs);
            languageDao.saveOrUpdate(languages);
            resourceDao.saveOrUpdate(resources);
            saveFiles(resources);
        }
    }
    
    private JSONObject getTalkJson(String talkReqUrl, String languageCode) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,en-US;q=0.8,zh;q=0.6,en;q=0.4");
        headers.put("Referer", "https://www.ted.com/talks");
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        headers.put("Upgrade-Insecure-Requests", "1");
        Connection connection = Jsoup.connect(talkReqUrl).headers(headers).timeout(CONNECTION_TIME_OUT_SECONDS * 1000).maxBodySize(0).ignoreHttpErrors(true);
        if (languageCode != null) {
            connection.data("language", languageCode);
        }
        
        Document doc;
        
        int maxErrorTimes = MAX_ERROR_TIMES;
        while (true) {
            try {
                Thread.sleep(NEXT_REQ_MILLIS);
                Connection.Response res = connection.execute();
                if (maxErrorTimes <= 0) {
                    log.error("Error URL: {}, check that the URL is correct", talkReqUrl);
                    break;
                }
                if (res != null && (res.statusCode() == 404 || res.statusCode() == 504)) {
                    maxErrorTimes--;
                }
                if (res != null && res.statusCode() == 200) {
                    doc = connection.get();
                    
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
                                return JSON.parseObject(jsonStr);
                            }
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                log.error("Timeout URL: {}, maxErrorTimes is left {}, will retry after {} seconds", talkReqUrl, --maxErrorTimes, RECONNECT_MILLIS / 1000);
                log.error("Error stack trace: ", e);
                try {
                    Thread.sleep(RECONNECT_MILLIS);
                } catch (InterruptedException e1) {
                    log.error("Error stack trace: ", e1);
                }
            }
        }
        return null;
    }
    
    private Talk parseTalk(JSONObject json) {
        Talk talk = new Talk();
        
        final Integer talkId = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getInteger("id");
        final String talkUrl = json.getJSONObject("__INITIAL_DATA__").getString("url");
        final String talkSlug = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getString("slug");
        final String talkDefaultLanguageCode = json.getJSONObject("__INITIAL_DATA__").getString("language");
        
        final JSONObject talksJson = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0);
        final JSONObject playerTalksJson = talksJson.getJSONArray("player_talks").getJSONObject(0);
        
        final Integer viewedCount = talksJson.getInteger("viewed_count");
        java.sql.Date recordedAt = null;
        String recordedAtStr = talksJson.getString("recorded_at");
        try {
            recordedAt = new java.sql.Date(DATE_FORMAT.parse(recordedAtStr).getTime());
        } catch (ParseException e) {
            log.error("Error when parse {} to sql.Date", recordedAtStr);
            log.error("Error stack trace: ", e);
        }
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
        Pattern pattern = Pattern.compile("^https://[^/]+(?:/.*)/(.*)$");
        Matcher matcher = pattern.matcher(thumbImgUrl);
        if (matcher.find()) {
            thumbImgSlug = matcher.group(1);
        }
        
        final Timestamp lastUpdateDatetime = new Timestamp(System.currentTimeMillis());
        talk.setTalkId(talkId);
        talk.setTalkUrl(talkUrl);
        talk.setTalkSlug(talkSlug);
        talk.setTalkDefaultLanguageCode(talkDefaultLanguageCode);
        talk.setViewedCount(viewedCount);
        talk.setRecordedAt(recordedAt);
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
        
        return talk;
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
        JSONObject mediaJson = json.getJSONObject("__INITIAL_DATA__").getJSONObject("media");
        if (mediaJson == null) {
            return talkDownloads;
        }
        JSONObject talkDownloadsJson = mediaJson.getJSONObject("internal");
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
    
    private List<TalkMultiLang> parseTalkMultiLangList(Integer talkId, JSONObject json, String talkUrl, String defaultLanguageCode) {
        log.info("开始解析多语言");
        List<TalkMultiLang> talkMultiLangs = new ArrayList<>();
        
        JSONArray languagesJsonArray = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0).getJSONArray("player_talks").getJSONObject(0).getJSONArray("languages");
        if (languagesJsonArray.size() == 0) {
            final JSONObject talksJson = json.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0);
            final JSONObject playerTalksJson = talksJson.getJSONArray("player_talks").getJSONObject(0);
    
            TalkMultiLang talkMultiLang = new TalkMultiLang();
            talkMultiLang.setTalkId(talkId);
            talkMultiLang.setLanguageCode(defaultLanguageCode);
            talkMultiLang.setTitle(playerTalksJson.getString("title"));
            talkMultiLang.setSpeaker(playerTalksJson.getString("speaker"));
            talkMultiLang.setDescription(talksJson.getString("description"));
    
            talkMultiLangs.add(talkMultiLang);
        } else {
            for (int i = 0; i < languagesJsonArray.size(); i++) {
                JSONObject languageJson = languagesJsonArray.getJSONObject(i);
                String languageCode = languageJson.getString("languageCode");
        
                JSONObject talkMultiLangJson;
                if (defaultLanguageCode.equals(languageCode)) {
                    talkMultiLangJson = json;
                } else {
                    talkMultiLangJson = getTalkJson(talkUrl, languageCode);
                }
        
                final JSONObject talksJson;
                if (talkMultiLangJson != null) {
                    talksJson = talkMultiLangJson.getJSONObject("__INITIAL_DATA__").getJSONArray("talks").getJSONObject(0);
                    final JSONObject playerTalksJson = talksJson.getJSONArray("player_talks").getJSONObject(0);
            
                    TalkMultiLang talkMultiLang = new TalkMultiLang();
                    talkMultiLang.setTalkId(talkId);
                    talkMultiLang.setLanguageCode(languageCode);
                    talkMultiLang.setTitle(playerTalksJson.getString("title"));
                    talkMultiLang.setSpeaker(playerTalksJson.getString("speaker"));
                    talkMultiLang.setDescription(talksJson.getString("description"));
            
                    talkMultiLangs.add(talkMultiLang);
                }
            }
        }
        return talkMultiLangs;
    }
    
    private List<Language> parseLanguageList(JSONObject json) {
        List<Language> languages = new ArrayList<>();
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
        return languages;
    }
    
    private List<Resource> parseResourceList(Talk talk, List<Speaker> speakers, List<Language> languages) {
        List<Resource> resourcesWithUrl = new ArrayList<>();
        resourcesWithUrl.add(new Resource(talk.getThumbImgUrl()));
        for (Speaker speaker : speakers) {
            resourcesWithUrl.add(new Resource(speaker.getPhotoUrl()));
        }
        for (Language language : languages) {
            Integer talkId = talk.getTalkId();
            String languageCode = language.getLanguageCode();
            String subUrl = "https://hls.ted.com/talks/" + talkId + "/subtitles/" + languageCode + "/full.vtt";
            resourcesWithUrl.add(new Resource(subUrl));
        }
        List<Resource> resourceListForReq = resourceDao.getResourceListForReq(resourcesWithUrl);
        
        Set<Resource> resourceSetForReq = new HashSet<>();
        resourceSetForReq.addAll(resourceListForReq);
        resourceSetForReq.addAll(resourcesWithUrl);
        
        return parseResourcesForReq(resourceSetForReq);
    }
    
    private void saveFiles(List<Resource> resources) {
        for (Resource resource : resources) {
            String filePath = resource.getFilePath();
            String fileName = resource.getFileName();
            byte[] bytes = resource.getBytes();
            bytes2File(bytes, defaultPath + File.separator + filePath, fileName);
        }
    }
    
    private List<Resource> parseResourcesForReq(Set<Resource> resourceListForReq) {
        List<Resource> resources = new ArrayList<>();
        for (Resource resourceForReq : resourceListForReq) {
            final String reqUrl = resourceForReq.getUrl();
            log.info("开始下载：reqUrl = " + reqUrl);
            String tag = resourceForReq.getTag();
            String lastModified = resourceForReq.getLastModified();
            int maxErrorTimes = MAX_ERROR_TIMES;
            while (true) {
                try {
                    URL url = new URL(reqUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(CONNECTION_TIME_OUT_SECONDS * 1000);
                    httpURLConnection.setReadTimeout(CONNECTION_TIME_OUT_SECONDS * 1000);
                    httpURLConnection.setRequestProperty("Accept", "*/*");
                    httpURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                    httpURLConnection.setRequestProperty("Referer", "https://www.ted.com/talks");
                    httpURLConnection.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
                    if (tag != null) {
                        httpURLConnection.setRequestProperty("If-None-Match", tag);
                    }
                    if (lastModified != null) {
                        httpURLConnection.setRequestProperty("If-Modified-Since", lastModified);
                    }
                    
                    
                    if (maxErrorTimes <= 0) {
                        log.error("Error URL: {}, check that the URL is correct", reqUrl);
                        break;
                    }
                    if (httpURLConnection.getResponseCode() == 404 || httpURLConnection.getResponseCode() == 504) {
                        maxErrorTimes--;
                    }
                    if (httpURLConnection.getResponseCode() == 304) {
                        break;
                    }
                    if (httpURLConnection.getResponseCode() == 200) {
                        String filePath = null;
                        String fileName = null;
                        
                        Pattern pattern = Pattern.compile("^https://[^/]+(/.*)/(.*)$");
                        Matcher matcher = pattern.matcher(reqUrl);
                        if (matcher.find()) {
                            filePath = matcher.group(1);
                            fileName = matcher.group(2);
                        }
                        
                        Integer contentLength = Integer.valueOf(httpURLConnection.getHeaderField("Content-Length"));
                        String contentType = httpURLConnection.getHeaderField("Content-Type");
                        String newTag = httpURLConnection.getHeaderField("ETag");
                        String newLastModified = httpURLConnection.getHeaderField("Last-Modified");
                        byte[] bytes = parseBytes(httpURLConnection);
                        
                        
                        Resource resource = new Resource();
                        resource.setUrl(reqUrl);
                        resource.setFilePath(filePath);
                        resource.setFileName(fileName);
                        resource.setContentLength(contentLength);
                        resource.setContentType(contentType);
                        resource.setTag(newTag);
                        resource.setLastModified(newLastModified);
                        resource.setBytes(bytes);
                        resources.add(resource);
                        break;
                    }
                } catch (IOException e) {
                    log.error("Timeout URL: {}, maxErrorTimes is left {}, will retry after {} seconds", reqUrl, --maxErrorTimes, RECONNECT_MILLIS / 1000);
                    log.error("Error stack trace: ", e);
                    try {
                        Thread.sleep(RECONNECT_MILLIS);
                    } catch (InterruptedException e1) {
                        log.error("Error stack trace: ", e1);
                    }
                }
            }
        }
        return resources;
    }
    
    private void bytes2File(byte[] buf, String filePath, String fileName) {
        File file;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            try (FileOutputStream fos = new FileOutputStream(file); BufferedOutputStream bos = new BufferedOutputStream(
                    fos)) {
                bos.write(buf);
            }
        } catch (IOException e) {
            log.error("Error stack trace: ", e);
        }
    }
    
    private byte[] parseBytes(HttpURLConnection httpURLConnection) throws IOException {
        String encoding = httpURLConnection.getHeaderField("Content-Encoding");
        if (encoding != null && encoding.toLowerCase().contains("gzip")) {
            GZIPInputStream in = new GZIPInputStream(httpURLConnection.getInputStream());
            return parseBytesFromInputStream(in);
        } else {
            InputStream in = httpURLConnection.getInputStream();
            return parseBytesFromInputStream(in);
        }
    }
    
    private static byte[] parseBytesFromInputStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024 * 1024];
            int len;
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            out.close();
        }
        return out.toByteArray();
    }
}
