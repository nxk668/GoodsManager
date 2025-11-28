package com.example.goodsmanager.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.goodsmanager.data.local.converter.DateConverter;
import com.example.goodsmanager.data.local.dao.BorrowRecordDao;
import com.example.goodsmanager.data.local.dao.ItemDao;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.local.entity.ItemEntity;

@Database(entities = {ItemEntity.class, BorrowRecordEntity.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class GoodsDatabase extends RoomDatabase {

    private static volatile GoodsDatabase INSTANCE;

    public abstract ItemDao itemDao();

    public abstract BorrowRecordDao borrowRecordDao();

    public static GoodsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GoodsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    GoodsDatabase.class, "goods.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

