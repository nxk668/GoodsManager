package com.example.goodsmanager.ui.statistics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.data.local.projection.CategoryCount;
import com.example.goodsmanager.data.model.StatisticsSnapshot;
import com.example.goodsmanager.data.repository.ItemRepository;

import java.util.List;

public class StatisticsViewModel extends AndroidViewModel {

    private final ItemRepository repository;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        repository = ((GoodsManagerApp) application).getAppContainer().itemRepository;
    }

    public LiveData<StatisticsSnapshot> observeStatistics() {
        return repository.observeStatistics();
    }

    public LiveData<List<CategoryCount>> observeCategories() {
        return repository.observeCategoryDistributionRaw();
    }
}

