package com.yezi.meizhi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeiZhiMeiZhi extends BaseModel {
    @SerializedName("results")
    public List<MeiZhiDetail> meizhi;
}
