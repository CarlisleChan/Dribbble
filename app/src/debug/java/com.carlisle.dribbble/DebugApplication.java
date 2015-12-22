package com.carlisle.dribbble;

import com.carlise.dribbble.BuildConfig;
import com.carlise.dribbble.application.DribbbleApplication;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by chengxin on 2/13/15.
 */
public class DebugApplication extends DribbbleApplication {

    private static DebugApplication instance;
    private RefWatcher refWatcher;

    public static DebugApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        refWatcher = installLeakCanary();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }

    protected RefWatcher installLeakCanary() {
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }
        return RefWatcher.DISABLED;
    }
}
