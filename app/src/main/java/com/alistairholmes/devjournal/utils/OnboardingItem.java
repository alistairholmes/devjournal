package com.alistairholmes.devjournal.utils;

public class OnboardingItem {

    int imageID;
    String header;
    String footer;

    public OnboardingItem() {
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getHeader() {
        return header;
    }

    public void setTitle(String title) {
        this.header = title;
    }

    public String getFooter() {
        return footer;
    }

    public void setDescription(String description) {
        this.footer = description;
    }

}
