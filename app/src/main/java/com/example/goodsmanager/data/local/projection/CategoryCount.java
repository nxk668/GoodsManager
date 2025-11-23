package com.example.goodsmanager.data.local.projection;

import androidx.annotation.NonNull;

public class CategoryCount {

    private String category;
    private int count;

    public CategoryCount(@NonNull String category, int count) {
        this.category = category;
        this.count = count;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

