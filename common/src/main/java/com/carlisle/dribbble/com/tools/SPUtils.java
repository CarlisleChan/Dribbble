package com.carlisle.dribbble.com.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by chengxin on 1/6/16.
 */
public class SPUtils {
    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(key, value);
        editor.commit();
    }
}
