package com.example.goodsmanager.ui.item;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.repository.ItemRepository;

import java.util.List;

public class ItemListViewModel extends AndroidViewModel {

    private final ItemRepository repository;
    private final MutableLiveData<String> keywordLiveData = new MutableLiveData<>("");
    private final LiveData<List<ItemEntity>> items;

    public ItemListViewModel(@NonNull Application application) {
        super(application);
        repository = ((GoodsManagerApp) application).getAppContainer().itemRepository;
        items = Transformations.switchMap(keywordLiveData, keyword -> {
            if (keyword == null || keyword.isEmpty()) {
                return repository.observeItems();
            } else {
                return repository.searchByKeyword(keyword);
            }
        });
    }

    public LiveData<List<ItemEntity>> getItems() {
        return items;
    }

    public void search(String keyword) {
        keywordLiveData.setValue(keyword);
    }
}

