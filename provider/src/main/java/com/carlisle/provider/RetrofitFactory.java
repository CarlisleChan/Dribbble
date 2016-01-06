package com.carlisle.provider;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by chengxin on 1/6/16.
 */
public class RetrofitFactory {
    public static RestAdapter getRestAdapter(Domain.DomainType domainType) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Domain.get(domainType))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        OkClient okClient = new OkClient(okHttpClient);
        builder.setClient(okClient);

//        builder.setRequestInterceptor(new RequestInterceptor() {
//            @Override
//            public void intercept(RequestFacade request) {
//                if (tokenGetter != null) {
//                    String token = tokenGetter.get();
//                    if (!TextUtils.isEmpty(token)) {
//                        request.addQueryParam("token", token);
//                    }
//                }
//
//            }
//        });

        RestAdapter restAdapter = builder.build();
        return restAdapter;
    }
}
