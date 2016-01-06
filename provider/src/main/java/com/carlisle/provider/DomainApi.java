package com.carlisle.provider;

import com.carlisle.model.AuthRequest;
import com.carlisle.model.AuthResult;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by chengxin on 1/6/16.
 */
public interface DomainApi {
    @POST("/oauth/token")
    Observable<AuthResult> auth(@Body AuthRequest authRequest);
}
