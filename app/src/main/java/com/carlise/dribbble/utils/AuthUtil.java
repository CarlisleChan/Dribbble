package com.carlise.dribbble.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.carlise.dribbble.main.HomeActivity;
import com.carlise.dribbble.main.LoginActivity;
import com.carlisle.model.DribleUser;
import com.carlisle.tools.SPUtils;

/**
 * Created by zhanglei on 15/7/28.
 */
public class AuthUtil {
    public static String getAccessToken(Context context) {
        String access_token = SPUtils.getSharedPreference(context).getString(PreferenceKey.DRIBLE_TOKEN_FIELD, null);

        if (TextUtils.isEmpty(access_token)) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
        return access_token;
    }

    public static DribleUser getMe(Context context) {
        int id = UserHelper.getInstance(context).getDribleUser().id;

        if (id == -1) {
            Intent intent = new Intent(context, LoginActivity.class);
            ((Activity) context).finish();
            ((Activity) context).overridePendingTransition(0, 0);

            context.startActivity(intent);

            return null;
        }

        return UserHelper.getInstance(context).getDribleUser();
    }

    public static void checkIfLogin(Context context) {
        String access = SPUtils.getSharedPreference(context).getString(PreferenceKey.DRIBLE_TOKEN_FIELD, null);
        if (!TextUtils.isEmpty(access)) {
            goHome(context);
        } else {
            goLogin(context);
        }
    }

    public static void goHome(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    public static void goLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static boolean hasUserInfo(Context context) {
        int id = UserHelper.getInstance(context).getDribleUser().id;
        return id != -1;
    }

    private static void clearAuthInfo(Context context) {
        SPUtils.getSharedPreference(context).edit().clear().commit();
    }

}
