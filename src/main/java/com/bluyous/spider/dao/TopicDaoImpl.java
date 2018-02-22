package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Topic;
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
public class TopicDaoImpl implements TopicDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public TopicDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdate(List<Topic> topics) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO topic (topic_id, label) VALUES (:topicId, :label)\n");
        sql.append("ON DUPLICATE KEY UPDATE label = values(label);\n");
        if (topics == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[topics.size()];
        for (int i = 0; i < topics.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(topics.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
}
