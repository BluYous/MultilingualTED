package com.bluyous.spider.bean;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-22
 */
public class Rating {
    private Integer talkId;
    private Integer ratingId;
    private String ratingName;
    private Integer ratingCount;
    
    @Override
    public String toString() {
        return "Rating{" +
                "talkId=" + talkId +
                ", ratingId=" + ratingId +
                ", ratingName='" + ratingName + '\'' +
                ", ratingCount=" + ratingCount +
                '}';
    }
    
    public Integer getTalkId() {
        return talkId;
    }
    
    public void setTalkId(Integer talkId) {
        this.talkId = talkId;
    }
    
    public Integer getRatingId() {
        return ratingId;
    }
    
    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }
    
    public String getRatingName() {
        return ratingName;
    }
    
    public void setRatingName(String ratingName) {
        this.ratingName = ratingName;
    }
    
    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }
}
