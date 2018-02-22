package com.bluyous.spider.bean;

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
