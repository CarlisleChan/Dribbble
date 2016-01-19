package com.carlisle.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleBucket {
    public static final String DRIBLE_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public long id;
    public String name;
    public String description;
    @SerializedName("shots_count")
    public int shotsCount;
    @SerializedName("created_at")
    public Date createdAt;
    @SerializedName("updated_at")
    public Date updatedAt;

}
