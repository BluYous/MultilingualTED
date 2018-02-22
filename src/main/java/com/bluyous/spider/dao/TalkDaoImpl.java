package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Talk;
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
}
