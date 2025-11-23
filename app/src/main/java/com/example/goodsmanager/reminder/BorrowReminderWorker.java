package com.example.goodsmanager.reminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.goodsmanager.R;
import com.example.goodsmanager.data.local.GoodsDatabase;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.utils.NotificationHelper;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BorrowReminderWorker extends Worker {

    public BorrowReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        GoodsDatabase database = GoodsDatabase.getInstance(getApplicationContext());
        Date deadline = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        List<BorrowRecordEntity> dueRecords = database.borrowRecordDao().getDueRecords(deadline);
        if (dueRecords != null && !dueRecords.isEmpty()) {
            int count = dueRecords.size();
            String content = getApplicationContext().getString(R.string.notification_due_borrow, count);
            NotificationHelper.showReminderNotification(getApplicationContext(),
                    getApplicationContext().getString(R.string.notification_title),
                    content);
        }
        return Result.success();
    }
}

