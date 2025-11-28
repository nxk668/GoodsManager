package com.example.goodsmanager.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.goodsmanager.GoodsManagerApp;
import com.example.goodsmanager.R;
import com.example.goodsmanager.core.Resource;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.data.model.StatisticsSnapshot;
import com.example.goodsmanager.databinding.ActivityMainBinding;
import com.example.goodsmanager.ui.borrow.BorrowManageActivity;
import com.example.goodsmanager.ui.LauncherActivity;
import com.example.goodsmanager.ui.item.ItemDetailActivity;
import com.example.goodsmanager.ui.item.ItemEditActivity;
import com.example.goodsmanager.ui.item.ItemListActivity;
import com.example.goodsmanager.ui.settings.SettingsActivity;
import com.example.goodsmanager.ui.statistics.StatisticsActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private RecentItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        setupRecyclerView();
        setupClicks();
        observeViewModel();
        requestNotificationPermission();
        viewModel.syncNow();
    }

    private void setupRecyclerView() {
        adapter = new RecentItemAdapter(this::openDetail);
        binding.recyclerRecent.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerRecent.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userId = ((GoodsManagerApp) getApplication()).getAppContainer().sessionManager.getCurrentUserId();
        if (userId == null) {
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
        }
    }

    private void setupClicks() {
        binding.buttonItems.setOnClickListener(v -> startActivity(new Intent(this, ItemListActivity.class)));
        binding.buttonBorrow.setOnClickListener(v -> startActivity(new Intent(this, BorrowManageActivity.class)));
        binding.buttonStats.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        binding.buttonSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        binding.fabAdd.setOnClickListener(v -> startActivity(new Intent(this, ItemEditActivity.class)));
        binding.buttonRefreshTip.setOnClickListener(v -> viewModel.refreshTip());
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshTip();
            viewModel.syncNow();
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void observeViewModel() {
        viewModel.getRecentItems().observe(this, this::renderRecentItems);
        viewModel.getStatisticsSnapshot().observe(this, this::renderStatistics);
        viewModel.getTipLiveData().observe(this, resource -> {
            if (resource == null) {
                return;
            }
            if (resource.status == Resource.Status.LOADING) {
                binding.textTip.setText(getString(R.string.msg_tip_loading));
            } else if (resource.status == Resource.Status.ERROR) {
                binding.textTip.setText(getString(R.string.msg_tip_error));
            } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.textTip.setText(resource.data);
            }
        });
    }

    private void renderRecentItems(List<ItemEntity> entities) {
        adapter.submit(entities);
        boolean empty = entities == null || entities.isEmpty();
        binding.textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void renderStatistics(StatisticsSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        binding.textTotalItems.setText(snapshot.getTotalItems() + " 件物品");
        binding.textTotalValue.setText(getString(R.string.stats_total_asset) + " ￥" + String.format("%.2f", snapshot.getTotalValue()));
        StringBuilder builder = new StringBuilder();
        if (snapshot.getTopCategories().isEmpty()) {
            binding.textCategories.setText(R.string.state_empty);
        } else {
            for (int i = 0; i < snapshot.getTopCategories().size(); i++) {
                builder.append(snapshot.getTopCategories().get(i));
                if (i < snapshot.getTopCategories().size() - 1) {
                    builder.append(" | ");
                }
            }
            binding.textCategories.setText(builder.toString());
        }
    }

    private void openDetail(ItemEntity entity) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra(ItemDetailActivity.EXTRA_ID, entity.getId());
        startActivity(intent);
    }

    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }
}

