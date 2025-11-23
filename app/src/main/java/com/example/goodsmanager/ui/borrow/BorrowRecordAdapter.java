package com.example.goodsmanager.ui.borrow;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.databinding.ItemBorrowBinding;
import com.example.goodsmanager.utils.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class BorrowRecordAdapter extends RecyclerView.Adapter<BorrowRecordAdapter.BorrowViewHolder> {

    public interface OnBorrowAction {
        void onMarkReturn(BorrowRecordEntity entity);
    }

    private final List<BorrowRecordEntity> records = new ArrayList<>();
    private final OnBorrowAction onBorrowAction;

    public BorrowRecordAdapter(OnBorrowAction onBorrowAction) {
        this.onBorrowAction = onBorrowAction;
    }

    @NonNull
    @Override
    public BorrowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBorrowBinding binding = ItemBorrowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BorrowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowViewHolder holder, int position) {
        holder.bind(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void submit(List<BorrowRecordEntity> list) {
        records.clear();
        if (list != null) {
            records.addAll(list);
        }
        notifyDataSetChanged();
    }

    class BorrowViewHolder extends RecyclerView.ViewHolder {
        private final ItemBorrowBinding binding;

        BorrowViewHolder(ItemBorrowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(BorrowRecordEntity entity) {
            binding.textBorrower.setText(entity.getBorrowerName());
            binding.textContact.setText(entity.getContact());
            binding.textBorrowDate.setText("借出：" + DateFormatUtils.format(entity.getBorrowDate()));
            binding.textReturnDate.setText("应还：" + DateFormatUtils.format(entity.getExpectedReturn()));
            binding.textStatus.setText(entity.getStatus());
            binding.buttonReturn.setEnabled(!"已归还".equals(entity.getStatus()));
            binding.buttonReturn.setOnClickListener(v -> {
                if (onBorrowAction != null) {
                    onBorrowAction.onMarkReturn(entity);
                }
            });
        }
    }
}

