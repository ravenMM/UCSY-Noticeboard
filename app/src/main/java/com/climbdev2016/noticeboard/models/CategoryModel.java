package com.climbdev2016.noticeboard.models;

/**
 * Created by zwe on 5/8/17.
 */

public class CategoryModel {
    private String category;

    public CategoryModel(){

    }

    public CategoryModel(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
