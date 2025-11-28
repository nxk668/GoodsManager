package com.example.goodsmanager.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.goodsmanager.R;
import com.example.goodsmanager.core.Resource;
import com.example.goodsmanager.databinding.ActivityAuthBinding;
import com.example.goodsmanager.ui.main.MainActivity;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private AuthViewModel viewModel;
    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        setupListeners();
        switchMode(false);
    }

    private void setupListeners() {
        binding.buttonSubmit.setOnClickListener(v -> handleSubmit());
        binding.textSwitchMode.setOnClickListener(v -> switchMode(!isRegisterMode));
    }

    private void switchMode(boolean register) {
        isRegisterMode = register;
        binding.textTitle.setText(register ? getString(R.string.auth_register) : getString(R.string.auth_login));
        binding.inputConfirmPasswordLayout.setVisibility(register ? View.VISIBLE : View.GONE);
        binding.buttonSubmit.setText(register ? getString(R.string.auth_register) : getString(R.string.auth_login));
        binding.textSwitchMode.setText(register ? getString(R.string.auth_switch_to_login) : getString(R.string.auth_switch_to_register));
    }

    private void handleSubmit() {
        String username = getText(binding.inputUsername);
        String password = getText(binding.inputPassword);
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isRegisterMode) {
            String confirm = getText(binding.inputConfirmPassword);
            if (!password.equals(confirm)) {
                binding.inputConfirmPassword.setError("两次输入不一致");
                return;
            }
            submitRegister(username, password);
        } else {
            submitLogin(username, password);
        }
    }

    private void submitLogin(String username, String password) {
        binding.progress.setVisibility(View.VISIBLE);
        viewModel.login(username, password).observe(this, resource -> handleResult(resource));
    }

    private void submitRegister(String username, String password) {
        binding.progress.setVisibility(View.VISIBLE);
        viewModel.register(username, password).observe(this, resource -> handleResult(resource));
    }

    private void handleResult(Resource<Boolean> resource) {
        if (resource == null) return;
        if (resource.status == Resource.Status.LOADING) {
            binding.progress.setVisibility(View.VISIBLE);
            return;
        }
        binding.progress.setVisibility(View.GONE);
        if (resource.status == Resource.Status.SUCCESS) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
        }
    }

    private String getText(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}

