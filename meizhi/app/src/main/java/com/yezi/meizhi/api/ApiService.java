package com.yezi.meizhi.api;

import android.app.Application;

import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    public static final String API_URL = MeiZhiApp.getAppResources().getString(R.string.api_url);
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private Retrofit retrofit;

    static OkHttpClient createOkHttpClient(Application app) {
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(cache);
        builder.addInterceptor(creatOkHttpInterceptor());
        return builder.build();
    }

    protected static HttpLoggingInterceptor creatOkHttpInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(createOkHttpClient(MeiZhiApp.getInstance()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
