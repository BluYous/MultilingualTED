package com.bluyous.spider.bean;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-03-02
 */
public class Resource {
    private String url;
    private String filePath;
    private String fileName;
    private Integer contentLength;
    private String contentType;
    private String tag;
    private String lastModified;
    private byte[] bytes;
    
    public Resource(String url) {
        this.url = url;
    }
    
    public Resource() {
    
    }
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(url);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(url, resource.url);
    }
    
    @Override
    public String toString() {
        return "Resource{" +
                "url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", contentLength=" + contentLength +
                ", contentType='" + contentType + '\'' +
                ", tag='" + tag + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Integer getContentLength() {
        return contentLength;
    }
    
    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public String getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
