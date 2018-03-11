package com.bluyous.spider.bean;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-22
 */
public class Speaker {
    private Integer speakerId;
    private String slug;
    private String firstName;
    private String lastName;
    private String description;
    private String photoUrl;
    private String photoSlug;
    private String whatOthersSay;
    private String whoTheyAre;
    private String whyListen;
    private String title;
    private String middleInitial;
    
    @Override
    public String toString() {
        return "Speaker{" +
                "speakerId=" + speakerId +
                ", slug='" + slug + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", description='" + description + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", photoSlug='" + photoSlug + '\'' +
                ", whatOthersSay='" + whatOthersSay + '\'' +
                ", whoTheyAre='" + whoTheyAre + '\'' +
                ", whyListen='" + whyListen + '\'' +
                ", title='" + title + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                '}';
    }
    
    public Integer getSpeakerId() {
        return speakerId;
    }
    
    public void setSpeakerId(Integer speakerId) {
        this.speakerId = speakerId;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    
    public String getPhotoSlug() {
        return photoSlug;
    }
    
    public void setPhotoSlug(String photoSlug) {
        this.photoSlug = photoSlug;
    }
    
    public String getWhatOthersSay() {
        return whatOthersSay;
    }
    
    public void setWhatOthersSay(String whatOthersSay) {
        this.whatOthersSay = whatOthersSay;
    }
    
    public String getWhoTheyAre() {
        return whoTheyAre;
    }
    
    public void setWhoTheyAre(String whoTheyAre) {
        this.whoTheyAre = whoTheyAre;
    }
    
    public String getWhyListen() {
        return whyListen;
    }
    
    public void setWhyListen(String whyListen) {
        this.whyListen = whyListen;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMiddleInitial() {
        return middleInitial;
    }
    
    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }
}
