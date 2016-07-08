package com.yezi.meizhi.api;

import com.yezi.meizhi.api.service.MeiZhiService;

public class ServiceFactory {
    public static MeiZhiService getMeiZhiService() {
        return ServiceUtils.getApiService().getRetrofit().create(MeiZhiService.class);
    }
}
