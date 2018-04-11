package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Speaker;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @since 2018-02-22
 */
@Repository
public class SpeakerDaoImpl implements SpeakerDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public SpeakerDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdate(List<Speaker> speakers) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO speaker (speaker_id, slug, first_name, last_name, description, photo_url, photo_slug, what_others_say,\n");
        sql.append("                     who_they_are, why_listen, title, middle_initial)\n");
        sql.append("VALUES (:speakerId, :slug, :firstName, :lastName, :description, :photoUrl, :photoSlug, :whatOthersSay, :whoTheyAre,\n");
        sql.append("                    :whyListen, :title, :middleInitial)\n");
        sql.append("ON DUPLICATE KEY UPDATE\n");
        sql.append("  slug            = values(slug),\n");
        sql.append("  first_name      = values(first_name),\n");
        sql.append("  last_name       = values(last_name),\n");
        sql.append("  description     = values(description),\n");
        sql.append("  photo_url       = values(photo_url),\n");
        sql.append("  photo_slug      = values(photo_slug),\n");
        sql.append("  what_others_say = values(what_others_say),\n");
        sql.append("  who_they_are    = values(who_they_are),\n");
        sql.append("  why_listen      = values(why_listen),\n");
        sql.append("  title           = values(title),\n");
        sql.append("  middle_initial  = values(middle_initial)\n");
        if (speakers == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[speakers.size()];
        for (int i = 0; i < speakers.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(speakers.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
    
    @Override
    public List<Map<String, Object>> getSpeakers(Integer talkId) {
        Map<String, Object> params = new HashMap<>();
        params.put("talkId", talkId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  speaker.speaker_id,\n");
        sql.append("  slug,\n");
        sql.append("  first_name,\n");
        sql.append("  last_name,\n");
        sql.append("  description,\n");
        sql.append("  concat(resource.file_path, '/', resource.file_name) photo_path,\n");
        sql.append("  what_others_say,\n");
        sql.append("  who_they_are,\n");
        sql.append("  why_listen,\n");
        sql.append("  title,\n");
        sql.append("  middle_initial\n");
        sql.append("FROM speaker\n");
        sql.append("  INNER JOIN talk_speaker_ref ON speaker.speaker_id = talk_speaker_ref.speaker_id\n");
        sql.append("  LEFT JOIN resource ON photo_url = resource.url\n");
        sql.append("WHERE talk_id = :talkId\n");
        
        List<Map<String, Object>> mapList = namedParameterJdbcTemplate.queryForList(sql.toString(), params);
        return mapList;
    }
}
