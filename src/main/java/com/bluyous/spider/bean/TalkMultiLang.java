package com.bluyous.spider.bean;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-23
 */
public class TalkMultiLang {
    private Integer talkId;
    private String languageCode;
    private String title;
    private String speaker;
    private String description;
    
    @Override
    public String toString() {
        return "TalkMultiLang{" +
                "talkId=" + talkId +
                ", languageCode='" + languageCode + '\'' +
                ", title='" + title + '\'' +
                ", speaker='" + speaker + '\'' +
                ", description='" + description + '\'' +
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSpeaker() {
        return speaker;
    }
    
    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
