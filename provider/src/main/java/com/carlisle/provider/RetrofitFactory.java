package com.carlisle.provider;

import android.text.TextUtils;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;

/**
 * Created by chengxin on 1/6/16.
 */
class RetrofitFactory {

    private static TokenGetter tokenGetter;

    private RetrofitFactory() {
    }

    public static void setTokenGetter(TokenGetter tokenGetter) {
        RetrofitFactory.tokenGetter = tokenGetter;
    }

    public static RestAdapter getRestAdapter(Domain.DomainType domainType) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Domain.get(domainType))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        OkClient okClient = new OkClient(okHttpClient);
        builder.setClient(okClient);

        builder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                String accessToken = tokenGetter.get();
                if (!TextUtils.isEmpty(accessToken)) {
                    request.addHeader("Authorization", " Bearer " + accessToken);
                }
            }
        }).setErrorHandler(new ErrorHandler() {
            @Override
            public Throwable handleError(RetrofitError cause) {
                return cause;
            }
        });

        RestAdapter restAdapter = builder.build();
        return restAdapter;
    }
}
