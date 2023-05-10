package com.qw73.fileManager.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.qw73.fileManager.glide.apk.ApkIconModelLoaderFactory;
import com.qw73.fileManager.glide.icon.IconModelLoaderFactory;
import com.qw73.fileManager.glide.model.IconRes;

@GlideModule
public class FileExplorerGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, Registry registry) {
        registry.prepend(String.class, Drawable.class, new ApkIconModelLoaderFactory(context));
        registry.prepend(IconRes.class, Drawable.class, new IconModelLoaderFactory(context));
    }
}