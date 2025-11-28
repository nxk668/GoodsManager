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

    @ColumnInfo(name = "item_remote_id")
    private String itemRemoteId;

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

    @ColumnInfo(name = "owner_id")
    private String ownerId;

    @ColumnInfo(name = "remote_id")
    private String remoteId;

    @ColumnInfo(name = "pending_sync")
    private boolean pendingSync;

    @ColumnInfo(name = "sync_action")
    private String syncAction;

    @ColumnInfo(name = "synced_at")
    private long syncedAt;

    public BorrowRecordEntity(long itemId, String borrowerName) {
        this.itemId = itemId;
        this.borrowerName = borrowerName;
        this.borrowDate = new Date();
        this.status = "借出中";
        this.ownerId = "";
        this.pendingSync = true;
        this.syncAction = "ADD";
        this.syncedAt = 0L;
        this.itemRemoteId = "";
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

    public String getItemRemoteId() {
        return itemRemoteId;
    }

    public void setItemRemoteId(String itemRemoteId) {
        this.itemRemoteId = itemRemoteId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public boolean isPendingSync() {
        return pendingSync;
    }

    public void setPendingSync(boolean pendingSync) {
        this.pendingSync = pendingSync;
    }

    public String getSyncAction() {
        return syncAction;
    }

    public void setSyncAction(String syncAction) {
        this.syncAction = syncAction;
    }

    public long getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(long syncedAt) {
        this.syncedAt = syncedAt;
    }
}

