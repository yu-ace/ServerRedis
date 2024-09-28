package com.example.serverredis.model;

import java.io.Serializable;

public class Record implements Serializable {
    Integer position;
    Integer length;
    String type;

    public Record() {
    }

    public Record(Integer position, Integer length, String type) {
        this.position = position;
        this.length = length;
        this.type = type;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
