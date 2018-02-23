package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Speaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-22
 */
@Repository
public class SpeakerDaoImpl implements SpeakerDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public SpeakerDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}
