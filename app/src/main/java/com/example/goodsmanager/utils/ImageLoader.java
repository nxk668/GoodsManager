package com.example.goodsmanager.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.goodsmanager.R;

public final class ImageLoader {

    private ImageLoader() {
    }

    public static void load(ImageView imageView, String uri) {
        Glide.with(imageView.getContext())
                .load(uri)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}

