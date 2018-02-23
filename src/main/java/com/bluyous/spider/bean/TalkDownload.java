package com.bluyous.spider.bean;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-22
 */
public class TalkDownload {
    private Integer talkId;
    private String downloadType;
    private String uri;
    private Integer fileSizeBytes;
    private String mimeType;
    
    @Override
    public String toString() {
        return "TalkDownload{" +
                "talkId=" + talkId +
                ", downloadType='" + downloadType + '\'' +
                ", uri='" + uri + '\'' +
                ", fileSizeBytes=" + fileSizeBytes +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
    
    public Integer getTalkId() {
        return talkId;
    }
    
    public void setTalkId(Integer talkId) {
        this.talkId = talkId;
    }
    
    public String getDownloadType() {
        return downloadType;
    }
    
    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }
    
    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public Integer getFileSizeBytes() {
        return fileSizeBytes;
    }
    
    public void setFileSizeBytes(Integer fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
