package com.brilliant.game.launcher.model;

public class News {

    private final String image;
    private final String title;

    public News(String image, String title) {
        this.image = image;
        this.title = title;
    }

    public String getImageUrl() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
