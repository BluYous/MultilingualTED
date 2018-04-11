package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Rating;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RatingDaoImpl implements RatingDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public RatingDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdate(List<Rating> ratings) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk_rating (talk_id, rating_id, rating_name, rating_count)\n");
        sql.append("VALUES (:talkId, :ratingId, :ratingName, :ratingCount)\n");
        sql.append("ON DUPLICATE KEY UPDATE rating_name = values(rating_name), rating_count = values(rating_count)\n");
        if (ratings == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[ratings.size()];
        for (int i = 0; i < ratings.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(ratings.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
}
