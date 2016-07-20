package com.yezi.meizhi.api.service;

import com.yezi.meizhi.model.MeiZhiMeiZhi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MeiZhiService {

    @GET("data/{type}/{count}/{page}")
    Call<MeiZhiMeiZhi> getCategoryList(
            @Path("type") String type,
            @Path("count") int count,
            @Path("page") int page
    );

    @GET("random/data/{type}/{count}")
    Call<MeiZhiMeiZhi> getRandomList(
            @Path("type") String type,
            @Path("count") int count
    );
}
