package com.example.goodsmanager.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.core.Resource;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.model.StatisticsSnapshot;
import com.example.goodsmanager.data.repository.ItemRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final ItemRepository repository;
    private final LiveData<List<ItemEntity>> recentItems;
    private final LiveData<StatisticsSnapshot> statisticsSnapshot;
    private final LiveData<Resource<String>> tipLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = ((GoodsManagerApp) application).getAppContainer().itemRepository;
        recentItems = repository.observeRecentItems(5);
        statisticsSnapshot = repository.observeStatistics();
        tipLiveData = repository.observeTip();
    }

    public LiveData<List<ItemEntity>> getRecentItems() {
        return recentItems;
    }

    public LiveData<StatisticsSnapshot> getStatisticsSnapshot() {
        return statisticsSnapshot;
    }

    public LiveData<Resource<String>> getTipLiveData() {
        return tipLiveData;
    }

    public void refreshTip() {
        repository.refreshTip();
    }
}

