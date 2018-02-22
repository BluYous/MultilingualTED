package com.bluyous.spider.bean;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-19
 */
public class Topic {
    private String topicId;
    private String label;
    
    @Override
    public String toString() {
        return "Topic{" +
                "topicId='" + topicId + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
    
    public String getTopicId() {
        return topicId;
    }
    
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    
}
