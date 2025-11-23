package com.example.goodsmanager.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.goodsmanager.core.Resource;
import com.example.goodsmanager.data.local.dao.BorrowRecordDao;
import com.example.goodsmanager.data.local.dao.ItemDao;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.local.projection.CategoryCount;
import com.example.goodsmanager.data.model.StatisticsSnapshot;
import com.example.goodsmanager.network.TipResponse;
import com.example.goodsmanager.network.TipService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemRepository {

    private final ItemDao itemDao;
    private final BorrowRecordDao borrowRecordDao;
    private final TipService tipService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Resource<String>> tipLiveData = new MutableLiveData<>();

    public ItemRepository(ItemDao itemDao, BorrowRecordDao borrowRecordDao, TipService tipService) {
        this.itemDao = itemDao;
        this.borrowRecordDao = borrowRecordDao;
        this.tipService = tipService;
    }

    public LiveData<List<ItemEntity>> observeItems() {
        refreshTip();
        return itemDao.observeAll();
    }

    public LiveData<List<ItemEntity>> observeRecentItems(int limit) {
        return itemDao.observeRecent(limit);
    }

    public LiveData<ItemEntity> observeItem(long id) {
        return itemDao.observeItem(id);
    }

    public LiveData<List<ItemEntity>> searchByKeyword(String keyword) {
        return itemDao.searchByKeyword(keyword);
    }

    public void insertItem(ItemEntity entity) {
        executor.execute(() -> itemDao.insert(entity));
    }

    public void updateItem(ItemEntity entity) {
        executor.execute(() -> itemDao.update(entity));
    }

    public void deleteItem(ItemEntity entity) {
        executor.execute(() -> itemDao.delete(entity));
    }

    public LiveData<List<BorrowRecordEntity>> observeBorrowRecords() {
        return borrowRecordDao.observeAll();
    }

    public LiveData<List<BorrowRecordEntity>> observeBorrowRecords(long itemId) {
        return borrowRecordDao.observeByItem(itemId);
    }

    public void insertBorrowRecord(BorrowRecordEntity entity) {
        executor.execute(() -> borrowRecordDao.insert(entity));
    }

    public void updateBorrowRecord(BorrowRecordEntity entity) {
        executor.execute(() -> borrowRecordDao.update(entity));
    }

    public List<BorrowRecordEntity> getDueRecords(Date deadline) {
        return borrowRecordDao.getDueRecords(deadline);
    }

    public LiveData<StatisticsSnapshot> observeStatistics() {
        MediatorLiveData<StatisticsSnapshot> mediator = new MediatorLiveData<>();
        StatsHolder holder = new StatsHolder();

        mediator.addSource(itemDao.observeTotalCount(), value -> {
            holder.total = value == null ? 0 : value;
            mediator.postValue(holder.toSnapshot());
        });
        mediator.addSource(itemDao.observeFavoriteCount(), value -> {
            holder.favorites = value == null ? 0 : value;
            mediator.postValue(holder.toSnapshot());
        });
        mediator.addSource(itemDao.observeTotalValue(), value -> {
            holder.totalValue = value == null ? 0d : value;
            mediator.postValue(holder.toSnapshot());
        });
        mediator.addSource(itemDao.observeCategoryDistribution(), list -> {
            holder.categoryCounts = list;
            mediator.postValue(holder.toSnapshot());
        });
        return mediator;
    }

    public LiveData<Resource<String>> observeTip() {
        return tipLiveData;
    }

    public LiveData<List<CategoryCount>> observeCategoryDistributionRaw() {
        return itemDao.observeCategoryDistribution();
    }

    public void refreshTip() {
        tipLiveData.postValue(Resource.loading(null));
        tipService.fetchTip().enqueue(new Callback<TipResponse>() {
            @Override
            public void onResponse(Call<TipResponse> call, Response<TipResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSlip() != null) {
                    tipLiveData.postValue(Resource.success(response.body().getSlip().getAdvice()));
                } else {
                    tipLiveData.postValue(Resource.error("暂无灵感，稍后再试", null));
                }
            }

            @Override
            public void onFailure(Call<TipResponse> call, Throwable t) {
                tipLiveData.postValue(Resource.error("网络异常：" + t.getMessage(), null));
            }
        });
    }

    private static class StatsHolder {
        int total = 0;
        int favorites = 0;
        double totalValue = 0d;
        List<CategoryCount> categoryCounts = new ArrayList<>();

        StatisticsSnapshot toSnapshot() {
            List<String> tops = new ArrayList<>();
            if (categoryCounts != null) {
                for (CategoryCount count : categoryCounts) {
                    tops.add(count.getCategory() + " · " + count.getCount());
                }
            }
            return new StatisticsSnapshot(total, favorites, totalValue, tops);
        }
    }
}

