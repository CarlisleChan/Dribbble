package com.carlisle.provider;

import com.carlisle.model.DribleUser;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by chengxin on 1/6/16.
 */
public interface DribleApi {
    @GET("/user")
    Observable<DribleUser> fetchUserInfo();
}
