package com.carlisle.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleComment {
    public int id;
    public String body;
    @SerializedName("likes_count")
    public int likesCount;
    @SerializedName("likes_url")
    public String likesUrl;
    @SerializedName("created_at")
    public Date createdAt;
    @SerializedName("updated_at")
    public Date updatedAt;
    public DribleUser user;
}
