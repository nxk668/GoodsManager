package com.example.goodsmanager.di;

import android.content.Context;

import com.example.goodsmanager.data.local.GoodsDatabase;
import com.example.goodsmanager.data.repository.ItemRepository;
import com.example.goodsmanager.data.repository.UserPreferencesRepository;
import com.example.goodsmanager.network.TipService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContainer {

    private static final String BASE_URL = "https://api.adviceslip.com/";

    public final ItemRepository itemRepository;
    public final UserPreferencesRepository preferencesRepository;

    public AppContainer(Context context) {
        Context appContext = context.getApplicationContext();
        GoodsDatabase database = GoodsDatabase.getInstance(appContext);
        TipService tipService = buildRetrofit().create(TipService.class);
        itemRepository = new ItemRepository(database.itemDao(), database.borrowRecordDao(), tipService);
        preferencesRepository = new UserPreferencesRepository(appContext);
    }

    private Retrofit buildRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
}

