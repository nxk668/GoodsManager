package com.example.goodsmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.local.projection.CategoryCount;
import com.example.goodsmanager.data.local.projection.ItemWithBorrowRecords;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE' ORDER BY is_favorite DESC, purchase_date DESC")
    LiveData<List<ItemEntity>> observeAll(String ownerId);

    @Query("SELECT * FROM items WHERE id = :itemId AND owner_id = :ownerId AND sync_action != 'DELETE' LIMIT 1")
    LiveData<ItemEntity> observeItem(long itemId, String ownerId);

    @Query("SELECT * FROM items WHERE id = :itemId LIMIT 1")
    ItemEntity getItemSync(long itemId);

    @Query("SELECT * FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE' AND (name LIKE '%' || :keyword || '%' OR tags LIKE '%' || :keyword || '%') ORDER BY purchase_date DESC")
    LiveData<List<ItemEntity>> searchByKeyword(String ownerId, String keyword);

    @Query("SELECT * FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE' ORDER BY purchase_date DESC LIMIT :limit")
    LiveData<List<ItemEntity>> observeRecent(String ownerId, int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ItemEntity entity);

    @Update
    void update(ItemEntity entity);

    @Delete
    void delete(ItemEntity entity);

    @Transaction
    @Query("SELECT * FROM items WHERE owner_id = :ownerId ORDER BY purchase_date DESC")
    LiveData<List<ItemWithBorrowRecords>> observeItemsWithBorrowRecords(String ownerId);

    @Query("SELECT SUM(value * quantity) FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE'")
    LiveData<Double> observeTotalValue(String ownerId);

    @Query("SELECT COUNT(*) FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE'")
    LiveData<Integer> observeTotalCount(String ownerId);

    @Query("SELECT COUNT(*) FROM items WHERE is_favorite = 1 AND owner_id = :ownerId AND sync_action != 'DELETE'")
    LiveData<Integer> observeFavoriteCount(String ownerId);

    @Query("SELECT category AS category, COUNT(*) AS count FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE' GROUP BY category")
    LiveData<List<CategoryCount>> observeCategoryDistribution(String ownerId);

    @Query("SELECT COUNT(*) FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE'")
    int getTotalCountSync(String ownerId);

    @Query("SELECT SUM(value * quantity) FROM items WHERE owner_id = :ownerId AND sync_action != 'DELETE'")
    double getTotalValueSync(String ownerId);

    @Query("SELECT * FROM items WHERE pending_sync = 1 AND owner_id = :ownerId")
    List<ItemEntity> getPendingItems(String ownerId);

    @Query("SELECT * FROM items WHERE remote_id = :remoteId AND owner_id = :ownerId LIMIT 1")
    ItemEntity findByRemoteId(String remoteId, String ownerId);
}

