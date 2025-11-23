package com.example.goodsmanager.ui.item;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.repository.ItemRepository;

public class ItemEditViewModel extends AndroidViewModel {

    private final ItemRepository repository;

    public ItemEditViewModel(@NonNull Application application) {
        super(application);
        repository = ((GoodsManagerApp) application).getAppContainer().itemRepository;
    }

    public LiveData<ItemEntity> loadItem(long id) {
        return repository.observeItem(id);
    }

    public void save(ItemEntity entity) {
        if (entity.getId() == 0) {
            repository.insertItem(entity);
        } else {
            repository.updateItem(entity);
        }
    }

    public void delete(ItemEntity entity) {
        repository.deleteItem(entity);
    }
}

