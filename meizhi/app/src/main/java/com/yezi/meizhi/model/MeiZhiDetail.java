package com.yezi.meizhi.model;

import com.google.gson.annotations.SerializedName;

public class MeiZhiDetail extends BaseModel {
    @SerializedName("_id")
    public String id;
    @SerializedName("_ns")
    public String ns;
    public String who;
    public String publishedAt;
    public String desc;
    public String type;
    public String url;
    public String used;
    public String createdAt;
    public String source;
}