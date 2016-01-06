package com.carlisle.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chengxin on 1/6/16.
 */
public class AuthResult {
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("token_type")
    public String tokenType;
    public String scope;
    public String error;
    @SerializedName("error_description")
    public String errorDescription;
}
