package com.mmushtaq.bank.remote;

import android.app.Application;
import android.content.Context;

public abstract class BaseApplication extends Application {

    private static Context context;


    public void onCreate() {
        super.onCreate();
        BaseApplication.context = getApplicationContext();

    }

    /**
     * @return context
     */
    public static Context getContext() {
        return BaseApplication.context;
    }
}