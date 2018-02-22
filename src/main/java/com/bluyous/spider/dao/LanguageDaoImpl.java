package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Language;
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
public class LanguageDaoImpl implements LanguageDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public LanguageDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdate(List<Language> languages) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO language (language_code, language_name) VALUES (:languageCode, :languageName)\n");
        sql.append("ON DUPLICATE KEY UPDATE language_name = values(language_name);\n");
        if (languages == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[languages.size()];
        for (int i = 0; i < languages.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(languages.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
}
