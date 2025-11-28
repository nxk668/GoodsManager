package com.example.goodsmanager.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.auth.AuthRepository;
import com.example.goodsmanager.data.repository.UserPreferencesRepository;

public class SettingsViewModel extends AndroidViewModel {

    private final UserPreferencesRepository repository;
    private final AuthRepository authRepository;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        GoodsManagerApp app = (GoodsManagerApp) application;
        repository = app.getAppContainer().preferencesRepository;
        authRepository = app.getAppContainer().authRepository;
    }

    public LiveData<Boolean> observeDarkMode() {
        return repository.observeDarkMode();
    }

    public LiveData<Boolean> observeReminder() {
        return repository.observeReminderEnabled();
    }

    public void setDarkMode(boolean enabled) {
        repository.setDarkMode(enabled);
    }

    public void setReminder(boolean enabled) {
        repository.setReminderEnabled(enabled);
    }

    public void logout() {
        authRepository.logout();
    }
}

