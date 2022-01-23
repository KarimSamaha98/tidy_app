package com.epfl.esl.tidy.ar;

public class Information {

    private String room;
    private String description;
    private String imageUrl;

    public Information() {

    }

    public Information(String email, String name) {
        this.room = room;
        this.description = description;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String name) {
        this.room = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
