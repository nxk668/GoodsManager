package com.example.goodsmanager.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.databinding.ItemRecentBinding;
import com.example.goodsmanager.utils.DateFormatUtils;
import com.example.goodsmanager.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class RecentItemAdapter extends RecyclerView.Adapter<RecentItemAdapter.RecentViewHolder> {

    public interface OnItemClick {
        void onClick(ItemEntity entity);
    }

    private final List<ItemEntity> items = new ArrayList<>();
    private final OnItemClick onItemClick;

    public RecentItemAdapter(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentBinding binding = ItemRecentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void submit(List<ItemEntity> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    class RecentViewHolder extends RecyclerView.ViewHolder {

        private final ItemRecentBinding binding;

        RecentViewHolder(ItemRecentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ItemEntity entity) {
            binding.textName.setText(entity.getName());
            binding.textCategory.setText(entity.getCategory());
            binding.textLocation.setText(entity.getLocationZone());
            binding.textDate.setText(DateFormatUtils.format(entity.getPurchaseDate()));
            ImageLoader.load(binding.imageCover, entity.getPhotoUri());
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClick != null) {
                    onItemClick.onClick(entity);
                }
            });
        }
    }
}

