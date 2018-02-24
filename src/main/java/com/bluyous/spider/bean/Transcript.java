package com.bluyous.spider.bean;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-23
 */
public class Transcript {
    private Integer talkId;
    private String languageCode;
    private Integer sid;
    private Integer paragraph;
    private Integer subtitleTime;
    private String subtitleText;
    
    @Override
    public String toString() {
        return "Transcript{" +
                "talkId=" + talkId +
                ", languageCode='" + languageCode + '\'' +
                ", sid=" + sid +
                ", paragraph=" + paragraph +
                ", subtitleTime=" + subtitleTime +
                ", subtitleText='" + subtitleText + '\'' +
                '}';
    }
    
    public Integer getTalkId() {
        return talkId;
    }
    
    public void setTalkId(Integer talkId) {
        this.talkId = talkId;
    }
    
    public String getLanguageCode() {
        return languageCode;
    }
    
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    public Integer getSid() {
        return sid;
    }
    
    public void setSid(Integer sid) {
        this.sid = sid;
    }
    
    public Integer getParagraph() {
        return paragraph;
    }
    
    public void setParagraph(Integer paragraph) {
        this.paragraph = paragraph;
    }
    
    public Integer getSubtitleTime() {
        return subtitleTime;
    }
    
    public void setSubtitleTime(Integer subtitleTime) {
        this.subtitleTime = subtitleTime;
    }
    
    public String getSubtitleText() {
        return subtitleText;
    }
    
    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText;
    }
}
