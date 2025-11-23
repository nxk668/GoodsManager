package com.example.goodsmanager.ui.item;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.databinding.ItemGoodsBinding;
import com.example.goodsmanager.utils.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public interface OnItemClick {
        void onClick(ItemEntity entity);
    }

    private final List<ItemEntity> data = new ArrayList<>();
    private final OnItemClick onItemClick;

    public ItemAdapter(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoodsBinding binding = ItemGoodsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void submit(List<ItemEntity> entities) {
        data.clear();
        if (entities != null) {
            data.addAll(entities);
        }
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemGoodsBinding binding;

        ItemViewHolder(ItemGoodsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ItemEntity entity) {
            binding.textName.setText(entity.getName());
            binding.textCategory.setText(entity.getCategory());
            String detail = entity.getStorageDetail() == null || entity.getStorageDetail().isEmpty() ? "--" : entity.getStorageDetail();
            binding.textLocation.setText(entity.getLocationZone() + " · " + detail);
            binding.textValue.setText("￥" + String.format("%.2f", entity.getValue()));
            binding.textDate.setText(DateFormatUtils.format(entity.getPurchaseDate()));
            binding.chipFavorite.setVisibility(entity.isFavorite() ? android.view.View.VISIBLE : android.view.View.GONE);
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClick != null) {
                    onItemClick.onClick(entity);
                }
            });
        }
    }
}

