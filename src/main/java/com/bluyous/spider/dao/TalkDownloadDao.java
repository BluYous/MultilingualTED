package com.bluyous.spider.dao;

import com.bluyous.spider.bean.TalkDownload;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-22
 */
public interface TalkDownloadDao {
    void saveOrUpdate(List<TalkDownload> talkDownloads);
}
