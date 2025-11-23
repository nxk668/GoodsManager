package com.example.goodsmanager.data.local.projection;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.local.entity.ItemEntity;

import java.util.List;

public class ItemWithBorrowRecords {

    @Embedded
    public ItemEntity item;

    @Relation(parentColumn = "id", entityColumn = "item_id")
    public List<BorrowRecordEntity> borrowRecords;
}

