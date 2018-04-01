package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Language;
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
public class LanguageDaoImpl implements LanguageDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public LanguageDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdateBasicInfo(List<Language> languages) {
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
    
    @Override
    public void saveOrUpdate(List<Language> languages) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO language (language_code, language_name, endonym, iana_code, is_rtl)\n");
        sql.append("VALUES (:languageCode, :languageName, :endonym, :ianaCode, :isRtl)\n");
        sql.append("ON DUPLICATE KEY UPDATE language_name = values(language_name), endonym = values(endonym),\n");
        sql.append("  iana_code                           = values(iana_code), is_rtl = values(is_rtl);\n");
        if (languages == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[languages.size()];
        int i = 0;
        for (Language language : languages) {
            sqlParameterSources[i++] = new BeanPropertySqlParameterSource(language);
        }
        
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
    
    @Override
    public List<Map<String, Object>> getLanguages() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  language.language_code,\n");
        sql.append("  language_name,\n");
        sql.append("  endonym,\n");
        sql.append("  is_rtl,\n");
        sql.append("  ifnull(t.count, 0) count\n");
        sql.append("FROM language\n");
        sql.append("  LEFT JOIN (\n");
        sql.append("              SELECT\n");
        sql.append("                language_code,\n");
        sql.append("                count(*) count\n");
        sql.append("              FROM talk_multi_lang\n");
        sql.append("              GROUP BY language_code\n");
        sql.append("            ) t ON t.language_code = language.language_code\n");
        sql.append("ORDER BY count DESC;\n");
        
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql.toString());
        return mapList;
    }
}
