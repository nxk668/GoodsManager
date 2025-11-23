package com.example.goodsmanager.ui.borrow;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.repository.ItemRepository;

import java.util.List;

public class BorrowManageViewModel extends AndroidViewModel {

    private final ItemRepository repository;

    public BorrowManageViewModel(@NonNull Application application) {
        super(application);
        repository = ((GoodsManagerApp) application).getAppContainer().itemRepository;
    }

    public LiveData<List<BorrowRecordEntity>> getRecords() {
        return repository.observeBorrowRecords();
    }

    public void markReturn(BorrowRecordEntity entity) {
        repository.updateBorrowRecord(entity);
    }
}

