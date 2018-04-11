package com.bluyous.spider.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.dao.LanguageDao;
import com.bluyous.spider.dao.SpeakerDao;
import com.bluyous.spider.dao.TalkDao;
import com.bluyous.spider.dao.TopicDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-30
 */
@Service
@Slf4j
public class ApiServiceImpl implements ApiService {
    private final SpeakerDao speakerDao;
    private final TalkDao talkDao;
    private final TopicDao TopicDao;
    private final LanguageDao languageDao;
    @Value("${resources.defaultPath}")
    private String defaultPath;
    
    public ApiServiceImpl(TalkDao talkDao, com.bluyous.spider.dao.TopicDao topicDao, LanguageDao languageDao,
                          SpeakerDao speakerDao) {
        this.talkDao = talkDao;
        TopicDao = topicDao;
        this.languageDao = languageDao;
        this.speakerDao = speakerDao;
    }
    
    @Override
    public Integer getTalksNum() {
        return talkDao.getTalkNum();
    }
    
    @Override
    public List<Map<String, Object>> getTopics() {
        return TopicDao.getTopics();
    }
    
    @Override
    public List<Map<String, Object>> getLanguages() {
        return languageDao.getLanguages();
    }
    
    @Override
    public List<Map<String, Object>> getEvents() {
        return talkDao.getEvents();
    }
    
    @Override
    public List<Map<String, Object>> getFilterResults(JSONObject json) {
        List<Map<String, Object>> filterResults = talkDao.getFilterResults(json);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
        for (Map<String, Object> result : filterResults) {
            Timestamp publishedDatetime = (Timestamp) result.get("published_datetime");
            result.put("published_datetime", dateFormat.format(publishedDatetime));
            
            Integer duration = (Integer) result.get("duration");
            result.put("duration", formatDuration(duration));
        }
        
        return filterResults;
    }
    
    @Override
    public Map<String, Object> getTalk(String talkId, String languageCode) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        Map<String, Object> talk = talkDao.getTalk(talkId, languageCode);
        List<Map<String, Object>> speakers = speakerDao.getSpeakers(Integer.valueOf(talkId));
        List<Map<String, Object>> ratings = talkDao.getRatings(Integer.valueOf(talkId));
        List<Map<String, Object>> topics = talkDao.getTopics(Integer.valueOf(talkId));
        List<Map<String, Object>> downloads = talkDao.getDownloads(Integer.valueOf(talkId));
        
        if (talk != null) {
            talk.put("speakers", speakers);
            talk.put("ratings", ratings);
            talk.put("topics", topics);
            talk.put("downloads", downloads);
            java.sql.Date recordedAt = (java.sql.Date) talk.get("recorded_at");
            talk.put("recorded_at", dateFormat.format(recordedAt));
            Timestamp publishedDatetime = (Timestamp) talk.get("published_datetime");
            talk.put("published_datetime", dateFormatWithTime.format(publishedDatetime));
            Timestamp lastUpdateDatetime = (Timestamp) talk.get("last_update_datetime");
            talk.put("last_update_datetime", dateFormatWithTime.format(lastUpdateDatetime));
            
            Integer duration = (Integer) talk.get("duration");
            talk.put("duration", formatDuration(duration));
        }
        return talk;
    }
    
    @Override
    public List<Map<String, Object>> getSubtitleLanguages(String talkId) {
        File dir = new File(defaultPath + File.separator + "talks/" + talkId + "/subtitles");
        File[] files = dir.listFiles();
        List<String> languageCodeList = new ArrayList<>();
        for (File file : files != null ? files : new File[0]) {
            languageCodeList.add(file.getName());
        }
        
        return languageDao.getSubtitleLanguages(languageCodeList);
    }
    
    @Override
    public List<List<Map<String, Object>>> getSubtitles(String talkId, JSONArray json) {
        List<List<Map<String, Object>>> subtitles = new ArrayList<>();
        for (int i = 0; i < json.size(); i++) {
            String languageCode = json.getString(i);
            String fileName = defaultPath + File.separator + "talks/" + talkId + "/subtitles/" + languageCode + "/full.vtt";
            List<Map<String, Object>> subtitle = readLineFile(fileName);
            subtitles.add(subtitle);
        }
        return subtitles;
    }
    
    private List<Map<String, Object>> readLineFile(String filename) {
        List<Map<String, Object>> paragraphs = new LinkedList<>();
        Pattern pattern = Pattern.compile(
                "^[\\d]{2}:(([\\d]{2}):[\\d]{2}.[\\d]{3}) --> [\\d]{2}:[\\d]{2}:[\\d]{2}.[\\d]{3}$");
        try (FileInputStream in = new FileInputStream(filename); InputStreamReader inReader = new InputStreamReader(in,
                "UTF-8"); BufferedReader bufReader = new BufferedReader(inReader)) {
            String line = null;
            int i = 1;
            int startLine = 4;
            int currentLine = 0;
            boolean isTimeLine = true;
            List<Map<String, String>> cues = null;
            Map<String, String> cue = null;
            String preMinute = "";
            while ((line = bufReader.readLine()) != null) {
                if (++currentLine >= startLine) {
                    if ("".equals(line)) {
                        isTimeLine = true;
                    } else if (isTimeLine) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            String startTime = matcher.group(1);
                            String minute = matcher.group(2);
                            if (minute.startsWith("0")) {
                                minute = minute.substring(1);
                            }
                            if (!preMinute.equals(minute)) {
                                cues = new LinkedList<>();
                                Map<String, Object> cuesObj = new HashMap<>();
                                cuesObj.put("minute", minute);
                                cuesObj.put("cues", cues);
                                paragraphs.add(cuesObj);
                                preMinute = minute;
                            }
                            cue = new HashMap<>();
                            if (cues != null) {
                                cues.add(cue);
                            }
                            cue.put("time", startTime);
                            isTimeLine = false;
                        }
                    } else {
                        if (cue.get("text") == null) {
                            cue.put("text", line);
                        } else {
                            cue.put("text", cue.get("text") + "\n" + line);
                        }
                        i++;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error stack trace: ", e);
        }
        return paragraphs;
    }
    
    private static String formatDuration(Integer duration) {
        int minute = duration / 60;
        int second = duration % 60;
        return String.valueOf(minute) + ":" + (second <= 9 ? "0" : "") + String.valueOf(second);
    }
}
