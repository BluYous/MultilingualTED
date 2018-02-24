package com.bluyous.spider.dao;

import com.bluyous.spider.bean.Transcript;

import java.util.List;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-23
 */
public interface TranscriptDao {
    List<Transcript> listToSynSubtitleList();
    
    void saveOrUpdate(List<Transcript> transcripts);
    
    /**
     * 删除多余的字幕信息，注意是删除 > sid 的数据
     *
     * @param redundantTranscript 必须包含 talkId, languageCode, sid
     */
    void deleteRedundantTranscript(Transcript redundantTranscript);
}
