package com.example.goodsmanager.sync;

import android.util.Log;

import com.example.goodsmanager.data.local.dao.BorrowRecordDao;
import com.example.goodsmanager.data.local.dao.ItemDao;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.local.entity.ItemEntity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;

public class LeanSyncManager {

    private static final String TAG = "LeanSyncManager";

    private final ItemDao itemDao;
    private final BorrowRecordDao borrowRecordDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LeanSyncManager(ItemDao itemDao, BorrowRecordDao borrowRecordDao) {
        this.itemDao = itemDao;
        this.borrowRecordDao = borrowRecordDao;
    }

    public void sync(String ownerId) {
        if (ownerId == null || ownerId.isEmpty()) {
            return;
        }
        executor.execute(() -> {
            pushItems(ownerId);
            pushBorrowRecords(ownerId);
            pullItems(ownerId);
            pullBorrowRecords(ownerId);
        });
    }

    private void pushItems(String ownerId) {
        List<ItemEntity> pending = itemDao.getPendingItems(ownerId);
        for (ItemEntity entity : pending) {
            try {
                if ("DELETE".equals(entity.getSyncAction())) {
                    if (entity.getRemoteId() != null) {
                        LCObject remote = LCObject.createWithoutData("Item", entity.getRemoteId());
                        remote.delete();
                    }
                    itemDao.delete(entity);
                } else {
                    LCObject remote = entity.getRemoteId() == null
                            ? new LCObject("Item")
                            : LCObject.createWithoutData("Item", entity.getRemoteId());
                    remote.put("ownerId", ownerId);
                    remote.put("name", entity.getName());
                    remote.put("category", entity.getCategory());
                    remote.put("locationZone", entity.getLocationZone());
                    remote.put("storageDetail", entity.getStorageDetail());
                    remote.put("tags", entity.getTags());
                    remote.put("value", entity.getValue());
                    remote.put("quantity", entity.getQuantity());
                    remote.put("favorite", entity.isFavorite());
                    remote.put("note", entity.getNote());
                    remote.put("qrPayload", entity.getQrPayload());
                    remote.put("remindTimestamp", entity.getRemindTimestamp());
                    remote.put("purchaseDate", entity.getPurchaseDate());
                    remote.put("warrantyEnd", entity.getWarrantyEnd());
                    remote.put("photoUri", entity.getPhotoUri());
                    remote.save();
                    entity.setRemoteId(remote.getObjectId());
                    entity.setPendingSync(false);
                    entity.setSyncedAt(remote.getUpdatedAt() != null ? remote.getUpdatedAt().getTime() : System.currentTimeMillis());
                    entity.setSyncAction("SYNCED");
                    itemDao.update(entity);
                }
            } catch (Exception e) {
                Log.e(TAG, "pushItems: ", e);
            }
        }
    }

    private void pushBorrowRecords(String ownerId) {
        List<BorrowRecordEntity> pending = borrowRecordDao.getPendingRecords(ownerId);
        for (BorrowRecordEntity entity : pending) {
            try {
                if ("DELETE".equals(entity.getSyncAction())) {
                    if (entity.getRemoteId() != null) {
                        LCObject remote = LCObject.createWithoutData("BorrowRecord", entity.getRemoteId());
                        remote.delete();
                    }
                    borrowRecordDao.delete(entity);
                } else {
                    LCObject remote = entity.getRemoteId() == null
                            ? new LCObject("BorrowRecord")
                            : LCObject.createWithoutData("BorrowRecord", entity.getRemoteId());
                    remote.put("ownerId", ownerId);
                    remote.put("itemId", entity.getItemId());
                    remote.put("itemRemoteId", resolveItemRemoteId(entity));
                    remote.put("borrowerName", entity.getBorrowerName());
                    remote.put("contact", entity.getContact());
                    remote.put("borrowDate", entity.getBorrowDate());
                    remote.put("expectedReturn", entity.getExpectedReturn());
                    remote.put("actualReturn", entity.getActualReturn());
                    remote.put("status", entity.getStatus());
                    remote.save();
                    entity.setRemoteId(remote.getObjectId());
                    entity.setPendingSync(false);
                    entity.setSyncedAt(remote.getUpdatedAt() != null ? remote.getUpdatedAt().getTime() : System.currentTimeMillis());
                    entity.setSyncAction("SYNCED");
                    borrowRecordDao.update(entity);
                }
            } catch (Exception e) {
                Log.e(TAG, "pushBorrowRecords: ", e);
            }
        }
    }

