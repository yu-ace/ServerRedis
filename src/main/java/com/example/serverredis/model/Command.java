package com.example.serverredis.model;

public class Command {
    String name;
    String key;
    Object value;

    public Command() {
    }

    public Command(String name, String key, Object value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
