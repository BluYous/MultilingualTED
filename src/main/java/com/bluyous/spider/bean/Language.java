package com.bluyous.spider.bean;

import java.util.Objects;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-19
 */
public class Language {
    private String languageCode;
    private String languageName;
    private String endonym;
    private String ianaCode;
    private String isRtl;
    
    @Override
    public int hashCode() {
        return Objects.hash(languageCode, languageName, endonym, ianaCode, isRtl);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return Objects.equals(languageCode, language.languageCode) &&
                Objects.equals(languageName, language.languageName) &&
                Objects.equals(endonym, language.endonym) &&
                Objects.equals(ianaCode, language.ianaCode) &&
                Objects.equals(isRtl, language.isRtl);
    }
    
    @Override
    public String toString() {
        return "Language{" +
                "languageCode='" + languageCode + '\'' +
                ", languageName='" + languageName + '\'' +
                ", endonym='" + endonym + '\'' +
                ", ianaCode='" + ianaCode + '\'' +
                ", isRtl='" + isRtl + '\'' +
                '}';
    }
    
    public String getLanguageCode() {
        return languageCode;
    }
    
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    public String getLanguageName() {
        return languageName;
    }
    
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
    
    public String getEndonym() {
        return endonym;
    }
    
    public void setEndonym(String endonym) {
        this.endonym = endonym;
    }
    
    public String getIanaCode() {
        return ianaCode;
    }
    
    public void setIanaCode(String ianaCode) {
        this.ianaCode = ianaCode;
    }
    
    public String getIsRtl() {
        return isRtl;
    }
    
    public void setIsRtl(String isRtl) {
        this.isRtl = isRtl;
    }
}
