package com.example.goodsmanager.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.goodsmanager.core.Resource;
import com.example.goodsmanager.data.local.dao.BorrowRecordDao;
import com.example.goodsmanager.data.local.dao.ItemDao;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.local.projection.CategoryCount;
import com.example.goodsmanager.data.model.StatisticsSnapshot;
import com.example.goodsmanager.network.TipResponse;
import com.example.goodsmanager.network.TipService;
import com.example.goodsmanager.session.SessionManager;
import com.example.goodsmanager.sync.LeanSyncManager;

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
    private final SessionManager sessionManager;
    private final LeanSyncManager syncManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Resource<String>> tipLiveData = new MutableLiveData<>();
    private final LiveData<String> ownerLiveData;

    public ItemRepository(ItemDao itemDao,
                          BorrowRecordDao borrowRecordDao,
                          TipService tipService,
                          SessionManager sessionManager,
                          LeanSyncManager syncManager) {
        this.itemDao = itemDao;
        this.borrowRecordDao = borrowRecordDao;
        this.tipService = tipService;
        this.sessionManager = sessionManager;
        this.syncManager = syncManager;
        this.ownerLiveData = sessionManager.observeUserId();
    }

    public LiveData<List<ItemEntity>> observeItems() {
        refreshTip();
        return Transformations.switchMap(ownerLiveData, ownerId -> ownerId == null
                ? emptyListLiveData()
                : itemDao.observeAll(ownerId));
    }

    public LiveData<List<ItemEntity>> observeRecentItems(int limit) {
        return Transformations.switchMap(ownerLiveData, ownerId -> ownerId == null
                ? emptyListLiveData()
                : itemDao.observeRecent(ownerId, limit));
    }

    public LiveData<ItemEntity> observeItem(long id) {
        return Transformations.switchMap(ownerLiveData, ownerId -> ownerId == null
                ? emptyItemLiveData()
                : itemDao.observeItem(id, ownerId));
    }

    public LiveData<List<ItemEntity>> searchByKeyword(String keyword) {
        return Transformations.switchMap(ownerLiveData, ownerId -> ownerId == null
                ? emptyListLiveData()
                : itemDao.searchByKeyword(ownerId, keyword));
    }

    public void insertItem(ItemEntity entity) {
        executor.execute(() -> {
            String ownerId = sessionManager.getCurrentUserId();
            if (ownerId == null) return;
            entity.setOwnerId(ownerId);
            entity.setPendingSync(true);
            entity.setSyncAction("ADD");
            entity.setSyncedAt(System.currentTimeMillis());
            itemDao.insert(entity);
            syncManager.sync(ownerId);
        });
    }

    public void updateItem(ItemEntity entity) {
        executor.execute(() -> {
            String ownerId = sessionManager.getCurrentUserId();
            if (ownerId == null) return;
            entity.setOwnerId(ownerId);
            entity.setPendingSync(true);
            entity.setSyncAction(entity.getRemoteId() == null ? "ADD" : "UPDATE");
            entity.setSyncedAt(System.currentTimeMillis());
            itemDao.update(entity);
            syncManager.sync(ownerId);
        });
    }

    public void deleteItem(ItemEntity entity) {
        executor.execute(() -> {
            String ownerId = sessionManager.getCurrentUserId();
            if (ownerId == null) return;
            entity.setOwnerId(ownerId);
            entity.setPendingSync(true);
            entity.setSyncAction("DELETE");
            entity.setSyncedAt(System.currentTimeMillis());
            itemDao.update(entity);
            syncManager.sync(ownerId);
        });
    }

    public LiveData<List<BorrowRecordEntity>> observeBorrowRecords() {
        return Transformations.switchMap(ownerLiveData, ownerId -> ownerId == null
                ? emptyBorrowLiveData()
                : borrowRecordDao.observeAll(ownerId));
    }

    public LiveData<List<BorrowRecordEntity>> observeBorrowRecords(long itemId) {
        return Transformations.switchMap(ownerLiveData, ownerId -> ownerId == null
                ? emptyBorrowLiveData()
                : borrowRecordDao.observeByItem(itemId, ownerId));
    }

    public void insertBorrowRecord(BorrowRecordEntity entity) {
        executor.execute(() -> {
            String ownerId = sessionManager.getCurrentUserId();
            if (ownerId == null) return;
            attachItemRemoteId(entity);
            entity.setOwnerId(ownerId);
            entity.setPendingSync(true);
            entity.setSyncAction("ADD");
            entity.setSyncedAt(System.currentTimeMillis());
            borrowRecordDao.insert(entity);
            syncManager.sync(ownerId);
        });
    }

    public void updateBorrowRecord(BorrowRecordEntity entity) {
        executor.execute(() -> {
            String ownerId = sessionManager.getCurrentUserId();
            if (ownerId == null) return;
            attachItemRemoteId(entity);
            entity.setOwnerId(ownerId);
            entity.setPendingSync(true);
            entity.setSyncAction(entity.getRemoteId() == null ? "ADD" : "UPDATE");
            entity.setSyncedAt(System.currentTimeMillis());
            borrowRecordDao.update(entity);
            syncManager.sync(ownerId);
        });
    }

    public List<BorrowRecordEntity> getDueRecords(Date deadline) {
        String ownerId = sessionManager.getCurrentUserId();
        if (ownerId == null) {
            return new ArrayList<>();
        }
        return borrowRecordDao.getDueRecords(deadline, ownerId);
    }

    public LiveData<StatisticsSnapshot> observeStatistics() {
        return Transformations.switchMap(ownerLiveData, ownerId -> {
            if (ownerId == null) {
                MutableLiveData<StatisticsSnapshot> empty = new MutableLiveData<>();
                empty.setValue(new StatisticsSnapshot(0, 0, 0d, new ArrayList<>()));
                return empty;
            }
            MediatorLiveData<StatisticsSnapshot> mediator = new MediatorLiveData<>();
            StatsHolder holder = new StatsHolder();
            mediator.addSource(itemDao.observeTotalCount(ownerId), value -> {
                holder.total = value == null ? 0 : value;
                mediator.postValue(holder.toSnapshot());
            });
            mediator.addSource(itemDao.observeFavoriteCount(ownerId), value -> {
                holder.favorites = value == null ? 0 : value;
                mediator.postValue(holder.toSnapshot());
            });
            mediator.addSource(itemDao.observeTotalValue(ownerId), value -> {
                holder.totalValue = value == null ? 0d : value;
                mediator.postValue(holder.toSnapshot());
            });
            mediator.addSource(itemDao.observeCategoryDistribution(ownerId), list -> {
                holder.categoryCounts = list;
                mediator.postValue(holder.toSnapshot());
            });
            return mediator;
        });
    }

    public LiveData<Resource<String>> observeTip() {
        return tipLiveData;
    }

    public LiveData<List<CategoryCount>> observeCategoryDistributionRaw() {
        return Transformations.switchMap(ownerLiveData, ownerId -> ownerId == null
                ? emptyCategoryLiveData()
                : itemDao.observeCategoryDistribution(ownerId));
    }

    public void triggerSync() {
        syncManager.sync(sessionManager.getCurrentUserId());
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

    private LiveData<List<ItemEntity>> emptyListLiveData() {
        MutableLiveData<List<ItemEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(new ArrayList<>());
        return liveData;
    }

    private LiveData<ItemEntity> emptyItemLiveData() {
        MutableLiveData<ItemEntity> data = new MutableLiveData<>();
        data.setValue(null);
        return data;
    }

    private LiveData<List<BorrowRecordEntity>> emptyBorrowLiveData() {
        MutableLiveData<List<BorrowRecordEntity>> data = new MutableLiveData<>();
        data.setValue(new ArrayList<>());
        return data;
    }

    private LiveData<List<CategoryCount>> emptyCategoryLiveData() {
        MutableLiveData<List<CategoryCount>> data = new MutableLiveData<>();
        data.setValue(new ArrayList<>());
        return data;
    }

    private void attachItemRemoteId(BorrowRecordEntity entity) {
        if (entity.getItemRemoteId() != null && !entity.getItemRemoteId().isEmpty()) {
            return;
        }
        ItemEntity itemEntity = itemDao.getItemSync(entity.getItemId());
        if (itemEntity != null) {
            entity.setItemRemoteId(itemEntity.getRemoteId());
        }
    }
}

