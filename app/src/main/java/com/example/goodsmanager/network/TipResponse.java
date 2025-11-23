package com.example.goodsmanager.network;

import com.google.gson.annotations.SerializedName;

public class TipResponse {

    @SerializedName("slip")
    private Slip slip;

    public Slip getSlip() {
        return slip;
    }

    public static class Slip {
        @SerializedName("advice")
        private String advice;

        public String getAdvice() {
            return advice;
        }
    }
}

