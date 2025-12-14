package com.example.wishlistapp;

public class Gift {
    private String imageUrl;
    private String name;
    private double price;
    private boolean isSelected;

    public Gift(String imageUrl, String name, double price) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.isSelected = false;
    }

    public String getImageUrl() { return imageUrl; }

    public String getName() { return name; }

    public double getPrice() { return price; }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}