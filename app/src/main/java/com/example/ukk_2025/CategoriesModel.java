package com.example.ukk_2025;

public class CategoriesModel {
    private String id;
    private String Categories;

    public CategoriesModel(String id, String categories) {
        this.id = id;
        this.Categories = Categories;
    }

    public String getId() {
        return id;
    }

    public String getKategori() {
        return Categories;
    }

    public void setKategori(String categories) {
        this.Categories = categories;
    }
}