package com.mmushtaq.bank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mmushtaq.bank.interfaces.NetworkChangeListener;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private final NetworkChangeListener networkChangeListener;
    public NetworkChangeReceiver(NetworkChangeListener networkChangeListener)
    {
        this.networkChangeListener=networkChangeListener;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (isNetworkAvailable(context)) {
            // Call the function you want to run when the device is connected to the internet
            networkChangeListener.onNetworkAvailable();
        }
        else {
            networkChangeListener.onNetworkUnavailable();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}