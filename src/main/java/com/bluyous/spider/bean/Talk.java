package com.bluyous.spider.bean;

import java.sql.Timestamp;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-21
 */
public class Talk {
    private Integer talkId;
    private String talkUrl;
    private String talkSlug;
    private String talkDefaultLanguageCode;
    private Integer viewedCount;
    private Timestamp filmedDatetime;
    private Timestamp publishedDatetime;
    private Integer duration;
    private Float introDuration;
    private Float adDuration;
    private Float postAdDuration;
    private String nativeLanguage;
    private String eventLabel;
    private String eventBlurb;
    private String thumbImgUrl;
    private String thumbImgSlug;
    private Timestamp lastUpdateDatetime;
    
    @Override
    public String toString() {
        return "Talk{" +
                "talkId=" + talkId +
                ", talkUrl='" + talkUrl + '\'' +
                ", talkSlug='" + talkSlug + '\'' +
                ", talkDefaultLanguageCode='" + talkDefaultLanguageCode + '\'' +
                ", viewedCount=" + viewedCount +
                ", filmedDatetime=" + filmedDatetime +
                ", publishedDatetime=" + publishedDatetime +
                ", duration=" + duration +
                ", introDuration=" + introDuration +
                ", adDuration=" + adDuration +
                ", postAdDuration=" + postAdDuration +
                ", nativeLanguage='" + nativeLanguage + '\'' +
                ", eventLabel='" + eventLabel + '\'' +
                ", eventBlurb='" + eventBlurb + '\'' +
                ", thumbImgUrl='" + thumbImgUrl + '\'' +
                ", thumbImgSlug='" + thumbImgSlug + '\'' +
                ", lastUpdateDatetime=" + lastUpdateDatetime +
                '}';
    }
    
    public Integer getTalkId() {
        return talkId;
    }
    
    public void setTalkId(Integer talkId) {
        this.talkId = talkId;
    }
    
    public String getTalkUrl() {
        return talkUrl;
    }
    
    public void setTalkUrl(String talkUrl) {
        this.talkUrl = talkUrl;
    }
    
    public String getTalkSlug() {
        return talkSlug;
    }
    
    public void setTalkSlug(String talkSlug) {
        this.talkSlug = talkSlug;
    }
    
    public String getTalkDefaultLanguageCode() {
        return talkDefaultLanguageCode;
    }
    
    public void setTalkDefaultLanguageCode(String talkDefaultLanguageCode) {
        this.talkDefaultLanguageCode = talkDefaultLanguageCode;
    }
    
    public Integer getViewedCount() {
        return viewedCount;
    }
    
    public void setViewedCount(Integer viewedCount) {
        this.viewedCount = viewedCount;
    }
    
    public Timestamp getFilmedDatetime() {
        return filmedDatetime;
    }
    
    public void setFilmedDatetime(Timestamp filmedDatetime) {
        this.filmedDatetime = filmedDatetime;
    }
    
    public Timestamp getPublishedDatetime() {
        return publishedDatetime;
    }
    
    public void setPublishedDatetime(Timestamp publishedDatetime) {
        this.publishedDatetime = publishedDatetime;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Float getIntroDuration() {
        return introDuration;
    }
    
    public void setIntroDuration(Float introDuration) {
        this.introDuration = introDuration;
    }
    
    public Float getAdDuration() {
        return adDuration;
    }
    
    public void setAdDuration(Float adDuration) {
        this.adDuration = adDuration;
    }
    
    public Float getPostAdDuration() {
        return postAdDuration;
    }
    
    public void setPostAdDuration(Float postAdDuration) {
        this.postAdDuration = postAdDuration;
    }
    
    public String getNativeLanguage() {
        return nativeLanguage;
    }
    
    public void setNativeLanguage(String nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }
    
    public String getEventLabel() {
        return eventLabel;
    }
    
    public void setEventLabel(String eventLabel) {
        this.eventLabel = eventLabel;
    }
    
    public String getEventBlurb() {
        return eventBlurb;
    }
    
    public void setEventBlurb(String eventBlurb) {
        this.eventBlurb = eventBlurb;
    }
    
    public String getThumbImgUrl() {
        return thumbImgUrl;
    }
    
    public void setThumbImgUrl(String thumbImgUrl) {
        this.thumbImgUrl = thumbImgUrl;
    }
    
    public String getThumbImgSlug() {
        return thumbImgSlug;
    }
    
    public void setThumbImgSlug(String thumbImgSlug) {
        this.thumbImgSlug = thumbImgSlug;
    }
    
    public Timestamp getLastUpdateDatetime() {
        return lastUpdateDatetime;
    }
    
    public void setLastUpdateDatetime(Timestamp lastUpdateDatetime) {
        this.lastUpdateDatetime = lastUpdateDatetime;
    }
}
