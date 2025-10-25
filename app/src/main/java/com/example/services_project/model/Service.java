package com.example.services_project.model;

public class Service {
    private String category;
    private String title;
    private String description;
    private int iconResId; // ID de ressource drawable pour l'image

    public Service(String category, String title, String description, int iconResId) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
    }

    // Getters
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }

    // Optionnel: Setters, toString(), etc.
}