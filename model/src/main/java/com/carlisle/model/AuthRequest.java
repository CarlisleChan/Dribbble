package com.carlisle.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chengxin on 1/6/16.
 */
public class AuthRequest {
    @SerializedName("client_id")
    public String clientId;
    @SerializedName("client_secret")
    public String clientSecret;
    public String code;
    public String state;
}
