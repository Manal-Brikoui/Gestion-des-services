package com.example.services_project.model;

import java.io.Serializable;

public class Service implements Serializable {

    private int id;
    private String category;
    private String title;
    private String description;
    private int imageResId;
    private String imageUri;
    private String location;
    private String price;
    private String moreDetails;
    private int userId; // ID de l'utilisateur

    public Service(int id, String category, String title, String description,
                   int imageResId, String imageUri,
                   String location, String price, String moreDetails,
                   int userId) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.imageUri = imageUri;
        this.location = location;
        this.price = price;
        this.moreDetails = moreDetails;
        this.userId = userId;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public String getImageUri() { return imageUri; }
    public String getLocation() { return location; }
    public String getPrice() { return price; }
    public String getMoreDetails() { return moreDetails; }
    public int getUserId() { return userId; }
}
