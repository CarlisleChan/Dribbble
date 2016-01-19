package com.carlise.dribbble.application;

import android.app.Application;

import com.carlise.dribbble.utils.PreferenceKey;
import com.carlisle.dribbble.com.tools.SPUtils;
import com.carlisle.provider.ApiFactory;
import com.carlisle.provider.TokenGetter;

/**
 * Created by chengxin on 12/21/15.
 */
public class DribbbleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiFactory.init(new TokenGetter() {
            @Override
            public String get() {
                return SPUtils.getSharedPreference(DribbbleApplication.this).getString(PreferenceKey.DRIBLE_TOKEN_FIELD, null);
            }
        });
    }
}
