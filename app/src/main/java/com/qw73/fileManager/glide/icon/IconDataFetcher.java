package com.qw73.fileManager.glide.icon;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.qw73.fileManager.glide.model.IconRes;

public class IconDataFetcher implements DataFetcher<Drawable> {

    private final Context context;
    private final IconRes model;

    public IconDataFetcher(Context context, IconRes model) {
        this.context = context;
        this.model = model;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Drawable> callback) {
        Context ctx = (model.getContext() != null) ? model.getContext() : context;
        Drawable drawable = ContextCompat.getDrawable(ctx, model.getResId());
        callback.onDataReady(drawable);
    }

    @Override
    public void cleanup() {
        // Intentionally empty only because we're not opening an InputStream or another I/O resource!
    }

    @Override
    public void cancel() {
        // No cancellation procedure
    }

    @NonNull
    @Override
    public Class<Drawable> getDataClass() {
        return Drawable.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
