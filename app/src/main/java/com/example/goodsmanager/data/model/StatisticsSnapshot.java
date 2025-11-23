package com.example.goodsmanager.data.model;

import java.util.List;

public class StatisticsSnapshot {
    private final int totalItems;
    private final int favorites;
    private final double totalValue;
    private final List<String> topCategories;

    public StatisticsSnapshot(int totalItems, int favorites, double totalValue, List<String> topCategories) {
        this.totalItems = totalItems;
        this.favorites = favorites;
        this.totalValue = totalValue;
        this.topCategories = topCategories;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getFavorites() {
        return favorites;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public List<String> getTopCategories() {
        return topCategories;
    }
}

