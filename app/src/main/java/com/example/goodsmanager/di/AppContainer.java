package com.example.goodsmanager.di;

import android.content.Context;

import com.example.goodsmanager.auth.AuthRepository;
import com.example.goodsmanager.data.local.GoodsDatabase;
import com.example.goodsmanager.data.repository.ItemRepository;
import com.example.goodsmanager.data.repository.UserPreferencesRepository;
import com.example.goodsmanager.network.TipService;
import com.example.goodsmanager.session.SessionManager;
import com.example.goodsmanager.sync.LeanSyncManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppContainer {

    private static final String BASE_URL = "https://api.adviceslip.com/";

    public final ItemRepository itemRepository;
    public final UserPreferencesRepository preferencesRepository;
    public final SessionManager sessionManager;
    public final AuthRepository authRepository;
    public final LeanSyncManager syncManager;

    public AppContainer(Context context) {
        Context appContext = context.getApplicationContext();
        GoodsDatabase database = GoodsDatabase.getInstance(appContext);
        TipService tipService = buildRetrofit().create(TipService.class);
        sessionManager = new SessionManager(appContext);
        syncManager = new LeanSyncManager(database.itemDao(), database.borrowRecordDao());
        itemRepository = new ItemRepository(database.itemDao(), database.borrowRecordDao(), tipService, sessionManager, syncManager);
        preferencesRepository = new UserPreferencesRepository(appContext);
        authRepository = new AuthRepository(sessionManager);
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

