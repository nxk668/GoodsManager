package com.example.goodsmanager.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.session.SessionManager;

public class LeanSyncWorker extends Worker {

    public LeanSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        GoodsManagerApp app = (GoodsManagerApp) getApplicationContext();
        SessionManager sessionManager = app.getAppContainer().sessionManager;
        String ownerId = sessionManager.getCurrentUserId();
        if (ownerId == null) {
            return Result.success();
        }
        app.getAppContainer().syncManager.sync(ownerId);
        return Result.success();
    }
}

