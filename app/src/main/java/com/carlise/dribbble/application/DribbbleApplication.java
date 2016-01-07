package com.carlise.dribbble.application;

import android.app.Application;

import com.carlise.dribbble.utils.PreferenceKey;
import com.carlisle.provider.ApiFactory;
import com.carlisle.provider.TokenGetter;
import com.carlisle.tools.SPUtils;

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
