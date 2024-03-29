package com.tellago;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

@GlideModule
public class GlideAppModule extends AppGlideModule {
    @Override
    public void registerComponents(@NotNull Context context, @NotNull Glide glide, Registry registry) {
        registry.append(
            StorageReference.class,
            InputStream.class,
            new FirebaseImageLoader.Factory()
        );
    }
}
