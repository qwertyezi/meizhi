package com.yezi.meizhi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeiZhiAll extends BaseModel {
    @SerializedName("休息视频")
    public List<MeiZhiDetail> video;
    @SerializedName("iOS")
    public List<MeiZhiDetail> iOS;
    @SerializedName("Android")
    public List<MeiZhiDetail> android;
    @SerializedName("前端")
    public List<MeiZhiDetail> frond;
    @SerializedName("App")
    public List<MeiZhiDetail> app;
    @SerializedName("福利")
    public List<MeiZhiDetail> meizhi;
    @SerializedName("瞎推荐")
    public List<MeiZhiDetail> recommend;
    @SerializedName("拓展资源")
    public List<MeiZhiDetail> resource;
}
