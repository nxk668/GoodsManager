package com.example.goodsmanager.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "items")
public class ItemEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "category")
    @NonNull
    private String category;

    @ColumnInfo(name = "location_zone")
    @NonNull
    private String locationZone;

    @ColumnInfo(name = "storage_detail")
    private String storageDetail;

    @ColumnInfo(name = "tags")
    private String tags;

    @ColumnInfo(name = "value")
    private double value;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "purchase_date")
    private Date purchaseDate;

    @ColumnInfo(name = "warranty_end")
    private Date warrantyEnd;

    @ColumnInfo(name = "is_favorite")
    private boolean favorite;

    @ColumnInfo(name = "photo_uri")
    private String photoUri;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "qr_payload")
    private String qrPayload;

    @ColumnInfo(name = "remind_timestamp")
    private long remindTimestamp;

    public ItemEntity(@NonNull String name,
                      @NonNull String category,
                      @NonNull String locationZone) {
        this.name = name;
        this.category = category;
        this.locationZone = locationZone;
        this.storageDetail = "";
        this.tags = "";
        this.quantity = 1;
        this.purchaseDate = new Date();
        this.favorite = false;
        this.value = 0d;
        this.note = "";
        this.qrPayload = "";
        this.remindTimestamp = 0L;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    @NonNull
    public String getLocationZone() {
        return locationZone;
    }

    public void setLocationZone(@NonNull String locationZone) {
        this.locationZone = locationZone;
    }

    public String getStorageDetail() {
        return storageDetail;
    }

    public void setStorageDetail(String storageDetail) {
        this.storageDetail = storageDetail;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getWarrantyEnd() {
        return warrantyEnd;
    }

    public void setWarrantyEnd(Date warrantyEnd) {
        this.warrantyEnd = warrantyEnd;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQrPayload() {
        return qrPayload;
    }

    public void setQrPayload(String qrPayload) {
        this.qrPayload = qrPayload;
    }

    public long getRemindTimestamp() {
        return remindTimestamp;
    }

    public void setRemindTimestamp(long remindTimestamp) {
        this.remindTimestamp = remindTimestamp;
    }
}

