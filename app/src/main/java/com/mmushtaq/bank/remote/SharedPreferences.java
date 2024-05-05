package com.mmushtaq.bank.remote;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;

import com.mmushtaq.bank.R;

public class SharedPreferences {


    public static void saveSharedPreference(String key, String value, Context context) {
        if (context != null) {
            android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    // for string only
    public static String getSharedPreferences(String key, Context context) {

        if (null != key) {
            if (context != null) {
                android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
                if (null != sharedPreferences.getString(key, "")) {
                    return sharedPreferences.getString(key, "");
                }
            }
        }
        return "";
    }
}
