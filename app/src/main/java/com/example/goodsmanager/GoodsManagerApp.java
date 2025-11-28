package com.example.goodsmanager;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.goodsmanager.di.AppContainer;
import com.example.goodsmanager.reminder.BorrowReminderScheduler;
import com.example.goodsmanager.sync.LeanSyncWorker;

import java.util.concurrent.TimeUnit;

import cn.leancloud.LeanCloud;

public class GoodsManagerApp extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        LeanCloud.initialize(this,
                "Vkowx6YqbvxZcsockOZ9hvGA-gzGzoHsz",
                "yFIclVnVMsAKoMgytdPxouke",
                "https://vkowx6yq.lc-cn-n1-shared.com");
        appContainer = new AppContainer(this);
        BorrowReminderScheduler.scheduleDailyReminder(this);
        scheduleSyncWorker();
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }

    private void scheduleSyncWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(LeanSyncWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "lean_sync",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
        );
    }
}

