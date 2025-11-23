package com.example.goodsmanager.ui.item;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.goodsmanager.R;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.databinding.ActivityItemEditBinding;
import com.example.goodsmanager.utils.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;

public class ItemEditActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private ActivityItemEditBinding binding;
    private ItemEditViewModel viewModel;
    private ItemEntity editingEntity;

    public static void start(Context context, long itemId) {
        Intent intent = new Intent(context, ItemEditActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(ItemEditViewModel.class);
        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId > 0) {
            binding.toolbar.setTitle(R.string.title_item_edit);
            binding.buttonDelete.setVisibility(android.view.View.VISIBLE);
            viewModel.loadItem(itemId).observe(this, entity -> {
                editingEntity = entity;
                fillForm(entity);
            });
        }

        binding.inputPurchaseDate.setOnClickListener(v -> showDatePicker(binding.inputPurchaseDate));
        binding.inputWarranty.setOnClickListener(v -> showDatePicker(binding.inputWarranty));
        binding.buttonSave.setOnClickListener(v -> save());
        binding.buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    private void fillForm(ItemEntity entity) {
        if (entity == null) return;
        binding.inputName.setText(entity.getName());
        binding.inputCategory.setText(entity.getCategory());
        binding.inputLocation.setText(entity.getLocationZone());
        binding.inputStorage.setText(entity.getStorageDetail());
        binding.inputTags.setText(entity.getTags());
        binding.inputValue.setText(String.valueOf(entity.getValue()));
        binding.inputQuantity.setText(String.valueOf(entity.getQuantity()));
        binding.inputPurchaseDate.setText(DateFormatUtils.format(entity.getPurchaseDate()));
        binding.inputWarranty.setText(DateFormatUtils.format(entity.getWarrantyEnd()));
        binding.checkboxFavorite.setChecked(entity.isFavorite());
        binding.inputNote.setText(entity.getNote());
    }

    private void showDatePicker(com.google.android.material.textfield.TextInputEditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            target.setText(DateFormatUtils.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        String name = binding.inputName.getText() != null ? binding.inputName.getText().toString() : "";
        if (name.isEmpty()) {
            binding.inputName.setError("必填");
            return;
        }
        String category = fallback(getText(binding.inputCategory), "未分类");
        String location = fallback(getText(binding.inputLocation), "未标记");
        ItemEntity entity = editingEntity == null ? new ItemEntity(name, category, location) : editingEntity;

        entity.setName(name);
        entity.setCategory(category);
        entity.setLocationZone(location);
        entity.setStorageDetail(getText(binding.inputStorage));
        entity.setTags(getText(binding.inputTags));
        entity.setValue(parseDouble(binding.inputValue.getText()));
        entity.setQuantity((int) parseDouble(binding.inputQuantity.getText()));
        entity.setPurchaseDate(resolveDate(entity.getPurchaseDate(), binding.inputPurchaseDate));
        entity.setWarrantyEnd(resolveDate(entity.getWarrantyEnd(), binding.inputWarranty));
        entity.setFavorite(binding.checkboxFavorite.isChecked());
        entity.setNote(getText(binding.inputNote));
        entity.setQrPayload(entity.getName() + "|" + entity.getCategory() + "|" + entity.getLocationZone());

        viewModel.save(entity);
        Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    private Date resolveDate(Date original, com.google.android.material.textfield.TextInputEditText input) {
        Date parsed = DateFormatUtils.parse(getText(input));
        if (parsed != null) {
            return parsed;
        }
        return original != null ? original : new Date();
    }

    private void confirmDelete() {
        if (editingEntity == null) return;
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_delete_item)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    viewModel.delete(editingEntity);
                    Toast.makeText(this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private String getText(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }

    private String fallback(String value, String def) {
        return value == null || value.trim().isEmpty() ? def : value;
    }

    private double parseDouble(CharSequence text) {
        try {
            if (text == null) return 0;
            String str = text.toString();
            if (str.isEmpty()) return 0;
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

