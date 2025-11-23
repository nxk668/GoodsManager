package com.example.goodsmanager.ui.item;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.repository.ItemRepository;

import java.util.List;

public class ItemDetailViewModel extends AndroidViewModel {

    private final ItemRepository repository;

    public ItemDetailViewModel(@NonNull Application application) {
        super(application);
        repository = ((GoodsManagerApp) application).getAppContainer().itemRepository;
    }

    public LiveData<ItemEntity> observeItem(long id) {
        return repository.observeItem(id);
    }

    public LiveData<List<BorrowRecordEntity>> observeBorrowRecords(long itemId) {
        return repository.observeBorrowRecords(itemId);
    }

    public void saveBorrowRecord(BorrowRecordEntity entity) {
        repository.insertBorrowRecord(entity);
    }

    public void markReturned(BorrowRecordEntity entity) {
        entity.setStatus("已归还");
        entity.setActualReturn(new java.util.Date());
        repository.updateBorrowRecord(entity);
    }
}

