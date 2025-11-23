package com.example.goodsmanager.ui.item;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.goodsmanager.R;
import com.example.goodsmanager.data.local.entity.BorrowRecordEntity;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.databinding.ActivityItemDetailBinding;
import com.example.goodsmanager.databinding.DialogBorrowBinding;
import com.example.goodsmanager.ui.borrow.BorrowRecordAdapter;
import com.example.goodsmanager.utils.DateFormatUtils;
import com.example.goodsmanager.utils.QrCodeGenerator;
import com.google.zxing.WriterException;

import java.util.Calendar;
import java.util.Date;

public class ItemDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_item_id";

    private ActivityItemDetailBinding binding;
    private ItemDetailViewModel viewModel;
    private long itemId;
    private ItemEntity currentItem;
    private BorrowRecordAdapter borrowRecordAdapter;

    public static void start(Context context, long itemId) {
        Intent intent = new Intent(context, ItemDetailActivity.class);
        intent.putExtra(EXTRA_ID, itemId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        itemId = getIntent().getLongExtra(EXTRA_ID, -1);
        viewModel = new ViewModelProvider(this).get(ItemDetailViewModel.class);

        borrowRecordAdapter = new BorrowRecordAdapter(record -> {
            viewModel.markReturned(record);
            Toast.makeText(this, R.string.msg_return_marked, Toast.LENGTH_SHORT).show();
        });
        binding.recyclerBorrow.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerBorrow.setAdapter(borrowRecordAdapter);

        binding.buttonEdit.setOnClickListener(v -> ItemEditActivity.start(this, itemId));
        binding.buttonShareQr.setOnClickListener(v -> shareQr());
        binding.buttonAddBorrow.setOnClickListener(v -> showBorrowDialog());

        observeData();
    }

    private void observeData() {
        viewModel.observeItem(itemId).observe(this, entity -> {
            currentItem = entity;
            if (entity != null) {
                binding.textName.setText(entity.getName());
                binding.textCategory.setText("分类：" + entity.getCategory());
                binding.textLocation.setText("位置：" + entity.getLocationZone() + " - " + entity.getStorageDetail());
                binding.textValue.setText("价值：￥" + entity.getValue() + " / 数量：" + entity.getQuantity());
                binding.textTags.setText("标签：" + entity.getTags());
                binding.textDates.setText("购入：" + DateFormatUtils.format(entity.getPurchaseDate()) +
                        " | 过保：" + DateFormatUtils.format(entity.getWarrantyEnd()));
                binding.textNote.setText(entity.getNote());
                renderQr(entity.getQrPayload());
            }
        });

        viewModel.observeBorrowRecords(itemId).observe(this, borrowRecordAdapter::submit);
    }

    private void renderQr(String payload) {
        if (payload == null || payload.isEmpty()) {
            binding.imageQr.setImageBitmap(null);
            return;
        }
        try {
            Bitmap bitmap = QrCodeGenerator.generate(payload, 400);
            binding.imageQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            binding.imageQr.setImageBitmap(null);
        }
    }

    private void showBorrowDialog() {
        DialogBorrowBinding dialogBinding = DialogBorrowBinding.inflate(LayoutInflater.from(this));
        dialogBinding.inputExpected.setOnClickListener(v -> showExpectedDatePicker(dialogBinding));

        new AlertDialog.Builder(this)
                .setTitle(R.string.title_borrow_manage)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.action_save, (dialog, which) -> {
                    BorrowRecordEntity entity = new BorrowRecordEntity(itemId,
                            dialogBinding.inputBorrower.getText() == null ? "" : dialogBinding.inputBorrower.getText().toString());
                    entity.setContact(dialogBinding.inputContact.getText() == null ? "" : dialogBinding.inputContact.getText().toString());
                    entity.setExpectedReturn(DateFormatUtils.parse(dialogBinding.inputExpected.getText() == null ? "" : dialogBinding.inputExpected.getText().toString()));
                    viewModel.saveBorrowRecord(entity);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showExpectedDatePicker(DialogBorrowBinding binding) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            binding.inputExpected.setText(DateFormatUtils.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void shareQr() {
        if (currentItem == null) return;
        String payload = currentItem.getQrPayload();
        if (payload == null || payload.isEmpty()) {
            Toast.makeText(this, R.string.msg_qr_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "：" + payload);
        startActivity(Intent.createChooser(intent, getString(R.string.action_share_qr)));
    }
}

