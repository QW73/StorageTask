package com.qw73.fileManager.glide.icon;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.qw73.fileManager.glide.model.IconRes;

public class IconModelLoaderFactory implements ModelLoaderFactory<IconRes, Drawable> {

    private final Context context;

    public IconModelLoaderFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ModelLoader<IconRes, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new IconModelLoader(context);
    }

    @Override
    public void teardown() {
    }
}