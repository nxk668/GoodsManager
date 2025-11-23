package com.example.goodsmanager.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.goodsmanager.databinding.ActivityStatisticsBinding;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private ActivityStatisticsBinding binding;
    private StatisticsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        observeData();
    }

    private void observeData() {
        viewModel.observeStatistics().observe(this, snapshot -> {
            if (snapshot == null) return;
            binding.textSummary.setText("共 " + snapshot.getTotalItems() + " 件，重点 " + snapshot.getFavorites() + " 件，资产约 ￥" + String.format("%.2f", snapshot.getTotalValue()));
        });

        viewModel.observeCategories().observe(this, categoryCounts -> {
            if (categoryCounts == null) return;
            List<PieEntry> entries = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();
            int[] palette = {Color.parseColor("#0A9396"), Color.parseColor("#94D2BD"), Color.parseColor("#EE9B00"), Color.parseColor("#CA6702"), Color.parseColor("#BB3E03")};
            for (int i = 0; i < categoryCounts.size(); i++) {
                entries.add(new PieEntry(categoryCounts.get(i).getCount(), categoryCounts.get(i).getCategory()));
                colors.add(palette[i % palette.length]);
            }
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(colors);
            PieData data = new PieData(dataSet);
            binding.pieChart.setData(data);
            Description description = new Description();
            description.setText("分类占比");
            binding.pieChart.setDescription(description);
            binding.pieChart.invalidate();

            StringBuilder builder = new StringBuilder();
            for (PieEntry entry : entries) {
                builder.append(entry.getLabel()).append("：").append((int) entry.getValue()).append("件\n");
            }
            binding.textDetail.setText(builder.toString());
        });
    }
}

