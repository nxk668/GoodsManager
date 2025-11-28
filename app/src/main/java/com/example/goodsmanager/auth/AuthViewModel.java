package com.example.goodsmanager.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.core.Resource;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = ((GoodsManagerApp) application).getAppContainer().authRepository;
    }

    public LiveData<Resource<Boolean>> login(String username, String password) {
        return repository.login(username, password);
    }

    public LiveData<Resource<Boolean>> register(String username, String password) {
        return repository.register(username, password);
    }

    public void logout() {
        repository.logout();
    }
}

