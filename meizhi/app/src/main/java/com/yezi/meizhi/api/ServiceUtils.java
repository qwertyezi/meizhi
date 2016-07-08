package com.yezi.meizhi.api;

public class ServiceUtils {
    private static ApiService sApiService;

    public static synchronized ApiService getApiService() {
        if (sApiService == null) {
            sApiService = new ApiService();
        }
        return sApiService;
    }
}
