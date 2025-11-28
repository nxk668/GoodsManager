package com.example.goodsmanager.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SessionManager {

    private static final String PREF_NAME = "gm_session";
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_USERNAME = "key_username";

    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<String> userIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> usernameLiveData = new MutableLiveData<>();

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userIdLiveData.setValue(sharedPreferences.getString(KEY_USER_ID, null));
        usernameLiveData.setValue(sharedPreferences.getString(KEY_USERNAME, null));
    }

    public void updateSession(String userId, String username) {
        sharedPreferences.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .apply();
        userIdLiveData.postValue(userId);
        usernameLiveData.postValue(username);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
        userIdLiveData.postValue(null);
        usernameLiveData.postValue(null);
    }

    public LiveData<String> observeUserId() {
        return userIdLiveData;
    }

    public LiveData<String> observeUsername() {
        return usernameLiveData;
    }

    public String getCurrentUserId() {
        return userIdLiveData.getValue();
    }

    public String getCurrentUsername() {
        return usernameLiveData.getValue();
    }
}

