package com.example.goodsmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface BorrowRecordDao {

    @Query("SELECT * FROM borrow_records WHERE owner_id = :ownerId AND sync_action != 'DELETE' ORDER BY borrow_date DESC")
    LiveData<List<BorrowRecordEntity>> observeAll(String ownerId);

    @Query("SELECT * FROM borrow_records WHERE item_id = :itemId AND owner_id = :ownerId AND sync_action != 'DELETE' ORDER BY borrow_date DESC")
    LiveData<List<BorrowRecordEntity>> observeByItem(long itemId, String ownerId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BorrowRecordEntity entity);

    @Update
    void update(BorrowRecordEntity entity);

    @Delete
    void delete(BorrowRecordEntity entity);

    @Query("SELECT * FROM borrow_records WHERE status = '借出中' AND expected_return IS NOT NULL AND expected_return < :deadline AND owner_id = :ownerId AND sync_action != 'DELETE'")
    List<BorrowRecordEntity> getDueRecords(Date deadline, String ownerId);

    @Query("SELECT * FROM borrow_records WHERE pending_sync = 1 AND owner_id = :ownerId")
    List<BorrowRecordEntity> getPendingRecords(String ownerId);

    @Query("SELECT * FROM borrow_records WHERE remote_id = :remoteId AND owner_id = :ownerId LIMIT 1")
    BorrowRecordEntity findByRemoteId(String remoteId, String ownerId);
}