    private void pullItems(String ownerId) {
        try {
            LCQuery<LCObject> query = new LCQuery<>("Item");
            query.whereEqualTo("ownerId", ownerId);
            List<LCObject> remoteItems = query.find();
            for (LCObject remote : remoteItems) {
                String remoteId = remote.getObjectId();
                ItemEntity local = itemDao.findByRemoteId(remoteId, ownerId);
                long remoteUpdated = remote.getUpdatedAt() != null ? remote.getUpdatedAt().getTime() : System.currentTimeMillis();
                if (local != null && "DELETE".equals(local.getSyncAction())) {
                    continue;
                }
                if (local == null) {
                    ItemEntity entity = new ItemEntity(
                            remote.getString("name"),
                            remote.getString("category"),
                            remote.getString("locationZone")
                    );
                    entity.setOwnerId(ownerId);
                    entity.setRemoteId(remoteId);
                    entity.setStorageDetail(remote.getString("storageDetail"));
                    entity.setTags(remote.getString("tags"));
                    entity.setValue(remote.getNumber("value") == null ? 0d : remote.getNumber("value").doubleValue());
                    entity.setQuantity(remote.getNumber("quantity") == null ? 1 : remote.getNumber("quantity").intValue());
                    entity.setFavorite(remote.getBoolean("favorite"));
                    entity.setNote(remote.getString("note"));
                    entity.setQrPayload(remote.getString("qrPayload"));
                    entity.setRemindTimestamp(remote.getNumber("remindTimestamp") == null ? 0L : remote.getNumber("remindTimestamp").longValue());
                    entity.setPurchaseDate(remote.getDate("purchaseDate"));
                    entity.setWarrantyEnd(remote.getDate("warrantyEnd"));
                    entity.setPhotoUri(remote.getString("photoUri"));
                    entity.setPendingSync(false);
                    entity.setSyncAction("SYNCED");
                    entity.setSyncedAt(remoteUpdated);
                    itemDao.insert(entity);
                } else if (remoteUpdated > local.getSyncedAt()) {
                    local.setName(remote.getString("name"));
                    local.setCategory(remote.getString("category"));
                    local.setLocationZone(remote.getString("locationZone"));
                    local.setStorageDetail(remote.getString("storageDetail"));
                    local.setTags(remote.getString("tags"));
                    local.setValue(remote.getNumber("value") == null ? 0d : remote.getNumber("value").doubleValue());
                    local.setQuantity(remote.getNumber("quantity") == null ? 1 : remote.getNumber("quantity").intValue());
                    local.setFavorite(remote.getBoolean("favorite"));
                    local.setNote(remote.getString("note"));
                    local.setQrPayload(remote.getString("qrPayload"));
                    local.setRemindTimestamp(remote.getNumber("remindTimestamp") == null ? 0L : remote.getNumber("remindTimestamp").longValue());
                    local.setPurchaseDate(remote.getDate("purchaseDate"));
                    local.setWarrantyEnd(remote.getDate("warrantyEnd"));
                    local.setPhotoUri(remote.getString("photoUri"));
                    local.setPendingSync(false);
                    local.setSyncAction("SYNCED");
                    local.setSyncedAt(remoteUpdated);
                    itemDao.update(local);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "pullItems: ", e);
        }
    }

    private void pullBorrowRecords(String ownerId) {
        try {
            LCQuery<LCObject> query = new LCQuery<>("BorrowRecord");
            query.whereEqualTo("ownerId", ownerId);
            List<LCObject> remoteRecords = query.find();
            for (LCObject remote : remoteRecords) {
                String remoteId = remote.getObjectId();
                BorrowRecordEntity local = borrowRecordDao.findByRemoteId(remoteId, ownerId);
                long remoteUpdated = remote.getUpdatedAt() != null ? remote.getUpdatedAt().getTime() : System.currentTimeMillis();
                if (local != null && "DELETE".equals(local.getSyncAction())) {
                    continue;
                }
                if (local == null) {
                    Number itemNumber = remote.getNumber("itemId");
                    long localItemId = itemNumber == null ? 0L : itemNumber.longValue();
                    BorrowRecordEntity entity = new BorrowRecordEntity(localItemId, remote.getString("borrowerName"));
                    entity.setOwnerId(ownerId);
                    entity.setRemoteId(remoteId);
                    String remoteItemId = remote.getString("itemRemoteId");
                    entity.setItemRemoteId(remoteItemId);
                    if (remoteItemId != null) {
                        ItemEntity localItem = itemDao.findByRemoteId(remoteItemId, ownerId);
                        if (localItem != null) {
                            entity.setItemId(localItem.getId());
                        }
                    }
                    entity.setContact(remote.getString("contact"));
                    entity.setBorrowDate(remote.getDate("borrowDate"));
                    entity.setExpectedReturn(remote.getDate("expectedReturn"));
                    entity.setActualReturn(remote.getDate("actualReturn"));
                    entity.setStatus(remote.getString("status"));
                    entity.setPendingSync(false);
                    entity.setSyncedAt(remoteUpdated);
                    entity.setSyncAction("SYNCED");
                    borrowRecordDao.insert(entity);
                } else if (remoteUpdated > local.getSyncedAt()) {
                    local.setBorrowerName(remote.getString("borrowerName"));
                    local.setContact(remote.getString("contact"));
                    local.setBorrowDate(remote.getDate("borrowDate"));
                    local.setExpectedReturn(remote.getDate("expectedReturn"));
                    local.setActualReturn(remote.getDate("actualReturn"));
                    local.setStatus(remote.getString("status"));
                    local.setPendingSync(false);
                    local.setSyncedAt(remoteUpdated);
                    local.setSyncAction("SYNCED");
                    String remoteItemId = remote.getString("itemRemoteId");
                    local.setItemRemoteId(remoteItemId);
                    if (remoteItemId != null) {
                        ItemEntity localItem = itemDao.findByRemoteId(remoteItemId, ownerId);
                        if (localItem != null) {
                            local.setItemId(localItem.getId());
                        }
                    }
                    borrowRecordDao.update(local);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "pullBorrowRecords: ", e);
        }
    }

    private String resolveItemRemoteId(BorrowRecordEntity entity) {
        if (entity.getItemRemoteId() != null && !entity.getItemRemoteId().isEmpty()) {
            return entity.getItemRemoteId();
        }
        ItemEntity local = itemDao.getItemSync(entity.getItemId());
        if (local != null && local.getRemoteId() != null) {
            entity.setItemRemoteId(local.getRemoteId());
            borrowRecordDao.update(entity);
            return local.getRemoteId();
        }
        return null;
    }
}

