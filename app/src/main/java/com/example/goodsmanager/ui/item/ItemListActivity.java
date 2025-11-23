package com.example.goodsmanager.ui.item;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.goodsmanager.databinding.ActivityItemListBinding;

public class ItemListActivity extends AppCompatActivity {

    private ActivityItemListBinding binding;
    private ItemListViewModel viewModel;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(ItemListViewModel.class);
        setupRecyclerView();
        setupSearch();
        observe();
    }

    private void setupRecyclerView() {
        adapter = new ItemAdapter(entity -> ItemDetailActivity.start(this, entity.getId()));
        binding.recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerItems.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void observe() {
        viewModel.getItems().observe(this, entities -> {
            adapter.submit(entities);
            boolean empty = entities == null || entities.isEmpty();
            binding.textEmpty.setVisibility(empty ? android.view.View.VISIBLE : android.view.View.GONE);
        });
    }
}

