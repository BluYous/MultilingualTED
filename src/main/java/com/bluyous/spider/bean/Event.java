package com.bluyous.spider.bean;


public class Event {
    private String id;
    private String label;
    private Integer year;
    
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", year=" + year +
                '}';
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
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
