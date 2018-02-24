package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Transcript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-23
 */
@Repository
public class TranscriptDaoImpl implements TranscriptDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public TranscriptDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public List<Transcript> listToSynSubtitleList() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  talk_id,\n");
        sql.append("  language_code\n");
        sql.append("FROM talk_multi_lang\n");
        
        List<Transcript> transcripts = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Transcript.class));
        return transcripts;
    }
    
    @Override
    public void saveOrUpdate(List<Transcript> transcripts) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO transcript (talk_id, language_code, sid, paragraph, subtitle_time, subtitle_text)\n");
        sql.append("VALUES (:talkId, :languageCode, :sid, :paragraph, :subtitleTime, :subtitleText)\n");
        sql.append("ON DUPLICATE KEY UPDATE\n");
        sql.append("  paragraph     = values(paragraph),\n");
        sql.append("  subtitle_time = values(subtitle_time),\n");
        sql.append("  subtitle_text = values(subtitle_text)\n");
        if (transcripts == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[transcripts.size()];
        for (int i = 0; i < transcripts.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(transcripts.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
    
    @Override
    public void deleteRedundantTranscript(Transcript redundantTranscript) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM transcript\n");
        sql.append("WHERE talk_id = :talkId AND language_code = :languageCode\n");
        sql.append("      AND sid > :sid\n");
        if (redundantTranscript == null) {
            return;
        }
        
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(redundantTranscript);
        namedParameterJdbcTemplate.update(sql.toString(), sqlParameterSource);
    }
}
