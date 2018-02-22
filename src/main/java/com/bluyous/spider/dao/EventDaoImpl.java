package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Event;
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
 * @since 2018-02-20
 */
@Repository
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public EventDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdate(List<Event> events) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO event (event_id, label, year) VALUES (:eventId, :label, :year)\n");
        sql.append("ON DUPLICATE KEY UPDATE label = values(label), year = values(year);\n");
        if (events == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[events.size()];
        for (int i = 0; i < events.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(events.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
}
