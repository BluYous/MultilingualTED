package com.bluyous.spider.dao;

import com.bluyous.spider.bean.TalkDownload;
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
public class TalkDownloadDaoImpl implements TalkDownloadDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public TalkDownloadDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    
    @Override
    public void saveOrUpdate(List<TalkDownload> talkDownloads) {
        if (talkDownloads == null) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO talk_download (talk_id, download_type, uri, file_size_bytes, mime_type)\n");
        sql.append("VALUES (:talkId, :downloadType, :uri, :fileSizeBytes, :mimeType)\n");
        sql.append("ON DUPLICATE KEY UPDATE\n");
        sql.append("  talk_id         = values(talk_id),\n");
        sql.append("  download_type   = values(download_type),\n");
        sql.append("  uri             = values(uri),\n");
        sql.append("  file_size_bytes = values(file_size_bytes),\n");
        sql.append("  mime_type       = values(mime_type)\n");
    
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[talkDownloads.size()];
        for (int i = 0; i < talkDownloads.size(); i++) {
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(talkDownloads.get(i));
        }
        namedParameterJdbcTemplate.batchUpdate(sql.toString(), sqlParameterSources);
    }
}
