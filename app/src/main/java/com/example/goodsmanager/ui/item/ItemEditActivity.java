package com.example.goodsmanager.ui.item;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.goodsmanager.R;
import com.example.goodsmanager.data.local.entity.ItemEntity;
import com.example.goodsmanager.databinding.ActivityItemEditBinding;
import com.example.goodsmanager.utils.DateFormatUtils;
import com.example.goodsmanager.utils.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ItemEditActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private ActivityItemEditBinding binding;
    private ItemEditViewModel viewModel;
    private ItemEntity editingEntity;
    private String selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

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
        setupCategoryDropdown();
        setupImagePickers();
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
        selectedImageUri = entity.getPhotoUri();
        updatePhotoPreview();
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
        entity.setPhotoUri(selectedImageUri);

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

    private String getText(android.widget.TextView editText) {
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

    private void setupCategoryDropdown() {
        String[] categories = getResources().getStringArray(R.array.item_categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(categories));
        binding.inputCategory.setAdapter(adapter);
        if (editingEntity == null && categories.length > 0) {
            binding.inputCategory.setText(categories[0], false);
        }
        binding.inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.inputCategory.showDropDown();
            }
        });
        binding.inputCategory.setOnClickListener(v -> binding.inputCategory.showDropDown());
    }

    private void setupImagePickers() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                persistUriPermission(uri);
                selectedImageUri = uri.toString();
                updatePhotoPreview();
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                openImagePicker();
            } else {
                Toast.makeText(this, R.string.msg_permission_media_denied, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonPickPhoto.setOnClickListener(v -> requestMediaPermissionThenPick());
        binding.buttonDefaultPhoto.setOnClickListener(v -> showDefaultIconDialog());
    }

    private void requestMediaPermissionThenPick() {
        String permission = getMediaPermission();
        if (permission == null || ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private String getMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    private void updatePhotoPreview() {
        if (selectedImageUri == null || selectedImageUri.isEmpty()) {
            binding.imagePhoto.setImageResource(R.drawable.ic_placeholder);
        } else {
            ImageLoader.load(binding.imagePhoto, selectedImageUri);
        }
    }

    private void showDefaultIconDialog() {
        try {
            String[] icons = getAssets().list("default_icons");
            if (icons == null || icons.length == 0) {
                Toast.makeText(this, R.string.msg_no_default_icon, Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_default_icon_title)
                    .setItems(icons, (dialog, which) -> {
                        Uri uri = copyAssetIcon(icons[which]);
                        if (uri != null) {
                            selectedImageUri = uri.toString();
                            updatePhotoPreview();
                        }
                    })
                    .show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Uri copyAssetIcon(String fileName) {
        File dir = new File(getFilesDir(), "default_icons");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outFile = new File(dir, fileName);
        try (InputStream in = getAssets().open("default_icons/" + fileName);
             FileOutputStream out = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[2048];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return Uri.fromFile(outFile);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void persistUriPermission(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            ContentResolver resolver = getContentResolver();
            try {
                resolver.takePersistableUriPermission(uri, flags);
            } catch (SecurityException ignored) {
            }
        }
    }
}

