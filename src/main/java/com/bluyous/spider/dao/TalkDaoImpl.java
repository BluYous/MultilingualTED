package com.bluyous.spider.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bluyous.spider.bean.Talk;
import com.bluyous.spider.bean.TalkMultiLang;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-21
 */
@Repository
@Slf4j
public class TalkDaoImpl implements TalkDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public TalkDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdate(Talk talk) {
        if (talk == null) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk (talk_id, talk_url, talk_slug, talk_default_language_code,\n");
        sql.append("                  viewed_count, recorded_at, published_datetime, duration,\n");
        sql.append("                  intro_duration, ad_duration, post_ad_duration, native_language,\n");
        sql.append("                  event_label, event_blurb, thumb_img_url, thumb_img_slug,\n");
        sql.append("                  last_update_datetime)\n");
        sql.append("VALUES\n");
        sql.append("  (:talkId, :talkUrl, :talkSlug, :talkDefaultLanguageCode, :viewedCount,\n");
        sql.append("            :recordedAt, :publishedDatetime, :duration, :introDuration,\n");
        sql.append("            :adDuration, :postAdDuration, :nativeLanguage, :eventLabel,\n");
        sql.append("   :eventBlurb, :thumbImgUrl, :thumbImgSlug, :lastUpdateDatetime)\n");
        sql.append("ON DUPLICATE KEY UPDATE\n");
        sql.append("  talk_url                   = values(talk_url),\n");
        sql.append("  talk_slug                  = values(talk_slug),\n");
        sql.append("  talk_default_language_code = values(talk_default_language_code),\n");
        sql.append("  viewed_count               = values(viewed_count),\n");
        sql.append("  recorded_at                = values(recorded_at),\n");
        sql.append("  published_datetime         = values(published_datetime),\n");
        sql.append("  duration                   = values(duration),\n");
        sql.append("  intro_duration             = values(intro_duration),\n");
        sql.append("  ad_duration                = values(ad_duration),\n");
        sql.append("  post_ad_duration           = values(post_ad_duration),\n");
        sql.append("  native_language            = values(native_language),\n");
        sql.append("  event_label                = values(event_label),\n");
        sql.append("  event_blurb                = values(event_blurb),\n");
        sql.append("  thumb_img_url              = values(thumb_img_url),\n");
        sql.append("  thumb_img_slug             = values(thumb_img_slug),\n");
        sql.append("  last_update_datetime       = values(last_update_datetime);\n");
        
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(talk);
        namedParameterJdbcTemplate.update(sql.toString(), sqlParameterSource);
    }
    
