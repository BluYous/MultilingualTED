package com.bluyous.spider.bean;

public class Topic {
    private String id;
    private String label;
    
    @Override
    public String toString() {
        return "Topic{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
}
