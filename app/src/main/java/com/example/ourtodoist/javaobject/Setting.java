package com.example.ourtodoist.javaobject;

public class Setting {
    private final String name;
    private String value;
    private final int imageId;
    public Setting(String name, String value, int imageId){
        this.name = name;
        this.value = value;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public int getImageId() {
        return imageId;
    }
}
