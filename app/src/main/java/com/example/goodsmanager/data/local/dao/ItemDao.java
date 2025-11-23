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

    @Query("SELECT * FROM items ORDER BY is_favorite DESC, purchase_date DESC")
    LiveData<List<ItemEntity>> observeAll();

    @Query("SELECT * FROM items WHERE id = :itemId LIMIT 1")
    LiveData<ItemEntity> observeItem(long itemId);

    @Query("SELECT * FROM items WHERE id = :itemId LIMIT 1")
    ItemEntity getItemSync(long itemId);

    @Query("SELECT * FROM items WHERE name LIKE '%' || :keyword || '%' OR tags LIKE '%' || :keyword || '%' ORDER BY purchase_date DESC")
    LiveData<List<ItemEntity>> searchByKeyword(String keyword);

    @Query("SELECT * FROM items ORDER BY purchase_date DESC LIMIT :limit")
    LiveData<List<ItemEntity>> observeRecent(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ItemEntity entity);

    @Update
    void update(ItemEntity entity);

    @Delete
    void delete(ItemEntity entity);

    @Transaction
    @Query("SELECT * FROM items ORDER BY purchase_date DESC")
    LiveData<List<ItemWithBorrowRecords>> observeItemsWithBorrowRecords();

    @Query("SELECT SUM(value * quantity) FROM items")
    LiveData<Double> observeTotalValue();

    @Query("SELECT COUNT(*) FROM items")
    LiveData<Integer> observeTotalCount();

    @Query("SELECT COUNT(*) FROM items WHERE is_favorite = 1")
    LiveData<Integer> observeFavoriteCount();

    @Query("SELECT category AS category, COUNT(*) AS count FROM items GROUP BY category")
    LiveData<List<CategoryCount>> observeCategoryDistribution();

    @Query("SELECT COUNT(*) FROM items")
    int getTotalCountSync();

    @Query("SELECT SUM(value * quantity) FROM items")
    double getTotalValueSync();
}