    @Override
    public void saveOrUpdateTalkSpeakerRef(List<Map<String, Object>> mapList) {
        if (mapList == null) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk_speaker_ref (talk_id, speaker_id) VALUES (:talkId, :speakerId)\n");
        sql.append("ON DUPLICATE KEY UPDATE talk_id = talk_id, speaker_id = speaker_id\n");
        
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), mapList.toArray(new Map[mapList.size()]));
    }
    
    @Override
    public void saveOrUpdateTalkTopicRef(List<Map<String, Object>> mapList) {
        if (mapList == null) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk_topic_ref (talk_id, topic_id) VALUES (:talkId, :topicId)\n");
        sql.append("ON DUPLICATE KEY UPDATE talk_id = talk_id, topic_id = topic_id\n");
        
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), mapList.toArray(new Map[mapList.size()]));
    }
    
    @Override
    public void saveOrUpdateTalkMultiLang(List<TalkMultiLang> talkMultiLangs) {
        if (talkMultiLangs == null) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk_multi_lang (talk_id, language_code, title, speaker, description)\n");
        sql.append("VALUES (:talkId, :languageCode, :title, :speaker, :description)\n");
        sql.append("ON DUPLICATE KEY UPDATE\n");
        sql.append("  title         = values(title),\n");
        sql.append("  speaker       = values(speaker),\n");
        sql.append("  description   = values(description)\n");
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[talkMultiLangs.size()];
        for (int i = 0; i < talkMultiLangs.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(talkMultiLangs.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
    
    @Override
    public Integer getTalkNum() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT count(*)\n");
        sql.append("FROM talk;\n");
        
        Integer talkNum = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
        return talkNum;
    }
    
    @Override
    public List<Map<String, Object>> getEvents() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  event_label,\n");
        sql.append("  count(*) count\n");
        sql.append("FROM talk\n");
        sql.append("GROUP BY event_label\n");
        sql.append("ORDER BY count DESC\n");
    
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString());
        return mapList;
    }
    
    @Override
    public List<Map<String, Object>> getFilterResults(JSONObject json) {
        if (json == null) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        String searchFilter = json.getString("searchFilter");
        String languageFilter = json.getString("languageFilter");
        String eventFilter = json.getString("eventFilter");
        JSONArray topicFilter = json.getJSONArray("topicFilter");
        Integer sortFilter = json.getInteger("sortFilter");
        
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  talk.talk_id,\n");
        sql.append("  speaker,\n");
        sql.append("  title,\n");
        sql.append("  published_datetime,\n");
        sql.append("  language_code,\n");
        sql.append("  duration,\n");
        sql.append("  rated,\n");
        sql.append("  concat(resource.file_path, '/', resource.file_name) thumb_img_path\n");
        sql.append("FROM talk\n");
        sql.append("  INNER JOIN (SELECT DISTINCT talk_id\n");
        sql.append("              FROM talk_topic_ref\n");
        if (topicFilter != null && topicFilter.size() != 0) {
            sql.append("              WHERE topic_id IN (:topicFilter)\n");
            params.put("topicFilter", topicFilter);
        }
        sql.append("             ) topic ON topic.talk_id = talk.talk_id\n");
        sql.append("  LEFT JOIN talk_multi_lang ON talk.talk_id = talk_multi_lang.talk_id\n");
        sql.append("  LEFT JOIN (SELECT\n");
        sql.append("               t.talk_id,\n");
        sql.append("               group_concat(t.rating_name ORDER BY t.group_rank SEPARATOR ', ') rated\n");
        sql.append("             FROM (SELECT\n");
        sql.append("                     talk_rating.talk_id,\n");
        sql.append("                     rating_id,\n");
        sql.append("                     rating_name,\n");
        sql.append("                     rating_count,\n");
        sql.append(
                "                     @cur_group_rank := if(@pre_group = talk_rating.talk_id, @cur_group_rank + 1, 1) group_rank,\n");
        sql.append(
                "                     @pre_group := talk_rating.talk_id                                               group_id\n");
        sql.append("                   FROM talk_rating\n");
        sql.append("                     ,\n");
        sql.append("                     (SELECT\n");
        sql.append("                        @cur_group_rank := 1,\n");
        sql.append("                        @pre_group := -1) init\n");
        sql.append("                   ORDER BY talk_id, rating_count DESC) t\n");
        sql.append("               INNER JOIN (SELECT talk_id\n");
        sql.append("                           FROM talk_rating\n");
        sql.append("                           GROUP BY talk_id\n");
        sql.append("                           HAVING sum(rating_count) > 50) t2 ON t2.talk_id = t.talk_id\n");
        sql.append("             WHERE t.group_rank <= 2\n");
        sql.append("             GROUP BY talk_id\n");
        sql.append("            ) t ON t.talk_id = talk.talk_id\n");
        sql.append("  LEFT JOIN resource ON thumb_img_url = resource.url\n");
        sql.append("WHERE\n");
        if (languageFilter == null || "".equals(languageFilter)) {
            sql.append("  talk_default_language_code = language_code\n");
        } else {
            sql.append("  language_code = :languageFilter\n");
            params.put("languageFilter", languageFilter);
        }
        
        if (searchFilter != null) {
            String searchText = null;
            if (languageFilter == null || "en".equals(languageFilter)) {
                searchText = "% " + searchFilter + " %";
            } else {
                searchText = "%" + searchFilter + "%";
            }
            sql.append("  AND title LIKE :searchText\n");
            params.put("searchText", searchText);
        }
        
        if (eventFilter != null && !"".equals(eventFilter)) {
            sql.append("  AND event_label = :eventFilter\n");
            params.put("eventFilter", eventFilter);
        }
        if (sortFilter == 1) {
            sql.append("ORDER BY published_datetime DESC\n");
        } else if (sortFilter == 2) {
            sql.append("ORDER BY published_datetime ASC\n");
        } else if (sortFilter == 3) {
            sql.append("ORDER BY viewed_count DESC\n");
        }
        
        List<Map<String, Object>> mapList = namedParameterJdbcTemplate.queryForList(sql.toString(), params);
        return mapList;
    }
    
    @Override
    public Map<String, Object> getTalk(String talkId, String languageCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("talkId", Integer.valueOf(talkId));
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  talk.talk_id,\n");
        sql.append("  talk_multi_lang.language_code,\n");
        sql.append("  title,\n");
        sql.append("  speaker,\n");
        sql.append("  description,\n");
        sql.append("  talk_url,\n");
        sql.append("  viewed_count,\n");
        sql.append("  recorded_at,\n");
        sql.append("  published_datetime,\n");
        sql.append("  duration,\n");
        sql.append("  l.language_name                                     native_language_name,\n");
        sql.append("  l.endonym                                           native_language_endonym,\n");
        sql.append("  event_label,\n");
        sql.append("  event_blurb,\n");
        sql.append("  last_update_datetime,\n");
        sql.append("  concat(resource.file_path, '/', resource.file_name) thumb_img_path\n");
        sql.append("FROM talk\n");
        sql.append("  LEFT JOIN talk_multi_lang ON talk.talk_id = talk_multi_lang.talk_id\n");
        sql.append("  LEFT JOIN language l ON native_language = l.language_code\n");
        sql.append("  LEFT JOIN resource ON thumb_img_url = resource.url\n");
        sql.append("WHERE talk.talk_id = :talkId\n");
        if (languageCode != null && !"".equals(languageCode)) {
            sql.append("      AND talk_multi_lang.language_code = :languageCode\n");
            params.put("languageCode", languageCode);
        } else {
            sql.append("      AND talk_default_language_code = talk_multi_lang.language_code\n");
        }
        
        Map<String, Object> map = null;
        try {
            map = namedParameterJdbcTemplate.queryForMap(sql.toString(), params);
        } catch (DataAccessException e) {
            log.info("无对应记录 {} {}", talkId, languageCode);
            return null;
        }
        return map;
    }
    
    public List<Map<String, Object>> getRatings(Integer talkId) {
        Map<String, Object> params = new HashMap<>();
        params.put("talkId", talkId);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  rating_id,\n");
        sql.append("  rating_name,\n");
        sql.append("  rating_count\n");
        sql.append("FROM talk_rating\n");
        sql.append("WHERE talk_id = :talkId\n");
        
        
        List<Map<String, Object>> map = null;
        map = namedParameterJdbcTemplate.queryForList(sql.toString(), params);
        return map;
    }
    
    public List<Map<String, Object>> getTopics(Integer talkId) {
        Map<String, Object> params = new HashMap<>();
        params.put("talkId", talkId);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  topic.topic_id,\n");
        sql.append("  label\n");
        sql.append("FROM talk_topic_ref\n");
        sql.append("  INNER JOIN topic ON topic.topic_id = talk_topic_ref.topic_id\n");
        sql.append("WHERE talk_id = :talkId\n");
        
        List<Map<String, Object>> map = null;
        map = namedParameterJdbcTemplate.queryForList(sql.toString(), params);
        return map;
    }
    
    public List<Map<String, Object>> getDownloads(Integer talkId) {
        Map<String, Object> params = new HashMap<>();
        params.put("talkId", talkId);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  download_type,\n");
        sql.append("  uri,\n");
        sql.append("  file_size_bytes,\n");
        sql.append("  mime_type\n");
        sql.append("FROM talk_download\n");
        sql.append("WHERE talk_id = :talkId AND download_type NOT LIKE 'podcast%'\n");
        sql.append("ORDER BY mime_type DESC, file_size_bytes DESC\n");
        
        
        List<Map<String, Object>> map = null;
        map = namedParameterJdbcTemplate.queryForList(sql.toString(), params);
        return map;
    }
}
