package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-02
 */
@Repository
public class ResourceDaoImpl implements ResourceDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public ResourceDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public List<Resource> getResourceListForReq(List<Resource> resourcesWithUrl) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT\n");
        sql.append("  url,\n");
        sql.append("  tag,\n");
        sql.append("  last_modified\n");
        sql.append("FROM resource\n");
        sql.append("WHERE url IN (:urls)\n");
        
        Map<String, Object> params = new HashMap<>();
        List<String> urls = new ArrayList<>();
        for (Resource resource : resourcesWithUrl) {
            urls.add(resource.getUrl());
        }
        params.put("urls", urls);
        
        
        List<Resource> resources = namedParameterJdbcTemplate.query(sql.toString(), params, new BeanPropertyRowMapper<>(Resource.class));
        return resources;
    }
    
    @Override
    public void saveOrUpdate(List<Resource> resources) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO resource (url, file_path, file_name, content_length, content_type, tag, last_modified)\n");
        sql.append("VALUES (:url, :filePath, :fileName, :contentLength, :contentType, :tag, :lastModified)\n");
        sql.append("ON DUPLICATE KEY UPDATE\n");
        sql.append("  file_path      = VALUES(file_path),\n");
        sql.append("  file_name      = VALUES(file_name),\n");
        sql.append("  content_length = VALUES(content_length),\n");
        sql.append("  content_type   = VALUES(content_type),\n");
        sql.append("  tag            = VALUES(tag),\n");
        sql.append("  last_modified  = VALUES(last_modified)\n");
        if (resources == null) {
            return;
        }
        
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[resources.size()];
        int i = 0;
        for (Resource language : resources) {
            sqlParameterSources[i++] = new BeanPropertySqlParameterSource(language);
        }
        
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
        
    }
}
