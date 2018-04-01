package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Topic;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Override
    public List<Map<String, Object>> getTopics() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  topic.topic_id,\n");
        sql.append("  label,\n");
        sql.append("  ifnull(t.count, 0) count\n");
        sql.append("FROM topic\n");
        sql.append("  LEFT JOIN (\n");
        sql.append("              SELECT\n");
        sql.append("                topic_id,\n");
        sql.append("                count(*) count\n");
        sql.append("              FROM talk_topic_ref\n");
        sql.append("              GROUP BY topic_id\n");
        sql.append("            ) t ON t.topic_id = topic.topic_id\n");
        sql.append("\n");
        sql.append("ORDER BY label ASC\n");
    
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString());
        return mapList;
    }
}
