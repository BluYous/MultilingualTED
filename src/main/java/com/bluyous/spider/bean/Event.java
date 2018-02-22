package com.bluyous.spider.bean;

/**
 * @author BluYous
 * @version 1.0
 * @since 2018-02-19
 */
public class Event {
    private String eventId;
    private String label;
    private Integer year;
    
    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", label='" + label + '\'' +
                ", year=" + year +
                '}';
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
}
