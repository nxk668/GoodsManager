package com.example.goodsmanager.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TipService {

    @GET("advice")
    Call<TipResponse> fetchTip();
}

