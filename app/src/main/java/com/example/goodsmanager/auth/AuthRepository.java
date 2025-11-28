package com.example.goodsmanager.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.goodsmanager.core.Resource;
import com.example.goodsmanager.session.SessionManager;

import cn.leancloud.LCException;
import cn.leancloud.LCUser;

public class AuthRepository {

    private final SessionManager sessionManager;

    public AuthRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public LiveData<Resource<Boolean>> login(String username, String password) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        new Thread(() -> {
            try {
                LCUser user = LCUser.logIn(username, password).blockingFirst();
                if (user != null) {
                    sessionManager.updateSession(user.getObjectId(), user.getUsername());
                    result.postValue(Resource.success(true));
                } else {
                    result.postValue(Resource.error("登录失败", false));
                }
            } catch (Exception e) {
                result.postValue(Resource.error(extractMessage(e), false));
            }
        }).start();
        return result;
    }

    public LiveData<Resource<Boolean>> register(String username, String password) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading(null));
        new Thread(() -> {
            try {
                LCUser user = new LCUser();
                user.setUsername(username);
                user.setPassword(password);
                user.signUp();
                sessionManager.updateSession(user.getObjectId(), user.getUsername());
                result.postValue(Resource.success(true));
            } catch (Exception e) {
                result.postValue(Resource.error(extractMessage(e), false));
            }
        }).start();
        return result;
    }

    public void logout() {
        new Thread(() -> {
            LCUser.logOut();
            sessionManager.clear();
        }).start();
    }

    private String extractMessage(Exception e) {
        if (e instanceof LCException) {
            return e.getMessage();
        }
        Throwable cause = e.getCause();
        if (cause instanceof LCException) {
            return cause.getMessage();
        }
        return e.getMessage() != null ? e.getMessage() : "网络异常";
    }
}

