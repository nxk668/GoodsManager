package com.example.goodsmanager.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.auth.AuthActivity;
import com.example.goodsmanager.session.SessionManager;
import com.example.goodsmanager.ui.main.MainActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = ((GoodsManagerApp) getApplication()).getAppContainer().sessionManager;
        String userId = sessionManager.getCurrentUserId();
        if (userId == null) {
            startActivity(new Intent(this, AuthActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}

