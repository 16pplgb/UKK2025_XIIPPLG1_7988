package com.example.ukk_2025;

public class CategoriesModel {
    private String id;
    private String categories;

    public CategoriesModel(String id, String categories) {
        this.id = id;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}