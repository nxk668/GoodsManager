package com.example.goodsmanager.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.example.goodsmanager.databinding.ActivitySettingsBinding;
import com.example.goodsmanager.ui.LauncherActivity;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.setDarkMode(isChecked);
            }
        });
        binding.switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                viewModel.setReminder(isChecked);
            }
        });
        binding.buttonLogout.setOnClickListener(v -> {
            viewModel.logout();
            Intent intent = new Intent(this, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });

        viewModel.observeDarkMode().observe(this, enabled -> {
            boolean value = Boolean.TRUE.equals(enabled);
            if (binding.switchDarkMode.isChecked() != value) {
                binding.switchDarkMode.setChecked(value);
            }
            AppCompatDelegate.setDefaultNightMode(value ?
                    AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO);
        });

        viewModel.observeReminder().observe(this, enabled -> {
            boolean value = Boolean.TRUE.equals(enabled);
            if (binding.switchReminder.isChecked() != value) {
                binding.switchReminder.setChecked(value);
            }
        });
    }
}

