package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Talk;
import com.bluyous.spider.bean.TalkMultiLang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-21
 */
@Repository
public class TalkDaoImpl implements TalkDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public TalkDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdateBasicInfo(List<Talk> talks) {
        if (talks == null) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk (talk_id, talk_url, talk_slug, talk_default_language_code)\n");
        sql.append("VALUES (:talkId, :talkUrl, :talkSlug, :talkDefaultLanguageCode)\n");
        sql.append("ON DUPLICATE KEY UPDATE talk_url = values(talk_url), talk_slug = values(talk_slug),\n");
        sql.append("  talk_default_language_code     = values(talk_default_language_code);\n");
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[talks.size()];
        for (int i = 0; i < talks.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(talks.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
    
    @Override
    public void saveOrUpdateExtraInfo(List<Talk> talks) {
        if (talks == null) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk (talk_id, viewed_count, filmed_datetime, published_datetime,\n");
        sql.append("                  duration, intro_duration, ad_duration, post_ad_duration,\n");
        sql.append("                  native_language, event_blurb, event_label, thumb_img_url,\n");
        sql.append("                  thumb_img_slug, last_update_datetime)\n");
        sql.append("VALUES\n");
        sql.append("  (:talkId, :viewedCount, :filmedDatetime, :publishedDatetime, :duration, :introDuration, :adDuration, :postAdDuration,\n");
        sql.append("            :nativeLanguage, :eventBlurb, :eventLabel, :thumbImgUrl, :thumbImgSlug, :lastUpdateDatetime)\n");
        sql.append("ON DUPLICATE KEY UPDATE\n");
        sql.append("  viewed_count         = values(viewed_count),\n");
        sql.append("  filmed_datetime      = values(filmed_datetime),\n");
        sql.append("  published_datetime   = values(published_datetime),\n");
        sql.append("  duration             = values(duration),\n");
        sql.append("  intro_duration       = values(intro_duration),\n");
        sql.append("  ad_duration          = values(ad_duration),\n");
        sql.append("  post_ad_duration     = values(post_ad_duration),\n");
        sql.append("  native_language      = values(native_language),\n");
        sql.append("  event_blurb          = values(event_blurb),\n");
        sql.append("  event_label          = values(event_label),\n");
        sql.append("  thumb_img_url        = values(thumb_img_url),\n");
        sql.append("  thumb_img_slug       = values(thumb_img_slug),\n");
        sql.append("  last_update_datetime = values(last_update_datetime);\n");
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[talks.size()];
        for (int i = 0; i < talks.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(talks.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
    
    @Override
    public List<Talk> listTalkBasicInfo() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  talk_id,\n");
        sql.append("  talk_url,\n");
        sql.append("  talk_default_language_code\n");
        sql.append("FROM talk\n");
        // todo 测试注释了下面两行代码
        // sql.append("WHERE last_update_datetime IS NULL\n");
        // sql.append("      OR TIMESTAMPDIFF(DAY, last_update_datetime, sysdate()) >= 1;\n");
        
        List<Talk> talks = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Talk.class));
        return talks;
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
}
