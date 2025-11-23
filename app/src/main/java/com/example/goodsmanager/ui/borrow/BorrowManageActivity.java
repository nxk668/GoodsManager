package com.example.goodsmanager.ui.borrow;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.goodsmanager.R;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.databinding.ActivityBorrowManageBinding;

public class BorrowManageActivity extends AppCompatActivity {

    private ActivityBorrowManageBinding binding;
    private BorrowManageViewModel viewModel;
    private BorrowRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBorrowManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(BorrowManageViewModel.class);
        adapter = new BorrowRecordAdapter(this::markReturn);
        binding.recyclerBorrow.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerBorrow.setAdapter(adapter);

        viewModel.getRecords().observe(this, adapter::submit);
    }

    private void markReturn(BorrowRecordEntity entity) {
        entity.setStatus("已归还");
        entity.setActualReturn(new java.util.Date());
        viewModel.markReturn(entity);
        Toast.makeText(this, R.string.msg_return_marked, Toast.LENGTH_SHORT).show();
    }
}

