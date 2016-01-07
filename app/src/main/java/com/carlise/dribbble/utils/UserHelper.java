package com.carlise.dribbble.utils;

import android.content.Context;
import android.text.TextUtils;

import com.carlisle.model.DribleUser;
import com.carlisle.tools.SPUtils;
import com.google.gson.Gson;

/**
 * Created by chengxin on 1/7/16.
 */
public class UserHelper {
    private static UserHelper instance;

    private Context context;
    private DribleUser dribleUser;

    public static UserHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserHelper();
        }
        instance.context = context;
        return instance;
    }

    public DribleUser getDribleUser() {
        if (dribleUser == null) {
            String userJson = SPUtils.getSharedPreference(context).getString(PreferenceKey.DRIBLE_USER_INFO, null);
            if (TextUtils.isEmpty(userJson)) {
                dribleUser = new DribleUser();
            } else {
                dribleUser = new Gson().fromJson(userJson, DribleUser.class);
            }
        }
        return dribleUser;
    }

    public void saveDribleUser(DribleUser dribleUser) {
        this.dribleUser = dribleUser;
        SPUtils.getSharedPreference(context).edit().putString(PreferenceKey.DRIBLE_USER_INFO, new Gson().toJson(dribleUser)).commit();
    }

    public void clearData() {
        SPUtils.getSharedPreference(context).edit().remove(PreferenceKey.DRIBLE_USER_INFO);
    }
}
