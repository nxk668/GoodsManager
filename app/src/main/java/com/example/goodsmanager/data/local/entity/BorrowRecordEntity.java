package com.example.goodsmanager.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "borrow_records")
public class BorrowRecordEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "item_id", index = true)
    private long itemId;

    @ColumnInfo(name = "borrower_name")
    private String borrowerName;

    @ColumnInfo(name = "contact")
    private String contact;

    @ColumnInfo(name = "borrow_date")
    private Date borrowDate;

    @ColumnInfo(name = "expected_return")
    private Date expectedReturn;

    @ColumnInfo(name = "actual_return")
    private Date actualReturn;

    @ColumnInfo(name = "status")
    private String status;

    public BorrowRecordEntity(long itemId, String borrowerName) {
        this.itemId = itemId;
        this.borrowerName = borrowerName;
        this.borrowDate = new Date();
        this.status = "借出中";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(Date expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    public Date getActualReturn() {
        return actualReturn;
    }

    public void setActualReturn(Date actualReturn) {
        this.actualReturn = actualReturn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

