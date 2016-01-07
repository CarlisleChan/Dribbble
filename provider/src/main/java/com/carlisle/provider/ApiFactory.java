package com.carlisle.provider;

/**
 * Created by chengxin on 1/6/16.
 */
public class ApiFactory {
    private static DomainApi domainApi;
    private static DribleApi dribleApi;

    public static void init(TokenGetter tokenGetter) {
        RetrofitFactory.setTokenGetter(tokenGetter);
    }

    public synchronized static DomainApi getDomainApi() {
        if (domainApi == null) {
            domainApi = RetrofitFactory.getRestAdapter(Domain.DomainType.DOMAIN).create(DomainApi.class);
        }
        return domainApi;
    }

    public synchronized static DribleApi getDribleApi() {
        if (dribleApi == null) {
            dribleApi = RetrofitFactory.getRestAdapter(Domain.DomainType.DRIBLE).create(DribleApi.class);
        }
        return dribleApi;
    }
}
