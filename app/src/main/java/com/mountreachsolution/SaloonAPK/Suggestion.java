package com.mountreachsolution.SaloonAPK;

public class Suggestion {
    private final String title;
    private final int imageDrawableId;

    public Suggestion(String title, int imageDrawableId) {
        this.title = title;
        this.imageDrawableId = imageDrawableId;
    }

    public String getTitle() { return title; }
    public int getImageDrawableId() { return imageDrawableId; }
}