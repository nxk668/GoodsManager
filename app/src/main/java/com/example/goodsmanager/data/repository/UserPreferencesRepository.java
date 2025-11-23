package com.example.goodsmanager.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserPreferencesRepository {

    private static final String PREFS_NAME = "goods_manager_prefs";
    private static final String KEY_DARK_MODE = "key_dark_mode";
    private static final String KEY_REMINDER = "key_reminder";

    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Boolean> darkModeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reminderLiveData = new MutableLiveData<>();

    public UserPreferencesRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        darkModeLiveData.setValue(sharedPreferences.getBoolean(KEY_DARK_MODE, false));
        reminderLiveData.setValue(sharedPreferences.getBoolean(KEY_REMINDER, true));
    }

    public LiveData<Boolean> observeDarkMode() {
        return darkModeLiveData;
    }

    public LiveData<Boolean> observeReminderEnabled() {
        return reminderLiveData;
    }

    public void setDarkMode(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        darkModeLiveData.postValue(enabled);
    }

    public void setReminderEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_REMINDER, enabled).apply();
        reminderLiveData.postValue(enabled);
    }
}

