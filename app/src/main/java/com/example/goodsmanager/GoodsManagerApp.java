package com.example.goodsmanager;

import android.app.Application;

import com.example.goodsmanager.di.AppContainer;
import com.example.goodsmanager.reminder.BorrowReminderScheduler;

public class GoodsManagerApp extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer(this);
        BorrowReminderScheduler.scheduleDailyReminder(this);
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}

